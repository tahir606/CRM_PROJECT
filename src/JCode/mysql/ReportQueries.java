package JCode.mysql;

import JCode.CommonTasks;
import objects.ClientProperty;
import objects.EmailProperty;
import objects.Users;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ReportQueries {

    private Connection static_con;

    public ReportQueries(Connection static_con) {
        this.static_con = static_con;
    }

    public List<Users> ticketsSolvedByUser(String filter) {
        String query = "SELECT US.UCODE, UNAME, FNAME, (SELECT COUNT(EMNO) FROM EMAIL_STORE AS ES WHERE ES.SOLVBY = US.UCODE " + filter + ") AS MAXEMNO " +
                " FROM USERS AS US " +
                " WHERE FREZE = 'N' " +
                " ORDER BY MAXEMNO DESC";

        List<Users> users = new ArrayList<>();
        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                Users user = new Users();
                user.setUCODE(set.getInt("UCODE"));
                user.setUNAME(set.getString("UNAME"));
                user.setFNAME(set.getString("FNAME"));
                user.setSolved(set.getInt("MAXEMNO"));
                users.add(user);
            }
        } catch (SQLException e) {

            e.getLocalizedMessage();
//            e.printStackTrace();
        }
        return users;
    }


    public List<ClientProperty> emailsPerClient(String filter) {
        String query = "SELECT CL_ID, CL_NAME, CL_OWNER, (SELECT COUNT(ES.EMNO) FROM email_list ER, email_store ES " +
                "WHERE ER.CL_ID =  CS.CL_ID AND ES.FRADD LIKE CONCAT("+"'%',er.EM_NAME,'%'"+") "+filter+" ) AS EMNO " +
                " FROM client_store CS WHERE CL_ID != 0 ORDER BY `EMNO`  DESC";


        List<ClientProperty> clients = new ArrayList<>();
        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                ClientProperty client = new ClientProperty();
                client.setCode(set.getInt("CL_ID"));
                client.setName(set.getString("CL_NAME"));
                client.setOwner(set.getString("CL_OWNER"));
                client.setTotalEmails(set.getInt("EMNO"));
                clients.add(client);
            }
        } catch (SQLException e) {

            e.getLocalizedMessage();
//            e.printStackTrace();
        }
        return clients;
    }

    public List<EmailProperty> clientReportWithDomain(ClientProperty clientProperty, String reportFilter) {
        String query = "SELECT EMNO, SBJCT, FRADD,SUBSTRING(EBODY, 1, 100) AS emailBody, TSTMP, LOCKTIME, SOLVTIME" +
                " FROM email_store  WHERE FREZE=0  AND  SUBSTRING_INDEX(SUBSTRING_INDEX(FRADD,'>',1),'<',-1)  IN (Select email_list.EM_NAME FROM email_list" +
                "  WHERE email_list.CL_ID =? and email_list.CL_ID!=0 GROUP BY email_list.EM_NAME ) " +reportFilter ;

        List<EmailProperty> allEmails = new ArrayList<>();
        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, clientProperty.getCode());
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                EmailProperty email = new EmailProperty();
                email.setEmail_No(set.getInt("EMNO"));
                email.setSubject(set.getString("SBJCT"));
                email.setFrom_Address(set.getString("FRADD").replace("^", " "));
                Document doc = Jsoup.parse(set.getString("emailBody"));
                String text = doc.body().text();
                email.setEmail_Body(text);
                email.setTimestamp(CommonTasks.getTimeFormatted(set.getString("TSTMP")));
                email.setLock_time(CommonTasks.getTimeFormatted(set.getString("LOCKTIME")));
                email.setSolve_Time(CommonTasks.getTimeFormatted(set.getString("SOLVTIME")));
                email.setDuration(CommonTasks.getTimeDuration(set.getString("LOCKTIME"), set.getString("SOLVTIME")));
                allEmails.add(email);
            }
        } catch (SQLException | ParseException ex) {
            ex.printStackTrace();
        }
        return allEmails;
    }
}
