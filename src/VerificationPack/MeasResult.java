package VerificationPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import DevicePack.Element;
import DevicePack.Includable;
import javafx.collections.ObservableList;

public class MeasResult implements Includable<Element>, dbStorable{
		
	protected String keys[] = {"m_S11", "p_S11", "m_S12", "p_S12", "m_S21", "p_S21", "m_S22", "p_S22"};
	
	MeasResult(){};
	
	MeasResult(Element ownerElement, ArrayList<Double> Freqs, ArrayList<ArrayList<Double>> Values){
		this.myElement = ownerElement;	
		freqs = new ArrayList<Double>();
		values = new HashMap<String, HashMap<Double, Double>>();
		this.countOfParams = Values.size();
		this.countOfFreq = Freqs.size();
		
		for (Double currentFr : Freqs) {
			this.freqs.add(currentFr);
		}
		
		for (int i=0; i< this.countOfParams; i++) {
			HashMap<Double, Double> tVals = new HashMap<Double, Double>();
			for (int j=0; j<Freqs.size(); j++) {
				tVals.put(Freqs.get(j), Values.get(i).get(j));
			}
			values.put(keys[i], tVals);
		}
		
	}
	
	protected String periodType;
	public String getPeriodType() {return periodType;}
	
	protected int countOfFreq;
	public int getCountOfFreq() {return this.countOfFreq;}
	
	protected int countOfParams;
	public int getCountOfParams() {return this.countOfParams;}
	
	public HashMap<String, HashMap<Double, Double>> values;
	public ArrayList<Double> freqs;
	
	protected  ObservableList<String> paramsNames;
	
//Includable<Element>
	protected Element myElement;
	@Override
	public Element getMyOwner() {return myElement;}
	
//dbStorable
	@Override
	public void saveInDB() throws SQLException {
		String listOfVerificationsTable = this.myElement.getListOfVerificationsTable();
		String dateOfVerification = "";
		String resultsTableName = "Results of verification " + listOfVerificationsTable.substring(listOfVerificationsTable.indexOf("Verifications of ")) + " at " + dateOfVerification;
		String sqlQuery = "INSERT INTO " + listOfVerificationsTable + " (dateOfVerification, resultsTableName) values ('"+ dateOfVerification +"','"+ listOfVerificationsTable +"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		sqlQuery = "CREATE TABLE " + resultsTableName + " (id INTEGER PRIMARY KEY AUTOINCREMENT, freq VARCHAR(20), ";
		for (int i=0; i<this.countOfParams; i++) {
			sqlQuery += (keys[i] + " VARCHAR(20)");
			if (i != this.countOfParams - 1) sqlQuery += ", ";
		}
		sqlQuery += ")";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		
		//Заполняем таблицу resultsTableName с результатами измерений
		for (int i=0; i<this.countOfFreq; i++) {	
			
			sqlQuery = "INSERT INTO " + resultsTableName + " (";
			
			for (int j=0; j<this.countOfParams; j++) {
				sqlQuery += (keys[j]);
				if (j != this.countOfParams - 1) sqlQuery += ", ";
			}
			
			sqlQuery += " values (";
			
			for (int j = 0; j < this.countOfParams; j++) {
				sqlQuery += ("'"+values.get(keys[j]).get(freqs.get(i)).toString()+"'");
				if (j != this.countOfParams - 1) sqlQuery += ", ";
			}
			
			sqlQuery += ")";
			
			DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		}
	}
	@Override
	public void deleteFromDB() throws SQLException {
		// TODO Auto-generated method stub
		
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
