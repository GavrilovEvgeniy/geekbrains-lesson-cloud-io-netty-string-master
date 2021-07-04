package gb.cloud.server.service.impl.workwithdb;

import gb.cloud.server.Main;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkWithDB {

    public static Connection connect;
    public static List<String> userList = new ArrayList<>();

    public static String privateCloudDir = "Cloud\\___private___";

    public static void connectToPG() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        connect = DriverManager
                .getConnection(Main.dbconnection, Main.dblogin, Main.dbpassword);
    }

    private static void createDB(String nameDB) {
        try {
            Statement st = connect.createStatement();
            st.executeUpdate("CREATE DATABASE " + nameDB);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();

            Main.log.error(e.getClass().getName()+": "+e.getMessage());

            System.exit(0);
        }
        Main.log.info("Created database " + nameDB + " successfully");
    }

    private static void createUserTable() throws SQLException {
        Statement st = connect.createStatement();
        String sql = "CREATE TABLE USERS" +
                "(ID INT PRIMARY KEY     NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " PASS           TEXT    NOT NULL)";
        st.executeUpdate(sql);
        st.close();

        Main.log.info("Table USERS created successfully");
    }

    public static String addUser(int id, String userName, String password) throws SQLException {

        try ( Statement st = connect.createStatement(); ResultSet rs = st.executeQuery( "SELECT * FROM USERS;" )) {
            while (rs.next()) {
                String name = rs.getString("NAME");
                String pass = rs.getString("PASS");
                if ((name.equals(userName)) | (pass.equals(password))) {

                    Main.log.error("User " + userName + " ignored, double name or pass");
                    rs.close();
                    st.close();
                    return "Failure";
                }
            }
            rs.close();

            String sql = "INSERT INTO USERS (ID, NAME, PASS) " + "VALUES ( " + id + ", '" + userName + "', '" + password + "');";
            st.executeUpdate(sql);
        }

        return "Success";
    }

    public static int showUsers() throws SQLException {

        userList.clear();

        connect.createStatement();
        Statement st;
        st = connect.createStatement();
        int id = 0;
        ResultSet rs = st.executeQuery( "SELECT * FROM USERS;" );
        while ( rs.next() ) {
            id = rs.getInt("id");
            String name = rs.getString("NAME");
            String pass = rs.getString("PASS");

            userList.add(name + " " + pass);
        }

        Main.log.info(userList);

        rs.close();
        st.close();
        return id;
    }

    private static void makePrivateDir(String userName) {
        String userDirName = privateCloudDir + userName;
        File userDir = new File(userDirName);
        if (!userDir.exists()) userDir.mkdir();
    }

    public static String findUserInDB(String userName, String userPass) throws SQLException, ClassNotFoundException {

        String userLogged = "";

        WorkWithDB.connectToPG();
        WorkWithDB.showUsers();

        for (String user : WorkWithDB.userList) {
            String[] userParams = user.split(" ", 2);
            if ((userParams[0].equals(userName)) & (userParams[1].equals(userPass))) {

                Main.log.info("Autentication done for " + userParams[0]);

                userLogged = "Logged " + userParams[0];
                makePrivateDir(userParams[0]);
                break;
            }
        }
        if (userLogged.equals("")) {
            Main.log.error("Autentication failed for " + userName);

            return "failure";
        }
        return userLogged;
    }

    public static String addToDataBase(String userName, String userPass) throws SQLException, ClassNotFoundException {

        WorkWithDB.connectToPG();
        int currentUserNum = WorkWithDB.showUsers();
        currentUserNum++;

        return WorkWithDB.addUser(currentUserNum, userName, userPass);
    }

}
