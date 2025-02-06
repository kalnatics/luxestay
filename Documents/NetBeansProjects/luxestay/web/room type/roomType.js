$(document).ready(function () {
    let roomTypeID, roomType, price, maxPerson, action;

    // Initialize DataTable
    const table = $('#tabelRoomType').DataTable({
        ajax: {
            url: '/luxestay/RoomTypeCtr',
            dataSrc: ''
        },
        columns: [
            { data: 'roomTypeID' },
            { data: 'roomType' },
            { data: 'price' },
            { data: 'maxPerson' },
            {
                data: null,
                render: function (data, type, row) {
                    return `
                        <button class="btn btn-warning btn-sm" id="btnEdit" data-id="${row.roomTypeID}">Edit</button>
                        <button class="btn btn-danger btn-sm" id="btnDelete" data-id="${row.roomTypeID}">Delete</button>
                    `;
                }
            }
        ]
    });

    // Add Room Type button
    $("#btnAdd").click(function () {
        $("#myModal").modal('show');
        $("#addModalLabel").text("Add Room Type");
        $("#formTambah")[0].reset();
        action = "create";
    });

    // Save Room Type (Add/Edit)
    $("#btnSave").on('click', function () {
        roomType = $("#roomType").val();
        price = $("#price").val();
        maxPerson = $("#maxPerson").val();

        if (!roomType || !price || !maxPerson) {
            alert("All fields are required!");
            return;
        }

        $.ajax({
            url: "/luxestay/RoomTypeCtr",
            method: "POST",
            data: {
                action: action,
                roomTypeID: action === 'edit' ? roomTypeID : null,
                roomType: roomType,
                price: price,
                maxPerson: maxPerson
            },
            success: function(response) {
                if (response.success) {
                    alert(response.message);
                    $("#myModal").modal('hide');
                    table.ajax.reload();
                } else {
                    alert("Error: " + response.message);
                }
            },
            error: function(xhr, status, error) {
                console.error("AJAX Error:", status, error);
                alert("An error occurred while processing your request.");
            }
        });
    });

    // Edit Room Type
    $('#tabelRoomType').on('click', '#btnEdit', function () {
        const row = $(this).closest('tr');
        roomTypeID = row.find('td').eq(0).text();
        roomType = row.find('td').eq(1).text();
        price = row.find('td').eq(2).text();
        maxPerson = row.find('td').eq(3).text();

        $("#myModal").modal('show');
        $("#addModalLabel").text("Edit Room Type");
        $("#roomType").val(roomType);
        $("#price").val(price);
        $("#maxPerson").val(maxPerson);
        action = "edit";
    });

    // Delete Room Type
    $('#tabelRoomType').on('click', '#btnDelete', function () {
        if (confirm("Are you sure you want to delete this room type?")) {
            const roomTypeID = $(this).data("id");

            $.ajax({
                url: "/luxestay/RoomTypeCtr",
                method: "POST",
                data: { action: "delete", roomTypeID: roomTypeID },
                success: function (response) {
                    if (response.success) {
                        alert(response.message);
                        table.ajax.reload();
                    } else {
                        alert("Error: " + response.message);
                    }
                },
                error: function(xhr, status, error) {
                    console.error("AJAX Error:", status, error);
                    alert("Error occurred while deleting room type.");
                }
            });
        }
    });
});
