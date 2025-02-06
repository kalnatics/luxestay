package dao;

import model.RoomType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import connection.Koneksi;

public class RoomTypeDAO {

    private final Connection koneksi;

    public RoomTypeDAO() {
         koneksi = Koneksi.getConnection();
    }

    public List<RoomType> getAllRoomTypes() throws SQLException {
        List<RoomType> roomTypes = new ArrayList<>();
        String query = "SELECT * FROM room_type";

        try (Statement stmt = koneksi.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                RoomType roomType = new RoomType();
                roomType.setRoomTypeID(rs.getInt("roomTypeID"));
                roomType.setRoomType(rs.getString("roomType"));
                roomType.setPrice(rs.getInt("price"));
                roomType.setMaxPerson(rs.getInt("maxPerson"));
                roomTypes.add(roomType);
            }
        }

        return roomTypes;
    }

    public RoomType getRoomTypeById(int roomTypeID) throws SQLException {
        String query = "SELECT * FROM room_type WHERE roomTypeID = ?";
        try (PreparedStatement stmt = koneksi.prepareStatement(query)) {
            stmt.setInt(1, roomTypeID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    RoomType roomType = new RoomType();
                    roomType.setRoomTypeID(rs.getInt("roomTypeID"));
                    roomType.setRoomType(rs.getString("roomType"));
                    roomType.setPrice(rs.getInt("price"));
                    roomType.setMaxPerson(rs.getInt("maxPerson"));
                    return roomType;
                }
            }
        }
        return null;
    }

    public boolean addOrUpdate(RoomType room, String action) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean result = false;
        try {
            String query = "";
            if ("edit".equals(action)) {
                query = "UPDATE room_type SET roomType = ?, price = ?, maxPerson = ? WHERE roomTypeID = ?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, room.getRoomType());
                stmt.setInt(2, room.getPrice());
                stmt.setInt(3, room.getMaxPerson());
                stmt.setInt(4, room.getRoomTypeID());
            } else if ("tambah".equals(action)) {
                query = "INSERT INTO room_type (roomType, price, maxPerson) VALUES (?, ?, ?)";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, room.getRoomType());
                stmt.setInt(2, room.getPrice());
                stmt.setInt(3, room.getMaxPerson());
            }

            int rowsAffected = stmt.executeUpdate();
            result = rowsAffected > 0; // Jika ada perubahan data, maka sukses
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return result;
    }



    public boolean deleteRoomType(int roomTypeID) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM room WHERE roomTypeID = ?";
        try (PreparedStatement checkStmt = koneksi.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, roomTypeID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Cannot delete room type, it is being used in the room table");
                }
            }
        }
        String deleteQuery = "DELETE FROM room_type WHERE roomTypeID = ?";
        try (PreparedStatement stmt = koneksi.prepareStatement(deleteQuery)) {
            stmt.setInt(1, roomTypeID);
            return stmt.executeUpdate() > 0;
        }
    }

}
