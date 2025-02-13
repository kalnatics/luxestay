package controller;

import com.google.gson.Gson;
import dao.BookingDAO;
import model.Booking;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "BookingCtr", urlPatterns = {"/BookingCtr"})
public class BookingCtr extends HttpServlet {
    private final BookingDAO dao;
    private final Gson gson;

    public BookingCtr() {
        this.dao = new BookingDAO();
        this.gson = new Gson();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        try {
            if (action == null || action.isEmpty() || action.equals("all")) {
                List<Booking> bookings = dao.getAllBookings();
                out.println(gson.toJson(bookings));
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
            String idStr = request.getParameter("reservationID");
            String customerIDStr = request.getParameter("customerID");
            String[] roomID = request.getParameterValues("roomID"); // Gunakan getParameterValues untuk menangani array
            String bookingDate = request.getParameter("bookingDate");
            String checkIn = request.getParameter("checkIn");
            String checkOut = request.getParameter("checkOut");
            String status = request.getParameter("status");
            String totalPriceStr = request.getParameter("totalPrice");

            if (customerIDStr == null || roomID == null || roomID.length == 0 || 
                bookingDate == null || checkIn == null || checkOut == null || 
                totalPriceStr == null || status == null) {
                sendError(out, "All fields are required");
                return;
            }

            int customerID = Integer.parseInt(customerIDStr);
            double totalPrice = Double.parseDouble(totalPriceStr);

            Booking booking = new Booking();
            booking.setCustomerID(customerID);
            booking.setBookingDate(bookingDate);
            booking.setCheckIn(checkIn);
            booking.setCheckOut(checkOut);
            booking.setTotalPrice(totalPrice);
            booking.setStatus(status);

            if ("edit".equals(action)) {
                if (idStr == null || idStr.isEmpty()) {
                    sendError(out, "Reservation ID is required for editing");
                    return;
                }
                int reservationID = Integer.parseInt(idStr);
                booking.setReservationID(reservationID);
            }

            boolean success = dao.addOrUpdate(booking, roomID, action.equals("edit") ? "edit" : "tambah");
            if (success) {
                sendSuccess(out, action.equals("edit") ? "Booking updated successfully" : "Booking added successfully");
            } else {
                sendError(out, "Failed to process the booking");
            }

        } catch (NumberFormatException e) {
            sendError(out, "Invalid number format");
        } catch (SQLException e) {
            sendError(out, "Database error: " + e.getMessage());
        }
    }


    private void handleGet(HttpServletRequest request, PrintWriter out) throws SQLException {
        try {
            String idStr = request.getParameter("reservationID");
            if (idStr == null || idStr.isEmpty()) {
                sendError(out, "Reservation ID is required");
                return;
            }
            int reservationID = Integer.parseInt(idStr);
            Booking booking = dao.getById(reservationID);
            if (booking == null) {
                sendError(out, "Booking not found");
                return;
            }
            out.println(gson.toJson(booking));
        } catch (NumberFormatException e) {
            sendError(out, "Invalid Reservation ID");
        }
    }


    private void handleDelete(HttpServletRequest request, PrintWriter out) {
        try {
            String idStr = request.getParameter("reservationID");
            if (idStr == null || idStr.isEmpty()) {
                sendError(out, "Reservation ID is required");
                return;
            }
            int reservationID = Integer.parseInt(idStr);
            if (dao.delete(reservationID)) {
                sendSuccess(out, "Booking deleted successfully");
            } else {
                sendError(out, "Failed to delete booking");
            }
        } catch (NumberFormatException e) {
            sendError(out, "Invalid Reservation ID");
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
