$(document).ready(function () {
    let table;

    function loadRoomTypes() {
        $.get('/luxestay/RoomTypeCtr', function (data) {
            $('#roomTypeID').empty();
            $('#roomTypeID').append('<option value="">Select Room Type</option>');
            if (Array.isArray(data)) {
                data.forEach(type => {
                    $('#roomTypeID').append(`<option value="${type.roomTypeID}">${type.roomType}</option>`);
                });
            }
        }).fail(function (xhr) {
            console.error("Failed to load room types:", xhr.responseText);
        });
    }

    // Initialize DataTable dengan refresh yang lebih baik
    $(document).ready(function () {
    let table = $('#roomTable').DataTable({
        ajax: {
            url: '/luxestay/RoomCtr',
            dataSrc: ''
        },
        columns: [
            { data: 'roomNo' },
            { data: 'roomTypeName' },
            { 
                data: 'price',
                render: function(data) {
                    return data ? `Rp ${parseInt(data).toLocaleString()}` : '-';
                }
            },
            { data: 'maxPerson' },
            {
                data: 'status',
                render: function (data) {
                    // Handle null/undefined status
                    if (data === null || data === undefined) {
                        return '<span class="badge bg-secondary">Not Set</span>';
                    }
                    return parseInt(data) === 1 ? 
                        '<span class="badge bg-danger">Terisi</span>' : 
                        '<span class="badge bg-success">Kosong</span>';
                }
            },
            {
                data: null,
                render: function (data, type, row) {
                    return `
                        <button class="btn btn-outline-success btn-sm btnEdit" data-id="${row.roomID}">
                            <i class="fas fa-edit"></i> Edit
                        </button>
                        <button class="btn btn-outline-danger btn-sm btnDelete" data-id="${row.roomID}">
                            <i class="fas fa-trash"></i> Delete
                        </button>`;
                }
            }
        ]
    });

    // Tambah Room
    $("#btnAddRoom").click(function () {
        loadRoomTypes();
        $("#roomModal").modal('show');
        $("#roomModalLabel").text("Add New Room");
        $("#roomForm")[0].reset();
        $("#roomID").val('');
        $("#roomForm").data('mode', 'tambah');
    });

    // Submit form
    $("#roomForm").on('submit', function (e) {
        e.preventDefault();
        
        const formData = {
            action: $(this).data('mode'),
            roomID: $("#roomID").val() || null,
            roomNo: $("#roomNo").val(),
            roomTypeID: $("#roomTypeID").val(),
            status: $("#status").val() || null
        };

        // Validasi
        if (!formData.roomNo || !formData.roomTypeID) {
            alert("Please fill required fields!");
            return;
        }

        $.ajax({
            url: "/luxestay/RoomCtr",
            method: "POST",
            data: formData,
            success: function (response) {
                if (response.success) {
                    $("#roomModal").modal('hide');
                    table.ajax.reload();
                    alert(response.message);
                } else {
                    alert(response.message || "Operation failed");
                }
            },
            error: function (xhr) {
                alert("Error: " + (xhr.responseJSON?.message || "Operation failed"));
            }
        });
    });
    
    // Edit button handler
    $('#roomTable').on('click', '.btnEdit', function () {
        const roomID = $(this).data('id');
        $.get('/luxestay/RoomCtr', { 
            action: 'get', 
            roomID: roomID 
        }, function (data) {
            $("#roomModalLabel").text("Edit Room");
            $("#roomID").val(data.roomID);
            $("#roomNo").val(data.roomNo);
            $("#roomTypeID").val(data.roomTypeID);
            $("#status").val(data.status);
            $("#roomForm").data('mode', 'edit');
            $("#roomModal").modal('show');
        });
    });

    // Delete button handler
    $('#roomTable').on('click', '.btnDelete', function () {
        const roomID = $(this).data('id');
        if (confirm("Are you sure you want to delete this room?")) {
            $.ajax({
                url: "/luxestay/RoomCtr",
                method: "POST",
                data: {
                    action: 'hapus',
                    roomID: roomID
                },
                dataType: 'json',
                success: function (response) {
                    if (response.success) {
                        table.ajax.reload();
                        alert(response.message);
                    } else {
                        alert(response.message || "Delete failed");
                    }
                },
                error: function (xhr) {
                    alert("Error: " + (xhr.responseJSON?.message || "Delete failed"));
                }
            });
        }
    });

    // Load initial data
    loadRoomTypes();
});