package VerificationPack;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import DevicePack.Element;
import DevicePack.Includable;
import FileManagePack.ResultReaderManager;
import NewElementPack.NewElementController;
import javafx.collections.ObservableList;

public class MeasResult implements Includable<Element>, dbStorable{
	
	private static String keys[] = {"MODULE_S11", "ERROR_MODULE_S11", "PHASE_S11", "ERROR_PHASE_S11",
			 "MODULE_S12", "ERROR_MODULE_S12", "PHASE_S12", "ERROR_PHASE_S12",
			 "MODULE_S21", "ERROR_MODULE_S21", "PHASE_S21", "ERROR_PHASE_S21", 
			 "MODULE_S22", "ERROR_MODULE_S22", "PHASE_S22", "ERROR_PHASE_S22"};
	
	protected int countOfFreq;
	protected int countOfParams;
	
	//Date
	protected DateFormat dateFormat;
	protected Date dateOfMeas;
	protected String datePattern = "dd/MM/yyyy HH:mm:ss";
	
	public int getCountOfFreq() {return this.countOfFreq;}
	public int getCountOfParams() {return this.countOfParams;}
	public Date getDateOfMeas() {return this.dateOfMeas;}
	public String getDateOfMeasByString() {
		return new SimpleDateFormat(datePattern).format(this.dateOfMeas);
	}
	public Map<String, Map<Double, Double>> values;
	public HashMap<String, HashMap<Double, String>> suitabilityDecision;
	public ArrayList<Double> freqs;
	protected  ObservableList<String> paramsNames;
	private String tableName;
	
//Constructors
//FILE
	public MeasResult(String fileWithResults, int resultNumber, Element ownerElement) throws IOException{
		
		myElement = ownerElement;
		freqs = new ArrayList<Double>();
		values = new LinkedHashMap<String, Map<Double, Double>>();
		suitabilityDecision = new HashMap<String, HashMap<Double, String>>();
		
		ResultReaderManager resReader = new ResultReaderManager(fileWithResults);			
		resReader.readResult(resultNumber, freqs, values);
				
		countOfParams = values.size();
		countOfFreq = freqs.size();
		dateOfMeas = Calendar.getInstance().getTime();
	}
	
//GUI
	public MeasResult(NewElementController elCtrl, Element ownerElement){		
		myElement = ownerElement;	
		freqs = new ArrayList<Double>();
		values = new HashMap<String, Map<Double, Double>>();
		freqs = elCtrl.getFreqsValues();
		values = elCtrl.getNominalValues();
		countOfParams = values.size();
		countOfFreq = freqs.size();
		dateOfMeas = Calendar.getInstance().getTime();
		dateFormat = new SimpleDateFormat(datePattern);
		String strDateOfMeas = dateFormat.format(dateOfMeas);
		tableName = "Результаты поверки для " +
				this.myElement.getMyOwner().getName() + " " + this.myElement.getMyOwner().getType() + " " + this.myElement.getMyOwner().getSerialNumber() + " " + this.myElement.getType() + " " + this.myElement.getSerialNumber() +
				" проведенной " + strDateOfMeas;		
	}	
//DB
	public MeasResult(Element ownerElement, int index) throws SQLException {		
		values = new LinkedHashMap<String, Map<Double, Double>>();
		freqs = new ArrayList<Double>();				
		myElement = ownerElement;		
		ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
		ArrayList<String> fieldsNames = new ArrayList<String>();		
		String listOfVerificationsTable = this.myElement.getListOfVerificationsTable();
		String sqlQuery = "SELECT dateOfVerification, resultsTableName  FROM [" + listOfVerificationsTable + "] WHERE id='" + index + "'";
		fieldsNames.add("dateOfVerification");
		fieldsNames.add("resultsTableName");
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldsNames, results);
		dateFormat = new SimpleDateFormat(datePattern);
		String strDate = results.get(0).get(0);
		try {
			this.dateOfMeas = dateFormat.parse(strDate);
		}
		catch(ParseException pExp) {
			this.dateOfMeas = Calendar.getInstance().getTime();
		}		
		tableName =  results.get(0).get(1);
		sqlQuery = "SELECT freq FROM [" + tableName + "]";
		DataBaseManager.getDB().sqlQueryDouble(sqlQuery, "freq", this.freqs);
		this.countOfFreq = this.freqs.size();
		for (String key : keys) {
			try {
				sqlQuery = "SELECT " + key + " FROM [" + tableName + "]";
				ArrayList<Double> arrayResults = new ArrayList<Double>();
				DataBaseManager.getDB().sqlQueryDouble(sqlQuery, key, arrayResults);
				
				HashMap<Double, Double> tempHM = new HashMap<Double, Double>();
				for (int i=0; i<this.countOfFreq; i++) {
					tempHM.put(freqs.get(i), arrayResults.get(i));
				}
				this.values.put(key, tempHM);
			}
			catch(Exception exp) {
				continue;
			}
		}		
	}
		
