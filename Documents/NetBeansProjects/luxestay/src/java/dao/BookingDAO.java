package dao;

import connection.Koneksi;
import model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private final Connection koneksi;

    public BookingDAO() {
        this.koneksi = Koneksi.getConnection();
    }

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookingList = new ArrayList<>();
        String query = "SELECT b.reservationID, b.customerID, c.name AS name, " +
                       "GROUP_CONCAT(r.roomNo ORDER BY r.roomNo ASC) AS roomNo, " +
                       "GROUP_CONCAT(rt.roomType ORDER BY r.roomNo ASC) AS roomType, " +
                       "b.bookingDate, b.checkIn, b.checkOut, b.totalPrice, b.status " +
                       "FROM booking b " +
                       "JOIN customer c ON b.customerID = c.customerID " +
                       "JOIN room r ON b.roomID = r.roomID " +
                       "JOIN room_type rt ON r.roomTypeID = rt.roomTypeID " +
                       "GROUP BY b.reservationID";

        try (PreparedStatement stmt = koneksi.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Booking booking;
                booking = new Booking(
                        rs.getInt("reservationID"),
                        rs.getInt("customerID"),
                        rs.getString("name"),
                        rs.getString("roomNo"), // Sudah dalam format "101, 102"
                        rs.getString("roomType"), // Sudah dalam format "Deluxe, Suite"
                        rs.getString("bookingDate"),
                        rs.getString("checkIn"),
                        rs.getString("checkOut"),
                        rs.getDouble("totalPrice"),
                        rs.getString("status")
                );
                bookingList.add(booking);
            }
        }
        return bookingList;
    }

    public Booking getById(int reservationID) throws SQLException {
        String query = "SELECT b.reservationID, b.customerID, c.name AS name, " +
                       "GROUP_CONCAT(r.roomNo ORDER BY r.roomNo ASC) AS roomNo, " +
                       "GROUP_CONCAT(rt.roomType ORDER BY r.roomNo ASC) AS roomType, " +
                       "b.bookingDate, b.checkIn, b.checkOut, b.totalPrice, b.status " +
                       "FROM booking b " +
                       "JOIN customer c ON b.customerID = c.customerID " +
                       "JOIN room r ON b.roomID = r.roomID " +
                       "JOIN room_type rt ON r.roomTypeID = rt.roomTypeID " +
                       "WHERE b.reservationID = ? " +
                       "GROUP BY b.reservationID";

        try (PreparedStatement stmt = koneksi.prepareStatement(query)) {
            stmt.setInt(1, reservationID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Booking(
                        rs.getInt("reservationID"),
                        rs.getInt("customerID"),
                        rs.getString("name"),
                        rs.getString("roomNo"),
                        rs.getString("roomType"),
                        rs.getString("bookingDate"),
                        rs.getString("checkIn"),
                        rs.getString("checkOut"),
                        rs.getDouble("totalPrice"),
                        rs.getString("status")
                    );
                } else {
                    return null; // Return null if no booking is found with the given ID
                }
            }
        }
    }

    
    public boolean addOrUpdate(Booking booking, String[] roomIDs, String mode) throws SQLException {
        String query;
        if (mode.equals("edit")) {
            query = "DELETE FROM booking WHERE reservationID = ?"; // Hapus dulu sebelum insert ulang
            try (PreparedStatement stmt = koneksi.prepareStatement(query)) {
                stmt.setInt(1, booking.getReservationID());
                stmt.executeUpdate();
            }
        }

        query = "INSERT INTO booking (customerID, roomID, bookingDate, checkIn, checkOut, totalPrice, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = koneksi.prepareStatement(query)) {
            for (String roomID : roomIDs) {
                stmt.setInt(1, booking.getCustomerID());
                stmt.setInt(2, Integer.parseInt(roomID));
                stmt.setString(3, booking.getBookingDate());
                stmt.setString(4, booking.getCheckIn());
                stmt.setString(5, booking.getCheckOut());
                stmt.setDouble(6, booking.getTotalPrice());
                stmt.setString(7, booking.getStatus());
                stmt.addBatch();
            }
            stmt.executeBatch(); // Eksekusi semua insert sekaligus
            return true;
        }
    }


    public boolean delete(int reservationID) throws SQLException {
        String query = "DELETE FROM booking WHERE reservationID = ?";

        try (PreparedStatement stmt = koneksi.prepareStatement(query)) {
            stmt.setInt(1, reservationID);
            boolean success = stmt.executeUpdate() > 0;

            // Update room status to Available
            if (success) {
                String updateRoomStatus = "UPDATE room SET status = 'Available' WHERE roomID = (SELECT roomID FROM booking WHERE reservationID = ?)";
                try (PreparedStatement updateStmt = koneksi.prepareStatement(updateRoomStatus)) {
                    updateStmt.setInt(1, reservationID);
                    updateStmt.executeUpdate();
                }
            }
            return success;
        }
    }
}
