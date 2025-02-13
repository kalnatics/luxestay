package dao;

import java.sql.*;
import java.util.ArrayList;
import model.User;
import connection.Koneksi;

public class UserDAO {
    private final Connection koneksi;
    
    public UserDAO() {
        koneksi = Koneksi.getConnection();
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> listUser = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY id DESC";
        
        try (PreparedStatement ps = koneksi.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                listUser.add(user);
            }
        } catch(SQLException e) {
            System.out.println("Error in getAllUsers: " + e.getMessage());
        }
        return listUser;
    }

    public void addOrUpdate(User user, String action) throws SQLException {
        String sql = "edit".equals(action) 
            ? "UPDATE user SET username=?, email=?, password=? WHERE id=?"
            : "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            if ("edit".equals(action)) {
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());
                ps.setInt(4, user.getId());
            } else {
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
            }
            ps.executeUpdate();
        }
    }

    public User getById(int id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id=?";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            }
        }
        return null;
    }
    
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM user WHERE email=?";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            }
        }
        return null;
    }


    public boolean hapus(int id) throws SQLException {
        String sql = "DELETE FROM user WHERE id=?";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}