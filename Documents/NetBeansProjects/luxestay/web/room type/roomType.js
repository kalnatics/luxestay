$(document).ready(function () {
    let roomTypeID, roomType, price, maxPerson, action;

    // Initialize DataTable
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
            console.log("Response:", response);  // Debugging: Cek response dari server
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



    // Add Room Type button
    $("#btnAdd").click(function () {
        $("#myModal").modal('show');
        $("#addModalLabel").text("Add Room Type");
        $("#formTambah")[0].reset();
        action = "tambah";  
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

        let fixedAction = (action === "create") ? "tambah" : (action === "edit") ? "edit" : action;

        let data = {
            action: fixedAction,  // Mengubah "create" -> "tambah"
            roomTypeID: action === 'edit' ? roomTypeID : null,
            roomType: roomType,
            price: price,
            maxPerson: maxPerson
        };

        console.log("📢 Fixed AJAX Request Data:", data); // Debugging

        $.ajax({
            url: "/luxestay/RoomTypeCtr",
            method: "POST",
            data: data,
            success: function(response) {
                console.log("✅ Response from server:", response);
                if (response.success) {
                    alert(response.message);
                    $("#myModal").modal('hide');
                    table.ajax.reload();
                } else {
                    alert("Error: " + response.message);
                }
            },
            error: function(xhr, status, error) {
                console.error("❌ AJAX Error:", status, error);
                alert("An error occurred while processing your request.");
            }
        });
    });



    // Edit Room Type
    $('#tabelRoomType').on('click', '#btnEdit', function () {
        const row = $(this).closest('tr');
        roomTypeID = $(this).data("id"); // Changed to use data-id attribute
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

            let data = {
                action: "hapus", // Mengubah "delete" -> "hapus"
                roomTypeID: roomTypeID
            };

            console.log("📢 Fixed DELETE Request Data:", data); // Debugging

            $.ajax({
                url: "/luxestay/RoomTypeCtr",
                method: "POST",
                data: data,
                success: function (response) {
                    console.log("✅ Response from server:", response);
                    if (response.success) {
                        alert(response.message);
                        table.ajax.reload();
                    } else {
                        alert("Error: " + response.message);
                    }
                },
                error: function(xhr, status, error) {
                    console.error("❌ AJAX Error:", status, error);
                    alert("Error occurred while deleting room type.");
                }
            });
        }
    });
});