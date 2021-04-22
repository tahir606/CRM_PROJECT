package JCode.mysql;

import JCode.CommonTasks;
import JCode.FileHelper;
import client.newClient.newClientController;
import objects.ClientProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientQueries {

    private Connection static_con;
    private FileHelper fHelper;
    private EmailPhoneQueries emailPhoneQueries;
    private NoteQueries noteQueries;

    public ClientQueries(Connection static_con, FileHelper fHelper, EmailPhoneQueries emailPhoneQueries, NoteQueries noteQueries) {
        this.static_con = static_con;
        this.fHelper = fHelper;
        this.emailPhoneQueries = emailPhoneQueries;
        this.noteQueries = noteQueries;
    }

    public boolean insertClient(ClientProperty client) {

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
            if (client.getJoinDate() == null) {
                statement.setString(8, null);
            } else {
                if (client.getJoinDate() != null || !client.getJoinDate().equals("null"))
                    statement.setString(8, client.getJoinDate());
                else
                    statement.setString(8, null);
            }
            statement.setInt(9, fHelper.ReadUserDetails().getUCODE());
            statement.setString(10, CommonTasks.getCurrentTimeStamp());

            statement.executeUpdate();

            emailPhoneQueries.emailsPhoneInsertion(statement, client);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
    public List<ClientProperty> clientName(){
        List<ClientProperty> clientPropertyList = new ArrayList<>();
        String query = "Select Cl_ID,Cl_NAME From client_store Where Cl_ID!=0";
        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while (set.next()){
                ClientProperty clientProperty = new ClientProperty();
                clientProperty.setCode(set.getInt("Cl_ID"));
                clientProperty.setName(set.getString("Cl_NAME"));
                clientPropertyList.add(clientProperty);
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return clientPropertyList;
    }
    public void updateClient(ClientProperty client) {

        String query = "UPDATE  client_store  SET  CL_NAME = ?, CL_OWNER = ?," +
                " CL_ADDR = ?, CL_CITY = ?, CL_COUNTRY = ?," +
                " CL_WEBSITE = ?, CL_TYPE = ?, CL_JOINDATE = ? WHERE CL_ID = ?";

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
            statement.setInt(9, client.getCode());

            statement.executeUpdate();

            emailPhoneQueries.emailsPhoneInsertion(statement, client);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // doRelease(con);
        }

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

                client.setNotes(noteQueries.getNotes(client));

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

    public ClientProperty getParticularClient(ClientProperty where) {
        String query = "SELECT CL_ID,CL_NAME,CL_OWNER,CL_ADDR," +
                "CL_CITY,CL_COUNTRY,CL_WEBSITE,CL_TYPE,CL_JOINDATE FROM CLIENT_STORE";

        query = query + " WHERE CL_ID =? ORDER BY CL_ID";

        String emails = "SELECT EM_NAME FROM EMAIL_LIST WHERE CL_ID = ?";
        String phones = "SELECT PH_NUM FROM PHONE_LIST WHERE CL_ID = ?";

        try {
            // Connection con = getConnection();

            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, where.getCode());
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

                client.setNotes(noteQueries.getNotes(client));

                return client;
            }

            // doRelease(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

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

    public boolean insertFromLead(ClientProperty client) {

        String query = "INSERT INTO CLIENT_STORE(CL_ID,CL_NAME,CL_OWNER,CL_ADDR,CL_CITY" +
                ",CL_COUNTRY,CL_WEBSITE,CL_TYPE,CL_JOINDATE,CREATEDBY,CREATEDON,FROM_LEAD) " +
                " SELECT IFNULL(max(CL_ID),0)+1,?,?,?,?,?,?,?,?,?,?,? from CLIENT_STORE";

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
            if (client.getJoinDate() == null) {
                statement.setString(8, null);
            } else {
                if (client.getJoinDate() != null || !client.getJoinDate().equals("null"))
                    statement.setString(8, client.getJoinDate());
                else
                    statement.setString(8, null);
            }
            statement.setInt(9, fHelper.ReadUserDetails().getUCODE());
            statement.setString(10, CommonTasks.getCurrentTimeStamp());

            statement.setInt(11, client.getFromLead());

            statement.executeUpdate();

            emailPhoneQueries.emailsPhoneInsertion(statement, client);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
