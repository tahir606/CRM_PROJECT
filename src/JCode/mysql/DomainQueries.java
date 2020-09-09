package JCode.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DomainQueries {

    private Connection static_con;

    public DomainQueries(Connection static_con) {
        this.static_con = static_con;
    }

    public void insertDomainsWhitelist(String domain) {
        String query = "INSERT INTO DOMAIN_LIST(DCODE,DNAME,DWB) " +
                " SELECT IFNULL(max(DCODE),0)+1,?,? from DOMAIN_LIST";

        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, domain);
            statement.setInt(2, 1); //WhiteList
            statement.executeUpdate();
            if (statement != null)
                statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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

    public List<String> getBlackListKeyword() {
        String query = "SELECT keyword_Value FROM keyword_list";

        try {
            // Connection con = getConnection();
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();

            List<String> l = new ArrayList<>();

            while (set.next()) {
                l.add(set.getString("keyword_Value"));
            }

            return l;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
//  this method insert blacklistKeywords
    public void insertBlackListKeywords(String[] list) {
        String query = "INSERT INTO keyword_list(keyword_Code,keyword_Value) " +
                " SELECT IFNULL(max(keyword_Code),0)+1,? from keyword_list";

        PreparedStatement statement = null;
        // Connection con = getConnection();
        try {

            for (int i = 0; i < list.length; i++) {
                if (list[i] == null || list[i].equals(""))
                    continue;
                statement = null;
                statement = static_con.prepareStatement(query);
                statement.setString(1, list[i]);
                statement.executeUpdate();
            }
            if (statement != null)
                statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
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