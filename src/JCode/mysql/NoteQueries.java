package JCode.mysql;

import JCode.CommonTasks;
import JCode.FileHelper;
import objects.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoteQueries {

    private Connection static_con;
    private FileHelper fHelper;
    String  createdByQuery =    " AND CREATEDBY = ? ";
    public NoteQueries(Connection static_con, FileHelper fHelper) {
        this.static_con = static_con;
        this.fHelper = fHelper;
    }

    //Contact notes-----------------------------------------------------------------------------------------------------
    public void addNewNote(String text, ContactProperty contact) {
        String query = "INSERT INTO NOTE_STORE(N_ID, N_TEXT, CS_ID, CREATEDON, CREATEDBY) " +
                " SELECT IFNULL(max(N_ID),0)+1,?,?,?,? from NOTE_STORE WHERE CS_ID =?";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, text);
            statement.setInt(2, contact.getCode());
            statement.setString(3, CommonTasks.getCurrentTimeStamp());
            statement.setInt(4, fHelper.ReadUserDetails().getUCODE());
            statement.setInt(5, contact.getCode());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateNote(Note note, ContactProperty contactProperty) {
        String query = " UPDATE NOTE_STORE SET N_TEXT = ? " +
                " WHERE CS_ID =?  " +
                " AND N_ID =? "+createdByQuery;

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, note.getText());
            statement.setInt(2, contactProperty.getCode());
            statement.setInt(3, note.getCode());
            statement.setInt(4 ,fHelper.ReadUserDetails().getUCODE());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Note> getNotes(ContactProperty contact) {
        String query = "SELECT N_ID, N_TEXT, CS_FNAME, NS.CREATEDON AS CREATEDON, FNAME,NS.CREATEDBY " +
                " FROM NOTE_STORE AS NS, CONTACT_STORE AS CS, USERS AS US " +
                " WHERE NS.CS_ID = ? " +
                " AND NS.CS_ID = CS.CS_ID " +
                " AND NS.CREATEDBY = US.UCODE";

        List<Note> notes = new ArrayList<>();

        try {
//            System.out.println(query);
            PreparedStatement statement = static_con.prepareStatement(query);
            System.out.println("contact.getContact:" + contact.getCode());
            statement.setInt(1, contact.getCode());
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            while (set.next()) {
                Note note = new Note();
                note.setCode(set.getInt("N_ID"));
                note.setContactName(set.getString("CS_FNAME"));
                note.setCreatedByName(set.getString("FNAME"));
                note.setCreatedOn(set.getString("CREATEDON"));
                note.setText(set.getString("N_TEXT"));
                note.setCreatedBy(set.getInt("CREATEDBY"));
                notes.add(note);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return notes;
    }

    public void deleteNote(Note note, ContactProperty contact) {
        String query = "DELETE FROM NOTE_STORE " +
                " WHERE N_ID = ? " +
                " AND CS_ID = ? "+createdByQuery;

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, note.getCode());
            statement.setInt(2, contact.getCode());
            statement.setInt(3 ,fHelper.ReadUserDetails().getUCODE());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Client Notes------------------------------------------------------------------------------------------------------
    public void addNewNote(String text, ClientProperty client) {
        String query = "INSERT INTO NOTE_STORE(N_ID, N_TEXT, CL_ID, CREATEDON, CREATEDBY) " +
                " SELECT IFNULL(max(N_ID),0)+1,?,?,?,? from NOTE_STORE WHERE CL_ID =?";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, text);
            statement.setInt(2, client.getCode());
            statement.setString(3, CommonTasks.getCurrentTimeStamp());
            statement.setInt(4, fHelper.ReadUserDetails().getUCODE());
            statement.setInt(5, client.getCode());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateNote(Note note, ClientProperty client) {
        String query = " UPDATE NOTE_STORE SET N_TEXT = ? " +
                " WHERE CL_ID =?  " +
                " AND N_ID =? "+createdByQuery;

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, note.getText());
            statement.setInt(2, client.getCode());
            statement.setInt(3, note.getCode());
            statement.setInt(4 ,fHelper.ReadUserDetails().getUCODE());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Note> getNotes(ClientProperty client) {
        String query = "SELECT N_ID, N_TEXT, CL_NAME, NS.CREATEDON AS CREATEDON, FNAME ,NS.CREATEDBY " +
                " FROM NOTE_STORE AS NS, CLIENT_STORE AS CS, USERS AS US " +
                " WHERE NS.CL_ID = ? " +
                " AND NS.CL_ID = CS.CL_ID " +
                " AND NS.CREATEDBY = US.UCODE";

        List<Note> notes = new ArrayList<>();

        try {
//            System.out.println(query);
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, client.getCode());
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            while (set.next()) {
                Note note = new Note();
                note.setCode(set.getInt("N_ID"));
                note.setContactName(set.getString("CL_NAME"));
                note.setCreatedByName(set.getString("FNAME"));
                note.setCreatedOn(set.getString("CREATEDON"));
                note.setText(set.getString("N_TEXT"));
                note.setCreatedBy(set.getInt("CREATEDBY"));
                notes.add(note);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return notes;
    }

    public void deleteNote(Note note, ClientProperty client) {
        String query = "DELETE FROM NOTE_STORE " +
                " WHERE N_ID = ? " +
                " AND CL_ID = ? "+createdByQuery;

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, note.getCode());
            statement.setInt(2, client.getCode());
            statement.setInt(3 ,fHelper.ReadUserDetails().getUCODE());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Lead Notes------------------------------------------------------------------------------------------------------
    public void addNewNote(String text, Lead lead) {
        String query = "INSERT INTO NOTE_STORE(N_ID, N_TEXT, LS_ID, CREATEDON, CREATEDBY) " +
                " SELECT IFNULL(max(N_ID),0)+1,?,?,?,? from NOTE_STORE WHERE LS_ID =?";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, text);
            statement.setInt(2, lead.getCode());
            statement.setString(3, CommonTasks.getCurrentTimeStamp());
            statement.setInt(4, fHelper.ReadUserDetails().getUCODE());
            statement.setInt(5, lead.getCode());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateNote(Note note, Lead lead) {
        String query = " UPDATE NOTE_STORE SET N_TEXT = ? " +
                " WHERE LS_ID =?  " +
                " AND N_ID =? "+createdByQuery;

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, note.getText());
            statement.setInt(2, lead.getCode());
            statement.setInt(3, note.getCode());
            statement.setInt(4 ,fHelper.ReadUserDetails().getUCODE());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Note> getNotes(Lead lead) {
        String query = "SELECT N_ID, N_TEXT, LS_CNAME, NS.CREATEDON AS CREATEDON, FNAME,NS.CREATEDBY " +
                " FROM NOTE_STORE AS NS, LEAD_STORE AS LS, USERS AS US " +
                " WHERE NS.LS_ID = ? " +
                " AND NS.LS_ID = LS.LS_ID " +
                " AND NS.CREATEDBY = US.UCODE ";

        List<Note> notes = new ArrayList<>();

        try {
//            System.out.println(query);
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, lead.getCode());
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            while (set.next()) {
                Note note = new Note();
                note.setCode(set.getInt("N_ID"));
                note.setContactName(set.getString("LS_CNAME"));
                note.setCreatedByName(set.getString("FNAME"));
                note.setCreatedOn(set.getString("CREATEDON"));
                note.setText(set.getString("N_TEXT"));
                note.setCreatedBy(set.getInt("CREATEDBY"));
                notes.add(note);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return notes;
    }

    public void deleteNote(Note note, Lead lead) {
        String query = "DELETE FROM NOTE_STORE " +
                " WHERE N_ID = ? " +
                " AND LS_ID = ? "+createdByQuery;

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, note.getCode());
            statement.setInt(2, lead.getCode());
            statement.setInt(3 ,fHelper.ReadUserDetails().getUCODE());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Product Notes----------------------------------------------------------------------------------------------------
    public void addNewNote(String text, ProductProperty product) {
        String query = "INSERT INTO NOTE_STORE(N_ID, N_TEXT, PS_ID, CREATEDON, CREATEDBY) " +
                " SELECT IFNULL(max(N_ID),0)+1,?,?,?,? from NOTE_STORE WHERE PS_ID =?";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, text);
            statement.setInt(2, product.getCode());
            statement.setString(3, CommonTasks.getCurrentTimeStamp());
            statement.setInt(4, fHelper.ReadUserDetails().getUCODE());
            statement.setInt(5, product.getCode());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateNote(Note note, ProductProperty product) {
        String query = " UPDATE NOTE_STORE SET N_TEXT = ? " +
                " WHERE PS_ID =?  " +
                " AND N_ID =? "+createdByQuery;

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, note.getText());
            statement.setInt(2, product.getCode());
            statement.setInt(3, note.getCode());
            statement.setInt(4 ,fHelper.ReadUserDetails().getUCODE());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Note> getNotes(ProductProperty product) {
        String query = "SELECT N_ID, N_TEXT, PS_NAME, NS.CREATEDON AS CREATEDON, FNAME ,NS.CREATEDBY " +
                " FROM NOTE_STORE AS NS, PRODUCT_STORE AS LS, USERS AS US " +
                " WHERE NS.PS_ID = ? " +
                " AND NS.PS_ID = LS.PS_ID " +
                " AND NS.CREATEDBY = US.UCODE ";

        List<Note> notes = new ArrayList<>();

        try {
//            System.out.println(query);
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, product.getCode());
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            while (set.next()) {
                Note note = new Note();
                note.setCode(set.getInt("N_ID"));
                note.setContactName(set.getString("PS_NAME"));
                note.setCreatedByName(set.getString("FNAME"));
                note.setCreatedOn(set.getString("CREATEDON"));
                note.setText(set.getString("N_TEXT"));
                note.setCreatedBy(set.getInt("CREATEDBY"));
                notes.add(note);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return notes;
    }

    public void deleteNote(Note note, ProductProperty product) {
        String query = "DELETE FROM NOTE_STORE " +
                " WHERE N_ID = ? " +
                " AND PS_ID = ? "+createdByQuery;

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, note.getCode());
            statement.setInt(2, product.getCode());
            statement.setInt(3 ,fHelper.ReadUserDetails().getUCODE());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Note> getNotes(Email email) {

        String query = "SELECT N_ID, N_TEXT, ES.EMNO , NS.CREATEDON AS CREATEDON, FNAME,NS.CREATEDBY " +
                "                 FROM NOTE_STORE AS NS,EMAIL_STORE AS ES,  USERS AS US " +
                "                 WHERE NS.EMNO = ? " +
                "                 AND NS.EMNO = ES.EMNO" +
                "                AND NS.CREATEDBY = US.UCODE ";

        List<Note> notes = new ArrayList<>();

        try {
//            System.out.println(query);
            PreparedStatement statement = static_con.prepareStatement(query);

            statement.setInt(1, email.getEmailNo());
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            while (set.next()) {
                Note note = new Note();
                note.setCode(set.getInt("N_ID"));
                note.setEmailNo(set.getInt("EMNO"));
                note.setCreatedByName(set.getString("FNAME"));
                note.setCreatedOn(set.getString("CREATEDON"));
                note.setText(set.getString("N_TEXT"));
                note.setCreatedBy(set.getInt("CREATEDBY"));
                notes.add(note);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return notes;
    }

    public void addNewNote(String note, Email email) {
        String query = "INSERT INTO NOTE_STORE(N_ID, N_TEXT, EMNO, CREATEDON, CREATEDBY) " +
                " SELECT IFNULL(max(N_ID),0)+1,?,?,?,? from NOTE_STORE WHERE EMNO =?";

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, note);
            statement.setInt(2, email.getEmailNo());
            statement.setString(3, CommonTasks.getCurrentTimeStamp());
            statement.setInt(4, fHelper.ReadUserDetails().getUCODE());
            statement.setInt(5,email.getEmailNo());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteNote(Note note, Email email) {
        String query = "DELETE FROM NOTE_STORE WHERE N_ID = ? AND EMNO = ? " + createdByQuery;


        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, note.getCode());
            statement.setInt(2, note.getEmailNo());
            statement.setInt(3 ,fHelper.ReadUserDetails().getUCODE());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateNote(Note note, Email email) {
        String query = " UPDATE NOTE_STORE SET N_TEXT = ? " +
                " WHERE EMNO =?  " +
                " AND N_ID =? "+createdByQuery;

        // Connection con = getConnection();
        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, note.getText());
            statement.setInt(2, email.getEmailNo());
            statement.setInt(3, note.getCode());
            statement.setInt(4 ,fHelper.ReadUserDetails().getUCODE());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
