package JCode.mysql;

import JCode.CommonTasks;
import JCode.FileHelper;
import objects.ProductModule;
import objects.ProductProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductQueries {
    
    private Connection static_con;
    private FileHelper fHelper;
    private NoteQueries noteQueries;
    
    public ProductQueries(Connection static_con, FileHelper fHelper, NoteQueries noteQueries) {
        this.static_con = static_con;
        this.fHelper = fHelper;
        this.noteQueries = noteQueries;
    }
    
    public void insertProduct(ProductProperty product) {
        
        String query = "INSERT INTO PRODUCT_STORE(PS_ID, PS_NAME ,PS_PRICE ,PS_DESC ,PS_STATUS ,PS_TYPE , " +
                "PS_STARTED ,PS_PRIORITY , CREATEDON, CREATEDBY) " +
                " SELECT IFNULL(max(PS_ID),0)+1,?,?,?,?,?,?,?,?,? from PRODUCT_STORE";
        
        // Connection con = getConnection();
        PreparedStatement statement = null;
        
        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, product.getName());
            statement.setInt(2, product.getPrice());
            statement.setString(3, product.getDesc());
            statement.setInt(4, product.getStatus());
            statement.setInt(5, product.getType());
            statement.setString(6, product.getStartedtimeStmp());
            statement.setInt(7, product.getPriority());
            statement.setString(8, CommonTasks.getCurrentTimeStamp());
            statement.setInt(9, fHelper.ReadUserDetails().getUCODE());
            
            statement.executeUpdate();
            
            for (ProductModule module : product.getProductModules()) {
                System.out.println(module);
                insertProductModule(module);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    public void updateProduct(ProductProperty product) {
        
        String query = "UPDATE PRODUCT_STORE SET PS_NAME=?,PS_PRICE=? ,PS_DESC=? ,PS_STATUS=? ,PS_TYPE=? , " +
                " PS_STARTED=? ,PS_PRIORITY=? , CREATEDON=?, CREATEDBY=?  " +
                " WHERE PS_ID = ? ";
        
        // Connection con = getConnection();
        PreparedStatement statement = null;
        
        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, product.getName());
            statement.setInt(2, product.getPrice());
            statement.setString(3, product.getDesc());
            statement.setInt(4, product.getStatus());
            statement.setInt(5, product.getType());
            statement.setString(6, product.getStartedtimeStmp());
            statement.setInt(7, product.getPriority());
            statement.setString(8, CommonTasks.getCurrentTimeStamp());
            statement.setInt(9, fHelper.ReadUserDetails().getUCODE());
            statement.setInt(10, product.getCode());
            
            statement.executeUpdate();
            
            deleteAllProductModules(product.getCode());
            for (ProductModule module : product.getProductModules()) {
                System.out.println(module);
                insertProductModule(module);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    public List<ProductProperty> getAllProducts(String where) {
        String query = "SELECT PS_ID, PS_NAME ,PS_PRICE ,PS_DESC ,PS_STATUS ,PS_TYPE ,PS_STARTED ,PS_PRIORITY ,CREATEDON, CREATEDBY FROM PRODUCT_STORE WHERE 1 ";
        
        if (where == null) {
//            query = query + " AND FREZE = 0";
        } else {
            query = query + " AND " + where;
        }
        
        List<ProductProperty> allProducts = new ArrayList<>();
        
        try {
            System.out.println(query);
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            while (set.next()) {
                ProductProperty product = new ProductProperty();
                product.setCode(set.getInt("PS_ID"));
                product.setName(set.getString("PS_NAME"));
                product.setPrice(set.getInt("PS_PRICE"));
                product.setDesc(set.getString("PS_DESC"));
                product.setStatus(set.getInt("PS_STATUS"));
                product.setType(set.getInt("PS_TYPE"));
                product.setStartedtimeStmp(set.getString("PS_STARTED"));
                product.setPriority(set.getInt("PS_PRIORITY"));
                product.setCreatedOn(set.getString("CREATEDON"));
                product.setCreatedBy(set.getInt("CREATEDBY"));
                
                product.setProductModules(getAllProductModules(product.getCode()));
                
                allProducts.add(product);
            }
            
            // doRelease(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return allProducts;
    }
    
    public ProductProperty getParticularProduct(ProductProperty where) {
        String query = "SELECT PS_ID, PS_NAME ,PS_PRICE ,PS_DESC ,PS_STATUS ,PS_TYPE ,PS_STARTED ,PS_PRIORITY ,CREATEDON, CREATEDBY FROM PRODUCT_STORE WHERE 1 " +
                " AND PS_ID = ? ";
        
        try {
//            System.out.println(query);
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, where.getCode());
            ResultSet set = statement.executeQuery();
            //-------------Creating Email-------------
            while (set.next()) {
                ProductProperty product = new ProductProperty();
                product.setCode(set.getInt("PS_ID"));
                product.setName(set.getString("PS_NAME"));
                product.setPrice(set.getInt("PS_PRICE"));
                product.setDesc(set.getString("PS_DESC"));
                product.setStatus(set.getInt("PS_STATUS"));
                product.setType(set.getInt("PS_TYPE"));
                product.setStartedtimeStmp(set.getString("PS_STARTED"));
                product.setPriority(set.getInt("PS_PRIORITY"));
                product.setCreatedOn(set.getString("CREATEDON"));
                product.setCreatedBy(set.getInt("CREATEDBY"));
                
                product.setProductModules(getAllProductModules(product.getCode()));
                product.setNotes(noteQueries.getNotes(product));
                
                return product;
            }
            
            // doRelease(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    public void insertProductModule(ProductModule productModule) {
        String query = "INSERT INTO PRODUCT_MODULE(PM_ID, PM_NAME ,PM_DESC, PS_ID, CREATEDON, CREATEDBY) " +
                " SELECT IFNULL(max(PM_ID),0)+1,?,?,?,?,? from PRODUCT_MODULE WHERE PS_ID =?";
        
        // Connection con = getConnection();
        PreparedStatement statement = null;
        
        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, productModule.getName());
            statement.setString(2, productModule.getDesc());
            statement.setInt(3, productModule.getProductCode());
            statement.setString(4, CommonTasks.getCurrentTimeStamp());
            statement.setInt(5, fHelper.ReadUserDetails().getUCODE());
            statement.setInt(6, productModule.getProductCode());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateProductModule(ProductModule productModule) {
        String query = "UPDATE PRODUCT_MODULE SET PM_ID=? ,PM_NAME=? ,PM_DESC=? ,PS_ID=? ,CREATEDON=? ,CREATEDBY=? " +
                " WHERE PM_ID=? " +
                " AND PS_ID=? ";
        
        // Connection con = getConnection();
        PreparedStatement statement = null;
        
        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, productModule.getName());
            statement.setString(2, productModule.getDesc());
            statement.setInt(3, productModule.getProductCode());
            statement.setString(4, CommonTasks.getCurrentTimeStamp());
            statement.setInt(5, fHelper.ReadUserDetails().getUCODE());
            statement.setInt(6, productModule.getProductCode());
            
            statement.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    public List<ProductModule> getAllProductModules(int productCode) {
        String query = " SELECT PM_ID, PM_NAME, PM_DESC, PS_ID, CREATEDBY, CREATEDON  " +
                " FROM PRODUCT_MODULE " +
                " WHERE PS_ID = ? ";
        
        ArrayList<ProductModule> modules = new ArrayList<>();
        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, productCode);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                ProductModule module = new ProductModule();
                module.setCode(set.getInt("PM_ID"));
                module.setName(set.getString("PM_NAME"));
                module.setDesc(set.getString("PM_DESC"));
                module.setProductCode(set.getInt("PS_ID"));
                
                modules.add(module);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return modules;
        
    }
    
    public ProductProperty getProductModuleStates(ProductProperty product) {
        String query = " SELECT LOCKEDTIME, UCODE " +
                " FROM MODULE_LOCKING " +
                " WHERE PM_ID = ? " +
                " AND PS_ID = ? " +
                " AND UNLOCKEDTIME is NULL ";
        
         for (ProductModule module : product.getProductModules()) {
            try {
                PreparedStatement statement = static_con.prepareStatement(query);
                statement.setInt(1, module.getCode());
                statement.setInt(2, module.getProductCode());
                
                ResultSet set = statement.executeQuery();
                if (!set.isBeforeFirst())          // 0 is for unlocked 1 is for locked
                    module.setState(0);
                else {
                    while (set.next()) {
                        if (set.getInt("UCODE") == fHelper.ReadUserDetails().getUCODE())
                            module.setState(1);
                        else
                            module.setState(2);
                    }
                }
                
                set.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return product;
    }
    
    public ArrayList<ProductModule> getLockedModules() {
        String query = "SELECT ML.PM_ID, ML.PS_ID, LOCKEDTIME, FNAME, PM_NAME, PS_NAME " +
                "FROM MODULE_LOCKING AS ML, PRODUCT_MODULE AS PM, PRODUCT_STORE AS PS, USERS AS U " +
                "WHERE UNLOCKEDTIME IS NULL " +
                "AND ML.PS_ID = PM.PS_ID " +
                "AND ML.PM_ID = PM.PM_ID " +
                "AND ML.PS_ID = PS.PS_ID " +
                "AND ML.UCODE = U.UCODE ";
        
        ArrayList<ProductModule> modules = new ArrayList<>();
        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                ProductModule module = new ProductModule();
                module.setCode(set.getInt("PM_ID"));
                module.setName(set.getString("PM_NAME"));
                module.setProductCode(set.getInt("PS_ID"));
                module.setProductName(set.getString("PS_NAME"));
                module.setLockedByName(set.getString("FNAME"));
                module.setLockedTime(set.getString("LOCKEDTIME"));
                
                modules.add(module);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return modules;
        
    }
    
    public void deleteAllProductModules(int product) {
        String query = "DELETE FROM PRODUCT_MODULE WHERE PS_ID = ?";
        
        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, product);
            
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public int getNewProductCode() {
        String query = "SELECT IFNULL(max(PS_ID),0)+1 AS PS_ID FROM PRODUCT_STORE";
        
        PreparedStatement statement = null;
        try {
            statement = static_con.prepareStatement(query);
            
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                return set.getInt("PS_ID");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    private int getModuleState(ProductModule module) {
        String query = " SELECT LOCKEDTIME, UCODE " +
                " FROM MODULE_LOCKING " +
                " WHERE PM_ID = ? " +
                " AND PS_ID = ? " +
                " AND UNLOCKEDTIME is NULL ";
        
        PreparedStatement statement = null;
        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, module.getCode());
            statement.setInt(2, module.getProductCode());
            
            ResultSet set = statement.executeQuery();
            if (!set.isBeforeFirst())          // 0 is for unlocked 1 is for locked 2 is for locked but not by you
                return 0;
            else {
                while (set.next()) {
                    if (set.getInt("UCODE") == fHelper.ReadUserDetails().getUCODE())
                        return 1;
                    else
                        return 2;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 1;
    }
    
    public boolean lockModule(ProductModule module) {
        
        if (getModuleState(module) != 0)
            return false;
        
        String query = "INSERT INTO MODULE_LOCKING(PM_ID, UCODE, LOCKEDTIME, PS_ID) " +
                " VALUES(?,?,?,?)";
        
        PreparedStatement statement = null;
        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, module.getCode());
            statement.setInt(2, fHelper.ReadUserDetails().getUCODE());
            statement.setString(3, CommonTasks.getCurrentTimeStamp());
            statement.setInt(4, module.getProductCode());
            
            statement.executeUpdate();
            
            statement.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return true;
    }
    
    public void unlockModule(ProductModule module, String desc) {
        String query = " UPDATE MODULE_LOCKING SET UNLOCKEDTIME = ?, DESCRIPTION = ? " +
                " WHERE LOCKEDTIME IS NOT NULL " +
                " AND UCODE = ? " +
                " AND PM_ID = ? " +
                " AND PS_ID = ? ";
        
        PreparedStatement statement = null;
        try {
            statement = static_con.prepareStatement(query);
            statement.setString(1, CommonTasks.getCurrentTimeStamp());
            statement.setString(2, desc);
            statement.setInt(3, fHelper.ReadUserDetails().getUCODE());
            statement.setInt(4, module.getCode());
            statement.setInt(5, module.getProductCode());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
