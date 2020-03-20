package Service;

import Entity.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

public class SupplierService {
    public Vector<Supplier> getAllSupplier() throws Exception {
        Vector<Supplier> list = new Vector<>();
        String query = "SELECT * FROM Suppliers";

        try (Connection c = DBConnection.openConnection(); Statement st = c.createStatement()) {
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String code = rs.getString("SupCode");
                String name = rs.getString("SupName");
                String address = rs.getString("Address");
                boolean colloborating = rs.getBoolean("colloborating");
                list.add(new Supplier(code, name, address, colloborating));
            }
        }

        return list;
    }

    public Vector<String> getAllSupplierCode() throws Exception {
        Vector<String> list = new Vector<>();
        String query = "SELECT SupCode FROM Suppliers";
        try (Connection c = DBConnection.openConnection(); Statement st = c.createStatement()) {
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String code = rs.getString("SupCode");
                list.add(code);
            }
        }

        return list;
    }

    public Supplier getSupplierByCode(String code) throws Exception {
        String query = "SELECT * FROM Suppliers WHERE SupCode = ?";
        try (Connection c = DBConnection.openConnection(); PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return new Supplier(rs.getString("supCode"), rs.getString("SupName"), rs.getString("Address"), rs.getBoolean("colloborating"));

        }
        return null;
    }

    public int insertSupplier(Supplier supplier) throws Exception {
        String query = "INSERT INTO Suppliers (SupCode, SupName, Address, colloborating) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.openConnection(); PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, supplier.getCode());
            ps.setString(2, supplier.getName());
            ps.setString(3, supplier.getAddress());
            ps.setBoolean(4, supplier.isCollaborating());
            return ps.executeUpdate();
        }
    }

    public int updateSupplier(Supplier supplier) throws Exception {
        String query = "UPDATE Suppliers SET SupName = ?, Address = ?, colloborating = ? WHERE SupCode = ?";
        try (Connection c = DBConnection.openConnection(); PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getAddress());
            ps.setBoolean(3, supplier.isCollaborating());
            ps.setString(4, supplier.getCode());
            return ps.executeUpdate();
        }
    }

    public int deleteSupplier(String code) throws Exception {
        String query = "DELETE Suppliers WHERE SupCode = ?";
        try (Connection c = DBConnection.openConnection(); PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, code);
            return ps.executeUpdate();
        }
    }
}
