package com.clipcat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Class to manage database operations.
 */
public class DBManager {

    private static Connection conn = null;
    private final static int TEXT = 0;
    private final static int IMAGE = 1;

    /**
     * Connect to an sqlite database and create the content table if it doesn't exist.
     */
    public static void connect() {
        try {
            // db parameters
            String url = "jdbc:sqlite:C:/sqlite/db/bruh.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // creating the table if its not already there
        String sql = "CREATE TABLE IF NOT EXISTS archive (\n" +
                "    ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    NAME VARCHAR UNIQUE ,\n" +
                "    DATA BLOB NOT NULL,\n" +
                "    TYPE INTEGER NOT NULL" + // 0 for text, 1 for image
                ");";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a ClipboardObject into the database with a certain name/label
     *
     * @param name              The name of the object, Will be used to retrieve it later.
     * @param clipboardObject   The clipboard object to be stored.
     */
    public static void insert(String name, ClipboardObject clipboardObject) {

        if(clipboardObject == null)
            throw new IllegalArgumentException("Clipboard object cannot be null");

        int type = (clipboardObject.objectType == ObjectType.TEXT) ? TEXT : IMAGE;
        String sql = "INSERT INTO archive(NAME, DATA, TYPE) VALUES(?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, name);
            pstmt.setBytes(2, clipboardObject.data);
            pstmt.setInt(3, type);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get a clipboard object from the database given a name.
     * @param name  The name used to store the clipboard object.
     * @return      The clipboard object with that name, if not found return null.
     */
    public static ClipboardObject get(String name)
    {
        String sql = "SELECT DATA, TYPE FROM archive WHERE NAME= ?;";
        ClipboardObject clipboardObject = null;

        try (PreparedStatement pstmt  = conn.prepareStatement(sql)){

            pstmt.setString(1, name);
            ResultSet rs  = pstmt.executeQuery();
            if (rs.isClosed())
                return null;

            // transforming the data into a clipboard object.
             byte[] data = rs.getBytes(1);
             ObjectType type =  (rs.getInt(2) == TEXT) ? ObjectType.TEXT : ObjectType.IMAGE;
             clipboardObject = new ClipboardObject(data, type);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return clipboardObject;
    }

    /**
     * Get the names and types of all the clipboard objects in the database.
     *
     * @return A list of the names and ids and types of all the clipboard objects in the database.
     */
    public static List<String> list()
    {
        String sql = "SELECT ID, NAME, TYPE FROM archive;";
        List<String> list = new ArrayList<>();
        try (PreparedStatement pstmt  = conn.prepareStatement(sql)){
            ResultSet rs  = pstmt.executeQuery();

            if (rs.isClosed())
                return null;

            while(rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String type =  (rs.getInt(3) == TEXT) ? "Text" : "Image";
                list.add(
                        String.format("%4d: %s\t[%s]",
                                id, name, type)
                );

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;

    }

    /**
     * Get the names and types of all the clipboard objects in the database containing the query.
     *
     * @param query A string to look for data containing it.
     *
     * @return A list of the names and ids and types of all the clipboard objects in the database.
     */
    public static List<String> list(String query)
    {
        String sql = "SELECT ID, NAME, TYPE FROM archive WHERE NAME LIKE '%"+ query +"%';";
        List<String> list = new ArrayList<>();
        try (PreparedStatement pstmt  = conn.prepareStatement(sql)){
            ResultSet rs  = pstmt.executeQuery();

            if (rs.isClosed())
                return null;

            while(rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String type =  (rs.getInt(3) == TEXT) ? "Text" : "Image";
                list.add(
                        String.format("%4d: %s\t[%s]",
                                id, name, type)
                );

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;

    }


}