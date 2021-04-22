package JCode.mysql;

import objects.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StartupQueries {

    private Connection static_con;
    private UserQueries userQueries;

    public StartupQueries(Connection static_con, UserQueries userQueries) {
        this.static_con = static_con;
        this.userQueries = userQueries;
    }

    public boolean checkAndCreateUser() {
        if (!checkForUsers()) {
            createFirstUser();
            return false;
        } else
            return true;
    }

    private boolean checkForUsers() {

        String query = "SELECT UCODE " +
                " FROM USERS ";

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();

            if (!set.next()) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
            return false;
        }

    }

    private void createFirstUser() {
        Users user = new Users();
        user.setFNAME("Auto Created User");
        user.setUNAME("first_user");
        user.setPassword("auto123");
        user.setUright("Admin");
        user.setEmailBool(true);
        userQueries.insertUpdateUser(user, 0);
    }

    public boolean checkAndPopulateRights() {
        if (!checkForRights()) {
            populateUserRights();
            return false;
        } else
            return true;
    }

    private boolean checkForRights() {

        String query = " SELECT RCODE " +
                " FROM RIGHTS_LIST ";

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();

            if (!set.next()) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    private void populateUserRights() {
        String[] rights = new String[]{"Email Viewer", "Clients", "Products", "Leads", "Activity", "Reports", "Documents", "General Setting"};

        String query = "INSERT INTO RIGHTS_LIST (RCODE, RNAME) VALUES (?, ?)";

        int c = 1;
        for (String r : rights) {
            try {
                PreparedStatement statement = static_con.prepareStatement(query);
                statement.setInt(1, c);
                statement.setString(2, r);
                statement.execute();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            c++;
        }
    }
}
