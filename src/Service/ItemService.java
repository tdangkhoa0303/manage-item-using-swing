package Service;

import Entity.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

public class ItemService {
    public Vector<Item> searchItemByName(String keyword) {
        Vector
    }

    public Vector<Item> getAllItem() throws Exception {
        Vector<Item> list = new Vector<>();
        String query = "SELECT * FROM Items";

        try (Connection c = DBConnection.openConnection(); Statement st = c.createStatement()) {
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String code = rs.getString("itemCode");
                String name = rs.getString("itemName");
                String supCode = rs.getString("supCode");
                String unit = rs.getString("unit");
                int price = rs.getInt("price");
                byte supplying = rs.getByte("supplying");
                list.add(new Item(code, name, supCode, unit, price, supplying));
            }
        }

        return list;
    }

    public Item getItemByCode(String code) throws Exception {
        String query = "SELECT * FROM Items WHERE itemCode = ?";
        try (Connection c = DBConnection.openConnection(); PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return new Item(rs.getString("itemCode"), rs.getString("itemName"), rs.getString("supCode"), rs.getString("unit"), rs.getInt("price"), rs.getByte("supplying"));
        }
        return null;
    }

    public int insertItem(Item item) throws Exception {
        String query = "INSERT INTO Items (itemCode, itemName, supCode, unit, price, supplying) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.openConnection(); PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, item.getCode());
            ps.setString(2, item.getName());
            ps.setString(3, item.getSupCode());
            ps.setString(4, item.getUnit());
            ps.setInt(5, item.getPrice());
            ps.setByte(6, item.getSupplying());
            return ps.executeUpdate();
        }
    }

    public int updateItem(Item item) throws Exception {
        String query = "UPDATE Items SET itemName = ?, supCode = ?, unit = ?, price = ?, supplying = ? WHERE itemCode = ?";
        try (Connection c = DBConnection.openConnection(); PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getSupCode());
            ps.setString(3, item.getUnit());
            ps.setInt(4, item.getPrice());
            ps.setByte(5, item.getSupplying());
            ps.setString(6, item.getCode());
            return ps.executeUpdate();
        }
    }

    public int deleteItem(String code) throws Exception {
        String query = "DELETE Items WHERE itemCode = ?";
        try (Connection c = DBConnection.openConnection(); PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, code);
            return ps.executeUpdate();
        }
    }

    public int getItemCount() throws Exception {
        String query = "SELECT COUNT(itemCode) AS itemCount FROM Items";
        try (Connection c = DBConnection.openConnection(); PreparedStatement ps = c.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("itemCount");
        }
        return 0;
    }

}
