package JCode.mysql;

import JCode.FileHelper;
import objects.ESetting;
import objects.NotificationSettings;
import objects.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsQueries {

    private Connection static_con;
    private FileHelper fHelper;

    public SettingsQueries(Connection static_con, FileHelper fHelper) {
        this.static_con = static_con;
        this.fHelper = fHelper;
    }

    public boolean checkIfUserIsLoggedIn(Users user) {

        if (user == null)
            return false;

        String query = "SELECT ISLOG FROM users WHERE UCODE = " + user.getUCODE();

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                int islog = set.getInt("ISLOG");
                if (islog == 1)
                    return true;
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    public void logUserOut(Users user) {
        String query = "UPDATE USERS " +
                " SET ISLOG = 0 " +
                " WHERE UCODE = ?";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, user.getUCODE());
            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ESetting getEmailSettings() {

        String query = "SELECT HOST, EMAIL, PASS, FSPATH, ES_GEN_EMAIL, AUTOCHK, DISCCHK, SOLVCHK, AUTOTXT, DISCTXT, SOLVTXT FROM " +
                "EMAIL_SETTINGS " +
                "WHERE 1";
        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            ESetting eSetting;
            while (set.next()) {
                eSetting = new ESetting(set.getString("HOST"), set.getString("EMAIL"),
                        set.getString("PASS"), set.getString("FSPATH"), set.getBoolean("AUTOCHK"),
                        set.getBoolean("DISCCHK"));
                eSetting.setSolv(set.getBoolean("SOLVCHK"));
                eSetting.setAutotext(set.getString("AUTOTXT"));
                eSetting.setDisctext(set.getString("DISCTXT"));
                eSetting.setSolvRespText(set.getString("SOLVTXT"));
                eSetting.setGenerated_reply_email(set.getString("ES_GEN_EMAIL"));
                return eSetting;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public void saveEmailSettings(ESetting eSetting) {

        String query = "UPDATE EMAIL_SETTINGS SET HOST = ?,EMAIL = ?, PASS = ?, FSPATH = ?," +
                " AUTOCHK = ?, DISCCHK = ?, AUTOTXT = ?, DISCTXT = ?, SOLVTXT = ?, SOLVCHK = ?, ES_GEN_EMAIL = ? WHERE ECODE = 1";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, eSetting.getHost());
            statement.setString(2, eSetting.getEmail());
            statement.setString(3, eSetting.getPass());
            statement.setString(4, eSetting.getFspath());
            statement.setBoolean(5, eSetting.isAuto());
            statement.setBoolean(6, eSetting.isDisc());
            statement.setString(7, eSetting.getAutotext());
            statement.setString(8, eSetting.getDisctext());
            statement.setString(9, eSetting.getSolvRespText());
            statement.setBoolean(10, eSetting.isSolv());
            statement.setString(11, eSetting.getGenerated_reply_email());
            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        fHelper.DeleteESettings();
        fHelper.WriteESettings(getEmailSettings());

    }


    public NotificationSettings getNotificationSettings() {

        String query = "SELECT NS_ID, NS_TASK1, NS_EVENT1 FROM " +
                " NOTIFICATION_SETTINGS " +
                " WHERE 1";
        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            NotificationSettings nSetting;
            while (set.next()) {
                nSetting = new NotificationSettings();
                nSetting.setNotifyTaskDueDate(set.getBoolean("NS_TASK1"));
                nSetting.setNotifyEventFromDate(set.getBoolean("NS_EVENT1"));
                return nSetting;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public void saveNotificationSettings(NotificationSettings nSettings) {

        String query = "UPDATE NOTIFICATION_SETTINGS SET NS_TASK1 =?, NS_EVENT1 =?  " +
                " WHERE NS_ID = 1";

        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setBoolean(1, nSettings.isNotifyTaskDueDate());
            statement.setBoolean(2, nSettings.isNotifyEventFromDate());
            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void insertNotificationSettings(NotificationSettings nSettings) {

        String query = "INSERT INTO NOTIFICATION_SETTINGS (NS_ID, NS_TASK1, NS_EVENT1) " +
                " VALUES (1, ?, ?)   ";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setBoolean(1, nSettings.isNotifyTaskDueDate());
            statement.setBoolean(2, nSettings.isNotifyEventFromDate());
            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void checkOnNotificationSettings() {
        if (getNotificationSettings() == null) {
            insertNotificationSettings(new NotificationSettings(true, true));
        }
    }

    //  this method update replacement keyword
    public void updateReplacementKeyword(String saveKeyword) {
        String query = "UPDATE  email_settings  SET  REPLACEMENT_KEYWORD = ? ";
        PreparedStatement statement = null;
        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, saveKeyword);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  this method return replacement keyword
    public String getReplacementKeyword() {
        String query = "SELECT REPLACEMENT_KEYWORD FROM email_settings";
        String replacedKeyword = "";
        try {
            // Connection con = getConnection();
            PreparedStatement statement = static_con.prepareStatement(query);
//            System.out.println(query);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                replacedKeyword = set.getString("REPLACEMENT_KEYWORD");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return replacedKeyword;
    }

    //  this method delete blackList keywords
    public void removeKeyword(String selectedItem) {
        String query = "DELETE FROM keyword_list Where keyword_Value =? ";
        PreparedStatement statement = null;
        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, selectedItem);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
