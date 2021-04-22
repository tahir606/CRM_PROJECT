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

public class EventQueries {

    private Connection static_con;
    private FileHelper fHelper;

    public EventQueries(Connection static_con, FileHelper fHelper) {
        this.static_con = static_con;
        this.fHelper = fHelper;
    }

    public void addEvent(Event event) {
        String query = "INSERT INTO EVENT_STORE(ES_ID, ES_TITLE, ES_DESC, ES_LOCATION, ES_FROM, ES_TO, ES_ALLDAY, CL_ID, " +
                "LS_ID, CREATEDON, CREATEDBY) " +
                " SELECT IFNULL(max(ES_ID),0)+1,?,?,?,?,?,?,?,?,?,? from EVENT_STORE";

        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, event.getTitle());
            statement.setString(2, event.getDesc());
            statement.setString(3, event.getLocation());
            statement.setString(4, event.getFromDate() + " " + event.getFromTime());
            statement.setString(5, event.getToDate() + " " + event.getToTime());
            statement.setBoolean(6, event.isAllDay());
            statement.setInt(7, event.getClient());
            statement.setInt(8, event.getLead());
            statement.setString(9, CommonTasks.getCurrentTimeStamp());
            statement.setInt(10, fHelper.ReadUserDetails().getUCODE());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }
    }

