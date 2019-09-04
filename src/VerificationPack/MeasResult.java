package VerificationPack;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import DevicePack.Element;
import DevicePack.Includable;
import FileManagePack.ResultReaderManager;
import NewElementPack.NewElementController;
import javafx.collections.ObservableList;

public class MeasResult implements Includable<Element>, dbStorable{
		
	//protected String keys[] = {"m_S11", "p_S11", "m_S12", "p_S12", "m_S21", "p_S21", "m_S22", "p_S22"};
	/*		String keys[] = {"m_S11", "sko_m_S11", "err_m_S11", "p_S11", "sko_p_S11", "err_m_S11", 
	 						 "m_S12", "sko_m_S12", "err_m_S12", "p_S12", "sko_p_S12", "err_m_S12",
	 						 "m_S21", "sko_m_S21", "err_m_S121", "p_S21", "sko_p_S21", "err_m_S21", 
	 						 "m_S22", "sko_m_S22", "err_m_S22", "p_S22", "sko_p_S22", "err_m_S22",};
	*/
	protected int countOfFreq;
	protected int countOfParams;
	protected Date dateOfMeas;
	protected String datePattern = "dd/MM/yyyy HH:mm:ss";
	
	public int getCountOfFreq() {return this.countOfFreq;}
	public int getCountOfParams() {return this.countOfParams;}
	public Date getDateOfMeas() {return this.dateOfMeas;}
	public String getDateOfMeasByString() {
		return new SimpleDateFormat(datePattern).format(this.dateOfMeas);
	}	
	
	//Измеренные значения 
	public HashMap<String, HashMap<Double, Double>> values;
	//Решения о пригодности
	public HashMap<String, HashMap<Double, String>> suitabilityDecision;
	//Списко частот (может использоваться списком ключей для 2-го HashMap)
	public ArrayList<Double> freqs;
	
	protected  ObservableList<String> paramsNames;
	
	//Получение результатов из файла
	public MeasResult(String fileWithResults, int resultNumber, Element ownerElement) throws IOException{
		
		this.myElement = ownerElement;
		freqs = new ArrayList<Double>();
		values = new HashMap<String, HashMap<Double, Double>>();
		suitabilityDecision = new HashMap<String, HashMap<Double, String>>();
		
		ResultReaderManager resReader = new ResultReaderManager(fileWithResults);			
		resReader.readResult(resultNumber, freqs, values);
				
		this.countOfParams = values.size();
		this.countOfFreq = freqs.size();
		this.dateOfMeas = Calendar.getInstance().getTime();
	}
	
	//Получение результатов из GUI
	public MeasResult(NewElementController elCtrl, Element ownerElement){
		
		this.myElement = ownerElement;	
		freqs = new ArrayList<Double>();
		values = new HashMap<String, HashMap<Double, Double>>();
		
		freqs = elCtrl.getFreqsValues();
		values = elCtrl.getNominalValues();
				
		this.countOfParams = values.size();
		this.countOfFreq = freqs.size();
		this.dateOfMeas = Calendar.getInstance().getTime();
	}
	
	//Получение результатов из БД
	public MeasResult(Element ownerElement, int index) throws SQLException {
		
		this.values = new HashMap<String, HashMap<Double, Double>>();
		this.freqs = new ArrayList<Double>();
				
		this.myElement = ownerElement;
		
		ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
		ArrayList<String> fieldsNames = new ArrayList<String>();
		
		String listOfVerificationsTable = this.myElement.getListOfVerificationsTable();
		String sqlQuery = "SELECT dateOfVerification, resultsTableName  FROM ["+listOfVerificationsTable+"] WHERE id='"+index+"'";
		fieldsNames.add("dateOfVerification");
		fieldsNames.add("resultsTableName");
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldsNames, results);
		
		//Дата
		DateFormat df = new SimpleDateFormat(datePattern);
		String strDate = results.get(0).get(0);
		try {
			this.dateOfMeas = df.parse(strDate);
		}
		catch(ParseException pExp) {
			this.dateOfMeas = Calendar.getInstance().getTime();
		}
		
		String resultsTableName =  results.get(0).get(1);
				
		//String keys[] = {"freq", "m_S11", "p_S11", "m_S12", "p_S12", "m_S21", "p_S21", "m_S22", "p_S22"};
	
		//Получим частоты
		sqlQuery = "SELECT freq FROM ["+resultsTableName+"]";
		DataBaseManager.getDB().sqlQueryDouble(sqlQuery, "freq", this.freqs);
		this.countOfFreq = this.freqs.size();
		
