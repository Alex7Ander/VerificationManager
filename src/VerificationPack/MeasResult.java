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
	protected int id;
	public int getId() {
		return this.id;
	}
	private static String keys[] = {"MODULE_S11", "ERROR_MODULE_S11", "PHASE_S11", "ERROR_PHASE_S11",
			 					    "MODULE_S12", "ERROR_MODULE_S12", "PHASE_S12", "ERROR_PHASE_S12",
			 					    "MODULE_S21", "ERROR_MODULE_S21", "PHASE_S21", "ERROR_PHASE_S21", 
			 					    "MODULE_S22", "ERROR_MODULE_S22", "PHASE_S22", "ERROR_PHASE_S22"};	
	protected int countOfFreq;
	protected int countOfParams;
	
	//Date
	protected DateFormat dateFormat;
	protected Date dateOfMeas;
	protected String dateOfMeasByString;
	protected String datePattern = "dd/MM/yyyy HH:mm:ss"; //"dd/MM/yyyy HH:mm:ss" YYYY-MM-DD HH:MM:ss.SSS
	
	public int getCountOfFreq() {return this.countOfFreq;}
	public int getCountOfParams() {return this.countOfParams;}
	public Date getDateOfMeas() {return this.dateOfMeas;}
	public String getDateOfMeasByString() {
		return dateOfMeasByString; 
	}
	public Map<String, Map<Double, Double>> values;
	public Map<String, Map<Double, String>> suitabilityDecision;
	public List<Double> freqs;
	protected  ObservableList<String> paramsNames;
	private String tableName;
	
//Constructors
//FILE
	public MeasResult(String fileWithResults, int resultNumber, Element ownerElement) throws IOException{		
		myElement = ownerElement;
		freqs = new ArrayList<Double>();
		values = new LinkedHashMap<String, Map<Double, Double>>();
		suitabilityDecision = new HashMap<String, Map<Double, String>>();		
		ResultReaderManager resReader = new ResultReaderManager(fileWithResults);			
		resReader.readResult(resultNumber, freqs, values);				
		countOfParams = values.size();
		countOfFreq = freqs.size();		
		dateOfMeas = Calendar.getInstance().getTime();
		dateFormat = new SimpleDateFormat(datePattern);
		dateOfMeasByString = dateFormat.format(dateOfMeas);
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
		dateOfMeasByString = dateFormat.format(dateOfMeas);
	}	
