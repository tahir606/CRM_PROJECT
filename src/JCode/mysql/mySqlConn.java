package JCode.mysql;

import JCode.FileHelper;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import objects.*;

import javax.mail.Message;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mySqlConn {

    private final static String USER = "crm",
            PASSWORD = "crm123!@#",
            DBNAME = "bits_crm";
    private String URL;

    private FileHelper fHelper;
    private ESetting eSetting;

    private Users user;

    private static Connection static_con;

    private UserQueries userQueries;
    private StartupQueries startupQueries;
    private EmailQueries emailQueries;
    private SettingsQueries settingsQueries;
    private EmailPhoneQueries emailPhoneQueries;
    private ContactQueries contactQueries;
    private ClientQueries clientQueries;
    private ProductQueries productQueries;
    private LeadQueries leadQueries;
    private DomainQueries domainQueries;
    private NoteQueries noteQueries;
    private TaskQueries taskQueries;
    private EventQueries eventQueries;
    private ReportQueries reportQueries;
    private DocumentQueries documentQueries;

    public mySqlConn() {
        fHelper = new FileHelper();
        Network network = fHelper.getNetworkDetails();
        if (network == null)
            return;
        URL = "jdbc:mysql://" + network.getHost() + ":" + network.getPort() + "/" + DBNAME + "?allowMultiQueries=true&autoReconnect=true";
        user = fHelper.ReadUserDetails();
        if (static_con == null)
            static_con = getConnection();

        userQueries = new UserQueries(static_con, fHelper);
        startupQueries = new StartupQueries(static_con, userQueries);
        settingsQueries = new SettingsQueries(static_con, fHelper);
        eSetting = getEmailSettings();
        emailPhoneQueries = new EmailPhoneQueries(static_con);
        noteQueries = new NoteQueries(static_con, fHelper);
        emailQueries = new EmailQueries(static_con, user, eSetting, emailPhoneQueries,noteQueries);
        contactQueries = new ContactQueries(static_con, fHelper, emailPhoneQueries, noteQueries);
        clientQueries = new ClientQueries(static_con, fHelper, emailPhoneQueries, noteQueries);
        leadQueries = new LeadQueries(static_con, fHelper, emailPhoneQueries, noteQueries);
        productQueries = new ProductQueries(static_con, fHelper, noteQueries);
        domainQueries = new DomainQueries(static_con);
        taskQueries = new TaskQueries(static_con, fHelper);
        eventQueries = new EventQueries(static_con, fHelper);
        reportQueries = new ReportQueries(static_con);
        documentQueries = new DocumentQueries(static_con);
    }

    private Connection getConnection() {
        int times = 1;

//        if (static_con != null) {
//            try {
//                static_con.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }

        while (true) {
            try {
                System.out.println("Trying: " + times);
                Class.forName("com.mysql.jdbc.Driver");
//                System.out.println(URL + "\n" + USER + "\n" + PASSWORD);
                Connection con = DriverManager.getConnection(
                        URL, USER, PASSWORD);
                return con;
            } catch (SQLException | ClassNotFoundException e) {
                times++;
                e.printStackTrace();
                System.out.println("Sleeping for 10 Seconds");
                try {
                    Thread.sleep(10000);
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
        return userQueries.authenticateLogin(username, password);
    }

    public void setLogin(int ucode, boolean log) {
        userQueries.setLogin(ucode, log);
    }

    public boolean getRights(Users user) {
        return userQueries.getRights(user);
    }

    public String getUserName(int ucode) {
        return userQueries.getUserName(ucode);
    }

    public Users getUserDetails(Users user) {
        return userQueries.getUserDetails(user);
    }

    public Users getUserDetails(int ucode) {
        Users user = new Users();
        user.setUCODE(ucode);
        return userQueries.getUserDetails(user);
    }

    public List<Users> getAllUsers() {
        return userQueries.getAllUsers();
    }

    public List<Users.uRights> getAllUserRights() {
        return userQueries.getAllUserRights();
    }

    public void insertUpdateUser(Users user, int choice) {
        userQueries.insertUpdateUser(user, choice);
    }

    public void deleteUser(Users u) {
        userQueries.deleteUser(u);
    }

//    public void archiveUser(Users u) {
//        userQueries.archiveUser(u);
//    }

    public boolean checkAndCreateUser() {
        return startupQueries.checkAndCreateUser();
    }

    public boolean checkAndPopulateRights() {
        return startupQueries.checkAndPopulateRights();
    }

    public int getLatestEmailNo(int email_type) {
        return emailQueries.getLatestEmailNo(email_type);
    }

    public Users getNoOfSolvedEmails(Users user) {
        return emailQueries.getNoOfSolvedEmails(user);
    }

    public void createEmailRelations(Email email) {
        emailQueries.createEmailRelations(email);
    }

    public List<ContactProperty> getEmailContactRelations(Email email) {
        return emailQueries.getEmailContactRelations(email);
    }

    public List<ClientProperty> getEmailClientRelations(Email email) {
        return emailQueries.getEmailClientRelations(email);
    }

    public int getNoOfLocked(Users user) {
        return emailQueries.getNoOfLocked(user);
    }

    public int getNoOfUnsolved() {
        return emailQueries.getNoOfUnsolved();
    }

    public int getNoOfUnlocked() {
        return emailQueries.getNoOfUnlocked();
    }

    public void insertEmail(Email email, Message message) {
        emailQueries.insertEmail(email, message);
    }

    public void insertEmailManual(Email email) {
        emailQueries.insertEmailManual(email);
    }
//help
    public List<Email> readAllEmails(Filters filters) {
        return emailQueries.readAllEmails(filters, userQueries);
    }

    public void insertEmailGeneral(Email email) {
        emailQueries.insertEmailGeneral(email);
    }

    public List<Email> readAllEmailsGeneral(String where) {
        return emailQueries.readAllEmailsGeneral(where);
    }

    public void insertEmailSent(Email email) {
        emailQueries.insertEmailSent(email);
    }

    public List<Email> readAllEmailsSent(String where) {
        return emailQueries.readAllEmailsSent(where);
    }

    public void lockEmail(Email email, int op) {
        emailQueries.lockEmail(email, op);
    }

    public void solvEmail(Email email, String flag, Users user, boolean choice, String msg) {
        emailQueries.solvEmail(email, flag, user, choice, msg);
    }

    public void ArchiveEmail(int type, String where) {    //Verb
        emailQueries.ArchiveEmail(type, where);
    }

    public void markAsSent(Email email) {
        emailQueries.markAsSent(email);
    }

    public boolean checkIfUserIsLoggedIn(Users user) {
        return settingsQueries.checkIfUserIsLoggedIn(user);
    }

    public void logUserOut(Users user) {
        settingsQueries.logUserOut(user);
    }

    public ESetting getEmailSettings() {
        return settingsQueries.getEmailSettings();
    }

    public void saveEmailSettings(ESetting eSetting) {
        settingsQueries.saveEmailSettings(eSetting);
    }

    public void checkOnNotificationSettings() {
        settingsQueries.checkOnNotificationSettings();
    }

    public void insertNotificationSettings(NotificationSettings nSettings) {
        settingsQueries.insertNotificationSettings(nSettings);
    }

    public NotificationSettings getNotificationSettings() {
        return settingsQueries.getNotificationSettings();
    }


    public boolean insertClient(ClientProperty client) {
        return clientQueries.insertClient(client);
    }

    public void updateClient(ClientProperty client) {
        clientQueries.updateClient(client);
    }

    public void insertDomainsWhitelist(String domain) {
        domainQueries.insertDomainsWhitelist(domain);
    }

    public void insertDomainsWhitelist(String[] list) {
        domainQueries.insertDomainsWhitelist(list);
    }

    public void updateDomainType(int type, String domain) {
        domainQueries.updateDomainType(type, domain);
    }

    public List<String> getWhiteBlackListDomains(int type) {
        return domainQueries.getWhiteBlackListDomains(type);
    }

    public int getNoClients() {
        return clientQueries.getNoClients();
    }

    public List<ClientProperty> getAllClients(String where) {
        return clientQueries.getAllClients(where);
    }

    public ClientProperty getParticularClient(ClientProperty client) {
        return clientQueries.getParticularClient(client);
    }

    public List<ClientProperty> getAllClientsProperty(String where) {
        return clientQueries.getAllClientsProperty(where);
    }

    public List<String> getClientTypes() {
        return clientQueries.getClientTypes();
    }

    public boolean insertFromLead(ClientProperty client) {
        return clientQueries.insertFromLead(client);
    }

    public void insertContact(ContactProperty contact) {
        contactQueries.insertContact(contact);
    }

    public void updateContact(ContactProperty contact) {
        contactQueries.updateContact(contact);
    }

    public List<ContactProperty> getAllContactsProperty(String where) {
        return contactQueries.getAllContactsProperty(where);
    }

    public int getNewContactCode() {
        return contactQueries.getNewContactCode();
    }

    public ContactProperty getParticularContact(ContactProperty contact) {
        return contactQueries.getParticularContact(contact);
    }

    public void insertProduct(ProductProperty product) {
        productQueries.insertProduct(product);
    }

    public void updateProduct(ProductProperty product) {
        productQueries.updateProduct(product);
    }

    public List<ProductProperty> getAllProducts(String where) {
        return productQueries.getAllProducts(where);
    }

    public ProductProperty getParticularProduct(ProductProperty product) {
        return productQueries.getParticularProduct(product);
    }

    public void insertProductModule(ProductModule productModule) {
        productQueries.insertProductModule(productModule);
    }

    public void updateProductModule(ProductModule productModule) {
        productQueries.updateProductModule(productModule);
    }

    public List<ProductModule> getAllProductModules(int productCode) {
        return productQueries.getAllProductModules(productCode);
    }

    public ProductProperty getProductModuleStates(ProductProperty product) {
        return productQueries.getProductModuleStates(product);
    }

    public ArrayList<ProductModule> getLockedModules() {
        return productQueries.getLockedModules();
    }

    public void deleteAllProductModules(int product) {
        productQueries.deleteAllProductModules(product);
    }

    public int getNewProductCode() {
        return productQueries.getNewProductCode();
    }

    public boolean lockModule(ProductModule module) {
        return productQueries.lockModule(module);
    }

    public void unlockModule(ProductModule module, String desc) {
        productQueries.unlockModule(module, desc);
    }

    public int getNewLeadCode() {
        return leadQueries.getNewLeadCode();
    }

    public void insertLead(Lead lead) {
        leadQueries.insertLead(lead);
    }

    public void updateLead(Lead lead) {
        leadQueries.updateLead(lead);
    }

    public List<Lead> getAllLeads(String where) {
        return leadQueries.getAllLeads(where);
    }

    public Lead getParticularLead(Lead lead) {
        return leadQueries.getParticularLead(lead);
    }

    public void checkAndPopulateSourcesOnCreation() {
        leadQueries.checkAndPopulateSourcesOnCreation();
    }

    public List<String> getAllSources() {
        return leadQueries.getAllSources(null);
    }

    public void markLeadAsClient(Lead lead) {
        leadQueries.markLeadAsClient(lead);
    }

    public void archiveLead(Lead lead) {
        leadQueries.archiveLead(lead);
    }

    public void addNote(String text, ContactProperty contact) {
        noteQueries.addNewNote(text, contact);
    }

    public void addNote(String text, ClientProperty client) {
        noteQueries.addNewNote(text, client);
    }

    public void addNote(String text, Lead lead) {
        noteQueries.addNewNote(text, lead);
    }

    public void addNote(String text, ProductProperty product) {
        noteQueries.addNewNote(text, product);
    }

    public void updateNote(Note note, ContactProperty contact) {
        noteQueries.updateNote(note, contact);
    }

    public void updateNote(Note note, ClientProperty client) {
        noteQueries.updateNote(note, client);
    }

    public void updateNote(Note note, Lead lead) {
        noteQueries.updateNote(note, lead);
    }

    public void updateNote(Note note, ProductProperty product) {
        noteQueries.updateNote(note, product);
    }

    public List<Note> getContactNotes(ContactProperty contact) {
        return noteQueries.getNotes(contact);
    }

    public void deleteNote(Note note, ContactProperty contact) {
        noteQueries.deleteNote(note, contact);
    }

    public void deleteNote(Note note, ClientProperty client) {
        noteQueries.deleteNote(note, client);
    }

    public void deleteNote(Note note, Lead lead) {
        noteQueries.deleteNote(note, lead);
    }

    public void deleteNote(Note note, ProductProperty product) {
        noteQueries.deleteNote(note, product);
    }

    public void addTask(Task task) {
        taskQueries.addTask(task);
    }

    public void updateTask(Task task) {
        taskQueries.updateTask(task);
    }

    public void closeTask(Task task) {
        taskQueries.closeTask(task);
    }

    public void archiveTask(Task task) {
        taskQueries.archiveTask(task);
    }

    public List<Task> getAllTasks(String where) {
        return taskQueries.getAlLTasks(where);
    }

    public List<Task> getTasks(ContactProperty obj) {
        return taskQueries.getTasks(obj);
    }

    public List<Task> getTasks(ClientProperty obj) {
        return taskQueries.getTasks(obj);
    }

    public List<Task> getTasks(Lead obj) {
        return taskQueries.getTasks(obj);
    }

    public List<Task> getTasks(ProductProperty obj) {
        return taskQueries.getTasks(obj);
    }

    public void markNotified(Task obj) {
        taskQueries.markNotified(obj);
    }

    public void addEvent(Event event) {
        eventQueries.addEvent(event);
    }

    public List<Event> getEvents(ClientProperty obj) {
        return eventQueries.getEvents(obj);
    }

    public List<Event> getEvents(Lead obj) {
        return eventQueries.getEvents(obj);
    }

    public List<Event> getAllEvents(String where) {
        return eventQueries.getAlLEvents(where);
    }

    public void updateEvent(Event event) {
        eventQueries.updateEvent(event);
    }

    public void closeEvent(Event event) {
        eventQueries.closeEvent(event);
    }

    public void archiveEvent(Event event) {
        eventQueries.archiveEvent(event);
    }

    public void markNotified(Event obj) {
        eventQueries.markNotified(obj);
    }

    public List<Users> ticketsSolvedByUser(String filter) {
        return reportQueries.ticketsSolvedByUser(filter);
    }

    public List<ClientProperty> emailsPerClient(String filter) {
        return reportQueries.emailsPerClient(filter);
    }

    public void insertDocument(Document document) throws Exception {
        documentQueries.insertDocument(document);
    }

    public List<Document> getAllDocuments() {
        return documentQueries.getAllDocuments();
    }

    public boolean deleteDocument(Document document) {
        return documentQueries.deleteDocument(document);
    }

    public String[] getAllEmailIDs(String where) {
        String query = "SELECT DISTINCT EM_NAME FROM EMAIL_LIST ";

        if (where == null)
            query = query + " ORDER BY EM_NAME";
        else
            query = query + where;

        try {
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

    public List<EmailProperty> readSolvedEmailsByUsers(Users newValue, String reportFilter) {
        return emailQueries.readSolvedEmailsByUsers(newValue,reportFilter);
    }

    public List<EmailProperty> average_Calculate() {
        return emailQueries.average_Calculate();
    }

    public int getManualTicketNo(Email em) {
        return emailQueries.getManualTicketNo(em);
    }


    public void updateResendEmail(Email email) {
         emailQueries.updateResendEmail(email);
    }

    public Email readSearchEmail(int emailNo) {
        return emailQueries.readSearchEmail(emailNo, userQueries);
    }

    public void insertBlackListKeywords(String[] array) {
        domainQueries.insertBlackListKeywords(array);
    }

    public List<String> getBlackListKeyword() {
        return domainQueries.getBlackListKeyword();
    }

    public void updateReplacementKeyword(String saveKeyword) {
        settingsQueries.updateReplacementKeyword(saveKeyword);
    }

    public String getReplacementKeyword() {
        return settingsQueries.getReplacementKeyword();
    }

    public void removeKeyword(String selectedItem) {
        settingsQueries.removeKeyword(selectedItem);
    }

    public Email getParticularEmail(Email email) {
        return emailQueries.getParticularEmail(email);
    }

    public void addNote(String note, Email email) {
        noteQueries.addNewNote(note, email);
    }

    public void deleteNote(Note note, Email email) {
        noteQueries.deleteNote(note, email);
    }

    public void updateNote(Note note, Email email) {
        noteQueries.updateNote(note, email);
    }
}
