package com.campuspulse.dao;

import com.campuspulse.model.Category;
import com.campuspulse.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> findAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name, is_critical, description FROM categories ORDER BY name ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setCritical(rs.getBoolean("is_critical"));
                c.setDescription(rs.getString("description"));
                list.add(c);
            }
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return list;
    }

    public Category findById(int id) throws SQLException {
        String sql = "SELECT id, name, is_critical, description FROM categories WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setCritical(rs.getBoolean("is_critical"));
                c.setDescription(rs.getString("description"));
                return c;
            }
            return null;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }
}
