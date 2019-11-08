package DataBasePack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

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
			sqlQueryUpdate("BEGIN TRANSACTION");
			return 0;
		}
		catch(SQLException exp) {
			return -1;
		}
	}
	
	public int RollBack() {
		try {
			sqlQueryUpdate("ROLLBACK");
			return 0;
		}
		catch(SQLException exp) {
			return -1;
		}
	}
	
	public int Commit() {
		try {
			sqlQueryUpdate("COMMIT");
			return 0;
		}
		catch(SQLException exp) {
			return -1;
		}
	}
	
	//Общие методы работы с БД Требуют написания SQl запроса
	//отправка запроса без получения result set; подходит для Update, Insert, etc.
    public void sqlQueryUpdate(String sqlString) throws SQLException {
    	state.executeUpdate(sqlString);
    }
    
    //отправка запроса Select count(*)
    public int sqlQueryCount(String sqlString) throws SQLException {
		ResultSet resSet = state.executeQuery(sqlString);
		resSet.next();
		int result = resSet.getInt(1);	
		return result;
    } 
    
    //отправка запроса с получением result set в виде массива String - подходит для запроса Select
    public void sqlQueryInteger(String sqlString, String fieldName, ArrayList<Integer> arrayResults) throws SQLException{
    	ResultSet rSet = this.state.executeQuery(sqlString);
    	while(rSet.next()) {
    		arrayResults.add(rSet.getInt(fieldName));
    	}
    }
    
    public void sqlQueryDouble(String sqlString, String fieldName, ArrayList<Double> arrayResults) throws SQLException{
    	ResultSet rSet = this.state.executeQuery(sqlString);
    	while(rSet.next()) {
    		arrayResults.add(rSet.getDouble(fieldName));
    	}
    }
    
    public void sqlQueryString(String sqlString, String fieldName, Collection<String> arrayResults) throws SQLException{
    	ResultSet rSet = this.state.executeQuery(sqlString);
    	while(rSet.next()) {
    		arrayResults.add(rSet.getString(fieldName));
    	}
    }
    
    public void sqlQueryString(String sqlString, ArrayList<String> fieldName, ArrayList<ArrayList<String>> arrayResults) throws SQLException{
    	ResultSet rSet = this.state.executeQuery(sqlString);
    	arrayResults.clear();
    	while(rSet.next()) {
    		ArrayList<String> tempArray = new ArrayList<String>();
    		for (int i=0; i<fieldName.size(); i++) {
    			tempArray.add(rSet.getString(fieldName.get(i)));
    		}
    		arrayResults.add(tempArray);
    	}
    }
    
    public void sqlQueryDouble(String sqlString, ArrayList<String> fieldName, ArrayList<ArrayList<Double>> arrayResults) throws SQLException{
    	ResultSet rSet = this.state.executeQuery(sqlString);
    	arrayResults.clear();
    	while(rSet.next()) {
    		ArrayList<Double> tempArray = new ArrayList<Double>();
    		for (int i=0; i<fieldName.size(); i++) {
    			tempArray.add((double) rSet.getFloat(fieldName.get(i)));
    		}
    		arrayResults.add(tempArray);
    	}
    }
      
    public void sqlQueryMapOfDouble(String sqlString, ArrayList<String> paramsNames, Map<String, Map<Double, Double>> mapResults) throws SQLException{
    	ResultSet rSet = this.state.executeQuery(sqlString);
    	int countOfParams = paramsNames.size();
    	ArrayList<Double[]> values = new ArrayList<Double[]>();  	
    	while(rSet.next()) {
    		Double[] params = new Double[countOfParams];
    		for (int i=0; i < countOfParams; i++) {
    			params[i] = rSet.getDouble(i+1);
    		}
    		values.add(params);
    	}
    	int freqCount = values.size();
    	for (int i=1; i < paramsNames.size(); i++) {
    		LinkedHashMap<Double, Double> tempMap = new LinkedHashMap<Double, Double>();
    		for (int j=0; j < freqCount; j++) {
    			double freq = values.get(j)[0];
    			double value = values.get(j)[i];
    			tempMap.put(freq, value);
    		}
    		String key = paramsNames.get(i);
    		mapResults.put(key, tempMap);
    	}
    }
    
}
