package ToleranceParamPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import DevicePack.Element;
import DevicePack.Includable;
import ErrorParamsPack.ErrorParams;
import NewElementPack.NewElementController;
import VerificationPack.MeasResult;

public abstract class ToleranceParametrs implements Includable<Element>, dbStorable {
	protected String keys[] = {"d_m_S11", "u_m_S11", "d_p_S11", "u_p_S11",  
							   "d_m_S12", "u_m_S12", "d_p_S12", "u_p_S12",
							   "d_m_S21", "u_m_S21", "d_p_S21", "u_p_S21",
							   "d_m_S22", "u_m_S22", "d_p_S22", "u_p_S22"};
	
	protected int countOfParams;
	protected int countOfFreq;
	protected String typeByTime;
	public HashMap<String, HashMap<Double, Double>> values;
	public ArrayList<Double> freqs;
	
	public int getCountOfParams() {return this.countOfParams;}
	public int getCountOfFreq() {return this.countOfFreq;}
	public String getTypeByTime() {return this.typeByTime;}
	
	//Абстрактный метод, который должен быть переопредел классими наследниками для определения пригодности 
	//устройства по тому или иному способу
	public abstract boolean checkResult(MeasResult result);
		
	//Конструктор для инициализации перед сохранением в БД
	ToleranceParametrs(String TypeByTime, NewElementController elCtrl, Element ownerElement){
		this.typeByTime = TypeByTime;		
		this.myElement = ownerElement;
		this.freqs = elCtrl.getFreqsValues();
		this.countOfFreq = this.freqs.size();
		this.values = elCtrl.getToleranceParamsValues(TypeByTime);
		this.countOfParams = this.values.size();
	}
	
	//Еще 1 вариант конструктора для сохранения в БД
	ToleranceParametrs(Element ParamsOwnerElement, ArrayList<Double> Freqs, ArrayList<ArrayList<Double>> Parametrs, String TypeByTime){
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
	ToleranceParametrs(String TypeByTime, Element ownerElement) throws SQLException {
		
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
		
		//ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
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
	
	public void measError(MeasResult result) throws SQLException {
		ErrorParams er = new ErrorParams();
		String tract = "";
		
		int countOfParams = result.getMyOwner().getPoleCount();
		if (countOfParams == 2) countOfParams = 1;
		
		String[] cKeys = {"S11", "S12", "S21", "S22"};
		
		for (int j=0; j < countOfParams; j++) {
			HashMap<Double, Double> errorsG = new HashMap<Double, Double>();
			HashMap<Double, Double> errorsPhi = new HashMap<Double, Double>();
			for (int i=0; i < result.getCountOfFreq(); i++) {			
				double cFreq = result.freqs.get(i);
				if (cFreq < 53.57) {
					tract = "5,2";
				}
				else if (cFreq >= 53.57 && cFreq < 78.33){
					tract = "3,6";
				}
				else if (cFreq >= 78.33 && cFreq < 118.1) {
					tract = "2,4";
				}
				else {
					tract = "1,6";
				}
			
				double a1 = er.getA1(tract);
				double b1 = er.getB1(tract);
				double c1 = er.getC1(tract);
				double d1 = er.getE1(tract);
				double e1 = er.getE1(tract);
				double a2 = er.getA2(tract);
				double b2 = er.getB2(tract);
				double c2 = er.getC2(tract);
				double d2 = er.getD2(tract);
			
				double G = result.values.get("m_" + cKeys[j]).get(cFreq);
				//double phi = result.values.get("p_" + cKeys[j]).get(cFreq);
				
				double nspG = (a1*G + b1)*cFreq + c1*G*G + d1*G + e1;
				double nspPhi = (a2*G + b2)*cFreq + c2*Math.pow(G, d2);
				
				double skoG = result.values.get("sko_m_" + cKeys[j]).get(cFreq);
				double skoPhi = result.values.get("sko_p_" + cKeys[j]).get(cFreq);
				
				double errG = 2*Math.pow((nspG*nspG/3 + skoG*skoG), 0.5);
				double errPhi = 2*Math.pow(nspPhi*nspPhi/3 + skoPhi*skoPhi, 0.5);
				
				errG = ((double)Math.round(errG*1000))/1000;
				errPhi = ((double)Math.round(errPhi*1000))/1000;
				
				errorsG.put(cFreq, errG);
				errorsPhi.put(cFreq, errPhi);
			}
			result.values.put("err_m_" + cKeys[j], errorsG);
			result.values.put("err_p_" + cKeys[j], errorsPhi);
		}
		
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
		for (int i=0; i<this.countOfFreq; i++) {	
					
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
