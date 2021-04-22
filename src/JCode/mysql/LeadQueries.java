package JCode.mysql;

import JCode.CommonTasks;
import JCode.FileHelper;
import objects.Lead;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeadQueries {

    private Connection static_con;
    private FileHelper fHelper;
    private EmailPhoneQueries emailPhoneQueries;
    private NoteQueries noteQueries;

    public LeadQueries(Connection static_con, FileHelper fHelper, EmailPhoneQueries emailPhoneQueries, NoteQueries noteQueries) {
        this.static_con = static_con;
        this.fHelper = fHelper;
        this.emailPhoneQueries = emailPhoneQueries;
        this.noteQueries = noteQueries;
    }

    public void insertLead(Lead lead) {

        String query = "INSERT INTO LEAD_STORE(LS_ID, LS_FNAME ,LS_LNAME ,LS_CNAME ,LS_WEBSITE ,LS_CITY , " +
                " LS_COUNTRY, LS_NOTE, S_ID, S_OTHER, CREATEDON, CREATEDBY) " +
                " SELECT IFNULL(max(LS_ID),0)+1,?,?,?,?,?,?,?,?,?,?,? from LEAD_STORE";

        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, lead.getFirstName());
            statement.setString(2, lead.getLastName());
            statement.setString(3, lead.getCompany());
            statement.setString(4, lead.getWebsite());
            statement.setString(5, lead.getCity());
            statement.setString(6, lead.getCountry());
            statement.setString(7, lead.getNote());
            if (lead.getOtherText() == null)
                statement.setInt(8, '\0');
            else
                statement.setInt(8, lead.getSource());
            statement.setString(9, lead.getOtherText());
            statement.setString(10, CommonTasks.getCurrentTimeStamp());
            statement.setInt(11, fHelper.ReadUserDetails().getUCODE());

            statement.executeUpdate();

            emailPhoneQueries.emailsPhoneInsertion(statement, lead);

        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }

    }

    public void updateLead(Lead lead) {

        String query = "UPDATE LEAD_STORE SET LS_FNAME=?,LS_LNAME=?,LS_WEBSITE=?,LS_CNAME=?,LS_CITY=?, " +
                " LS_COUNTRY=?,LS_NOTE=?,S_ID=?,S_OTHER=?,CREATEDON=?,CREATEDBY=?" +
                " WHERE LS_ID=?";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, lead.getFirstName());
            statement.setString(2, lead.getLastName());
            statement.setString(3, lead.getWebsite());
            statement.setString(4, lead.getCompany());
            statement.setString(5, lead.getCity());
            statement.setString(6, lead.getCountry());
            statement.setString(7, lead.getNote());
            if (lead.getOtherText() != null) {
                statement.setInt(8, '\0');
            } else {
                statement.setInt(8, lead.getSource());
            }
            statement.setString(9, lead.getOtherText());
            statement.setString(10, CommonTasks.getCurrentTimeStamp());
            statement.setInt(11, fHelper.ReadUserDetails().getUCODE());
            statement.setInt(12, lead.getCode());

            statement.executeUpdate();

            emailPhoneQueries.emailsPhoneInsertion(statement, lead);

        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }

    }

    public List<Lead> getAllLeads(String where) {

        String query = " SELECT LS.LS_ID AS LS_ID,LS_FNAME,LS_LNAME,LS_CNAME,LS_CITY,LS_COUNTRY,LS_NOTE,LS_WEBSITE,EM_NAME,PH_NUM,S_ID,S_OTHER " +
                " FROM LEAD_STORE AS LS, EMAIL_LIST AS EL,  PHONE_LIST AS PL " +
                " WHERE EL.LS_ID = LS.LS_ID   " +
                " AND PL.LS_ID = LS.LS_ID " +
                " AND LS.FREZE = 0" +
                " UNION ALL " +
                " SELECT LS.LS_ID AS LS_ID,LS_FNAME,LS_LNAME,LS_CNAME, LS_CITY,LS_COUNTRY,LS_NOTE,LS_WEBSITE,null EM_NAME,null PH_NUM,S_ID,S_OTHER " +
                " FROM LEAD_STORE AS LS  " +
                " WHERE LS.LS_ID NOT IN (SELECT IFNULL(LS_ID,0) FROM EMAIL_LIST) " +
                " AND LS.LS_ID NOT IN (SELECT IFNULL(LS_ID,0) FROM phone_list) " +
                " AND LS.FREZE = 0 " +
                " AND LS.CONVERTED = 0";

        if (where == null) {
            query = query + " ";
        } else {
            query = query + " AND " + where;
        }
        List<Lead> allLeads = new ArrayList<>();
        try {

            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            while (set.next()) {
                Lead lead = new Lead();
                lead.setCode(set.getInt("LS_ID"));
                lead.setFirstName(set.getString("LS_FNAME"));
                lead.setLastName(set.getString("LS_LNAME"));
                lead.setCity(set.getString("LS_CITY"));
                lead.setCountry(set.getString("LS_COUNTRY"));
                lead.setWebsite(set.getString("LS_WEBSITE"));
                lead.setCompany(set.getString("LS_CNAME"));
                lead.setNote(set.getString("LS_NOTE"));
                lead.setEmail(set.getString("EM_NAME"));
                lead.setPhone(set.getString("PH_NUM"));
                lead.setSource(set.getInt("S_ID"));
                lead.setOtherText(set.getString("S_OTHER"));
                allLeads.add(lead);
            }

            // doRelease(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return allLeads;
    }

    public Lead getParticularLead(Lead where) {

        String query = " SELECT LS.LS_ID AS LS_ID,LS_FNAME,LS_LNAME,LS_CNAME,LS_CITY,LS_COUNTRY,LS_NOTE,LS_WEBSITE,EM_NAME,PH_NUM " +
                " FROM LEAD_STORE AS LS, EMAIL_LIST AS EL,  PHONE_LIST AS PL  " +
                " WHERE EL.LS_ID = LS.LS_ID   " +
                " AND PL.LS_ID = LS.LS_ID " +
                " AND LS.LS_ID = ? " +
                " UNION ALL " +
                " SELECT LS.LS_ID AS LS_ID,LS_FNAME,LS_LNAME,LS_CNAME, LS_CITY,LS_COUNTRY,LS_NOTE,LS_WEBSITE,null EM_NAME,null PH_NUM FROM LEAD_STORE AS LS  " +
                " WHERE LS.LS_ID NOT IN (SELECT IFNULL(LS_ID,0) FROM EMAIL_LIST) " +
                " AND LS.LS_ID NOT IN (SELECT IFNULL(LS_ID,0) FROM phone_list) " +
                " AND LS.LS_ID = ? ";
        try {

            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, where.getCode());
            statement.setInt(2, where.getCode());
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            while (set.next()) {
                Lead lead = new Lead();
                lead.setCode(set.getInt("LS_ID"));
                lead.setFirstName(set.getString("LS_FNAME"));
                lead.setLastName(set.getString("LS_LNAME"));
                lead.setCity(set.getString("LS_CITY"));
                lead.setCountry(set.getString("LS_COUNTRY"));
                lead.setWebsite(set.getString("LS_WEBSITE"));
                lead.setCompany(set.getString("LS_CNAME"));
                lead.setNote(set.getString("LS_NOTE"));
                lead.setEmail(set.getString("EM_NAME"));
                lead.setPhone(set.getString("PH_NUM"));
                lead.setNotes(noteQueries.getNotes(lead));

                return lead;
            }

            // doRelease(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public int getNewLeadCode() {
        String query = "SELECT IFNULL(max(LS_ID),0)+1 AS LS_ID FROM LEAD_STORE";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);

            ResultSet set = statement.executeQuery();
            while (set.next()) {
                return set.getInt("LS_ID");
            }

        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }

        return 0;
    }

    public List<String> getAllSources(String where) {

        String query = " SELECT S_NAME FROM SOURCE_LIST ";
        List<String> sources = new ArrayList<>();
        try {

            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                sources.add(set.getString("S_NAME"));
            }
            set.close();
            statement.close();
            // doRelease(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return sources;
    }


    public void insertSourceCreation(String name, String desc) {    //Set sources on  first startup
        String query = "INSERT INTO SOURCE_LIST (S_ID, S_NAME, S_DESC) " +
                " SELECT IFNULL(MAX(S_ID),0) + 1,?,? FROM SOURCE_LIST ";

        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, desc);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }

    }

    private boolean checkIfSources() {
        String query = "SELECT S_ID FROM SOURCE_LIST";

        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);

            ResultSet set = statement.executeQuery();
            if (!set.next()) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }

        return false;
    }

    public void checkAndPopulateSourcesOnCreation() {
        if (!checkIfSources()) {
            insertSourceCreation("Referall", "");
            insertSourceCreation("Website", "");
        }
    }

    public void markLeadAsClient(Lead lead) {
        String query = "UPDATE LEAD_STORE SET CONVERTED = 1 WHERE LS_ID = ?";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, lead.getCode());
            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        } finally {
            // // doRelease(con);
        }
    }

    public void archiveLead(Lead lead) {
        String query = "UPDATE LEAD_STORE SET FREZE = 1 WHERE LS_ID = ?";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, lead.getCode());
            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        } finally {
            // // doRelease(con);
        }
    }
}
