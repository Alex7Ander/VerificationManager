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
import Exceptions.NoMeasResultDataForThisVerificationException;
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
	protected String datePattern = "dd/MM/yyyy HH:mm:ss"; //"dd/MM/yyyy HH:mm:ss" YYYY-MM-DD HH:MM:ss.SSS
	protected DateFormat dateFormat = new SimpleDateFormat(datePattern);
	protected Date dateOfMeas;
	protected String dateOfMeasByString;
	
	protected int verificationId;
	
	public int getCountOfFreq() {return this.countOfFreq;}
	public int getCountOfParams() {return this.countOfParams;}
	public Date getDateOfMeas() {return this.dateOfMeas;}
	public String getDateOfMeasByString() {
		return dateOfMeasByString; 
	}
	public Map<String, Map<Double, Double>> values;
	public Map<String, Map<Double, String>> suitabilityDecision;
	public Map<String, Map<Double, Double>> differenceBetweenNominal;
	public List<Double> freqs;
	protected  ObservableList<String> paramsNames;
	//private String tableName;
	
//Constructors
//FILE
	public MeasResult(String fileWithResults, int resultNumber, Element ownerElement) throws IOException {		
		this.myElement = ownerElement;
		this.freqs = new ArrayList<Double>();
		this.values = new LinkedHashMap<String, Map<Double, Double>>();
		this.suitabilityDecision = new HashMap<String, Map<Double, String>>();	
		this.differenceBetweenNominal = new HashMap<String, Map<Double, Double>>();
		ResultReaderManager resReader = new ResultReaderManager(fileWithResults);			
		resReader.readResult(resultNumber, freqs, values);				
		this.countOfParams = values.size();
		this.countOfFreq = freqs.size();		
		this.dateOfMeas = Calendar.getInstance().getTime();
		this.dateOfMeasByString = this.dateFormat.format(dateOfMeas);
	}
	
//GUI
	public MeasResult(NewElementController elCtrl, Element ownerElement){		
		this.myElement = ownerElement;	
		this.freqs = elCtrl.getFreqsValues();
		this.values = elCtrl.getNominalValues();
		this.countOfParams = values.size();
		this.countOfFreq = freqs.size();
		this.dateOfMeas = Calendar.getInstance().getTime();
		this.dateOfMeasByString = dateFormat.format(dateOfMeas);
	}	
	
