package database;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class DB {


    private static DB instance;
    private static String serverName;
    private static String dbName;
    private static String userName;
    private static String userPassword;
    private static String connectionString;
    private static int serverPort = 0;

    private static final String server = "localhost";
    private static final String database = "kundedb";
    private static final String user = "root";
    private static final String password = "";
    private static final int port = 3306;

    private DB() {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException e) {

            e.printStackTrace();
        } catch (IllegalAccessException e) {

            e.printStackTrace();
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        }

        connectToDB(server, port, database, user, password);

    }

    public static DB getInstance() {

        if (instance == null) {

            instance = new DB();
        }

        return instance;

    }

    public void connectToDB(String server, int port, String database,
                            String user, String password) {

        serverName = server;
        dbName = database;
        serverPort = port;
        userName = user;
        userPassword = password;

        connectionString = "jdbc:mysql://" + serverName + ":"
                + String.valueOf(serverPort) + "/" + dbName;

    }

    public void tableInsert(String sql) {

        if (!isConnected())
            return;

        System.out.println(sql);
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = DriverManager.getConnection(connectionString, userName,
                    userPassword);
            st = con.prepareStatement(sql);
            st.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {

                st.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public DefaultTableModel tableSelect(String sql) {



        if (!isConnected())
            return null;

        System.out.println(sql);

        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        DefaultTableModel tb = null;
        try {
            con = DriverManager.getConnection(connectionString, userName,
                    userPassword);
            st = con.prepareStatement(sql);
            rs = st.executeQuery();

            tb = buildTableModel(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                st.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return tb;

    }

    private static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column).substring(0, 1)
                    .toUpperCase()
                    + metaData.getColumnName(column).substring(1));
        }

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);

    }

    public Object scalarSelect(String sql) {

        DefaultTableModel model = tableSelect(sql);
        if(model.getRowCount()==0)
            return null;

        return model.getValueAt(0, 0);

    }

    private boolean isConnected() {

        return serverName != null && dbName != null && userName != null
                && userPassword != null && connectionString != null
                && serverPort != 0;

    }

}