package ErrorParamsPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;

public class ErrorParams implements dbStorable  {

	public HashMap<String, ArrayList<Double>> value;
	public ErrorParams() throws SQLException {
		value = new HashMap<String, ArrayList<Double>>();
		ArrayList<Double> arrayResults1 = new ArrayList<Double>();
		ArrayList<Double> arrayResults2 = new ArrayList<Double>();
		ArrayList<Double> arrayResults3 = new ArrayList<Double>();
		ArrayList<Double> arrayResults5 = new ArrayList<Double>();
		
		String sqlQuery = "SELECT (tr1) FROM ErrorParams";
		DataBaseManager.getDB().sqlQueryDouble(sqlQuery, "Tr1", arrayResults1);
		this.value.put("1,6", arrayResults1);
		
		sqlQuery = "SELECT (tr2) FROM ErrorParams";
		DataBaseManager.getDB().sqlQueryDouble(sqlQuery, "Tr2", arrayResults2);
		this.value.put("2,4", arrayResults2);
		
		sqlQuery = "SELECT (tr3) FROM ErrorParams";
		DataBaseManager.getDB().sqlQueryDouble(sqlQuery, "Tr3", arrayResults3);
		this.value.put("3,6", arrayResults3);
		
		sqlQuery = "SELECT (tr5) FROM ErrorParams";
		DataBaseManager.getDB().sqlQueryDouble(sqlQuery, "Tr5", arrayResults5);
		this.value.put("5,2", arrayResults5);
	}
	
	public ErrorParams(ArrayList<Double> Tr1, ArrayList<Double> Tr2, ArrayList<Double> Tr3, ArrayList<Double> Tr5) {
		value = new HashMap<String, ArrayList<Double>>();
		value.put("1,6", Tr1);
		value.put("2,4", Tr2);
		value.put("3,6", Tr3);
		value.put("5.2", Tr5);
	}
	
	public double getA1(String tract) {
		return value.get(tract).get(0);
	}
	
	public double getB1(String tract) {
		return value.get(tract).get(1);
	}
	
	public double getC1(String tract) {
		return value.get(tract).get(2);
	}
	
	public double getD1(String tract) {
		return value.get(tract).get(3);
	}
	
	public double getE1(String tract) {
		return value.get(tract).get(4);
	}
	
	public double getA2(String tract) {
		return value.get(tract).get(5);
	}
	
	public double getB2(String tract) {
		return value.get(tract).get(6);
	}
	
	public double getC2(String tract) {
		return value.get(tract).get(7);
	}
	
	public double getD2(String tract) {
		return value.get(tract).get(8);
	}
	
	public int setTr1(ArrayList<Double> params) {
		this.value.get("1,6").clear();
		int i = 0;
		for (i = 0; i<params.size(); i++) {
			this.value.get("1,6").add(params.get(i));
		}
		return i;
	}
	
	public int setTr2(ArrayList<Double> params) {
		this.value.get("2,4").clear();
		int i = 0;
		for (i = 0; i<params.size(); i++) {
			this.value.get("2,4").add(params.get(i));
		}
		return i;
	}
	
	public int setTr3(ArrayList<Double> params) {
		this.value.get("3,6").clear();
		int i = 0;
		for (i = 0; i<params.size(); i++) {
			this.value.get("3,6").add(params.get(i));
		}
		return i;
	}
	
	public int setTr5(ArrayList<Double> params) {
		this.value.get("5,2").clear();
		int i = 0;
		for (i = 0; i<params.size(); i++) {
			this.value.get("5,2").add(params.get(i));
		}
		return i;
	}
	
	@Override
	public void saveInDB() throws SQLException {
		String sqlQuery = "DROP TABLE ErrorParams";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		sqlQuery = "CREATE TABLE ErrorParams (id INTEGER PRIMARY KEY AUTOINCREMENT, tr1 REAL, tr2 REAL, tr3 REAL, tr5 REAL)";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		for (int i=0; i<9; i++) {
			String v1 = value.get("1,6").get(i).toString();
			String v2 = value.get("2,4").get(i).toString();
			String v3 = value.get("3,6").get(i).toString();
			String v5 = value.get("5,2").get(i).toString();
			sqlQuery = "INSERT INTO ErrorParams (tr1, tr2, tr3, tr5) values ('"+v1+"','"+v2+"','"+v3+"','"+v5+"')";
			DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		}
	}

	@Override
	public void deleteFromDB() throws SQLException {
		String sqlQuery = "DROP TABLE ErrorParams";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);	
	}
	
	@Override
	public void editInfoInDB() throws SQLException {
		// TODO Auto-generated method stub		
	}

	@Override
	public void getData() {
		// TODO Auto-generated method stub		
	}
	
	

}
