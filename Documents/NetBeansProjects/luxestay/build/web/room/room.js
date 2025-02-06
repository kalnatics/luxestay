$(document).ready(function () {
    let table;

    function loadRoomTypes() {
        $.get('/luxestay/RoomTypeCtr', function (data) {
            $('#roomTypeID').empty();
            if (Array.isArray(data)) {
                data.forEach(type => {
                    $('#roomTypeID').append(`<option value="${type.roomTypeID}">${type.roomType}</option>`);
                });
            } else {
                console.error("Received data is not an array:", data);
            }
        }).fail(function (xhr) {
            console.error("Failed to load room types:", xhr.responseText);
        });
    }

    loadRoomTypes();

    // Initialize DataTable
    table = $('#roomTable').DataTable({
        ajax: {
            url: '/luxestay/RoomCtr',
            dataSrc: ''
        },
        columns: [
            { data: 'roomNo' },
            { data: 'roomTypeName' }, // Menggunakan roomTypeName dari tabel room_type
            { data: 'price' },        // Tambahkan kolom price
            { data: 'maxPerson' },    // Menggunakan maxPerson dari tabel room_type
            { data: 'status' },
            {
                data: null,
                render: function (data, type, row) {
                    return `
                        <button class="btn btn-outline-success btn-sm btnEdit" data-id="${row.roomID}">Edit</button>
                        <button class="btn btn-outline-danger btn-sm btnDelete" data-id="${row.roomID}">Delete</button>`;
                }
            }
        ]
    });

    // Tombol untuk menambah room
    $("#btnAddRoom").click(function () {
        loadRoomTypes();
        $("#roomModal").modal('show');
        $("#roomModalLabel").text("Add New Room");
        $("#roomForm")[0].reset();
        $("#roomForm").data('mode', 'create');
    });

    // Submit form tambah/edit room
    $("#roomForm").on('submit', function (e) {
        e.preventDefault();
        const mode = $(this).data('mode');
        const data = {
            action: mode,
            roomID: mode === 'edit' ? $("#roomID").val() : null,
            roomNo: $("#roomNo").val(),
            roomTypeID: $("#roomTypeID").val(),
            status: $("#status").val() || 'Available'
        };

        if (!data.roomNo || !data.roomTypeID) {
            alert("Please fill all required fields!");
            return;
        }

        $.ajax({
            url: "/luxestay/RoomCtr",
            method: "POST",
            data: JSON.stringify(data),
            contentType: "application/json",
            success: function (response) {
                if (response.success) {
                    $("#roomModal").modal('hide');
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

    // Klik tombol edit
    $('#roomTable').on('click', '.btnEdit', function () {
        const roomID = $(this).data('id');
        $.get('/luxestay/RoomCtr', { action: 'get', roomID: roomID }, function (data) {
            loadRoomTypes();
            $("#roomModalLabel").text("Edit Room");
            $("#roomID").val(data.roomID);
            $("#roomNo").val(data.roomNo);
            $("#roomTypeID").val(data.roomTypeID);
            $("#status").val(data.status);
            $("#roomForm").data('mode', 'edit');
            $("#roomModal").modal('show');
        });
    });

    // Klik tombol delete
    $('#roomTable').on('click', '.btnDelete', function () {
        const roomID = $(this).data('id');
        if (confirm("Are you sure you want to delete this room?")) {
            $.post("/luxestay/RoomCtr", {
                action: 'delete',
                roomID: roomID
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

    loadRoomTypes();
});