//    public Task getSpecificEvent(Event where) {
//        String query = "SELECT TS_ID, TS_SUBJECT, TS_DESC, TS_DDATE, TS_REPEAT, NS.CREATEDON AS CREATEDON, FNAME " +
//                " FROM TASK_STORE AS NS, USERS AS US " +
//                " WHERE NS.CREATEDBY = US.UCODE " +
//                " AND NS.FREZE = 0";
//
//        try {
//            PreparedStatement statement = static_con.prepareStatement(query);
//            ResultSet set = statement.executeQuery();
//            //-------------Creating Task-------------
//            while (set.next()) {
//                Task task = new Task();
//                task.setCode(set.getInt("TS_ID"));
//                task.setSubject(set.getString("TS_SUBJECT"));
//                task.setDesc(set.getString("TS_DESC"));
//                task.setDueDate(set.getString("TS_DDATE"));
//                task.setCreatedBy(set.getString("FNAME"));
//                task.setRepeat(set.getBoolean("TS_REPEAT"));
//                task.setCreatedOn(set.getString("CREATEDON"));
//                return task;
//            }
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }

    public List<Event> getAlLEvents(String where) {
        String query = "SELECT ES_ID, ES_TITLE, ES_LOCATION, ES_DESC, ES_FROM, ES_TO, ES_ALLDAY, ES_STATUS, NS.CREATEDON AS CREATEDON, NS.CREATEDBY AS CREATEDBY, FNAME,  " +
                "                 NS.CL_ID, (SELECT CL.CL_NAME FROM client_store as CL WHERE CL.CL_ID = NS.CL_ID) AS CLNAME ," +
                "                 NS.LS_ID, (SELECT CONCAT(LS.LS_FNAME,' ',LS.LS_LNAME) FROM lead_store as LS WHERE LS.LS_ID = NS.LS_ID) AS LSNAME " +
                "                 FROM EVENT_STORE AS NS, USERS AS US " +
                "                 WHERE NS.CREATEDBY = US.UCODE " +
                "                 AND NS.FREZE = 0";

        if (where == null || where.equals(""))
            query = query + "";
        else
            query = query + where;

        List<Event> events = new ArrayList<>();

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            //-------------Creating Task-------------
            while (set.next()) {
                Event event = new Event();
                event.setCode(set.getInt("ES_ID"));
                event.setTitle(set.getString("ES_TITLE"));
                event.setDesc(set.getString("ES_DESC"));
                event.setAllDay(set.getBoolean("ES_ALLDAY"));
                event.setLocation(set.getString("ES_LOCATION"));
                event.setFromDate(set.getString("ES_FROM").split("\\s+")[0]);
                event.setFromTime(set.getString("ES_FROM").split("\\s+")[1]);
                event.setToDate(set.getString("ES_TO").split("\\s+")[0]);
                event.setToTime(set.getString("ES_TO").split("\\s+")[1]);
                event.setStatus(set.getBoolean("ES_STATUS"));
                event.setCreatedBy(set.getString("FNAME"));
                event.setCreatedOn(set.getString("CREATEDON"));
                event.setCreatedByCode(set.getInt("CREATEDBY"));

                event.setClient(set.getInt("CL_ID"));
                event.setLead(set.getInt("LS_ID"));
                if (event.getClient() != 0)
                    event.setRelationName(set.getString("CLNAME"));
                else
                    event.setRelationName(set.getString("LSNAME"));
                events.add(event);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return events;
    }

    public List<Event> getEvents(ClientProperty client) {
        String query = "SELECT ES_ID, ES_TITLE, ES_DESC, ES_LOCATION, ES_ALLDAY, ES_STATUS, ES_FROM, ES_TO, FNAME, CL_NAME, NS.CREATEDON " +
                " FROM EVENT_STORE AS NS, CLIENT_STORE AS CS, USERS AS US " +
                " WHERE NS.CL_ID = ? " +
                " AND NS.CL_ID = CS.CL_ID " +
                " AND NS.CREATEDBY = US.UCODE " +
                " AND NS.FREZE = 0 ";

        List<Event> events = new ArrayList<>();

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, client.getCode());
            ResultSet set = statement.executeQuery();
            //-------------Creating Task-------------
            while (set.next()) {
                Event event = new Event();
                event.setCode(set.getInt("ES_ID"));
                event.setTitle(set.getString("ES_TITLE"));
                event.setDesc(set.getString("ES_DESC"));
                event.setAllDay(set.getBoolean("ES_ALLDAY"));
                event.setLocation(set.getString("ES_LOCATION"));
                event.setFromDate(set.getString("ES_FROM").split("\\s+")[0]);
                event.setFromTime(set.getString("ES_FROM").split("\\s+")[1]);
                event.setToDate(set.getString("ES_TO").split("\\s+")[0]);
                event.setToTime(set.getString("ES_TO").split("\\s+")[1]);
                event.setRelationName(set.getString("CL_NAME"));
                event.setStatus(set.getBoolean("ES_STATUS"));
                event.setCreatedBy(set.getString("FNAME"));
                event.setCreatedOn(set.getString("CREATEDON"));
                events.add(event);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return events;
    }

    public List<Event> getEvents(Lead lead) {
        String query = "SELECT ES_ID, ES_TITLE, ES_DESC, ES_LOCATION, ES_ALLDAY, ES_STATUS, ES_FROM, ES_TO, FNAME, LS_CNAME, NS.CREATEDON " +
                " FROM EVENT_STORE AS NS, LEAD_STORE AS CS, USERS AS US " +
                " WHERE NS.LS_ID = ? " +
                " AND NS.LS_ID = CS.LS_ID " +
                " AND NS.CREATEDBY = US.UCODE " +
                " AND NS.FREZE = 0 ";

        List<Event> events = new ArrayList<>();

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, lead.getCode());
            ResultSet set = statement.executeQuery();
            //-------------Creating Task-------------
            while (set.next()) {
                Event event = new Event();
                event.setCode(set.getInt("ES_ID"));
                event.setTitle(set.getString("ES_TITLE"));
                event.setDesc(set.getString("ES_DESC"));
                event.setAllDay(set.getBoolean("ES_ALLDAY"));
                event.setLocation(set.getString("ES_LOCATION"));
                event.setFromDate(set.getString("ES_FROM").split("\\s+")[0]);
                event.setFromTime(set.getString("ES_FROM").split("\\s+")[1]);
                event.setToDate(set.getString("ES_TO").split("\\s+")[0]);
                event.setToTime(set.getString("ES_TO").split("\\s+")[1]);
                event.setRelationName(set.getString("LS_CNAME"));
                event.setStatus(set.getBoolean("ES_STATUS"));
                event.setCreatedBy(set.getString("FNAME"));
                event.setCreatedOn(set.getString("CREATEDON"));
                events.add(event);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return events;
    }


    public void updateEvent(Event event) {
        String query = " UPDATE EVENT_STORE SET " +
                " ES_TITLE =?, ES_DESC =?, ES_LOCATION =?, ES_FROM =?, ES_TO =?, ES_ALLDAY=?" +
                " WHERE ES_ID = ? ";

        PreparedStatement statement = null;
        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, event.getTitle());
            statement.setString(2, event.getDesc());
            statement.setString(3, event.getLocation());
            statement.setString(4, event.getFromDate() + " " + event.getFromTime());
            statement.setString(5, event.getToDate() + " " + event.getToTime());
            statement.setBoolean(6, event.isAllDay());
            statement.setInt(7, event.getCode());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }
    }

    //
    public void closeEvent(Event event) {
        String query = "UPDATE EVENT_STORE SET ES_STATUS = ?, ES_CLOSEDON = ? " +
                " WHERE ES_ID = ? ";

        PreparedStatement statement = null;
        try {
            statement = static_con.prepareStatement(query);
            statement.setBoolean(1, true);
            statement.setString(2, CommonTasks.getCurrentTimeStamp());
            statement.setInt(3, event.getCode());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }
    }

    public void archiveEvent(Event event) {
        String query = "UPDATE EVENT_STORE SET FREZE = 1 " +
                " WHERE ES_ID = ? ";

        PreparedStatement statement = null;
        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, event.getCode());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }
    }


    public void markNotified(Event event) {
        String query = " UPDATE EVENT_STORE SET NOTIFIED = 1 " +
                " WHERE ES_ID = ? ";

        PreparedStatement statement = null;
        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, event.getCode());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }
    }
}
