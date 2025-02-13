package controller;

import com.google.gson.Gson;
import dao.RoomDAO;
import model.Room;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "RoomCtr", urlPatterns = {"/RoomCtr"})
public class RoomCtr extends HttpServlet {
    private final RoomDAO dao;
    private final Gson gson;

    public RoomCtr() {
        this.dao = new RoomDAO();
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
                // Default action: get all rooms
                List<Room> rooms = dao.getAllRooms();
                out.println(gson.toJson(rooms));
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
                    sendError(out, "Invalid action: " + action);
            }
        } catch (Exception e) {
            sendError(out, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void handleSaveOrUpdate(HttpServletRequest request, PrintWriter out, String action) {
        try {
            String idStr = request.getParameter("roomID");
            String roomTypeIDStr = request.getParameter("roomTypeID");
            String roomNo = request.getParameter("roomNo");
            String status = request.getParameter("status");

            if (roomTypeIDStr == null || roomNo == null || status == null ||
                roomTypeIDStr.isEmpty() || roomNo.isEmpty() || status.isEmpty()) {
                sendError(out, "All fields are required");
                return;
            }

            int roomTypeID = Integer.parseInt(roomTypeIDStr);
            Room room = new Room();
            room.setRoomNo(roomNo);
            room.setRoomTypeID(roomTypeID);
            room.setStatus(status.equals("Terisi") ? "1" : "0");

            if ("edit".equals(action)) {
                if (idStr == null || idStr.isEmpty()) {
                    sendError(out, "Room ID is required for editing");
                    return;
                }
                int roomID = Integer.parseInt(idStr);
                room.setRoomID(roomID);
            }

            boolean success = dao.addOrUpdate(room, action.equals("edit") ? "edit" : "tambah");
            if (success) {
                sendSuccess(out, action.equals("edit") ? "Room updated successfully" : "Room added successfully");
            } else {
                sendError(out, "Failed to process the room");
            }

        } catch (NumberFormatException e) {
            sendError(out, "Invalid number format");
        } catch (SQLException e) {
            sendError(out, "Database error: " + e.getMessage());
        }
    }

    private void handleGet(HttpServletRequest request, PrintWriter out) {
        try {
            String idStr = request.getParameter("roomID");

            if (idStr == null || idStr.isEmpty()) {
                sendError(out, "Room ID is required");
                return;
            }

            int roomID = Integer.parseInt(idStr);
            Room room = dao.getById(roomID);

            if (room == null) {
                sendError(out, "Room not found");
                return;
            }

            out.println(gson.toJson(room));
        } catch (NumberFormatException e) {
            sendError(out, "Invalid Room ID");
        } catch (SQLException e) {
            sendError(out, "Database error: " + e.getMessage());
        }
    }

    private void handleDelete(HttpServletRequest request, PrintWriter out) {
        try {
            String idStr = request.getParameter("roomID");

            if (idStr == null || idStr.isEmpty()) {
                sendError(out, "Room ID is required");
                return;
            }

            int roomID = Integer.parseInt(idStr);
            if (dao.delete(roomID)) {
                sendSuccess(out, "Room deleted successfully");
            } else {
                sendError(out, "Failed to delete room");
            }
        } catch (NumberFormatException e) {
            sendError(out, "Invalid Room ID");
        } catch (SQLException e) {
            sendError(out, "Database error: " + e.getMessage());
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