//DB
	public MeasResult(Element ownerElement, int index) throws SQLException {	
		System.out.println("\nПолучаем результаты c id = " + index);
		this.id = index;
		this.values = new LinkedHashMap<String, Map<Double, Double>>();
		this.freqs = new ArrayList<Double>();				
		this.myElement = ownerElement;		
		List<List<String>> results = new ArrayList<List<String>>();
		List<String> fieldsNames = new ArrayList<String>();		
		String sqlQuery = "SELECT MeasDate, ValuesTable  FROM [Results] WHERE id='" + index + "'";
		fieldsNames.add("MeasDate");
		fieldsNames.add("ValuesTable");
		System.out.println(sqlQuery);
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldsNames, results);
		tableName =  results.get(0).get(1);
		sqlQuery = "SELECT freq FROM [" + tableName + "]";
		System.out.println(sqlQuery);
		DataBaseManager.getDB().sqlQueryDouble(sqlQuery, "freq", this.freqs);
		this.countOfFreq = this.freqs.size();
		for (String key : keys) {
			try {
				sqlQuery = "SELECT " + key + " FROM [" + tableName + "]";
				ArrayList<Double> arrayResults = new ArrayList<Double>();
				System.out.println(sqlQuery);
				DataBaseManager.getDB().sqlQueryDouble(sqlQuery, key, arrayResults);				
				HashMap<Double, Double> tempHM = new HashMap<Double, Double>();
				for (int i=0; i<this.countOfFreq; i++) {
					tempHM.put(freqs.get(i), arrayResults.get(i));
				}
				this.values.put(key, tempHM);
			}
			catch(Exception exp) {
				System.out.println("Exception: " + exp.getMessage());
				continue;
			}
		}
		dateFormat = new SimpleDateFormat(datePattern);
		String strDate = results.get(0).get(0);
		try {
			this.dateOfMeas = dateFormat.parse(strDate);
		}
		catch(ParseException pExp) {
			this.dateOfMeas = Calendar.getInstance().getTime();
		}	
		dateOfMeasByString = dateFormat.format(dateOfMeas);
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
		List<String> currentKeys = new ArrayList<String>();
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
		String ValuesTable = "Results_" + this.myElement.getId() + "_" + this.getDateOfMeasByString();
		String sqlQuery = "INSERT INTO [Results] (ElementId, MeasDate, ValuesTable) VALUES (" + this.myElement.getId() + ", '" + this.getDateOfMeasByString() + "', '" + ValuesTable + "')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		
		//get id
		sqlQuery = "SELECT id FROM [Results] WHERE ElementId=" + this.myElement.getId() + " AND MeasDate='" + this.getDateOfMeasByString() + "'";
		this.id = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
		
		//creating table
		sqlQuery = "CREATE TABLE [" + ValuesTable + "] (id INTEGER PRIMARY KEY AUTOINCREMENT, freq REAL, ";
		for (int i = 0; i < currentKeys.size(); i++) {
			sqlQuery += (currentKeys.get(i) + " REAL");
			if (i != currentKeys.size() - 1) sqlQuery += ", ";
		}
		sqlQuery += ")";		
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		
		//
		for (int i = 0; i < countOfFreq; i++) {				
			sqlQuery = "INSERT INTO [" + ValuesTable + "] (freq, ";			
			for (int j = 0; j < currentKeys.size(); j++) {
				sqlQuery += (currentKeys.get(j));
				if (j != currentKeys.size() - 1) sqlQuery += ", ";
			}			
			sqlQuery += ") values ("+freqs.get(i)+", ";			
			for (int j = 0; j < currentKeys.size(); j++) {
				sqlQuery += (values.get(currentKeys.get(j)).get(freqs.get(i)).toString());
				if (j != currentKeys.size() - 1) sqlQuery += ", ";
			}			
			sqlQuery += ")";			
			DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
			System.out.println(sqlQuery);
		}
	}
	
	@Override
	public void deleteFromDB() throws SQLException {
		String listOfVerificationsTable = myElement.getListOfVerificationsTable();				
		String sqlQuery = "DELETE FROM ["+ listOfVerificationsTable +"] WHERE resultsTableName='" + tableName + "'";	
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);		
		sqlQuery = "DROP TABLE [" + tableName + "]";
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
		String sqlQuery = "UPDATE [" + myElement.getListOfVerificationsTable() + "] SET resultsTableName='" + newTableName + "' WHERE dateOfVerification='" + strDateOfMeas + "'";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		sqlQuery = "ALTER TABLE [" + tableName + "] RENAME TO [" + newTableName + "]";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
	}
	
	public void setNominalStatus() throws SQLException {
		if (myElement == null) {
			return;
		}
		String sqlQuery = "UPDATE [Elements] SET NominalId='" + Integer.toString(this.id) + "' WHERE id=" + this.myElement.getId();
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
	}
	
	public boolean isNominal() {
		if (myElement == null) {
			return false;
		}
		try {
			String strDateOfMeas = dateFormat.format(dateOfMeas);
			String sqlQuery = "SELECT id FROM [" + myElement.getListOfVerificationsTable() + "] WHERE dateOfVerification = '" + strDateOfMeas + "'";
			int myIndex = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
			sqlQuery = "SELECT NominalIndex FROM [" + myElement.getMyOwner().getElementsTableName() + "] WHERE ElementType='" + myElement.getType() + "' AND ElementSerNumber='" + myElement.getSerialNumber() + "'";
			int nominalIndex = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
			if (myIndex == nominalIndex) {
				return true;
			}
			else {
				return false;
			}
		}
		catch(SQLException sqlExp) {
			System.out.println(sqlExp.getStackTrace());
			return false;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		MeasResult otherResult = null;
		try {
			otherResult = (MeasResult)obj;
		}
		catch(ClassCastException cExp) {
			return false;
		}
		if (otherResult == this) return true;
		if (otherResult.freqs.size() != this.freqs.size()) return false;
		for (int i = 0; i < otherResult.freqs.size(); i++) {
			double f1 = otherResult.freqs.get(i);
			double f2 = this.freqs.get(i);
			if (f1 != f2) {
				return false;
			}
		}
		if(!otherResult.values.keySet().equals(this.values.keySet())){
			return false;
		}
		for (String key : otherResult.values.keySet()) {
			if (!otherResult.values.get(key).equals(this.values.get(key))) {
				return false;
			}
		}		
		return true;		
	}
}