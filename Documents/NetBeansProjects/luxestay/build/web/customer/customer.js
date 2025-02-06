$(document).ready(function () {
    let customerID, idCardTypeID, name, phone, email, idCardNo, action;

    function loadIDCardTypes() {
        $.get('/luxestay/IDCardTypeCtr?action=all', function (data) {
            $('#idCardTypeID').empty();
            if (Array.isArray(data)) {
                data.forEach(type => {
                    $('#idCardTypeID').append(`<option value="${type.idCardTypeID}">${type.idCardType}</option>`);
                });
            } else {
                console.error("Received data is not an array:", data);
            }
        }).fail(function (xhr) {
            console.error("Failed to load ID card types:", xhr.responseText);
        });
    }

    const table = $('#customerTable').DataTable({
        ajax: {
            url: '/luxestay/CustomerCtr?action=all',
            dataSrc: ''
        },
        columns: [
            { data: 'customerID' },
            { data: 'name' },
            { data: 'phone' },
            { data: 'email' },
            { data: 'idCardNo' },
            { data: 'idCardType' },
            {
                data: null,
                render: function (data, type, row) {
                    return `
                        <button class="btn btn-warning btn-sm btnEdit" data-id="${row.customerID}">Edit</button>
                        <button class="btn btn-danger btn-sm btnDelete" data-id="${row.customerID}">Delete</button>`;
                }
            }
        ]
    });

    $("#btnAddCustomer").click(function () {
        loadIDCardTypes();
        $("#customerModal").modal('show');
        $("#customerModalLabel").text("Add Customer");
        $("#customerForm")[0].reset();
        action = "create";
    });

    $("#customerForm").submit(function (event) {
        event.preventDefault();
        customerID = $("#customerID").val();
        idCardTypeID = $("#idCardTypeID").val();
        name = $("#name").val();
        phone = $("#phone").val();
        email = $("#email").val();
        idCardNo = $("#idCardNo").val();

        if (!idCardTypeID || !name || !phone || !email || !idCardNo) {
            alert("All fields are required!");
            return;
        }

        const data = {
            customerID: customerID,
            idCardTypeID: idCardTypeID,
            name: name,
            phone: phone,
            email: email,
            idCardNo: idCardNo
        };

        const method = action === 'edit' ? "update" : "create";

        $.ajax({
            url: `/luxestay/CustomerCtr?action=${method}`,
            method: "POST",
            data: JSON.stringify(data),
            contentType: "application/json",
            success: function (response) {
                if (response.success) {
                    alert(response.message);
                    $("#customerModal").modal('hide');
                    table.ajax.reload();
                } else {
                    alert("Error: " + response.message);
                }
            },
            error: function (xhr, status, error) {
                console.error("AJAX Error:", status, error);
                alert("An error occurred while processing your request.");
            }
        });
    });

    $('#customerTable').on('click', '.btnEdit', function () {
        const customerID = $(this).data('id');
        $.get(`/luxestay/CustomerCtr?action=get&customerID=${customerID}`, function (customer) {
            $("#customerID").val(customer.customerID);
            $("#idCardTypeID").val(customer.idCardTypeID);
            $("#name").val(customer.name);
            $("#phone").val(customer.phone);
            $("#email").val(customer.email);
            $("#idCardNo").val(customer.idCardNo);
            $("#customerModalLabel").text("Edit Customer");
            $("#customerModal").modal('show');
            action = "edit";
            loadIDCardTypes();
        }).fail(function (xhr) {
            console.error("Failed to load customer:", xhr.responseText);
        });
    });

    $('#customerTable').on('click', '.btnDelete', function () {
        if (confirm("Are you sure you want to delete this customer?")) {
            const customerID = $(this).data("id");

            $.ajax({
                url: "/luxestay/CustomerCtr?action=delete",
                method: "POST",
                data: JSON.stringify({ customerID: customerID }),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        alert(response.message);
                        table.ajax.reload();
                    } else {
                        alert("Error: " + response.message);
                    }
                },
                error: function (xhr, status, error) {
                    console.error("AJAX Error:", status, error);
                    alert("Error occurred while deleting customer.");
                }
            });
        }
    });
});
