package JCode.mysql;

import JCode.CommonTasks;
import JCode.emailControl;
import JCode.fileHelper;
import client.newClient.newClientController;
import client.newContact.newContactController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import objects.*;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class mySqlConn {

    private static String USER;
    private static String PASSWORD;

    private static String URL;

    private fileHelper fHelper;
    private ESetting eSetting;

    private Users user;

    private static Connection static_con;

    public mySqlConn() {
        fHelper = new fileHelper();
        Network network = fHelper.getNetworkDetails();
        if (network == null)
            return;
        URL = "jdbc:mysql://" + network.getHost() + ":" + network.getPort() + "/" + network.getDbname() + "?allowMultiQueries=true";
        USER = network.getRoot();
        PASSWORD = network.getPass();
        user = fHelper.ReadUserDetails();
        if (static_con == null)
            static_con = getConnection();

        eSetting = getEmailSettings();
    }

    private Connection getConnection() {

        int times = 1;

        while (true) {
            try {
                System.out.println("Trying times: " + times);
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println(URL + "\n" + USER + "\n" + PASSWORD);
                Connection con = DriverManager.getConnection(
                        URL, USER, PASSWORD);
                return con;
            } catch (SQLException | ClassNotFoundException e) {
                times++;
                System.out.println(e);
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (times == 10) {
                    showAlertDialog();
                    break;
                }
            }
        }

        return null;
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
        String query = " SELECT FNAME, SOLV, LOCKD FROM USERS " +
                " WHERE UCODE = ?";

//        // Connection con = getConnection();
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, user.getUCODE());
            set = statement.executeQuery();

            while (set.next()) {
                user.setLocked(set.getInt("LOCKD"));
                user.setSolved(set.getInt("SOLV"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public List<Users> getAllUsers() {

        List<Users> userList = new ArrayList<>();

        String query = "SELECT UCODE, UNAME, FNAME, EMAIL, UPASS, URIGHT, FREZE, ISEMAIL FROM USERS";

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
                if (set.getString("UNAME").equals("Y")) {
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

        String query = "SELECT RCODE, RNAME FROM RIGHTS_LIST WHERE FREZE = 'N'";

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

    public Users getNoOfSolvedEmails(Users user) {
        String query = " SELECT COUNT(EMNO) AS EMNO FROM EMAIL_STORE " +
                " WHERE SOLVBY = ?";

//        // Connection con = getConnection();
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, user.getUCODE());
            set = statement.executeQuery();

            while (set.next()) {
                user.setSolved(set.getInt("EMNO"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
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

    public void createEmailRelations(Email email) {
        try {
            for (Address address : email.getFromAddress()) {
                System.out.println("In from");
                subCreateEmailRelation(address, email);
            }
            if (email.getCcAddress() != null) {
                for (Address address : email.getCcAddress()) {
                    System.out.println("in Cc");
                    subCreateEmailRelation(address, email);
                }
            }
        } catch (SQLException e) {
            e.getLocalizedMessage();
        }
    }

    private String mainQuery = "SELECT DISTINCT EM_ID, EM_NAME, CL_ID, CS_ID, UCODE FROM EMAIL_LIST WHERE EM_NAME LIKE ";
    private String relQuery = "INSERT INTO EMAIL_RELATION (EMNO, EM_ID, EMTYPE, CL_ID, UCODE, CS_ID) VALUES (?, ?, ?, ?, ?, ?)";

    private void subCreateEmailRelation(Address address, Email email) throws SQLException {
        if (address != null) {
            String splitted = "";
            try {
                splitted = address.toString().split("\\<")[1];
                splitted = splitted.split("\\>")[0];    //Split email into only the bare minimum to scan
            } catch (Exception e) {
                System.out.println(e);
                splitted = address.toString();
            }
            System.out.println("Email: " + splitted);
            PreparedStatement statement = static_con.prepareStatement(mainQuery + " '%" + splitted + "%'");
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                int emno = email.getEmailNo(),
                        emid = set.getInt("EM_ID"),
                        cl = set.getInt("CL_ID"),
                        cs = set.getInt("CS_ID"),
                        ucode = set.getInt("UCODE");

                if (cl == 0 && cs == 0 && ucode == 0)   //Unrelated Email
                    continue;

                PreparedStatement stmnt = static_con.prepareStatement(relQuery);
                stmnt.setInt(1, emno);
                stmnt.setInt(2, emid);
                stmnt.setInt(3, 1);
                stmnt.setInt(4, cl);
                stmnt.setInt(5, ucode);
                stmnt.setInt(6, cs);
                stmnt.executeUpdate();

            }
        }
    }

    public List<ContactProperty> getEmailContactRelations(Email email) {
        String query = "SELECT DISTINCT CS.CS_ID, CS_FNAME, CS_LNAME " +
                "FROM CONTACT_STORE as CS, EMAIL_RELATION as ER " +
                "WHERE CS.CS_ID = ER.CS_ID " +
                "AND ER.EMNO = ?";

        PreparedStatement statement = null;
        List<ContactProperty> contacts = new ArrayList<>();
        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, email.getEmailNo());
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                ContactProperty c = new ContactProperty();
                c.setCode(set.getInt("CS_ID"));
                c.setFirstName(set.getString("CS_FNAME"));
                c.setLastName(set.getString("CS_LNAME"));
                contacts.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contacts;
    }

    public List<ClientProperty> getEmailClientRelations(Email email) {
        String query = "SELECT DISTINCT CS.CL_ID, CL_NAME " +
                " FROM CLIENT_STORE AS CS, EMAIL_RELATION as ER " +
                " WHERE CS.CL_ID = ER.CL_ID " +
                " AND ER.CL_ID != 0 " +
                " AND ER.EMNO = ?";

        PreparedStatement statement = null;
        List<ClientProperty> clients = new ArrayList<>();
        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, email.getEmailNo());
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                ClientProperty c = new ClientProperty();
                c.setCode(set.getInt("CL_ID"));
                c.setName(set.getString("CL_NAME"));
                clients.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    public int getNoOfUnsolved() {
        String query = " SELECT COUNT(EMNO) AS EMNO FROM EMAIL_STORE " +
                " WHERE ESOLV != 'S' AND FREZE = 0";

//        // Connection con = getConnection();
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = static_con.prepareStatement(query);
            set = statement.executeQuery();

            while (set.next()) {
                return set.getInt("EMNO");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getNoOfUnlocked() {
        String query = " SELECT COUNT(EMNO) AS EMNO FROM EMAIL_STORE " +
                " WHERE LOCKD = 0 AND ESOLV != 'S' AND FREZE = 0";

//        // Connection con = getConnection();
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = static_con.prepareStatement(query);
            set = statement.executeQuery();

            while (set.next()) {
                return set.getInt("EMNO");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void insertEmail(Email email, Message message) {

        String query = "INSERT INTO email_store(EMNO,SBJCT,TOADD,FRADD,TSTMP,EBODY,ATTCH,CCADD,ESOLV,MSGNO,LOCKD," +
                "FREZE) SELECT IFNULL(max(EMNO),0)+1,?,?,?,?,?,?,?,?,?,?,? from EMAIL_STORE";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        System.out.println(email.getTimestamp());

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, email.getSubject());
            statement.setString(2, email.getToAddressString());
            statement.setString(3, email.getFromAddressString());
            statement.setString(4, email.getTimestamp());
            statement.setString(5, email.getBody());
            statement.setString(6, email.getAttch());
            statement.setString(7, email.getCcAddressString());
            statement.setString(8, String.valueOf(email.getSolvFlag()));
            statement.setInt(9, email.getMsgNo());
            statement.setInt(10, email.getLockd());
            statement.setBoolean(11, email.isFreze());
            statement.executeUpdate();

            statement.close();

            int emno = getEmailNo(email);
            email.setEmailNo(emno);

            createEmailRelations(email);

            if (eSetting.isAuto())
                autoReply(email, message);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void insertEmailManual(Email email) {

        String query = "INSERT INTO email_store(EMNO,SBJCT,TOADD,FRADD,TSTMP,EBODY,ATTCH,CCADD,ESOLV,MSGNO,LOCKD," +
                "FREZE,MANUAL) SELECT IFNULL(max(EMNO),0)+1,?,?,?,?,?,?,?,?,?,?,?,? from EMAIL_STORE";

        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, email.getSubject());
            statement.setString(2, email.getToAddressString());
            statement.setString(3, email.getFromAddressString());
            statement.setString(4, email.getTimestamp());
            statement.setString(5, email.getBody());
            statement.setString(6, email.getAttch());
            statement.setString(7, email.getCcAddressString());
            statement.setString(8, "N");
            statement.setInt(9, email.getMsgNo());
            statement.setInt(10, email.getLockd());
            statement.setBoolean(11, email.isFreze());
            statement.setBoolean(12, true);
            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static String autoReplySubject = "Burhani Customer Support - Ticket Number: ";

    private void autoReply(Email email, Message message) {

        String body = "The Ticket Number Issued to you is: " + email.getEmailNo() + "\n" + eSetting.getAutotext();

        Email e = new Email();
        e.setSubject(autoReplySubject + email.getEmailNo());
        e.setToAddress(new Address[]{email.getFromAddress()[0]});
        e.setBody(body + "\n\n\n" + "--------In Reply To--------" + "\n\nSubject:   " + email.getSubject() + "\n\n" + email.getBody());

        emailControl.sendEmail(e, message);

    }

    private int getEmailNo(Email email) {
        String queryEMNO = "SELECT emno FROM email_store" +
                " WHERE msgno = ?" +
                " AND sbjct = ? " +
                " AND tstmp = ?";

        try {

            PreparedStatement statementEMNO = static_con.prepareStatement(queryEMNO);
            statementEMNO.setInt(1, email.getMsgNo());
            statementEMNO.setString(2, email.getSubject());
            statementEMNO.setString(3, email.getTimestamp());
            ResultSet set = statementEMNO.executeQuery();

            int emno = 0;
            //" Mr. " + RecieveName + "\n" +
            while (set.next()) {
                emno = set.getInt(1);
            }

            statementEMNO.close();
            set.close();

            return emno;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // doRelease(con);
        }

        return 0;
    }

    //Reading tickets
    public List<Email> readAllEmails(Filters filters) {

        String query = "SELECT EMNO, MSGNO, SBJCT, FRADD, TOADD, CCADD, TSTMP, " +
                " EBODY, ATTCH, ESOLV, LOCKD, SOLVBY, SOLVTIME FROM EMAIL_STORE";

        if (filters == null) {
            query = query + " ORDER BY EMNO DESC";
        } else {
            query = query + " WHERE " + filters.toString();
        }

        List<Email> allEmails = new ArrayList<>();

        try {
            // Connection con = getConnection();
            PreparedStatement statement = static_con.prepareStatement(query);
            System.out.println(query);
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            if (!set.isBeforeFirst()) {
                return null;
            }

            while (set.next()) {
                Email email = new Email();
                email.setEmailNo(set.getInt("EMNO"));
                email.setMsgNo(set.getInt("MSGNO"));
                email.setSubject(set.getString("SBJCT"));
                email.setTimestamp(set.getString("TSTMP"));
                email.setTimeFormatted(CommonTasks.getTimeFormatted(email.getTimestamp()));

                email.setBody(set.getString("EBODY"));
                email.setAttch(set.getString("ATTCH"));
                email.setSolvFlag(set.getString("ESOLV").charAt(0));
                email.setLockd(set.getInt("LOCKD"));
                if (set.getInt("LOCKD") == '\0') {
                    email.setLockedByName("Unlocked");
                } else {
                    email.setLockedByName(getUserName(email.getLockd())); //Getting name of username that locked
                }                                                              // particular email

                //------From Address
                String[] from = set.getString("FRADD").split("\\^");
                Address[] fromAddress = new Address[from.length];
                for (int i = 1, j = 0; i < from.length; i++, j++) {
                    try {
                        fromAddress[j] = new InternetAddress(from[i]);
                    } catch (AddressException e) {
                        e.printStackTrace();
                    }
                }
                email.setFromAddress(fromAddress);

                //-----To Address
                String[] to = set.getString("TOADD").split("\\^");
                Address[] toAddress = new Address[to.length];
                for (int i = 1, j = 0; i < to.length; i++, j++) {
                    try {
                        toAddress[j] = new InternetAddress(to[i]);
                    } catch (AddressException e) {
                        e.printStackTrace();
                    }
                }
                email.setToAddress(toAddress);

                //----- CC Address
                String[] cc = set.getString("CCADD").split("\\^");
                Address[] ccAddress = new Address[cc.length];
                for (int i = 1, j = 0; i < cc.length; i++, j++) {
                    try {
                        ccAddress[j] = new InternetAddress(cc[i]);
                    } catch (AddressException e) {
                        e.printStackTrace();
                    }
                }
                email.setCcAddress(ccAddress);

                email.setRelatedContacts(getEmailContactRelations(email));
                email.setRelatedClients(getEmailClientRelations(email));

                allEmails.add(email);
            }

            // doRelease(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return allEmails;
    }


    public void insertEmailGeneral(Email email) {

        String query = "INSERT INTO email_general(EMNO,SBJCT,TOADD,FRADD,TSTMP,EBODY,ATTCH,CCADD,MSGNO," +
                "FREZE) SELECT IFNULL(max(EMNO),0)+1,?,?,?,?,?,?,?,?,? from EMAIL_GENERAL";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        System.out.println(email);

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, email.getSubject());
            statement.setString(2, email.getToAddressString());
            statement.setString(3, email.getFromAddressString());
            statement.setString(4, email.getTimestamp());
            statement.setString(5, email.getBody());
            statement.setString(6, email.getAttch());
            statement.setString(7, email.getCcAddressString());
            statement.setInt(8, email.getMsgNo());
            statement.setBoolean(9, email.isFreze());
            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<Email> readAllEmailsGeneral(String where) {

        String query = "SELECT EMNO,MSGNO,SBJCT,FRADD,TOADD,CCADD,TSTMP,EBODY,ATTCH FROM EMAIL_GENERAL";

        if (where == null) {
            query = query + " ORDER BY EMNO DESC";
        } else {
            query = query + where + " ORDER BY EMNO DESC";
        }

        List<Email> allEmails = new ArrayList<>();

        try {
            // Connection con = getConnection();
            PreparedStatement statement = static_con.prepareStatement(query);
            System.out.println(query);
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            if (!set.isBeforeFirst()) {
                return null;
            }

            while (set.next()) {
                Email email = new Email();
                email.setEmailNo(set.getInt("EMNO"));
                email.setMsgNo(set.getInt("MSGNO"));
                email.setSubject(set.getString("SBJCT"));
                email.setTimestamp(set.getString("TSTMP"));

                // Note, MM is months, not mm
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");

                Date date = null;
                try {
                    date = inputFormat.parse(email.getTimestamp());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String outputText = outputFormat.format(date);
                email.setTimeFormatted(outputText);

                email.setBody(set.getString("EBODY"));
                email.setAttch(set.getString("ATTCH"));
                // particular email

                //------From Address
                String[] from = set.getString("FRADD").split("\\^");
                Address[] fromAddress = new Address[from.length];
                for (int i = 1, j = 0; i < from.length; i++, j++) {
                    try {
                        if (from[j] != null)
                            fromAddress[j] = new InternetAddress(from[i]);
                    } catch (AddressException e) {
                        System.out.println("Missing Character");
                        fromAddress[j] = new InternetAddress();
                        continue;
                    }
                }
                email.setFromAddress(fromAddress);

                //-----To Address
                String[] to = set.getString("TOADD").split("\\^");
                Address[] toAddress = new Address[to.length];
                for (int i = 1, j = 0; i < to.length; i++, j++) {
                    try {
                        toAddress[j] = new InternetAddress(to[i]);
                    } catch (AddressException e) {
                        e.printStackTrace();
                    }
                }
                email.setToAddress(toAddress);

                //----- CC Address
                String[] cc = set.getString("CCADD").split("\\^");
                Address[] ccAddress = new Address[cc.length];
                for (int i = 1, j = 0; i < cc.length; i++, j++) {
                    try {
                        ccAddress[j] = new InternetAddress(cc[i]);
                    } catch (AddressException e) {
                        e.printStackTrace();
                    }
                }
                email.setCcAddress(ccAddress);

                allEmails.add(email);
            }

            // doRelease(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return allEmails;
    }

    public void insertEmailSent(Email email) {
        String query = "INSERT INTO EMAIL_SENT(EMNO,SBJCT,FRADD,TOADD,CCADD,BCCADD,TSTMP,EBODY,ATTCH,UCODE,FREZE" +
                ") SELECT IFNULL(max(EMNO),0)+1,?,?,?,?,?,?,?,?,?,? from EMAIL_SENT";

        PreparedStatement statement = null;

        System.out.println(email.getTimestamp());

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, email.getSubject());
            statement.setString(2, email.getFromAddressString());
            statement.setString(3, email.getToAddressString());
            statement.setString(4, email.getCcAddressString());
            statement.setString(5, email.getBccAddressString());
            statement.setString(6, email.getTimestamp());
            statement.setString(7, email.getBody());
            statement.setString(8, email.getAttch());
            statement.setInt(9, user.getUCODE());
            statement.setBoolean(10, false);
            statement.executeUpdate();

            statement.close();

            String[] allEmails = (email.getToAddressString() + "^"
                    + email.getCcAddressString() + "^"
                    + email.getBccAddressString()).split("\\^");

            EmailsListInsertion(allEmails);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Email> readAllEmailsSent(String where) {

        String query = "SELECT EMNO,SBJCT,FRADD,TOADD,CCADD,BCCADD,TSTMP,EBODY,ATTCH,U.FNAME FROM EMAIL_SENT E, users" +
                " U WHERE E.UCODE = U.UCODE";

        if (where == null) {
            query = query + " ORDER BY EMNO DESC";
        } else {
            query = query + where;
        }

        List<Email> allEmails = new ArrayList<>();

        try {
            // Connection con = getConnection();
            PreparedStatement statement = static_con.prepareStatement(query);
            System.out.println(query);
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            if (!set.isBeforeFirst()) {
                return null;
            }

            while (set.next()) {
                Email email = new Email();
                email.setEmailNo(set.getInt("EMNO"));
                email.setSubject(set.getString("SBJCT"));
                email.setTimestamp(set.getString("TSTMP"));

                // Note, MM is months, not mm
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");

                Date date = null;
                try {
                    date = inputFormat.parse(email.getTimestamp());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String outputText = outputFormat.format(date);
                email.setTimeFormatted(outputText);

                email.setBody(set.getString("EBODY"));
                email.setAttch(set.getString("ATTCH"));

                //------From Address
                String[] from = set.getString("FRADD").split("\\^");
                Address[] fromAddress = new Address[from.length];
                for (int i = 1, j = 0; i < from.length; i++, j++) {
                    try {
                        fromAddress[j] = new InternetAddress(from[i]);
                    } catch (AddressException e) {
                        e.printStackTrace();
                    }
                }
                email.setFromAddress(fromAddress);

                //-----To Address
                String[] to = set.getString("TOADD").split("\\^");
                Address[] toAddress = new Address[to.length];
                for (int i = 1, j = 0; i < to.length; i++, j++) {
                    try {
                        toAddress[j] = new InternetAddress(to[i]);
                    } catch (AddressException e) {
                        e.printStackTrace();
                    }
                }
                email.setToAddress(toAddress);

                //----- CC Address
                String[] cc = set.getString("CCADD").split("\\^");
                Address[] ccAddress = new Address[cc.length];
                for (int i = 1, j = 0; i < cc.length; i++, j++) {
                    try {
                        ccAddress[j] = new InternetAddress(cc[i]);
                    } catch (AddressException e) {
                        e.printStackTrace();
                    }
                }
                email.setCcAddress(ccAddress);

                //----- CC Address
                String[] bcc = set.getString("BCCADD").split("\\^");
                Address[] bccAddress = new Address[bcc.length];
                for (int i = 1, j = 0; i < bcc.length; i++, j++) {
                    try {
                        bccAddress[j] = new InternetAddress(bcc[i]);
                    } catch (AddressException e) {
                        e.printStackTrace();
                    }
                }
                email.setBccAddress(bccAddress);
                email.setUser(set.getString("FNAME"));

                allEmails.add(email);
            }

            // doRelease(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return allEmails;
    }


    public void lockEmail(Email email, int op) {        //0 Unlock 1 Lock

        String query = " UPDATE EMAIL_STORE " +
                " SET LOCKD = ? " +
                " WHERE EMNO = ? ";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);

            if (op == 1) {  //Locking
                statement.setInt(1, user.getUCODE());
                statement.setInt(2, email.getEmailNo());
            } else if (op == 0) {   //Unlocking
                statement.setInt(1, 0);
                statement.setInt(2, email.getEmailNo());
            }
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // doRelease(con);
        }

    }

    public void solvEmail(Email email, String flag, Users user, boolean choice, String msg) {

        String query = " UPDATE EMAIL_STORE " +     //Query to Update Email status to solve
                " SET ESOLV = ?, " +
                " SOLVBY = ?, " +
                " SOLVTIME = ? " +
                " WHERE EMNO = ? ";

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(Calendar.getInstance().getTime());
        email.setTimestamp(timeStamp);

        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, flag);
            statement.setInt(2, user.getUCODE());
            statement.setString(3, email.getTimestamp());
            statement.setInt(4, email.getEmailNo());
            statement.executeUpdate();
            statement.close();

            if (eSetting.isSolv() && choice) {
                solvResponder(email, msg);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // doRelease(con);
        }

    }

    private void solvResponder(Email email, String msg) {

        String sb = "Ticket Number: " + email.getEmailNo() + " Resolved";

        String bd = msg;

        Email send = new Email();
        send.setSubject(sb);
        send.setToAddress(email.getFromAddress());
        send.setCcAddress(email.getCcAddress());
        send.setBody(bd);

        emailControl.sendEmail(send, null);

    }

    public void ArchiveEmail(int type, String where) {    //Verb

        String query = "";

        switch (type) {
            case 1:
                query = "UPDATE EMAIL_STORE SET FREZE = 1 WHERE ";
                break;
            case 2:
                query = "UPDATE EMAIL_GENERAL SET FREZE = 1 WHERE ";
        }

        query = query + where;

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ESetting getEmailSettings() {

        String query = "SELECT HOST, EMAIL, PASS, FSPATH, AUTOCHK, DISCCHK, SOLVCHK, AUTOTXT, DISCTXT, SOLVTXT FROM " +
                "EMAIL_SETTINGS " +
                "WHERE 1";
//        // Connection con = getConnection();
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
                " AUTOCHK = ?, DISCCHK = ?, AUTOTXT = ?, DISCTXT = ?, SOLVTXT = ?, SOLVCHK = ? WHERE ECODE = 1";

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
            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        fHelper.DeleteESettings();
        fHelper.WriteESettings(getEmailSettings());

    }

    public void insertClient(ClientProperty client) {

        String query = "INSERT INTO CLIENT_STORE(CL_ID,CL_NAME,CL_OWNER,CL_ADDR,CL_CITY" +
                ",CL_COUNTRY,CL_WEBSITE,CL_TYPE,CL_JOINDATE,CREATEDBY,CREATEDON) " +
                " SELECT IFNULL(max(CL_ID),0)+1,?,?,?,?,?,?,?,?,?,? from CLIENT_STORE";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, client.getName());
            statement.setString(2, client.getOwner());
            statement.setString(3, client.getAddr());
            statement.setString(4, client.getCity());
            statement.setString(5, client.getCountry());
            statement.setString(6, client.getWebsite());
            statement.setInt(7, client.getType());
            if (!client.getJoinDate().equals("null"))
                statement.setString(8, client.getJoinDate());
            else
                statement.setString(8, null);
            statement.setInt(9, fHelper.ReadUserDetails().getUCODE());
            statement.setString(10, CommonTasks.getCurrentTimeStamp());

            statement.executeUpdate();

            EmailsPhoneInsertion(statement, client);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateClient(ClientProperty client) {

        String query = "UPDATE  client_store  SET  CL_NAME = ?, CL_OWNER = ?," +
                " CL_ADDR = ?, CL_CITY = ?, CL_COUNTRY = ?," +
                " CL_WEBSITE = ?, CL_TYPE = ?, CL_JOINDATE = ? WHERE CL_ID = ?";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        System.out.println("Client Owner: " + client.getOwner());

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, client.getName());
            statement.setString(2, client.getOwner());
            statement.setString(3, client.getAddr());
            statement.setString(4, client.getCity());
            statement.setString(5, client.getCountry());
            statement.setString(6, client.getWebsite());
            statement.setInt(7, client.getType());
            if (!client.getJoinDate().equals("null"))
                statement.setString(8, client.getJoinDate());
            else
                statement.setString(8, null);
            statement.setInt(9, client.getCode());

            statement.executeUpdate();

            EmailsPhoneInsertion(statement, client);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // doRelease(con);
        }


    }

    private void EmailsPhoneInsertion(PreparedStatement statement, ClientProperty client) {

        String deleteEmails = "DELETE FROM EMAIL_LIST WHERE CL_ID = ?";

        String deletePhones = "DELETE FROM PHONE_LIST WHERE CL_ID = ?";

        String emailList = "INSERT INTO EMAIL_LIST(EM_ID,EM_NAME,CL_ID,UCODE,CS_ID) " +
                "SELECT IFNULL(max(EM_ID),0)+1,?,?,?,? from EMAIL_LIST";

        String phoneList = "INSERT INTO PHONE_LIST(PH_ID,PH_NUM,CL_ID,UCODE,CS_ID) " +
                "SELECT IFNULL(max(PH_ID),0)+1,?,?,?,? from PHONE_LIST";

        try {
            statement = null;
            statement = static_con.prepareStatement(deleteEmails);
            statement.setInt(1, client.getCode());
            statement.executeUpdate();
            //Adding Emails
            String[] emails = client.getEmails();

            for (int i = 0; i < emails.length; i++) {   //Inserting Emailss
                statement = null;
                if (emails[i] == null)
                    continue;

                statement = static_con.prepareStatement(emailList);
                statement.setString(1, emails[i]);
                statement.setInt(2, client.getCode());
                statement.setInt(3, 0);
                statement.setInt(4, 0);
                statement.executeUpdate();
            }

            statement = null;
            statement = static_con.prepareStatement(deletePhones);
            statement.setInt(1, client.getCode());
            statement.executeUpdate();

            //Adding Phones
            String[] phones = client.getPhones();

            for (int i = 0; i < phones.length; i++) {   //Inserting Emailss
                statement = null;
                if (phones[i] == null)
                    continue;

                statement = static_con.prepareStatement(phoneList);
                statement.setString(1, phones[i]);
                statement.setInt(2, client.getCode());
                statement.setInt(3, 0);
                statement.setInt(4, 0);
                statement.executeUpdate();
            }

            for (int i = 0; i < emails.length; i++) {
                try {
                    if (emails[i] == null)
                        continue;
                    String[] t = emails[i].split("\\@");
                    System.out.println(t);
                    insertDomainsWhitelist(t[1]);
                } catch (Exception e) {
                    System.out.println(e);
                    continue;
                }
            }

            if (statement != null)
                statement.close();
            // doRelease(con);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void EmailsPhoneInsertion(PreparedStatement statement, ContactProperty contact) {

        String deleteEmails = "DELETE FROM EMAIL_LIST WHERE CS_ID = ?";

        String deletePhones = "DELETE FROM PHONE_LIST WHERE CS_ID = ?";

        String emailList = "INSERT INTO EMAIL_LIST(EM_ID,EM_NAME,CS_ID,UCODE,CL_ID) " +
                "SELECT IFNULL(max(EM_ID),0)+1,?,?,?,? from EMAIL_LIST";

        String phoneList = "INSERT INTO PHONE_LIST(PH_ID,PH_NUM,CS_ID,UCODE,CL_ID) " +
                "SELECT IFNULL(max(PH_ID),0)+1,?,?,?,? from PHONE_LIST";

        try {
            statement = null;
            statement = static_con.prepareStatement(deleteEmails);
            statement.setInt(1, contact.getCode());
            statement.executeUpdate();
            //Adding Emails
            String[] emails = new String[]{contact.getEmail()};

            for (int i = 0; i < emails.length; i++) {   //Inserting Emailss
                statement = null;
                if (emails[i] == null)
                    continue;

                statement = static_con.prepareStatement(emailList);
                statement.setString(1, emails[i]);
                statement.setInt(2, contact.getCode());
                statement.setInt(3, 0);
                statement.setInt(4, 0);
                statement.executeUpdate();
            }

            statement = null;
            statement = static_con.prepareStatement(deletePhones);
            statement.setInt(1, contact.getCode());
            statement.executeUpdate();

            //Adding Phones
            String[] phones = new String[]{contact.getMobile()};

            for (int i = 0; i < phones.length; i++) {   //Inserting Emailss
                statement = null;
                if (phones[i] == null)
                    continue;

                statement = static_con.prepareStatement(phoneList);
                statement.setString(1, phones[i]);
                statement.setInt(2, contact.getCode());
                statement.setInt(3, 0);
                statement.setInt(4, 0);
                statement.executeUpdate();
            }


            if (statement != null)
                statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void EmailsListInsertion(String[] emails) {

        String emailList = "INSERT INTO EMAIL_LIST(EM_ID,EM_NAME,CL_ID,UCODE,CS_ID) " +
                "SELECT IFNULL(max(EM_ID),0)+1,?,?,?,? from EMAIL_LIST";

        try {
            PreparedStatement statement = null;

            for (int i = 0; i < emails.length; i++) {   //Inserting Emailss
                statement = null;
                if (emails[i] == null || emails[i].equals(""))
                    continue;

                statement = static_con.prepareStatement(emailList);
                statement.setString(1, emails[i]);
                statement.setInt(2, 0);
                statement.setInt(3, 0);
                statement.setInt(4, 0);
                statement.executeUpdate();
            }

            if (statement != null)
                statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void insertDomainsWhitelist(String domain) {
        String query = "INSERT INTO DOMAIN_LIST(DCODE,DNAME,DWB) " +
                " SELECT IFNULL(max(DCODE),0)+1,?,? from DOMAIN_LIST";

        PreparedStatement statement = null;

//        if (con == null)
//            con = getConnection();

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, domain);
            statement.setInt(2, 1); //WhiteList
            statement.executeUpdate();
            if (statement != null)
                statement.close();
//            // doRelease(con);
        } catch (SQLException e) {
            System.out.println(e);
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void insertDomainsWhitelist(String[] list) {
        String query = "INSERT INTO DOMAIN_LIST(DCODE,DNAME,DWB) " +
                " SELECT IFNULL(max(DCODE),0)+1,?,? from DOMAIN_LIST";

        PreparedStatement statement = null;
        // Connection con = getConnection();
        try {

            for (int i = 0; i < list.length; i++) {
                if (list[i] == null || list[i].equals(""))
                    continue;
                statement = null;
                statement = static_con.prepareStatement(query);
                statement.setString(1, list[i]);
                statement.setInt(2, 1); //WhiteList
                statement.executeUpdate();
            }
            if (statement != null)
                statement.close();
        } catch (SQLException e) {
            System.out.println(e);
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void updateDomainType(int type, String domain) {    //1 for White List 2 for Black List
        String query = "UPDATE  DOMAIN_LIST  SET  DWB = ? WHERE DNAME = ?";
        PreparedStatement statement = null;
        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, type);
            statement.setString(2, domain);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // doRelease(con);
        }
    }

    public List<String> getWhiteBlackListDomains(int type) {
        String query = "SELECT DNAME FROM domain_list WHERE DWB = ?";

        try {
            // Connection con = getConnection();
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, type);
            ResultSet set = statement.executeQuery();

            List<String> l = new ArrayList<>();

            while (set.next()) {
                l.add(set.getString("DNAME"));
            }

            return l;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getNoClients() {
        String query = "SELECT COUNT(CL_ID) FROM CLIENT_STORE WHERE CL_TYPE = 1";

        try {
            // Connection con = getConnection();
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();

            while (set.next())
                return set.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<ClientProperty> getAllClients(String where) {
        String query = "SELECT CL_ID,CL_NAME,CL_OWNER,CL_ADDR," +
                "CL_CITY,CL_COUNTRY,CL_WEBSITE,CL_TYPE,CL_JOINDATE FROM CLIENT_STORE";


        if (where == null) {
            query = query + " WHERE CL_ID != 0 ORDER BY CL_ID";
        } else {
            query = query + " WHERE " + where;
        }

        String emails = "SELECT EM_NAME FROM EMAIL_LIST WHERE CL_ID = ?";
        String phones = "SELECT PH_NUM FROM PHONE_LIST WHERE CL_ID = ?";

        List<ClientProperty> allClients = new ArrayList<>();

        try {
            // Connection con = getConnection();
            System.out.println(query);
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            if (!set.isBeforeFirst()) {
                return null;
            }

            while (set.next()) {
                ClientProperty client = new ClientProperty();
                client.setCode(set.getInt("CL_ID"));
                client.setName(set.getString("CL_NAME"));
                client.setOwner(set.getString("CL_OWNER"));
                client.setAddr(set.getString("CL_ADDR"));
                client.setCity(set.getString("CL_CITY"));
                client.setCountry(set.getString("CL_COUNTRY"));
                client.setWebsite(set.getString("CL_WEBSITE"));
                client.setType(set.getInt("CL_TYPE"));
                client.setJoinDate(set.getString("CL_JOINDATE"));

                //Get all Emails
                PreparedStatement st = static_con.prepareStatement(emails);
                st.setInt(1, client.getCode());
                ResultSet setArray = st.executeQuery();

                String[] dataArr = new String[newClientController.noOfFields];
                int c = 0;
                while (setArray.next()) {
                    try {
                        dataArr[c] = setArray.getString("EM_NAME");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("ArrayIndexOutOfBoundsException");
                    }
                    c++;
                }
                client.setEmails(dataArr);

                //Get all Phone Numbers
                st = null;
                st = static_con.prepareStatement(phones);
                st.setInt(1, client.getCode());
                setArray = null;
                setArray = st.executeQuery();

                dataArr = new String[newClientController.noOfFields];
                c = 0;
                while (setArray.next()) {
                    dataArr[c] = setArray.getString("PH_NUM");
                    c++;
                }
                client.setPhones(dataArr);

                allClients.add(client);
            }

            // doRelease(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return allClients;
    }

    public List<ClientProperty> getAllClientsProperty(String where) {
        String query = "SELECT CL_ID,CL_NAME,CL_OWNER,CL_ADDR," +
                "CL_CITY,CL_COUNTRY,CL_WEBSITE,CL_TYPE,CL_JOINDATE FROM CLIENT_STORE";


        if (where == null) {
            query = query + " WHERE CL_ID != 0 ORDER BY CL_ID";
        } else {
            query = query + " WHERE " + where;
        }

        String emails = "SELECT EM_NAME FROM EMAIL_LIST WHERE CL_ID = ?";
        String phones = "SELECT PH_NUM FROM PHONE_LIST WHERE CL_ID = ?";

        List<ClientProperty> allClients = new ArrayList<>();

        try {
            // Connection con = getConnection();
            System.out.println(query);
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            if (!set.isBeforeFirst()) {
                return null;
            }

            while (set.next()) {
                ClientProperty client = new ClientProperty();
                client.setCode(set.getInt("CL_ID"));
                client.setName(set.getString("CL_NAME"));
                client.setOwner(set.getString("CL_OWNER"));
                client.setAddr(set.getString("CL_ADDR"));
                client.setCity(set.getString("CL_CITY"));
                client.setCountry(set.getString("CL_COUNTRY"));
                client.setWebsite(set.getString("CL_WEBSITE"));
                client.setType(set.getInt("CL_TYPE"));
                client.setJoinDate(set.getString("CL_JOINDATE"));

                //Get all Emails
                PreparedStatement st = static_con.prepareStatement(emails);
                st.setInt(1, client.getCode());
                ResultSet setArray = st.executeQuery();

                String[] dataArr = new String[newClientController.noOfFields];
                int c = 0;
                while (setArray.next()) {
                    try {
                        dataArr[c] = setArray.getString("EM_NAME");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("ArrayIndexOutOfBoundsException");
                    }
                    c++;
                }
                client.setEmails(dataArr);

                //Get all Phone Numbers
                st = null;
                st = static_con.prepareStatement(phones);
                st.setInt(1, client.getCode());
                setArray = null;
                setArray = st.executeQuery();

                dataArr = new String[newClientController.noOfFields];
                c = 0;
                while (setArray.next()) {
                    dataArr[c] = setArray.getString("PH_NUM");
                    c++;
                }
                client.setPhones(dataArr);

                allClients.add(client);
            }

            // doRelease(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return allClients;
    }


//    public String[] getClientEmails() {
//
//    }

    public List<String> getClientTypes() {

        String query = "SELECT CT_NAME FROM CLIENT_TYPE";

        try {
            // Connection con = getConnection();
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            if (!set.isBeforeFirst()) {
                return null;
            }

            List<String> types = new ArrayList<>();

            while (set.next()) {
                types.add(set.getString("CT_NAME"));
            }

            // doRelease(con);

            return types;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String[] getAllEmailIDs(String where) {
        String query = "SELECT DISTINCT EM_NAME FROM EMAIL_LIST ";

        if (where == null)
            query = query + " ORDER BY EM_NAME";
        else
            query = query + where;

        Connection con = null;
        try {
            con = getConnection();
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            if (!set.isBeforeFirst()) {
                return null;
            }

            set.last();
            int size = set.getRow();
            set.beforeFirst();

            String[] ems = new String[size];

            int c = 0;
            while (set.next()) {
                ems[c] = set.getString("EM_NAME");
                c++;
            }

            return ems;

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // doRelease(con);
        }

        return null;
    }

    //---------------------Contact------------------
    public void insertContact(ContactProperty contact) {

        String query = "INSERT INTO CONTACT_STORE(CS_ID, CS_FNAME ,CS_LNAME ,CS_DOB ,CS_ADDR ,CS_CITY , " +
                "CS_COUNTRY ,CS_NOTE ,FREZE ,CL_ID, CREATEDON, CREATEDBY) " +
                " SELECT IFNULL(max(CS_ID),0)+1,?,?,?,?,?,?,?,?,?,?,? from CONTACT_STORE";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, contact.getFirstName());
            statement.setString(2, contact.getLastName());
            statement.setString(3, contact.getDob());
            statement.setString(4, contact.getAddress());
            statement.setString(5, contact.getCity());
            statement.setString(6, contact.getCountry());
            statement.setString(7, contact.getNote());
            statement.setBoolean(8, contact.isIsFreeze());
            statement.setInt(9, contact.getClID());
            statement.setString(10, CommonTasks.getCurrentTimeStamp());
            statement.setInt(11, fHelper.ReadUserDetails().getUCODE());

            statement.executeUpdate();


            EmailsPhoneInsertion(statement, contact);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateContact(ContactProperty contact) {

        String query = "UPDATE CONTACT_STORE SET CS_FNAME =?, CS_LNAME =?, CS_DOB =?,CS_ADDR =?, CS_CITY =?, " +
                " CS_COUNTRY =?, CS_NOTE =?, FREZE =?, CL_ID =?, CREATEDON  =?, CREATEDBY =? " +
                " WHERE CS_ID =?";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, contact.getFirstName());
            statement.setString(2, contact.getLastName());
            statement.setString(3, contact.getDob());
            statement.setString(4, contact.getAddress());
            statement.setString(5, contact.getCity());
            statement.setString(6, contact.getCountry());
            statement.setString(7, contact.getNote());
            statement.setBoolean(8, contact.isIsFreeze());
            statement.setInt(9, contact.getClID());
            statement.setString(10, CommonTasks.getCurrentTimeStamp());
            statement.setInt(11, fHelper.ReadUserDetails().getUCODE());
            statement.setInt(12, contact.getCode());

            statement.executeUpdate();

            EmailsPhoneInsertion(statement, contact);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<ContactProperty> getAllContactsProperty(String where) {
        String query = "SELECT CS.CS_ID AS CS_ID,CS_FNAME,CS_LNAME,CS_DOB,CS_ADDR," +
                " CS_CITY,CS_COUNTRY,CS_NOTE,CS.CREATEDON,FREZE,EM_NAME,PH_NUM,CL_NAME,CS.CL_ID AS CL_ID" +
                " FROM CONTACT_STORE AS CS, EMAIL_LIST AS EL, PHONE_LIST AS PL, CLIENT_STORE AS CL " +
                " WHERE EL.CS_ID = CS.CS_ID " +
                " AND PL.CS_ID = CS.CS_ID " +
                " AND CL.CL_ID = CS.CL_ID";

        if (where == null) {
            query = query + " AND FREZE = 0 ORDER BY CS.CS_ID";
        } else {
            query = query + " AND " + where;
        }

        List<ContactProperty> allContacts = new ArrayList<>();

        try {
            System.out.println(query);
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            while (set.next()) {
                ContactProperty contact = new ContactProperty();
                contact.setCode(set.getInt("CS_ID"));
                contact.setFirstName(set.getString("CS_FNAME"));
                contact.setLastName(set.getString("CS_LNAME"));
                contact.setAddress(set.getString("CS_ADDR"));
                contact.setCity(set.getString("CS_CITY"));
                contact.setCountry(set.getString("CS_COUNTRY"));
                contact.setEmail(set.getString("EM_NAME"));
                contact.setMobile(set.getString("PH_NUM"));
                contact.setDob(set.getString("CS_DOB"));
                contact.setClID(set.getInt("CL_ID"));
                contact.setClientName(set.getString("CL_NAME"));
                contact.setAge(CommonTasks.getAge(contact.getDob()));
                contact.setNote(set.getString("CS_NOTE"));
                contact.setIsFreeze(set.getBoolean("FREZE"));

                allContacts.add(contact);
            }

            // doRelease(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return allContacts;
    }

    public int getNewContactCode() {
        String query = "SELECT IFNULL(max(CS_ID),0)+1 AS CS_ID FROM CONTACT_STORE";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);

            ResultSet set = statement.executeQuery();
            while (set.next()) {
                return set.getInt("CS_ID");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    //----------------------------Products-----------------------------
    public void insertProduct(ProductProperty product) {

        String query = "INSERT INTO PRODUCT_STORE(PS_ID, PS_NAME ,PS_PRICE ,PS_DESC ,PS_STATUS ,PS_TYPE , " +
                "PS_STARTED ,PS_PRIORITY , CREATEDON, CREATEDBY) " +
                " SELECT IFNULL(max(PS_ID),0)+1,?,?,?,?,?,?,?,?,? from PRODUCT_STORE";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, product.getName());
            statement.setInt(2, product.getPrice());
            statement.setString(3, product.getDesc());
            statement.setInt(4, product.getStatus());
            statement.setInt(5, product.getType());
            statement.setString(6, product.getStartedtimeStmp());
            statement.setInt(7, product.getPriority());
            statement.setString(8, CommonTasks.getCurrentTimeStamp());
            statement.setInt(9, fHelper.ReadUserDetails().getUCODE());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateProduct(ProductProperty product) {

        String query = "UPDATE PRODUCT_STORE SET PS_NAME=?,PS_PRICE=? ,PS_DESC=? ,PS_STATUS=? ,PS_TYPE=? , " +
                " PS_STARTED=? ,PS_PRIORITY=? , CREATEDON=?, CREATEDBY=?  " +
                " WHERE PS_ID = ? ";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, product.getName());
            statement.setInt(2, product.getPrice());
            statement.setString(3, product.getDesc());
            statement.setInt(4, product.getStatus());
            statement.setInt(5, product.getType());
            statement.setString(6, product.getStartedtimeStmp());
            statement.setInt(7, product.getPriority());
            statement.setString(8, CommonTasks.getCurrentTimeStamp());
            statement.setInt(9, fHelper.ReadUserDetails().getUCODE());
            statement.setInt(10, product.getCode());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<ProductProperty> getAllProducts(String where) {
        String query = "SELECT PS_ID, PS_NAME ,PS_PRICE ,PS_DESC ,PS_STATUS ,PS_TYPE ,PS_STARTED ,PS_PRIORITY ,CREATEDON, CREATEDBY FROM PRODUCT_STORE WHERE 1 ";

        if (where == null) {
//            query = query + " AND FREZE = 0";
        } else {
            query = query + " AND " + where;
        }

        List<ProductProperty> allProducts = new ArrayList<>();

        try {
            System.out.println(query);
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            while (set.next()) {
                ProductProperty product = new ProductProperty();
                product.setCode(set.getInt("PS_ID"));
                product.setName(set.getString("PS_NAME"));
                product.setPrice(set.getInt("PS_PRICE"));
                product.setDesc(set.getString("PS_DESC"));
                product.setStatus(set.getInt("PS_STATUS"));
                product.setType(set.getInt("PS_TYPE"));
                product.setStartedtimeStmp(set.getString("PS_STARTED"));
                product.setPriority(set.getInt("PS_PRIORITY"));
                product.setCreatedOn(set.getString("CREATEDON"));
                product.setCreatedBy(set.getInt("CREATEDBY"));
                allProducts.add(product);
            }

            // doRelease(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return allProducts;
    }
    
    

    public static boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    private void showAlertDialog() {

        Alert alert2 = new Alert(Alert.AlertType.ERROR, "Cannot Connect to the Database!",
                ButtonType.OK);
        alert2.showAndWait();

        if (alert2.getResult() == ButtonType.OK) {
            System.exit(0);
        }

    }

    private void doRelease(Connection con) {

        try {
            if (con != null)
                static_con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}