		String keys[] = {"m_S11", "sko_m_S11", "err_m_S11", "p_S11", "sko_p_S11", "err_p_S11", 
				 "m_S12", "sko_m_S12", "err_m_S12", "p_S12", "sko_p_S12", "err_p_S12",
				 "m_S21", "sko_m_S21", "err_m_S21", "p_S21", "sko_p_S21", "err_p_S21", 
				 "m_S22", "sko_m_S22", "err_m_S22", "p_S22", "sko_p_S22", "err_p_S22"};
		/*
		int countOfKeys = 0;
		if (this.myElement.getPoleCount() == 2) {
			countOfKeys = 6;
		}
		else {
			countOfKeys = 24;
		}
		fieldsNames.clear();
		for (int i=0; i<countOfKeys; i++) fieldsNames.add(keys[i]);
		*/
		//Получим значения параметров
		for (String key : keys) {
			try {
				sqlQuery = "SELECT " + key + " FROM ["+resultsTableName+"]";
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
		/*
		sqlQuery = "SELECT freq, ";
		for (int i=0; i<countOfKeys; i++) {
			sqlQuery += keys[i];
			if (i != countOfKeys - 1) sqlQuery += ", ";
		}
		sqlQuery += " FROM ["+resultsTableName+"]";
		DataBaseManager.getDB().sqlQueryMapOfDouble(sqlQuery, fieldsNames, this.values);
		this.countOfParams = this.values.size();
		*/
		
	}
		
//Includable<Element>
	protected Element myElement;
	@Override
	public Element getMyOwner() {return myElement;}
	
//dbStorable
	@Override
	public void saveInDB() throws SQLException {
		//String keys[] = {"m_S11", "p_S11", "m_S12", "p_S12", "m_S21", "p_S21", "m_S22", "p_S22"};
		
		String keys[] = {"m_S11", "sko_m_S11", "err_m_S11", "p_S11", "sko_p_S11", "err_p_S11", 
				 "m_S12", "sko_m_S12", "err_m_S12", "p_S12", "sko_p_S12", "err_p_S12",
				 "m_S21", "sko_m_S21", "err_m_S21", "p_S21", "sko_p_S21", "err_p_S21", 
				 "m_S22", "sko_m_S22", "err_m_S22", "p_S22", "sko_p_S22", "err_p_S22"};
		ArrayList<String> currentKeys = new ArrayList<String>();
		//Перепишем все параметры, которые необходимо сохранить в список currentKeys
		for (String k: keys) {
			try {
				int size = this.values.get(k).size();
				if (size > 0) {
					currentKeys.add(k);
				}
			}
			catch(Exception exp) {
				
			}
		}
						
		DateFormat df = new SimpleDateFormat(datePattern);
		String dateOfVerification = df.format(this.dateOfMeas);
		
		String listOfVerificationsTable = this.myElement.getListOfVerificationsTable();
		String resultsTableName = "Результат поверки " + 
				this.myElement.getMyOwner().getName() + " " + this.myElement.getMyOwner().getType() + " " + this.myElement.getMyOwner().getSerialNumber() + " " + this.myElement.getType() + " " + this.myElement.getSerialNumber() +
				" проведенной " + dateOfVerification;
		String sqlQuery = "INSERT INTO [" + listOfVerificationsTable + "] (dateOfVerification, resultsTableName) values ('"+ dateOfVerification +"','"+ resultsTableName +"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		sqlQuery = "CREATE TABLE [" + resultsTableName + "] (id INTEGER PRIMARY KEY AUTOINCREMENT, freq VARCHAR(20), ";
		for (int i = 0; i < currentKeys.size(); i++) {
			sqlQuery += (currentKeys.get(i) + " VARCHAR(20)");
			if (i != currentKeys.size() - 1) sqlQuery += ", ";
		}
		sqlQuery += ")";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		
		//Заполняем таблицу resultsTableName с результатами измерений
		for (int i=0; i<this.countOfFreq; i++) {	
			
			sqlQuery = "INSERT INTO [" + resultsTableName + "] (freq, ";
			
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
		String dateOfVerification = this.dateOfMeas.toString();
		String listOfVerificationsTable = this.myElement.getListOfVerificationsTable();
		String resultsTableName = "Results of verification " + listOfVerificationsTable.substring(listOfVerificationsTable.indexOf("Measurements of ")) + " at " + dateOfVerification;
		
		String sqlQuery = "DELETE FROM ["+listOfVerificationsTable+"] WHERE resultsTableName='"+resultsTableName+"'";	
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		
		sqlQuery = "DROP TABLE [" + resultsTableName + "]";
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