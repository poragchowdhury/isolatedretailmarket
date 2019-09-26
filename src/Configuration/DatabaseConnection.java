package Configuration;
import java.sql.*;

public class DatabaseConnection{
	private enum DB {na, id, qVal};
	private String databaseURL = "jdbc:mysql://localhost:";
	private String databasePort = "3306/";
	private String databaseName = ""; // Learning optimal policy
	private String databaseUser = "root";
	private String databasePassword = "PASSWORD";
	public double maxQValue = Configuration.MAX_TARIFF_PRICE * Configuration.POPULATION*1000;
	public double noQvalue = -1.0;
	private Connection con;
	private Statement stmt;
	private PreparedStatement prepStmt;
	private ResultSet rs;
    private DatabaseMetaData dbm = null;
    public DatabaseConnection(String dbName) {
    	if(dbName.length() > 0) {
    		databaseName = dbName;
    		System.out.println("Setting db name to "+ databaseName);
    	}
    	try{  
			Class.forName("com.mysql.jdbc.Driver");  
			this.con = DriverManager.getConnection(databaseURL+databasePort+databaseName,
					databaseUser,
					databasePassword);
			this.stmt = con.createStatement();
		}
		catch(Exception e)
		{ 
			System.out.println(e);
		}  
	}

    public void closeDBConnection() throws Exception{
    	con.close();
    }
    
    public void queryTable(String tableName) throws Exception{
    	Statement stmt=con.createStatement();  
    	rs = stmt.executeQuery("select * from " + tableName);  
		while(rs.next())  
			System.out.println("id, " + rs.getDouble(DB.id.ordinal()) + "qvalue, " + rs.getDouble(DB.qVal.ordinal()));    
    }
    
    public boolean tableExists(String tableName) throws Exception{
    	dbm = con.getMetaData();
        rs = dbm.getTables(null, null, tableName, new String[] {"TABLE"});
        return rs.next();
    }
    
    public void createTable(String tableName) throws Exception{
    	String createTable = "CREATE TABLE IF NOT EXISTS " + tableName + 
				" (id INT NOT NULL AUTO_INCREMENT, qvalue DOUBLE NOT NULL, PRIMARY KEY ( id ));";
    	stmt.executeUpdate(createTable);
    	System.out.println("Creating table " + tableName);
    }
    
    public void insertQValue(String tableName, double qValue) throws Exception{
    	String insertTable = "INSERT INTO " + tableName + " (qvalue) VALUES ("+ qValue +");";
    	stmt.executeUpdate(insertTable);
    	//System.out.println("Inserted qvalue: "+ qValue +" in " + tableName);
    }
    
    public double getQValue(String tableName) throws Exception {
    	double qValue = noQvalue;
    	String query = "SELECT * FROM " + tableName +" ORDER BY id DESC LIMIT 1;";
    	if(tableExists(tableName)) {
	    	prepStmt = con.prepareStatement(query);
	    	rs = prepStmt.executeQuery();
	    	if(rs.next()) {
	    		qValue = rs.getDouble(DB.qVal.ordinal());
	    		//System.out.println("id : " + rs.getInt(DB.id.ordinal()) + ", qValue : " + qValue);
	    	}
    	}
    	else {
    		createTable(tableName);
    		insertQValue(tableName, maxQValue);
    		qValue = maxQValue;
    	}
        return qValue;
    }
}