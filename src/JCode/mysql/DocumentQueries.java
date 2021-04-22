package JCode.mysql;

import JCode.FileHelper;
import objects.Document;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DocumentQueries {

    //my-sql: set global max_allowed_packet=1024*1024*15;
    //my-sql: change my.ini file innodb_log_file_size = 256M

    private Connection static_con;

    public DocumentQueries(Connection static_con) {
        this.static_con = static_con;
    }

    public void insertDocument(Document document) throws Exception {

        String query = "INSERT INTO DOCUMENT_STORE(DCODE, DNAME, DFILE)" +
                " SELECT IFNULL(MAX(DCODE),0)+1,?,? FROM DOCUMENT_STORE";

        //saving the image
//        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setString(1, document.getName());
            statement.setBlob(2, new FileInputStream(document.getFile()));
            statement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    public List<Document> getAllDocuments() {

        FileHelper.createDirectoryIfDoesNotExist(FileHelper.FADD_DOCS);

        String query = "SELECT DCODE, DNAME, DFILE FROM DOCUMENT_STORE WHERE 1";

        List<Document> allDocs = new ArrayList<>();

        try {
            // Connection con = getConnection();
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            while (set.next()) {
                // write binary stream into file
                File file = new File(FileHelper.FADD_DOCS + "\\" + set.getString("DNAME"));
                file.createNewFile();
                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.getLocalizedMessage();
//                    e.printStackTrace();
                }

                InputStream input = set.getBinaryStream("DFILE");
                byte[] buffer = new byte[16384];
                while (input.read(buffer) > 0) {
                    output.write(buffer);
                }

                allDocs.add(new Document(set.getInt("DCODE"), file));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }

        return allDocs;
    }

    public boolean deleteDocument(Document document) {
        String query = "DELETE FROM DOCUMENT_STORE WHERE DCODE = ?";


        //saving the image
        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, document.getCode());
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }

        return false;
    }

}
