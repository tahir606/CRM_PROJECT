package JCode.mysql;

import objects.ClientProperty;
import objects.ContactProperty;
import objects.Lead;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmailPhoneQueries {

    private Connection static_con;

    public EmailPhoneQueries(Connection static_con) {
        this.static_con = static_con;
    }

    public void emailsListInsertion(String[] emails) {

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
                try {
                    statement.executeUpdate();
                } catch (Exception e) {
//                    e.printStackTrace();
                    System.out.println(e);
                }
            }

            if (statement != null)
                statement.close();
        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }
    }

    public void emailsPhoneInsertion(PreparedStatement statement, ContactProperty contact) {

        String deleteEmails = "DELETE FROM EMAIL_LIST WHERE CS_ID = ?";

        String deletePhones = "DELETE FROM PHONE_LIST WHERE CS_ID = ?";

        String emailList = "INSERT INTO EMAIL_LIST(EM_ID,EM_NAME,CS_ID,UCODE,CL_ID,LS_ID) " +
                "SELECT IFNULL(max(EM_ID),0)+1,?,?,0,0,0 from EMAIL_LIST";

        String phoneList = "INSERT INTO PHONE_LIST(PH_ID,PH_NUM,CS_ID,UCODE,CL_ID,LS_ID) " +
                "SELECT IFNULL(max(PH_ID),0)+1,?,?,0,0,0 from PHONE_LIST";

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
                statement.executeUpdate();
            }

            statement = null;
            statement = static_con.prepareStatement(deletePhones);
            statement.setInt(1, contact.getCode());
            statement.executeUpdate();

            //Adding Phones
            String[] phones = new String[]{contact.getMobile()};

            for (int i = 0; i < phones.length; i++) {   //Inserting Emails
                statement = null;
                if (phones[i] == null)
                    continue;

                statement = static_con.prepareStatement(phoneList);
                statement.setString(1, phones[i]);
                statement.setInt(2, contact.getCode());
                statement.executeUpdate();
            }

            if (statement != null)
                statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void emailsPhoneInsertion(PreparedStatement statement, ClientProperty client) {

        String deleteEmails = "DELETE FROM EMAIL_LIST WHERE CL_ID = ?";

        String deletePhones = "DELETE FROM PHONE_LIST WHERE CL_ID = ?";

        String emailList = "INSERT INTO EMAIL_LIST(EM_ID,EM_NAME,CL_ID,UCODE,CS_ID,LS_ID) " +
                "SELECT IFNULL(max(EM_ID),0)+1,?,?,0,0,0 from EMAIL_LIST";

        String phoneList = "INSERT INTO PHONE_LIST(PH_ID,PH_NUM,CL_ID,UCODE,CS_ID,LS_ID) " +
                "SELECT IFNULL(max(PH_ID),0)+1,?,?,0,0,0 from PHONE_LIST";

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
                statement.executeUpdate();
            }

            for (int i = 0; i < emails.length; i++) {
                try {
                    if (emails[i] == null)
                        continue;
                    String[] t = emails[i].split("\\@");
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

    public void emailsPhoneInsertion(PreparedStatement statement, Lead lead) {

        String deleteEmails = "DELETE FROM EMAIL_LIST WHERE LS_ID = ?";

        String deletePhones = "DELETE FROM PHONE_LIST WHERE LS_ID = ?";

        String emailList = "INSERT INTO EMAIL_LIST(EM_ID,EM_NAME,CL_ID,UCODE,CS_ID,LS_ID) " +
                "SELECT IFNULL(max(EM_ID),0)+1,?,0,0,0,? from EMAIL_LIST";

        String phoneList = "INSERT INTO PHONE_LIST(PH_ID,PH_NUM,CL_ID,UCODE,CS_ID,LS_ID) " +
                "SELECT IFNULL(max(PH_ID),0)+1,?,0,0,0,? from PHONE_LIST";

        try {
            //Delete Email
            statement = null;
            statement = static_con.prepareStatement(deleteEmails);
            statement.setInt(1, lead.getCode());
            statement.executeUpdate();
            //Add Email
            statement = null;
            statement = static_con.prepareStatement(emailList);
            statement.setString(1, lead.getEmail());
            statement.setInt(2, lead.getCode());
            statement.executeUpdate();

            //Delete Phone
            statement = null;
            statement = static_con.prepareStatement(deletePhones);
            statement.setInt(1, lead.getCode());
            statement.executeUpdate();
            //Add Phone
            statement = null;
            statement = static_con.prepareStatement(phoneList);
            statement.setString(1, lead.getPhone());
            statement.setInt(2, lead.getCode());
            statement.executeUpdate();


            String[] t = lead.getEmail().split("\\@");
            insertDomainsWhitelist(t[1]);

            if (statement != null)
                statement.close();
            // doRelease(con);
        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
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
}
