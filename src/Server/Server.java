package Server;

import Server.Request.LoginReply;
import Server.Request.LoginRequest;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Properties;
import java.util.Random;

public class Server {
    public static Connection connection;
    public static Statement statement;
    public static User user;

    /**
     * SQL String to create a table named billboard in the database
     *
     * @author Law
     */
    private static final String DELETE_ALL =
            "DROP table billboard;" +
                    "DROP table user;" +
                    "DROP table Schedule;";

    private static final String CREATE_BILLBOARD_TABLE =
            "CREATE TABLE IF NOT EXISTS Billboard ("
                    + "BillboardName VARCHAR(30) PRIMARY KEY NOT NULL UNIQUE,"
                    + "UserName VARCHAR(30),"
                    + "TextColour VARCHAR(30),"
                    + "BackGroundColour VARCHAR(30),"
                    + "Message VARCHAR(30),"
                    + "Image VARCHAR(30),"
                    + "Information VARCHAR(30)" + ");";

    private static final String CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS User ("
                    + "UserName VARCHAR(30) PRIMARY KEY NOT NULL UNIQUE,"
                    + "UserPassword VARCHAR(64) NOT NULL,"
                    + "CreateBillboardsPermission BOOLEAN NOT NULL,"
                    + "EditAllBillboardPermission BOOLEAN NOT NULL,"
                    + "ScheduleBillboardsPermission BOOLEAN NOT NULL,"
                    + "EditUsersPermission BOOLEAN NOT NULL,"
                    + "SaltValue VARCHAR(64) NOT NULL" + ");";

    private static final String CREATE_SCHEDULE_TABLE =
            "CREATE TABLE IF NOT EXISTS Schedule ("
                    + "BillboardName VARCHAR(30) PRIMARY KEY NOT NULL UNIQUE,"
                    + "ScheduleTime DATETIME NOT NULL,"
                    + "Duration INT NOT NULL,"
                    + "RecurType VARCHAR(10),"
                    + "RecurDuration INT" + ");"; //only required for minutes

    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        /* Initiate database connection */
        initDBConnection();
        /* Setup socket connection */
        Properties props = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream("./network.props");
            props.load(in);
            in.close();

            // specify the socket port
            int port = Integer.parseInt(props.getProperty("port"));
            serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            System.err.println(e);
        }

