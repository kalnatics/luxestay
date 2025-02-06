package dao;

import connection.Koneksi;
import model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private final Connection connection;

    public CustomerDAO() {
        connection = Koneksi.getConnection();
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT c.*, t.idCardType FROM customer c JOIN id_card_type t ON c.idCardTypeID = t.idCardTypeID";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerID(rs.getInt("customerID"));
                customer.setIdCardTypeID(rs.getInt("idCardTypeID"));
                customer.setIdCardType(rs.getString("idCardType"));
                customer.setName(rs.getString("name"));
                customer.setPhone(rs.getString("phone"));
                customer.setEmail(rs.getString("email"));
                customer.setIdCardNo(rs.getString("idCardNo"));
                customers.add(customer);
            }
        }

        return customers;
    }

    public Customer getCustomerById(int customerID) throws SQLException {
        Customer customer = null;
        String query = "SELECT c.*, t.idCardType FROM customer c JOIN id_card_type t ON c.idCardTypeID = t.idCardTypeID WHERE c.customerID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer();
                    customer.setCustomerID(rs.getInt("customerID"));
                    customer.setIdCardTypeID(rs.getInt("idCardTypeID"));
                    customer.setIdCardType(rs.getString("idCardType"));
                    customer.setName(rs.getString("name"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setEmail(rs.getString("email"));
                    customer.setIdCardNo(rs.getString("idCardNo"));
                }
            }
        }

        return customer;
    }

    public void addOrUpdate(Customer customer, String action) throws SQLException {
        String query;
        if (action.equals("edit")) {
            query = "UPDATE customer SET idCardTypeID = ?, name = ?, phone = ?, email = ?, idCardNo = ? WHERE customerID = ?";
        } else {
            query = "INSERT INTO customer (idCardTypeID, name, phone, email, idCardNo) VALUES (?, ?, ?, ?, ?)";
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customer.getIdCardTypeID());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getEmail());
            stmt.setString(5, customer.getIdCardNo());

            if (action.equals("edit")) {
                stmt.setInt(6, customer.getCustomerID());
            }

            stmt.executeUpdate();
        }
    }

    public boolean delete(int customerID) throws SQLException {
        String query = "DELETE FROM customer WHERE customerID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
