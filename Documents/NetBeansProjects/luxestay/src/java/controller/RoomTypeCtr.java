package controller;

import com.google.gson.Gson;
import dao.RoomTypeDAO;
import model.RoomType;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "RoomTypeCtr", urlPatterns = {"/RoomTypeCtr"})
public class RoomTypeCtr extends HttpServlet {
    private final RoomTypeDAO dao;
    private final Gson gson;

    public RoomTypeCtr() {
        this.dao = new RoomTypeDAO();
        this.gson = new Gson();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        try {
            if (action == null || action.isEmpty()) {
                out.println(gson.toJson(dao.getAllRoomTypes()));
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
                default:
                    sendError(out, "Invalid operation requested");
            }

        } catch (Exception e) {
            sendError(out, "Error processing request: " + e.getMessage());
        }
    }

    private void handleSaveOrUpdate(HttpServletRequest request, PrintWriter out, String action) {
        try {
            String idStr = request.getParameter("roomTypeID");
            String roomType = request.getParameter("roomType");
            String priceStr = request.getParameter("price");
            String maxPersonStr = request.getParameter("maxPerson");

            // Cek apakah ada parameter yang kosong
            if (roomType == null || priceStr == null || maxPersonStr == null) {
                sendError(out, "All fields are required.");
                return;
            }

            int price = Integer.parseInt(priceStr);
            int maxPerson = Integer.parseInt(maxPersonStr);

            // Buat objek RoomType
            RoomType room = new RoomType();
            room.setRoomType(roomType);
            room.setPrice(price);
            room.setMaxPerson(maxPerson);

            if ("edit".equals(action)) {
                if (idStr != null) {
                    int roomTypeID = Integer.parseInt(idStr);
                    room.setRoomTypeID(roomTypeID);
                }
            }

            // Panggil DAO untuk simpan/ubah
            boolean success = dao.addOrUpdate(room, action);
            if (success) {
                sendSuccess(out, "Room type " + (action.equals("edit") ? "updated" : "added") + " successfully.");
            } else {
                sendError(out, "Failed to process the room type.");
            }

        } catch (Exception e) {
            sendError(out, "Error processing request: " + e.getMessage());
        }
    }



    private void handleGet(HttpServletRequest request, PrintWriter out) {
        try {
            String idStr = request.getParameter("roomTypeID");

            if (idStr == null) {
                sendError(out, "Room Type ID is required");
                return;
            }

            int roomTypeID = Integer.parseInt(idStr);
            RoomType roomType = dao.getRoomTypeById(roomTypeID);

            if (roomType == null) {
                sendError(out, "Room type not found");
                return;
            }

            out.println(gson.toJson(roomType));
        } catch (Exception e) {
            sendError(out, "Error retrieving data: " + e.getMessage());
        }
    }

    private void handleDelete(HttpServletRequest request, PrintWriter out) {
        try {
            String idStr = request.getParameter("roomTypeID");

            if (idStr == null) {
                sendError(out, "Room Type ID is required");
                return;
            }

            int roomTypeID = Integer.parseInt(idStr);
            boolean success = dao.deleteRoomType(roomTypeID);

            if (success) {
                sendSuccess(out, "Room type deleted successfully");
            } else {
                sendError(out, "Failed to delete room type");
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
