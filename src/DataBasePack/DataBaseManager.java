package DataBasePack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataBaseManager {

	private static DataBaseManager instanceDB;
	public static DataBaseManager getDB() {
		if (instanceDB == null) {
			instanceDB = new DataBaseManager();
		}
		return instanceDB;
	}
	
	private Connection dbConnection = null;
	private final String connectionString = "jdbc:sqlite:AksolDataBase.db";
	private Statement state = null;	
	private String connectionStatus;
	
	public String getConnectionStatus() {
		return connectionStatus;
	}
			
	private DataBaseManager() {
		try {
			 connect();
			 connectionStatus = "connected";
		}
		catch(SQLException exp) {
			connectionStatus = "SQL exception occured while connecting";
		}
		catch(ClassNotFoundException exp) {
			connectionStatus = "Class org.sqlite.JDBC was not found";
		}
	}
	
	private int connect() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		dbConnection = DriverManager.getConnection(connectionString);
		state = dbConnection.createStatement();
		return 0;
	}
	
	public void disConnect() throws SQLException {
		dbConnection.close();
	}
	
//TCL	
	public int BeginTransaction() {
		try {
			dbConnection.setAutoCommit(false);
			return 0;
		}
		catch(SQLException exp) {
			return -1;
		}
	}
	
	public int RollBack() {
		try {
			dbConnection.rollback();
			dbConnection.setAutoCommit(true);
			return 0;
		}
		catch(SQLException exp) {
			return -1;
		}
	}
	
	public int Commit() {
		try {
			dbConnection.commit();
			dbConnection.setAutoCommit(true);
			return 0;
		}
		catch(SQLException exp) {
			return -1;
		}
	}
	
	public boolean getAutoCommit() throws SQLException {
		return dbConnection.getAutoCommit();
	}
	
	//����� ������ ������ � �� ������� ��������� SQl �������
	//�������� ������� ��� ��������� result set; �������� ��� Update, Insert, etc.
    public void sqlQueryUpdate(String sqlString) throws SQLException {
    	state.executeUpdate(sqlString);
    }
    
    //�������� ������� Select count(*)
    public int sqlQueryCount(String sqlString) throws SQLException {
		ResultSet resSet = state.executeQuery(sqlString);
		resSet.next();
		int result = resSet.getInt(1);	
		return result;
    } 
    
    //�������� ������� � ���������� result set � ���� ������� String - �������� ��� ������� Select
    public void sqlQueryInteger(String sqlString, String fieldName, List<Integer> arrayResults) throws SQLException{
    	ResultSet rSet = this.state.executeQuery(sqlString);
    	while(rSet.next()) {
    		arrayResults.add(rSet.getInt(fieldName));
    	}
    }
    
    public void sqlQueryDouble(String sqlString, String fieldName, List<Double> arrayResults) throws SQLException{
    	ResultSet rSet = this.state.executeQuery(sqlString);
    	while(rSet.next()) {
    		arrayResults.add(rSet.getDouble(fieldName));
    	}
    }
    
    public void sqlQueryString(String sqlString, String fieldName, List<String> arrayResults) throws SQLException{
    	ResultSet rSet = this.state.executeQuery(sqlString);
    	while(rSet.next()) {
    		arrayResults.add(rSet.getString(fieldName));
    	}
    }
    
    public void sqlQueryString(String sqlString, List<String> fieldName, List<List<String>> arrayResults) throws SQLException{
    	ResultSet rSet = this.state.executeQuery(sqlString);
    	arrayResults.clear();
    	while(rSet.next()) {
    	List<String> tempArray = new ArrayList<String>();
    		for (int i=0; i<fieldName.size(); i++) {
    			tempArray.add(rSet.getString(fieldName.get(i)));
    		}
    		arrayResults.add(tempArray);
    	}
    }
    
    public void sqlQueryDouble(String sqlString, List<String> fieldName, List<List<Double>> arrayResults) throws SQLException{
    	ResultSet rSet = this.state.executeQuery(sqlString);
    	arrayResults.clear();
    	while(rSet.next()) {
    		List<Double> tempArray = new ArrayList<Double>();
    		for (int i=0; i<fieldName.size(); i++) {
    			tempArray.add((double) rSet.getFloat(fieldName.get(i)));
    		}
    		arrayResults.add(tempArray);
    	}
    }
      
    public void sqlQueryMapOfDouble(String sqlString, List<String> paramsNames, Map<String, Map<Double, Double>> mapResults) throws SQLException{
    	ResultSet rSet = this.state.executeQuery(sqlString);
    	int countOfParams = paramsNames.size();
    	List<Double[]> values = new ArrayList<Double[]>();  	
    	while(rSet.next()) {
    		Double[] params = new Double[countOfParams];
    		for (int i=0; i < countOfParams; i++) {
    			params[i] = rSet.getDouble(i+1);
    		}
    		values.add(params);
    	}
    	int freqCount = values.size();
    	for (int i=1; i < paramsNames.size(); i++) {
    		Map<Double, Double> tempMap = new LinkedHashMap<Double, Double>();
    		for (int j=0; j < freqCount; j++) {
    			double freq = values.get(j)[0];
    			double value = values.get(j)[i];
    			tempMap.put(freq, value);
    		}
    		String key = paramsNames.get(i);
    		mapResults.put(key, tempMap);
    	}
    }
    
    public void zeroingAllElements() throws SQLException {
    	String sqlString = "UPDATE [Elements] SET lastVerificationId=0";
    	state.executeUpdate(sqlString);
    }
    
}