//DB
	public MeasResult(Element ownerElement, int index) throws SQLException {
		System.out.println("\nПолучаем результаты c id = " + index);
		this.id = index;
		this.values = new LinkedHashMap<String, Map<Double, Double>>();
		this.suitabilityDecision = new HashMap<String, Map<Double, String>>();
		this.differenceBetweenNominal = new HashMap<String, Map<Double, Double>>();
		this.freqs = new ArrayList<Double>();				
		this.myElement = ownerElement;		
		List<List<String>> results = new ArrayList<List<String>>();
		List<String> fieldsNames = new ArrayList<String>();	
		String sqlQuery = "SELECT MeasDate FROM [Results] WHERE id='" + index + "'";
		fieldsNames.add("measDate");
		System.out.println(sqlQuery);
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldsNames, results);				
		this.dateOfMeasByString = results.get(0).get(0);

		sqlQuery = "SELECT verificationId FROM [Results] WHERE id='" + index + "'";
		System.out.println(sqlQuery);
		this.verificationId = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
		System.out.println("Результатам измерений с id = " + this.id + " соответствует поверка с id = " + this.verificationId);
		
		try {
			this.dateOfMeas = dateFormat.parse(this.dateOfMeasByString);
		}
		catch(ParseException pExp) {
			this.dateOfMeas = Calendar.getInstance().getTime();
		}			
		sqlQuery = "SELECT freq FROM [Results_values] WHERE ResultId="+this.id;
		System.out.println(sqlQuery);
		DataBaseManager.getDB().sqlQueryDouble(sqlQuery, "freq", this.freqs);
		this.countOfFreq = this.freqs.size();
		for (String key : keys) {
			try {
				sqlQuery = "SELECT " + key + " FROM [Results_values] WHERE ResultId=" + this.id;
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
	}
	
	public static int getResultIdsByVerificationForElement(int verificationId, Element element) throws SQLException, NoMeasResultDataForThisVerificationException {
		System.out.println("\nПолучаем результаты для поверки с id = " + verificationId);
		List<Integer> resultsIds = new ArrayList<>();
		String sqlQuery = "SELECT id FROM [Results] WHERE verificationId=" + verificationId + " AND elementId=" + element.getId();
		DataBaseManager.getDB().sqlQueryInteger(sqlQuery, "id", resultsIds);
		if (resultsIds.size() == 0) {
			String msg = "Не найден результат измерений, проведенных\nв рамках поверки с id = " + verificationId + "\nдля элемента с id = " + element.getId();
			System.err.println(msg);
			throw new NoMeasResultDataForThisVerificationException(msg);
		}
		return resultsIds.get(0);
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
		//String ValuesTable = "Results_" + this.myElement.getId() + "_" + this.getDateOfMeasByString();
		String sqlQuery = "INSERT INTO [Results] (elementId, measDate) VALUES (" + this.myElement.getId() + ", '" + this.getDateOfMeasByString() + "')";
		System.out.println("Вносим информацию в табл. Results:\n" + sqlQuery);
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		System.out.println("Успешно");
		//get id
		sqlQuery = "SELECT id FROM [Results] WHERE elementId=" + this.myElement.getId() + " AND measDate='" + this.getDateOfMeasByString() + "'";
		System.out.println("Получаем id для результата измерения запросом:\n" + sqlQuery);
		this.id = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
		System.out.println("id = " + this.id);

		for (int i = 0; i < countOfFreq; i++) {				
			sqlQuery = "INSERT INTO [Results_values] (resultId, freq, ";				
			for (int j = 0; j < currentKeys.size(); j++) {
				sqlQuery += (currentKeys.get(j));
				if (j != currentKeys.size() - 1) sqlQuery += ", ";
			}			
			sqlQuery += ") values (" + this.id + ", " + freqs.get(i)+ ", ";			
			for (int j = 0; j < currentKeys.size(); j++) {
				sqlQuery += (values.get(currentKeys.get(j)).get(freqs.get(i)).toString());
				if (j != currentKeys.size() - 1) sqlQuery += ", ";
			}			
			sqlQuery += ")";	
			System.out.print(sqlQuery);
			DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
			System.out.println("\t - Успешно");
		}
	}
	
	@Override
	public void deleteFromDB() throws SQLException {				
		String sqlQuery = "DELETE FROM Results WHERE id=" + this.id;	
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);		
	}
	
	@Override
	public void editInfoInDB(HashMap<String, String> editingValues) throws SQLException {
		// TODO Auto-generated method stub		
	}
	
	public void setNominalStatus() throws SQLException {
		if (this.myElement == null) {
			return;
		}
		System.out.print("Устанавливаем статус номинала для результат измерений с id = " + this.id);
		String sqlQuery = "UPDATE [Elements] SET nominalId='" + Integer.toString(this.id) + "' WHERE id=" + this.myElement.getId();
		System.out.print(sqlQuery);
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		System.out.println("\t - Успешно");
	}
	
	public boolean isNominal() {
		if (this.myElement == null) {
			return false;
		}
		try {
			String strDateOfMeas = dateFormat.format(dateOfMeas);
			String sqlQuery = "SELECT id FROM [Results] WHERE dateOfVerification = '" + strDateOfMeas + "' AND elementId='" + myElement.getId() + "'";
			int currentResultId = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
			sqlQuery = "SELECT nominalId FROM [Elements] WHERE id='" + myElement.getId() + "'";
			int nominalId = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
			if (currentResultId == nominalId) {
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
	
	public int getVerificationId() {
		return verificationId;
	}
	
	public void setVerificationId(int verificationId) throws SQLException {
		String sqlQuery = "UPDATE [Results] SET verificationId=" + verificationId + " WHERE id = " + this.id;
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
	}
	
	public void setElement(Element element) {
		this.myElement = element;
	}
}