package JCode.mysql;

import JCode.FileHelper;
import objects.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserQueries {

    private Connection static_con;
    private FileHelper fHelper;

    public UserQueries(Connection static_con, FileHelper fHelper) {
        this.static_con = static_con;
        this.fHelper = fHelper;
    }

    public boolean authenticateLogin(String username, String password) {

        String query = "SELECT UCODE, FNAME, URIGHT, ISEMAIL FROM USERS " +
                "WHERE UNAME = ? " +
                "AND UPASS = ? " +
                "AND FREZE = ? " +
                "AND ISLOG = false ";

//        // Connection con = getConnection();

        Users user = new Users();

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, "N");
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                user.setUCODE(set.getInt(1));
                user.setFNAME(set.getString(2));
                user.setUright(set.getString(3));
                if (set.getString(4).equals("Y")) {
                    user.setEmailBool(true);
                } else {
                    user.setEmailBool(false);
                }
            }

            if (user.getUCODE() == '\0') {
                return false;
            } else {
                setLogin(user.getUCODE(), true);
            }

            user.setUNAME(username);

            return getRights(user);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setLogin(int Ucode, boolean log) {
        String query = "UPDATE USERS SET ISLOG = ? WHERE UCODE = ?";

        boolean newCon = false; //If con has been initialized (In case of logout)

//        if (static_con == null) {
//            newCon = true;
//            static_con = getConnection();
//        }

        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setBoolean(1, log);
            statement.setInt(2, Ucode);

            statement.executeUpdate();

            statement.close();

//            if (newCon == true)
//                // doRelease(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getRights(Users user) {

        String query1 = "SELECT  RL.RCODE, RL.RNAME FROM RIGHTS_CHART RC, RIGHTS_LIST RL" +
                " WHERE RC.UCODE = ?" +
                " AND RC.RCODE = RL.RCODE" +
                " AND FREZE = 'N'";

        String query2 = "SELECT RCODE, RNAME FROM RIGHTS_LIST" +
                " WHERE FREZE = 'N'";

        ArrayList<Users.uRights> userRights = new ArrayList<>();

        PreparedStatement statement;
        ResultSet set = null;

        try {
            if (user.getUright().equals("Admin")) {
                statement = static_con.prepareStatement(query2);
                set = statement.executeQuery();
            } else {
                statement = static_con.prepareStatement(query1);
                statement.setInt(1, user.getUCODE());
                set = statement.executeQuery();
            }


            while (set.next()) {
                userRights.add(new Users.uRights(set.getString(1), set.getString(2)));
            }

            return fHelper.WriteUserDetails(user, userRights);


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
//            // doRelease(con);
        }

        return false;

    }


    public String getUserName(int ucode) { //For Locking Names

        String query = " SELECT FNAME FROM USERS " +
                " WHERE UCODE = ?";

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, ucode);
            set = statement.executeQuery();

            while (set.next()) {
                return set.getString("FNAME");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";

    }

    public Users getUserDetails(Users user) {
        String query = " SELECT FNAME, EMAIL FROM USERS " +
                " WHERE UCODE = ?";

//        // Connection con = getConnection();
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, user.getUCODE());
            set = statement.executeQuery();

            while (set.next()) {
                user.setFNAME(set.getString("FNAME"));
                user.setEmail(set.getString("EMAIL"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public List<Users> getAllUsers() {

        List<Users> userList = new ArrayList<>();

        String query = "SELECT UCODE, UNAME, FNAME, EMAIL, UPASS, URIGHT, FREZE, ISEMAIL FROM USERS ";
//        String query = "SELECT UCODE, UNAME, FNAME, EMAIL, UPASS, URIGHT, FREZE, ISEMAIL FROM USERS WHERE FREZE = 'N'";
        String query2 = "SELECT RCODE FROM RIGHTS_CHART WHERE UCODE = ?";

//        // Connection con = getConnection();
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = static_con.prepareStatement(query);
            set = statement.executeQuery();

            while (set.next()) {
                Users user = new Users();
                user.setUCODE(set.getInt("UCODE"));
                user.setUNAME(set.getString("UNAME"));
                user.setFNAME(set.getString("FNAME"));
                user.setEmail(set.getString("EMAIL"));
                user.setPassword(set.getString("UPASS"));
                if (set.getString("FREZE").equals("Y")) {
                    user.setFreeze(true);
                } else {
                    user.setFreeze(false);
                }

                if (set.getString("ISEMAIL").equals("Y")) {
                    user.setEmailBool(true);
                } else {
                    user.setEmailBool(false);
                }
                user.setUright(set.getString("URIGHT"));

                if (!user.getUright().equalsIgnoreCase("Admin")) {
                    ArrayList<Users.uRights> rights = new ArrayList<>();
                    PreparedStatement statement1 = static_con.prepareStatement(query2);
                    statement1.setInt(1, user.getUCODE());
                    ResultSet set1 = statement1.executeQuery();
                    while (set1.next()) {
                        Users.uRights r = new Users.uRights(set1.getInt("RCODE"), "");
                        rights.add(r);
                    }
                    user.setuRightsList(rights);
                }

                userList.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
//            // doRelease(con);
        }

        return userList;
    }

    public List<Users.uRights> getAllUserRights() {

        List<Users.uRights> rightsList = new ArrayList<>();
        String query = "SELECT RCODE, RNAME FROM RIGHTS_LIST ";
//        String query = "SELECT RCODE, RNAME FROM RIGHTS_LIST WHERE FREZE = 'N'";

//        // Connection con = getConnection();
        PreparedStatement statement = null;
        ResultSet set = null;

        try {

            statement = static_con.prepareStatement(query);
            set = statement.executeQuery();

            while (set.next()) {
                Users.uRights r = new Users.uRights();
                r.setRCODE(set.getInt("RCODE"));
                r.setRNAME(set.getString("RNAME"));
                rightsList.add(r);
            }

            return rightsList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
//            // doRelease(con);
        }

        return rightsList;
    }

    public void insertUpdateUser(Users user, int choice) {
        String query = "";

        if (choice == 0) {          //New
            query = " INSERT INTO USERS( UCODE ,  FNAME ,  UNAME ,  Email ,  UPASS ,  URIGHT ,  FREZE , " +
                    "  ISEMAIL, ISLOG ) SELECT IFNULL(max(UCODE),0)+1,?,?,?,?,?,?,?,? from USERS";
        } else if (choice == 1) {   //Update
            query = "UPDATE USERS SET  FNAME =?, UNAME =?, Email =?, " +
                    " UPASS =?, URIGHT =?, FREZE =?, ISEMAIL =? WHERE UCODE = ? ";
        }

        String delete = "DELETE FROM RIGHTS_CHART WHERE UCODE = ?";
        String insert = "INSERT INTO RIGHTS_CHART (RCODE, UCODE) VALUES (?,?)";


        try {
//            // Connection con = getConnection();
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setString(1, user.getFNAME());
            statement.setString(2, user.getUNAME());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getUright());
            if (user.isFreeze()) {
                statement.setString(6, "Y");
            } else {
                statement.setString(6, "N");
            }
            if (user.isEmail()) {
                statement.setString(7, "Y");
            } else {
                statement.setString(7, "N");
            }
            if (choice == 1)
                statement.setInt(8, user.getUCODE());
            else
                statement.setBoolean(8, false);

            statement.executeUpdate();
            statement = null;

            if (!user.getUright().equals("Admin")) {
                statement = static_con.prepareStatement(delete);
                statement.setInt(1, user.getUCODE());
                statement.executeUpdate();

                for (Users.uRights u : user.getRights()) {
                    statement = null;
                    statement = static_con.prepareStatement(insert);
                    statement.setInt(1, u.getRCODE());
                    statement.setInt(2, user.getUCODE());
                    statement.executeUpdate();
                }
            }

//            // doRelease(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void deleteUser(Users u) {

        String query = "DELETE FROM USERS WHERE UCODE = ?";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, u.getUCODE());
            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // // doRelease(con);
        }

    }
//
//    public void archiveUser(Users u) {
//
//        String query = "UPDATE USERS SET FREZE = 'Y' WHERE UCODE = ?";
//
//        // Connection con = getConnection();
//        PreparedStatement statement = null;
//
//        try {
//            statement = static_con.prepareStatement(query);
//            statement.setInt(1, u.getUCODE());
//            statement.executeUpdate();
//
//            statement.close();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            // // doRelease(con);
//        }
//
//    }
}
