package JCode.mysql;

import objects.ClientProperty;
import objects.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            e.printStackTrace();
        }
        return users;
    }


    public List<ClientProperty> emailsPerClient(String filter) {
        String query = "SELECT CL_ID, CL_NAME, CL_OWNER, (SELECT COUNT(ES.EMNO) FROM email_list ER, email_store ES " +
                "WHERE ER.CL_ID =  CS.CL_ID AND ES.FRADD LIKE CONCAT("+"'%',er.EM_NAME,'%'"+") "+filter+" ) AS EMNO " +
                " FROM client_store CS WHERE CL_ID != 0 ORDER BY `EMNO`  DESC";

        System.out.println(query);
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
            e.printStackTrace();
        }
        return clients;
    }
}
