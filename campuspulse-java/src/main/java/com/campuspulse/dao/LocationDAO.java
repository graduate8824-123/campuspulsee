package com.campuspulse.dao;

import com.campuspulse.model.Location;
import com.campuspulse.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocationDAO {

    public List<Location> findAll() throws SQLException {
        List<Location> list = new ArrayList<>();
        String sql = "SELECT id, name, zone FROM locations ORDER BY name ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Location loc = new Location();
                loc.setId(rs.getInt("id"));
                loc.setName(rs.getString("name"));
                loc.setZone(rs.getString("zone"));
                list.add(loc);
            }
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return list;
    }

    public Location findById(int id) throws SQLException {
        String sql = "SELECT id, name, zone FROM locations WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Location loc = new Location();
                loc.setId(rs.getInt("id"));
                loc.setName(rs.getString("name"));
                loc.setZone(rs.getString("zone"));
                return loc;
            }
            return null;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }
}