//Includable<Element>
	protected Element myElement;
	@Override
	public Element getMyOwner() {
		return myElement;
	}
	@Override
	public void onAdding(Element Owner) {
		this.myElement = Owner;	
	}
//dbStorable
	@Override
	public void saveInDB() throws SQLException {
		ArrayList<String> currentKeys = new ArrayList<String>();
		for (String k: keys) {
			try {
				int size = this.values.get(k).size();
				if (size > 0) {
					currentKeys.add(k);
				}
			}
			catch(Exception exp) {
				//
			}
		}								
		String listOfVerificationsTable = this.myElement.getListOfVerificationsTable();
		String strDateOfMeas = dateFormat.format(dateOfMeas);
		String sqlQuery = "INSERT INTO [" + listOfVerificationsTable + "] (dateOfVerification, resultsTableName) values ('"+ strDateOfMeas +"','"+ tableName +"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		sqlQuery = "CREATE TABLE [" + tableName + "] (id INTEGER PRIMARY KEY AUTOINCREMENT, freq VARCHAR(20), ";
		for (int i = 0; i < currentKeys.size(); i++) {
			sqlQuery += (currentKeys.get(i) + " VARCHAR(20)");
			if (i != currentKeys.size() - 1) sqlQuery += ", ";
		}
		sqlQuery += ")";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		for (int i=0; i<this.countOfFreq; i++) {				
			sqlQuery = "INSERT INTO [" + tableName + "] (freq, ";			
			for (int j = 0; j < currentKeys.size(); j++) {
				sqlQuery += (currentKeys.get(j));
				if (j != currentKeys.size() - 1) sqlQuery += ", ";
			}
			
			sqlQuery += ") values ('"+freqs.get(i)+"', ";			
			for (int j = 0; j < currentKeys.size(); j++) {
				sqlQuery += ("'"+values.get(currentKeys.get(j)).get(freqs.get(i)).toString()+"'");
				if (j != currentKeys.size() - 1) sqlQuery += ", ";
			}			
			sqlQuery += ")";			
			DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		}
	}
	
	@Override
	public void deleteFromDB() throws SQLException {
		DateFormat df = new SimpleDateFormat(datePattern);
		String when = df.format(this.dateOfMeas);
		String listOfVerificationsTable = myElement.getListOfVerificationsTable();
		String what = myElement.getMyOwner().getName() + " " + myElement.getMyOwner().getType() + " " + myElement.getMyOwner().getSerialNumber() + " " + myElement.getType() + " " + myElement.getSerialNumber();
		String resultsTableName = "Результаты поверки для " + what + " проведенной  " + when;		
		String sqlQuery = "DELETE FROM ["+listOfVerificationsTable+"] WHERE resultsTableName='"+resultsTableName+"'";	
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);		
		sqlQuery = "DROP TABLE [" + resultsTableName + "]";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
	}
	
	@Override
	public void editInfoInDB(HashMap<String, String> editingValues) throws SQLException {
		// TODO Auto-generated method stub		
	}
	
	public void rewriteTableNames() throws SQLException {
		String strDateOfMeas = dateFormat.format(dateOfMeas);
		String newTableName = "Результаты поверки для " +
				myElement.getMyOwner().getName() + " " + myElement.getMyOwner().getType() + " " + myElement.getMyOwner().getSerialNumber() + " " + myElement.getType() + " " + myElement.getSerialNumber() +
				" проведенной " + strDateOfMeas;	
		String sqlQuery = "ALTER TABLE " + tableName + " RENAME TO " + newTableName;
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
	}
}