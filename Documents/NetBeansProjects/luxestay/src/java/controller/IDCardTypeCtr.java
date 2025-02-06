package controller;

import com.google.gson.Gson;
import dao.IDCardTypeDAO;
import model.IDCardType;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "IDCardTypeCtr", urlPatterns = {"/IDCardTypeCtr"})
public class IDCardTypeCtr extends HttpServlet {
    private final IDCardTypeDAO idCardTypeDAO;
    private final Gson gson;

    public IDCardTypeCtr() {
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
                List<IDCardType> idCardTypes = idCardTypeDAO.getAllIDCardTypes();
                out.println(gson.toJson(idCardTypes));
                return;
            }

            switch (action) {
                case "create":
                case "update":
                    handleSaveOrUpdate(request, out, action);
                    break;

                case "get":
                    handleGet(request, out);
                    break;

                case "delete":
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
            String idStr = request.getParameter("idCardTypeID");
            String idCardType = request.getParameter("idCardType");

            if (idCardType == null) {
                sendError(out, "Type Name is required");
                return;
            }

            IDCardType idc = new IDCardType();
            idc.setIdCardType(idCardType);

            if (action.equals("update") && idStr != null) {
                int idCardTypeID = Integer.parseInt(idStr);
                idc.setIdCardTypeID(idCardTypeID);
            }

            idCardTypeDAO.addOrUpdate(idc, action.equals("update") ? "edit" : "create");
            sendSuccess(out, action.equals("update") ? "ID Card Type updated successfully" : "ID Card Type added successfully");

        } catch (Exception e) {
            sendError(out, "Error processing " + action + " request: " + e.getMessage());
        }
    }

    private void handleGet(HttpServletRequest request, PrintWriter out) {
        try {
            String idStr = request.getParameter("idCardTypeID");

            if (idStr == null) {
                sendError(out, "ID Card Type ID is required");
                return;
            }

            int idCardTypeID = Integer.parseInt(idStr);
            IDCardType idCardType = idCardTypeDAO.getIDCardTypeById(idCardTypeID);

            if (idCardType == null) {
                sendError(out, "Record not found");
                return;
            }

            out.println(gson.toJson(idCardType));

        } catch (Exception e) {
            sendError(out, "Error retrieving data: " + e.getMessage());
        }
    }

    private void handleDelete(HttpServletRequest request, PrintWriter out) {
        try {
            String idStr = request.getParameter("idCardTypeID");

            if (idStr == null) {
                sendError(out, "ID Card Type ID is required");
                return;
            }

            int idCardTypeID = Integer.parseInt(idStr);
            if (idCardTypeDAO.delete(idCardTypeID)) {
                sendSuccess(out, "ID Card Type deleted successfully");
            } else {
                sendError(out, "Failed to delete ID Card Type");
            }

        } catch (Exception e) {
            sendError(out, "Error deleting data: " + e.getMessage());
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
