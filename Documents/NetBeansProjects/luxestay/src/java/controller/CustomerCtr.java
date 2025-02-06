package controller;

import com.google.gson.Gson;
import dao.CustomerDAO;
import dao.IDCardTypeDAO;
import model.Customer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "CustomerCtr", urlPatterns = {"/CustomerCtr"})
public class CustomerCtr extends HttpServlet {
    private final CustomerDAO customerDAO;
    private final IDCardTypeDAO idCardTypeDAO;
    private final Gson gson;

    public CustomerCtr() {
        this.customerDAO = new CustomerDAO();
        this.idCardTypeDAO = new IDCardTypeDAO();
        this.gson = new Gson();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        try {
            if (action == null || action.isEmpty() || "all".equals(action)) {
                List<Customer> customers = customerDAO.getAllCustomers();
                out.println(gson.toJson(customers));
                return;
            }

            if ("create".equals(action) || "update".equals(action) || "delete".equals(action)) {
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = request.getReader();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                Customer customer = gson.fromJson(sb.toString(), Customer.class);
                handleSaveOrUpdateDelete(request, out, action, customer);
            } else if ("get".equals(action)) {
                handleGet(request, out);
            } else {
                sendError(out, "Invalid operation requested");
            }

        } catch (Exception e) {
            sendError(out, "Error processing request: " + e.getMessage());
        }
    }

    private void handleSaveOrUpdateDelete(HttpServletRequest request, PrintWriter out, String action, Customer customer) {
        try {
            if (action.equals("update") && customer.getCustomerID() == 0) {
                sendError(out, "Customer ID is required for update");
                return;
            }

            if (action.equals("delete") && customer.getCustomerID() == 0) {
                sendError(out, "Customer ID is required for delete");
                return;
            }

            if (!action.equals("delete") && (customer.getName() == null || customer.getPhone() == null || 
                    customer.getEmail() == null || customer.getIdCardNo() == null || customer.getIdCardTypeID() == 0)) {
                sendError(out, "All fields are required");
                return;
            }

            if ("delete".equals(action)) {
                if (customerDAO.delete(customer.getCustomerID())) {
                    sendSuccess(out, "Customer deleted successfully");
                } else {
                    sendError(out, "Failed to delete customer");
                }
            } else {
                customerDAO.addOrUpdate(customer, "update".equals(action) ? "edit" : "create");
                sendSuccess(out, "update".equals(action) ? "Customer updated successfully" : "Customer added successfully");
            }

        } catch (Exception e) {
            sendError(out, "Error processing " + action + " request: " + e.getMessage());
        }
    }

    private void handleGet(HttpServletRequest request, PrintWriter out) {
        try {
            String idStr = request.getParameter("customerID");

            if (idStr == null) {
                sendError(out, "Customer ID is required");
                return;
            }

            int customerID = Integer.parseInt(idStr);
            Customer customer = customerDAO.getCustomerById(customerID);

            if (customer == null) {
                sendError(out, "Record not found");
                return;
            }

            out.println(gson.toJson(customer));

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
