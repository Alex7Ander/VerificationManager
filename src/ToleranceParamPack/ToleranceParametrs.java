package ToleranceParamPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import DevicePack.Element;
import DevicePack.Includable;
import VerificationPack.MeasResult;

public abstract class ToleranceParametrs implements Includable<Element>, dbStorable {
	protected String keys[] = {"up_m_S11", "down_m_S11", "up_p_S11", "down_p_S11", 
					 		   "up_m_S12", "down_m_S12", "up_p_S12", "down_p_S12",
					 		   "up_m_S21", "down_m_S21", "up_p_S21", "down_p_S21",
					 		   "up_m_S22", "down_m_S22", "up_p_S22", "down_p_S22"};
	
	//Конструктор для инициализации перед сохранением
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
	
	protected int countOfParams;
	protected int countOfFreq;
	protected String typeByTime;
	
	public int getCountOfParams() {return this.countOfParams;}
	public int getCountOfFreq() {return this.countOfFreq;}
	public String getTypeByTime() {return this.typeByTime;}
	
	public HashMap<String, HashMap<Double, Double>> values;
	public ArrayList<Double> freqs;
	
	public abstract boolean checkResult(MeasResult result, HashMap<Double, Double> report);
	
//Includable<Element>	
	private Element myElement;
	@Override
	public Element getMyOwner() {return myElement;}
	
//dbStorable
	@Override
	public void saveInDB() throws SQLException {
		String paramsTableName = "";
		if (this.typeByTime.equals("primary")) {
			paramsTableName = this.myElement.getPrimaryParamTable();
		}
		else if (this.typeByTime.equals("periodic")) {
			paramsTableName = this.myElement.getPeriodicParamTable();
		}
		String sqlQuery = "CREATE TABLE ["+paramsTableName+"] (id INTEGER PRIMARY KEY AUTOINCREMENT, freq VARCHAR(20), "; //m_s11_d REAL, m_s11_n REAL, m_s11_u REAL, p_s11_d REAL, p_s11_n REAL,  p_s11_u REAL)";
		for (int i=0; i<this.countOfParams; i++) {
			sqlQuery += (keys[i] + " VARCHAR(20)");
			if (i != this.countOfParams - 1) sqlQuery += ", ";
		}
		sqlQuery += ")";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		
		//Заполняем таблицу paramsTableName с результатами измерений
		for (int i=0; i<this.countOfFreq; i++) {	
					
			sqlQuery = "INSERT INTO " + paramsTableName + " (";
					
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
