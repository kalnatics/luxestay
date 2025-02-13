$(document).ready(function () {
    let table;

    function loadCustomers() {
        $.get('/luxestay/BookingCtr', function (data) {
            $('#customerID').empty();
            if (Array.isArray(data)) {
                data.forEach(customer => {
                    $('#customerID').append(`<option value="${customer.customerID}">${customer.name}</option>`);
                });
            } else {
                console.error("Received data is not an array:", data);
            }
        }).fail(function (xhr) {
            console.error("Failed to load customers:", xhr.responseText);
        });
    }

    function loadRooms() {
        $.get('/luxestay/RoomCtr', function (data) {
            $('#roomID').empty();
            if (Array.isArray(data)) {
                data.forEach(room => {
                    $('#roomID').append(`<option value="${room.roomID}">${room.roomNo} - ${room.roomType}</option>`);
                });
            } else {
                console.error("Received data is not an array:", data);
            }
        }).fail(function (xhr) {
            console.error("Failed to load rooms:", xhr.responseText);
        });
    }

    function calculateTotalPrice() {
        let totalPrice = 0;
        const roomIDs = $('#roomID').val();

        if (roomIDs && roomIDs.length > 0) {
            $.get('/luxestay/RoomCtr', function (data) {
                if (Array.isArray(data)) {
                    data.forEach(room => {
                        if (roomIDs.includes(room.roomID.toString())) {
                            totalPrice += room.price;
                        }
                    });
                    $("#totalPrice").val(totalPrice);
                }
            }).fail(function (xhr) {
                console.error("Failed to load room data:", xhr.responseText);
            });
        }
    }

    // Initialize DataTable
    table = $('#bookingTable').DataTable({
        ajax: {
            url: '/luxestay/BookingCtr',
            dataSrc: ''
        },
        columns: [
            { data: 'name', title: 'Customer Name' },
            { 
                data: 'roomNo', 
                title: 'Room Numbers',
                render: function(data) {
                    return data ? data.split(',').join(', ') : '';
                }
            },
            { 
                data: 'roomType', 
                title: 'Room Types',
                render: function(data) {
                    return data ? data.split(',').join(', ') : '';
                }
            },
            { data: 'bookingDate', title: 'Booking Date' },
            { data: 'checkIn', title: 'Check In' },
            { data: 'checkOut', title: 'Check Out' },
            { data: 'totalPrice', title: 'Total Price' },
            {
                data: null,
                render: function (data, type, row) {
                    return ` 
                        <button class="btn btn-outline-success btn-sm btnEdit" data-id="${row.reservationID}">Edit</button>
                        <button class="btn btn-outline-danger btn-sm btnDelete" data-id="${row.reservationID}">Delete</button>`;
                }
            }
        ]
    });

    // Load Customers and Rooms only once
    loadCustomers();
    loadRooms();

    // Add new booking
    $("#btnAddBooking").click(function () {
        $("#bookingModal").modal('show');
        $("#bookingModalLabel").text("Add New Booking");
        $("#bookingForm")[0].reset();
        $("#bookingForm").data('mode', 'create');
        $("#roomID").val(null).trigger('change');
        calculateTotalPrice();
    });

    // Submit form for add/edit booking
    $("#bookingForm").on('submit', function (e) {
        e.preventDefault();
        const mode = $(this).data('mode');
        const selectedRooms = $('#roomID').val();

        const data = {
            action: mode === 'create' ? 'tambah' : 'edit',
            reservationID: mode === 'edit' ? $("#reservationID").val() : null,
            customerID: $("#customerID").val(),
            bookingDate: $("#bookingDate").val(),
            checkIn: $("#checkIn").val(),
            checkOut: $("#checkOut").val(),
            totalPrice: $("#totalPrice").val(),
            roomIDs: selectedRooms
        };

        if (!data.customerID || !data.bookingDate || !data.checkIn || !data.checkOut || !data.totalPrice || !data.roomIDs || data.roomIDs.length === 0) {
            alert("Please fill all required fields!");
            return;
        }

        $.ajax({
            url: "/luxestay/BookingCtr",
            method: "POST",
            data: JSON.stringify(data),
            contentType: "application/json",
            success: function (response) {
                if (response.success) {
                    $("#bookingModal").modal('hide');
                    table.ajax.reload();
                    alert(response.message);
                } else {
                    alert(response.message);
                }
            },
            error: function () {
                alert("Error processing request");
            }
        });
    });

    // Edit booking
    $('#bookingTable').on('click', '.btnEdit', function () {
        const reservationID = $(this).data('id');
        $.get('/luxestay/BookingCtr', { action: 'get', reservationID: reservationID }, function (data) {
            $("#bookingModalLabel").text("Edit Booking");
            $("#reservationID").val(data.reservationID);
            $("#customerID").val(data.customerID);
            $("#bookingDate").val(data.bookingDate);
            $("#checkIn").val(data.checkIn);
            $("#checkOut").val(data.checkOut);
            $("#totalPrice").val(data.totalPrice);
            $("#status").val(data.status);
            $("#roomID").val(data.roomIDs).trigger('change');
            $("#bookingForm").data('mode', 'edit');
            $("#bookingModal").modal('show');
        }).fail(function () {
            alert("Error loading booking data.");
        });
    });

    // Delete booking
    $('#bookingTable').on('click', '.btnDelete', function () {
        const reservationID = $(this).data('id');
        if (confirm("Are you sure you want to delete this booking?")) {
            $.post("/luxestay/BookingCtr", {
                action: 'hapus',
                reservationID: reservationID
            }, function (response) {
                if (response.success) {
                    table.ajax.reload();
                    alert(response.message);
                } else {
                    alert(response.message);
                }
            });
        }
    });

});
