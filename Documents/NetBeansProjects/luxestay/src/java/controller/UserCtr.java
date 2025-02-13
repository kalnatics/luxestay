package controller;

import com.google.gson.Gson;
import dao.UserDAO;
import model.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "UserCtr", urlPatterns = {"/UserCtr"})
public class UserCtr extends HttpServlet {
    private final UserDAO dao;
    private final Gson gson;

    public UserCtr() {
        this.dao = new UserDAO();
        this.gson = new Gson();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        System.out.println("Action received: " + action); // Debugging message

        try {
            if (action == null || action.isEmpty()) {
                List<User> users = dao.getAllUsers();
                out.println(gson.toJson(users));
                return;
            }

            switch (action) {
                case "tambah":
                case "edit":
                    handleSaveOrUpdate(request, out, action);
                    break;

                case "get":
                    handleGet(request, out);
                    break;

                case "hapus":
                    handleDelete(request, out);
                    break;
                
                case "login":
                    handleLogin(request, out);
                    break;

                default:
                    sendError(out, "Invalid operation requested");
            }

        } catch (Exception e) {
            sendError(out, "Error processing request: " + e.getMessage());
        }
    }
    
    private void handleLogin(HttpServletRequest request, PrintWriter out) {
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
                sendError(out, "Email dan password wajib diisi");
                return;
            }

            User user = dao.getUserByEmail(email);
            if (user == null || !user.getPassword().equals(password)) { // Pastikan password cocok
                sendError(out, "Email atau password salah");
                return;
            }

            sendSuccess(out, "Login berhasil");
        } catch (Exception e) {
            sendError(out, "Kesalahan saat login: " + e.getMessage());
        }
    }


    private void handleSaveOrUpdate(HttpServletRequest request, PrintWriter out, String action) {
        try {
            String idStr = request.getParameter("id");
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String email = request.getParameter("email");

            if (username == null || password == null || email == null || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                sendError(out, "Semua field wajib diisi");
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

            if (action.equals("edit")) {
                if (idStr == null || idStr.isEmpty()) {
                    sendError(out, "ID wajib untuk proses edit");
                    return;
                }
                int id = Integer.parseInt(idStr);
                user.setId(id);
            }

            dao.addOrUpdate(user, action.equals("edit") ? "edit" : "tambah");
            sendSuccess(out, action.equals("edit") ? "User berhasil diperbarui" : "User berhasil ditambahkan");
        } catch (NumberFormatException e) {
            sendError(out, "ID tidak valid");
        } catch (SQLException e) {
            sendError(out, "Kesalahan database: " + e.getMessage());
        }
    }


    private void handleGet(HttpServletRequest request, PrintWriter out) {
        try {
            String idStr = request.getParameter("id");

            if (idStr == null) {
                sendError(out, "User ID is required");
                return;
            }

            int id = Integer.parseInt(idStr);
            User user = dao.getById(id);

            if (user == null) {
                sendError(out, "Record not found");
                return;
            }

            out.println(gson.toJson(user));  // Menyaring data pengguna dalam format JSON

        } catch (Exception e) {
            sendError(out, "Error retrieving data: " + e.getMessage());
        }
    }


    private void handleDelete(HttpServletRequest request, PrintWriter out) {
        try {
            String idStr = request.getParameter("id");

            if (idStr == null) {
                sendError(out, "User ID is required");
                return;
            }

            int id = Integer.parseInt(idStr);
            if (dao.hapus(id)) {
                sendSuccess(out, "User hapusd successfully");
            } else {
                sendError(out, "Failed to hapus user");
            }

        } catch (Exception e) {
            sendError(out, "Error deleting data: " + e.getMessage());
        }
    }

    private void sendError(PrintWriter out, String message) {
        out.println(gson.toJson(new ResponseMessage(false, message)));
    }

    private void sendSuccess(PrintWriter out, String message) {
        out.println(gson.toJson(new ResponseMessage(true, message)));
    }

    private static class ResponseMessage {
        private final boolean success;
        private final String message;

        public ResponseMessage(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
