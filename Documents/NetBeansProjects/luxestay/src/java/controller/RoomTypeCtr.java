package controller;

import com.google.gson.Gson;
import dao.RoomTypeDAO;
import model.RoomType;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "RoomTypeCtr", urlPatterns = {"/RoomTypeCtr"})
public class RoomTypeCtr extends HttpServlet {
    private final RoomTypeDAO roomTypeDAO;
    private final Gson gson;

    public RoomTypeCtr() {
        this.roomTypeDAO = new RoomTypeDAO();
        this.gson = new Gson();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        System.out.println("📩 Action received in Servlet: " + action); // Debugging

        try {
            if (action == null || action.isEmpty() || "all".equals(action)) {
                List<RoomType> roomTypes = roomTypeDAO.getAllRoomTypes();
                out.println(gson.toJson(roomTypes));
                return;
            }

            if ("create".equals(action) || "update".equals(action) || "delete".equals(action)) {
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = request.getReader();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                RoomType roomType = gson.fromJson(sb.toString(), RoomType.class);
                handleSaveOrUpdateDelete(request, out, action, roomType);
            } else if ("get".equals(action)) {
                handleGet(request, out);
            } else {
                sendError(out, "Invalid operation requested");
            }

        } catch (Exception e) {
            sendError(out, "Error processing request: " + e.getMessage());
        }
    }

    private void handleSaveOrUpdateDelete(HttpServletRequest request, PrintWriter out, String action, RoomType roomType) {
        try {
            if (action.equals("update") && roomType.getRoomTypeID() == 0) {
                sendError(out, "Room Type ID is required for update");
                return;
            }

            if (action.equals("delete") && roomType.getRoomTypeID() == 0) {
                sendError(out, "Room Type ID is required for delete");
                return;
            }

            if (!action.equals("delete") && (roomType.getRoomType() == null || roomType.getPrice() == 0 || roomType.getMaxPerson() == 0)) {
                sendError(out, "All fields are required");
                return;
            }

            if ("delete".equals(action)) {
                if (roomTypeDAO.deleteRoomType(roomType.getRoomTypeID())) {
                    sendSuccess(out, "Room type deleted successfully");
                } else {
                    sendError(out, "Failed to delete room type");
                }
            } else {
                roomTypeDAO.addOrUpdate(roomType, "update".equals(action) ? "edit" : "create");
                sendSuccess(out, "update".equals(action) ? "Room type updated successfully" : "Room type added successfully");
            }

        } catch (Exception e) {
            sendError(out, "Error processing " + action + " request: " + e.getMessage());
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
            RoomType roomType = roomTypeDAO.getRoomTypeById(roomTypeID);

            if (roomType == null) {
                sendError(out, "Room type not found");
                return;
            }

            out.println(gson.toJson(roomType));

        } catch (Exception e) {
            sendError(out, "Error retrieving data: " + e.getMessage());
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