        for (;;) {
            Socket socket = serverSocket.accept();
            System.out.println("Received connection from " + socket.getInetAddress());

            // Stream
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            Object o = ois.readObject();

            // Handle request
            if (o instanceof LoginRequest){
                LoginRequest loginRequest = (LoginRequest) o;
                boolean loginState = checkPasswordSQL(loginRequest.getUserName(), loginRequest.getPassword());
                System.out.println(loginRequest.getUserName());
                System.out.println(loginRequest.getPassword());

                if (loginState){
                    LoginReply loginReply = new LoginReply(loginState, "1827439182731");
                    System.out.println("Successful login");
                    //setUserSQL(user, loginRequest.getUserName());
                    oos.writeObject(loginReply);
                    oos.flush();
                }
            }

            /** TODO: We receive the action enum and object from client
             * then create the user/bb object using object.username....
              */

            oos.close();
            ois.close();
            socket.close();
        }
    }

    private static void initDBConnection(){
        connection = DBConnection.newConnection();
        try {
            statement = connection.createStatement();
            statement.execute(CREATE_BILLBOARD_TABLE);
            statement.execute(CREATE_USER_TABLE);
            statement.execute(CREATE_SCHEDULE_TABLE);

//################code below is just to create a test user with no name or password for testing

            // Username and Password are added.
            try {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
                String testAdmin = "admin";
                String testPassword = "test1";
                String testSaltString = saltString();
                Boolean AdminExists = false;
                String hashedPassword = hashAString(testPassword + testSaltString);

                while (resultSet.next()) {
                    if (testAdmin.equals(resultSet.getString("userName"))) {
                        AdminExists = true;
                    }
                }
                if (!AdminExists) {
                    PreparedStatement pstatement = connection.prepareStatement("INSERT INTO user  " +
                            "(userName, userPassword,  createBillboardsPermission, editAllBillboardPermission, scheduleBillboardsPermission, editUsersPermission, SaltValue) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)");
                    pstatement.setString(1, testAdmin);
                    pstatement.setString(2, hashedPassword);
                    pstatement.setBoolean(3, true);
                    pstatement.setBoolean(4, true);
                    pstatement.setBoolean(5, true);
                    pstatement.setBoolean(6, true);
                    pstatement.setString(7, testSaltString);
                    pstatement.executeUpdate();
                    pstatement.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
//#########################above code is just to create a test user with no name or password for testing


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static String hashAString(String hashString) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(hashString.getBytes());
        StringBuffer sb = new StringBuffer();

        for (byte b : hash){
            sb.append(String.format("%02x", b & 0xFF));
        }

        return sb.toString();
    }

    public static String saltString(){
        Random rng = new Random();
        byte[] saltBytes = new byte[32];
        rng.nextBytes(saltBytes);
        StringBuffer sb = new StringBuffer();

        for (byte b : saltBytes){
            sb.append(String.format("%02x", b & 0xFF));
        }

        return sb.toString();
    }

    private static void setUserSQL(User user, String userName) throws SQLException {
        PreparedStatement pstatement = connection.prepareStatement("SELECT * FROM  user where userName = ?");
        pstatement.setString(1, userName);
        ResultSet resultSet = pstatement.executeQuery();

        while (resultSet.next()) {
            if (userName.equals(resultSet.getString("userName"))) {
                user.setUserName(resultSet.getString("userName"));
                user.setCreateBillboardsPermission(resultSet.getBoolean("createBillboardsPermission"));
                user.setEditAllBillboardsPermission(resultSet.getBoolean("editAllBillboardPermission"));
                user.setScheduleBillboardsPermission(resultSet.getBoolean("scheduleBillboardsPermission"));
                user.setEditUsersPermission(resultSet.getBoolean("editUsersPermission"));
                break;
            }
        }

        pstatement.close();
    }

    private static boolean checkPasswordSQL(String userName, String userPassword) throws SQLException, NoSuchAlgorithmException {
        boolean correctPassword = false;

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT userName, userPassword, saltValue FROM user");

        while(resultSet.next()){
            String hasedRecievedString = Server.hashAString(userPassword+resultSet.getString(3));

            if (userName.equals(resultSet.getString(1)) && hasedRecievedString.equals(resultSet.getString(2))){
                correctPassword = true;
                break;
            }
        }

        statement.close();
        return correctPassword;
    }
    /**
     * @author Foo
     * @param userName
     * @exception SQLException , happens if any sql query error happens
     * This method deletes the user that has been entered into the textfield
     */
    private void DeleteUserSQL(String userName) throws SQLException {
        if(userName != usernamefield.getText()){
            PreparedStatement deletestatement = Server.connection.prepareStatement("delete from user where userName=?");
            deletestatement.setString(1,userName);
            deletestatement.executeQuery();
            deletestatement.close();
        }
        else{
            JOptionPane.showMessageDialog(null,"why");
        }
    }
    /**
     * @author Foo
     * @param userName
     * @exception SQLException , happens if any sql query error happens
     * This method deletes every billboard that was created by the deleted user
     */
    private void DeleteUserBillboardSQL (String userName) throws SQLException{
        try{
            PreparedStatement deletebillboardstatement = Server.connection.prepareStatement(
                    "delete from billboard where UserName=?"
            );
            deletebillboardstatement.setString(1,userName);
            deletebillboardstatement.executeQuery();
            deletebillboardstatement.close();
        }
        catch(SQLException e){
            System.out.println();
        }
    }
}
