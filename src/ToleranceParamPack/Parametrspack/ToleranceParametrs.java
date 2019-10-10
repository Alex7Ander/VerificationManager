package ToleranceParamPack.Parametrspack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import DevicePack.Element;
import DevicePack.Includable;
import ErrorParamsPack.ErrorParams;
import NewElementPack.NewElementController;
import ToleranceParamPack.StrategyPack.StrategyOfSuitability;
import VerificationPack.MeasResult;

public class ToleranceParametrs implements Includable<Element>, dbStorable {
	protected String keys[];/* = {"d_m_S11", "u_m_S11", "d_p_S11", "u_p_S11",  
							   "d_m_S12", "u_m_S12", "d_p_S12", "u_p_S12",
							   "d_m_S21", "u_m_S21", "d_p_S21", "u_p_S21",
							   "d_m_S22", "u_m_S22", "d_p_S22", "u_p_S22"};*/
	
	private String unitPrefix;
	
	protected int countOfParams;
	protected int countOfFreq;
	protected String typeByTime;
	public HashMap<String, HashMap<Double, Double>> values;
	public ArrayList<Double> freqs;
	
	public String getUnitPrefix() {return this.unitPrefix;}
	public int getCountOfParams() {return this.countOfParams;}
	public int getCountOfFreq() {return this.countOfFreq;}
	public String getTypeByTime() {return this.typeByTime;}
	
	//Стратегия определения годности прибора
	private StrategyOfSuitability strategy;
	public void setStratege(StrategyOfSuitability anyStrategy) {
		this.strategy = anyStrategy;
	}
	//Абстрактный метод, который должен быть переопредел классими наследниками для определения пригодности 
	//устройства по тому или иному способу
	public boolean checkResult(MeasResult result) {
		return this.strategy.checkResult(result, this);		
	}
		
	//Конструктор для инициализации перед сохранением в БД
	public ToleranceParametrs(String TypeByTime, NewElementController elCtrl, Element ownerElement, String UnitPrefix){
		this.typeByTime = TypeByTime;		
		this.myElement = ownerElement;
		this.freqs = elCtrl.getFreqsValues();
		this.countOfFreq = this.freqs.size();
		this.values = elCtrl.getToleranceParamsValues(TypeByTime);
		this.countOfParams = this.values.size();
		this.unitPrefix = UnitPrefix;
	}
	
	//Еще 1 вариант конструктора для сохранения в БД
	public ToleranceParametrs(Element ParamsOwnerElement, ArrayList<Double> Freqs, ArrayList<ArrayList<Double>> Parametrs, String TypeByTime, String UnitPrefix){
		this.unitPrefix = UnitPrefix;
		this.myElement = ParamsOwnerElement;
		this.countOfParams = Parametrs.size();
		this.countOfFreq = Freqs.size();
		this.typeByTime = TypeByTime;
		values = new HashMap<String, HashMap<Double, Double>>();
		freqs = new ArrayList<Double>();
		
		for (Double currentFr : Freqs) {
			this.freqs.add(currentFr);
		}
		
		for (int i=0; i < countOfParams; i++) {
			HashMap<Double, Double> tVals = new HashMap<Double, Double>();
			for (int j=0; j < countOfFreq; j++) {
				tVals.put(Freqs.get(j), Parametrs.get(i).get(j));
			}
			values.put(keys[i], tVals);
		}
	}
	
	//Конструктор для получения данных из БД
	public ToleranceParametrs(String TypeByTime, Element ownerElement, String UnitPrefix) throws SQLException {
		
		this.unitPrefix = UnitPrefix;
		this.values = new HashMap<String, HashMap<Double, Double>>();
		this.freqs = new ArrayList<Double>();
		
		this.myElement = ownerElement;
		this.typeByTime = TypeByTime;
		
		String paramTableName = "";
		if (TypeByTime.equals("primary")) {
			paramTableName = ownerElement.getPrimaryParamTable();
		}
		else {
			paramTableName = ownerElement.getPeriodicParamTable(); 
		}
		
		ArrayList<String> fieldsNames = new ArrayList<String>();
		fieldsNames.add("freq");
		
		int countOfKeys = 0;
		if (this.myElement.getPoleCount() == 2) {
			countOfKeys = 5;
		}
		else {
			countOfKeys = 17;
		}
		for (int i=0; i<countOfKeys-1; i++) fieldsNames.add(keys[i]);
		
		String sqlQuery = "SELECT ";
		for (int i=0; i<countOfKeys; i++) {
			sqlQuery += fieldsNames.get(i);
			if (i != countOfKeys - 1) sqlQuery += ", ";
		}
		sqlQuery += " FROM ["+paramTableName+"]";
		DataBaseManager.getDB().sqlQueryMapOfDouble(sqlQuery, fieldsNames, this.values);
		this.countOfParams = this.values.size();
		
		sqlQuery = "SELECT freq FROM ["+paramTableName+"]";
		DataBaseManager.getDB().sqlQueryDouble(sqlQuery, "freq", this.freqs);
		this.countOfFreq = this.freqs.size();		
	}
	
//Includable<Element>	
	private Element myElement;
	@Override
	public Element getMyOwner() {return myElement;}
	@Override
	public void onAdding(Element Owner) {
		this.myElement = Owner;		
	}
	
//dbStorable
	@Override
	public void saveInDB() throws SQLException {
		String paramsTableName = null;
		if (this.typeByTime.equals("primary")) {
			paramsTableName = this.myElement.getPrimaryParamTable();
		}
		else if (this.typeByTime.equals("periodic")) {
			paramsTableName = this.myElement.getPeriodicParamTable();
		}
		String sqlQuery = "CREATE TABLE ["+paramsTableName+"] (id INTEGER PRIMARY KEY AUTOINCREMENT, freq VARCHAR(20), ";
		for (int i=0; i<this.countOfParams; i++) {
			sqlQuery += (keys[i] + " VARCHAR(20)");
			if (i != this.countOfParams - 1) sqlQuery += ", ";
		}
		sqlQuery += ")";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		System.out.println("\t\tСоздана таблица с параметрами ");
		
		//Заполняем таблицу paramsTableName с результатами измерений
		for (int i = 0; i < this.countOfFreq; i++) {	
					
			sqlQuery = "INSERT INTO [" + paramsTableName + "] (freq, ";
					
			for (int j=0; j<this.countOfParams; j++) {
				sqlQuery += (keys[j]);
				if (j != this.countOfParams - 1) sqlQuery += ", ";
			}
					
			sqlQuery += ") values ('"+freqs.get(i)+"', ";
					
			for (int j = 0; j < this.countOfParams; j++) {
				sqlQuery += ("'"+values.get(keys[j]).get(freqs.get(i)).toString()+"'");
				if (j != this.countOfParams - 1) sqlQuery += ", ";
			}
					
			sqlQuery += ")";
					
			DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
			System.out.println("\t\tВнесены праметры для частоты " + freqs.get(i) + ": ");
			System.out.println("\t\t" + sqlQuery + "");
		}
	}
	
	@Override
	public void deleteFromDB() throws SQLException {
		String paramsTableName = "";
		if (this.typeByTime.equals("primary")) {
			paramsTableName = this.myElement.getPrimaryParamTable();
		}
		else if (this.typeByTime.equals("periodic")) {
			paramsTableName = this.myElement.getPeriodicParamTable();
		}
		String sqlQuery = "DROP TABLE ["+paramsTableName+"]";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
	}
	
	@Override
	public void editInfoInDB(HashMap<String, String> editingValues) throws SQLException {
		// TODO Auto-generated method stub		
	}
					
}
