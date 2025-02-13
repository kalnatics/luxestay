package dao;

import model.RoomType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import connection.Koneksi;

public class RoomTypeDAO {

    private final Connection koneksi;

    public RoomTypeDAO() {
         this.koneksi = Koneksi.getConnection();
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

    public boolean addOrUpdate(RoomType room, String action) throws SQLException {
        String query;
        if ("edit".equals(action)) {
            query = "UPDATE room_type SET roomType = ?, price = ?, maxPerson = ? WHERE roomTypeID = ?";
        } else if ("tambah".equals(action)) {
            query = "INSERT INTO room_type (roomType, price, maxPerson) VALUES (?, ?, ?)";
        } else {
            throw new SQLException("Invalid action: " + action);
        }

        try (PreparedStatement stmt = koneksi.prepareStatement(query)) {
            stmt.setString(1, room.getRoomType());
            stmt.setInt(2, room.getPrice());
            stmt.setInt(3, room.getMaxPerson());

            if ("edit".equals(action)) {
                stmt.setInt(4, room.getRoomTypeID());
            }

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }



    public boolean deleteRoomType(int roomTypeID) throws SQLException {
        // Cek apakah masih digunakan di tabel room
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
