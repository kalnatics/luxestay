$(document).ready(function () {
    // Inisialisasi DataTable
    const table = $('#tabelUser').DataTable({
        processing: true,
        ajax: {
            url: '/luxestay/UserCtr',
            dataSrc: ''
        },
        columns: [
            { data: 'id' },
            { data: 'username' },
            { data: 'email' },
            {
                data: null,
                render: function (data) {
                    return `<button class="btn btn-outline-success btn-sm btnEdit" data-id="${data.id}">Edit</button>
                            <button class="btn btn-outline-danger btn-sm btnDel" data-id="${data.id}">Hapus</button>`;
                }
            }
        ]
    });

    // Tambah User
    $("#btnAdd").click(function () {
        $("#myModal").modal('show');
        $("#addModalLabel").text("Tambah Data Pengguna");
        $("#formTambah")[0].reset();
        $("#myModal").removeData('id');
    });

    // Simpan User
    $('#btnSave').on('click', function () {
        const id = $('#myModal').data('id');
        const username = $('#username').val();
        const email = $('#email').val();
        const password = $('#password').val();

        if (!username || !email || !password) {
            alert('Semua field wajib diisi!');
            return;
        }

        $.ajax({
            url: '/luxestay/UserCtr',
            type: 'POST',
            data: {
                action: id ? 'edit' : 'tambah',
                id: id,
                username: username,
                email: email,
                password: password
            },
            success: function (response) {
                if (response.success) {
                    alert(response.message);
                    $('#myModal').modal('hide');
                    table.ajax.reload();
                } else {
                    alert(response.message);
                }
            },
            error: function () {
                alert('Terjadi kesalahan saat memproses data.');
            }
        });
    });

    // Edit User
    $("#tabelUser").on('click', '.btnEdit', function () {
        const id = $(this).data('id');
        
        $.ajax({
            url: "/luxestay/UserCtr",
            method: "GET",
            data: { action: 'get', id: id },
            dataType: "json",
            success: function (data) {
                if (data) {
                    $("#addModalLabel").text("Edit Data Pengguna");
                    $("#myModal").data('id', data.id);
                    $("#username").val(data.username);
                    $("#email").val(data.email);
                    $("#myModal").modal('show');
                }
            },
            error: function (xhr, status, error) {
                console.error("Error:", status, error);
                alert("Terjadi kesalahan saat mengambil data.");
            }
        });
    });

    // Hapus User
    $("#tabelUser").on('click', '.btnDel', function () {
        const id = $(this).data('id');

        if (confirm("Anda yakin ingin menghapus pengguna ini?")) {
            $.ajax({
                url: "/luxestay/UserCtr",
                type: "POST",
                data: {
                    action: "hapus",
                    id: id
                },
                success: function (response) {
                    if (response.success) {
                        alert(response.message);
                        table.ajax.reload();
                    } else {
                        alert(response.message);
                    }
                },
                error: function () {
                    alert("Terjadi kesalahan saat menghapus data.");
                }
            });
        }
    });
});