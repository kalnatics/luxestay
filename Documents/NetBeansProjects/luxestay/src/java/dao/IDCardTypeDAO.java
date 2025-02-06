package dao;

import connection.Koneksi;
import model.IDCardType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IDCardTypeDAO {
    private final Connection connection;

    public IDCardTypeDAO() {
        connection = Koneksi.getConnection();
    }

    public List<IDCardType> getAllIDCardTypes() throws SQLException {
        List<IDCardType> idCardTypes = new ArrayList<>();
        String query = "SELECT * FROM id_card_type"; // Updated table name

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                IDCardType idCardType = new IDCardType();
                idCardType.setIdCardTypeID(rs.getInt("idCardTypeID"));
                idCardType.setIdCardType(rs.getString("idCardType"));
                idCardTypes.add(idCardType);
            }
        }

        return idCardTypes;
    }

    public IDCardType getIDCardTypeById(int idCardTypeID) throws SQLException {
        IDCardType idCardType = null;
        String query = "SELECT * FROM id_card_type WHERE idCardTypeID = ?"; // Updated table name

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idCardTypeID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    idCardType = new IDCardType();
                    idCardType.setIdCardTypeID(rs.getInt("idCardTypeID"));
                    idCardType.setIdCardType(rs.getString("idCardType"));
                }
            }
        }

        return idCardType;
    }

    public void addOrUpdate(IDCardType idCardType, String action) throws SQLException {
        String query;
        if (action.equals("edit")) {
            query = "UPDATE id_card_type SET idCardType = ? WHERE idCardTypeID = ?"; // Updated table name
        } else {
            query = "INSERT INTO id_card_type (idCardType) VALUES (?)"; // Updated table name
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, idCardType.getIdCardType());

            if (action.equals("edit")) {
                stmt.setInt(2, idCardType.getIdCardTypeID());
            }

            stmt.executeUpdate();
        }
    }

    public boolean delete(int idCardTypeID) throws SQLException {
        String query = "DELETE FROM id_card_type WHERE idCardTypeID = ?"; // Updated table name
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idCardTypeID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
