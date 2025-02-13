package dao;

import connection.Koneksi;
import model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    private final Connection koneksi;

    public RoomDAO() {
        this.koneksi = Koneksi.getConnection();
    }

    public List<Room> getAllRooms() throws SQLException {
        List<Room> roomList = new ArrayList<>();
        String query = "SELECT r.roomID, r.roomNo, r.roomTypeID, rt.roomType AS roomTypeName, " +
                      "rt.price, rt.maxPerson, COALESCE(r.status, 0) as status " +
                      "FROM room r " +
                      "JOIN room_type rt ON r.roomTypeID = rt.roomTypeID";

        try (PreparedStatement stmt = koneksi.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Room room = new Room(
                    rs.getInt("roomID"),
                    rs.getString("roomNo"),
                    rs.getInt("roomTypeID"),
                    rs.getString("roomTypeName"),
                    rs.getInt("status"),
                    rs.getInt("price"),
                    rs.getInt("maxPerson")
                );
                roomList.add(room);
            }
        }
        return roomList;
    }

    public Room getById(int roomID) throws SQLException {
        String query = "SELECT r.roomID, r.roomNo, r.roomTypeID, rt.roomType AS roomTypeName, rt.price, rt.maxPerson, r.status "
                + "FROM room r "
                + "JOIN room_type rt ON r.roomTypeID = rt.roomTypeID "
                + "WHERE r.roomID = ?";

        try (PreparedStatement stmt = koneksi.prepareStatement(query)) {
            stmt.setInt(1, roomID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Room(
                            rs.getInt("roomID"),
                            rs.getString("roomNo"),
                            rs.getInt("roomTypeID"),
                            rs.getString("roomTypeName"),
                            rs.getString("status"),
                            rs.getInt("price"), // Menyimpan price
                            rs.getInt("maxPerson") // Menyimpan maxPerson
                    );
                }
            }
        }
        return null;
    }

    public boolean addOrUpdate(Room room, String action) throws SQLException {
        String query;
        if ("edit".equals(action)) {
            query = "UPDATE room SET roomTypeID=?, roomNo=?, status=? WHERE roomID=?";
        } else {
            query = "INSERT INTO room (roomTypeID, roomNo, status) VALUES (?, ?, ?)";
        }

        try (PreparedStatement stmt = koneksi.prepareStatement(query)) {
            stmt.setInt(1, room.getRoomTypeID());
            stmt.setString(2, room.getRoomNo());
            
            // Handle NULL status
            if (room.getStatus() == null) {
                stmt.setNull(3, Types.TINYINT);
            } else {
                stmt.setInt(3, room.getStatus());
            }

            if ("edit".equals(action)) {
                stmt.setInt(4, room.getRoomID());
            }

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int roomID) throws SQLException {
        String query = "DELETE FROM room WHERE roomID = ?";
        try (PreparedStatement stmt = koneksi.prepareStatement(query)) {
            stmt.setInt(1, roomID);
            return stmt.executeUpdate() > 0;
        }
    }
}
