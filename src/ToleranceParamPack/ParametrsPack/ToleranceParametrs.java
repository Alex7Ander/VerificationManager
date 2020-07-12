package ToleranceParamPack.ParametrsPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import DevicePack.Element;
import DevicePack.Includable;
import Exceptions.SavingException;
import NewElementPack.NewElementController;
import ToleranceParamPack.StrategyPack.StrategyOfSuitability;
import VerificationPack.MeasResult;

public class ToleranceParametrs implements Includable<Element>, dbStorable {
	private int id;
	public int getId() {
		return this.id;
	}
	public Map<String, Map<Double, Double>> values = new LinkedHashMap<String, Map<Double, Double>>();
	public List<Double> freqs = new ArrayList<Double>();
	 
	public TimeType timeType;
	public MeasUnitPart measUnitPart;
//Constructors 
//GUI
	public ToleranceParametrs(TimeType currentTimeType, MeasUnitPart currentUnitPart, NewElementController elCtrl, Element ownerElement){		
		this.myElement = ownerElement;
		this.timeType = currentTimeType;
		this.measUnitPart = currentUnitPart;		
		this.freqs = elCtrl.getFreqsValues();
		this.values = elCtrl.getToleranceParamsValues(timeType, measUnitPart);
	}
//DataBase
	public ToleranceParametrs(TimeType currentTimeType, MeasUnitPart currentUnitPart, Element ownerElement) throws SQLException {	
		this.myElement = ownerElement;
		this.timeType = currentTimeType;
		this.measUnitPart = currentUnitPart;
		System.out.println("\n�������� �������� ��� " + this.timeType + " " + this.measUnitPart);
		//Get id of this complex of tables
		String type = this.timeType + "_" + this.measUnitPart;
		String sqlQuery = "SELECT id FROM [Tolerance_params] WHERE ElementId=" + this.myElement.getId() + " AND Type='"+type+"'";
		System.out.println("�������� id ������� �������� �������� ��������:\n" + sqlQuery);
		this.id = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
						
		ArrayList<String> fieldsNames = new ArrayList<String>();
		fieldsNames.add("freq");
		for (int i = 0; i < this.myElement.getSParamsCout(); i++) {
			fieldsNames.add("DOWN_" + measUnitPart + "_" + S_Parametr.values()[i]);
			fieldsNames.add("UP_" + measUnitPart + "_" + S_Parametr.values()[i]);
		}
		
		sqlQuery = "SELECT ";
		int stop = fieldsNames.size();
		for (int i = 0; i < stop; i++) {
			sqlQuery += fieldsNames.get(i);
			if (i != stop - 1) sqlQuery += ", ";
		}
		sqlQuery += " FROM [Tolerance_params_values] WHERE ToleranceParamsId=" + this.id + "";
		System.out.println(sqlQuery);
		DataBaseManager.getDB().sqlQueryMapOfDouble(sqlQuery, fieldsNames, values);
			
		sqlQuery = "SELECT freq FROM [Tolerance_params_values] WHERE ToleranceParamsId=" + this.id;
		System.out.println(sqlQuery);
		DataBaseManager.getDB().sqlQueryDouble(sqlQuery, "freq", freqs);		
	}
//interface dbStorable		    
	 @Override
	 public void saveInDB() throws SQLException, SavingException {
		String type = this.timeType + "_" + this.measUnitPart;
		String sqlQuery = "INSERT INTO [Tolerance_params] (elementId, type) VALUES (" + this.myElement.getId() + ", '" + type + "')";
		System.out.print(sqlQuery);
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		System.out.println("\t - �������");		 
		sqlQuery = "SELECT id FROM [Tolerance_params] WHERE elementId=" + this.myElement.getId() + " AND type='" + type + "'";
		System.out.println("�������� id ��������:\n" + sqlQuery);
		this.id = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
		System.out.println("id = " + this.id);

		//Inserting values of parametrs
		for (int i = 0; i < freqs.size(); i++) {			
			sqlQuery = "INSERT INTO [Tolerance_params_values] (toleranceParamsId, freq,  ";		
			for (int j = 0; j < myElement.getSParamsCout(); j++) {
				sqlQuery += ("DOWN_" + measUnitPart + "_" + S_Parametr.values()[j] + ", ");
				sqlQuery += ("UP_" + measUnitPart + "_" + S_Parametr.values()[j]);
				if (j !=  myElement.getSParamsCout() - 1) sqlQuery += ", ";
			}
			sqlQuery += ") values (" + this.id + ", " + freqs.get(i) + ", ";		
			for (int j = 0; j < myElement.getSParamsCout(); j++) {
				String key = "DOWN_" + measUnitPart + "_" + S_Parametr.values()[j];
				sqlQuery += ("'"+values.get(key).get(freqs.get(i)).toString()+"', ");
				key = "UP_" + measUnitPart + "_" + S_Parametr.values()[j];
				sqlQuery += ("'"+values.get(key).get(freqs.get(i)).toString()+"'");
				if (j != myElement.getSParamsCout() - 1) sqlQuery += ", ";
			}				
			sqlQuery += ")";
			System.out.print(sqlQuery);
			try {
				DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
				System.out.println("\t - �������");
			} 
			catch (SQLException sqlExp) {
				throw new SavingException("�� ������� ��������� �������� ���������� �� �������\n ." +
					freqs.get(i).toString() + "��� S ��������� ��� " +
					timeType.getTableNamePart() + " �������" );
			}
		}		
	}
		 
	@Override
	public void deleteFromDB() throws SQLException {
		String sqlQuery = "DELETE FROM Tolerance_params WHERE id=" + this.id;
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
	}
	@Override
	public void editInfoInDB(HashMap<String, String> editingValues) throws SQLException {
		 // TODO Auto-generated method stub
	}	
	private Element myElement; 
	@Override
	public Element getMyOwner() {
		return myElement;
	}
	@Override
	public void onAdding(Element Owner) {
		this.myElement = Owner;
	}
	private StrategyOfSuitability strategy;
	public void setStrategy(StrategyOfSuitability anyStrategy) {
		this.strategy = anyStrategy;
	}
	public boolean checkResult(MeasResult nominals, MeasResult result) {
		 return this.strategy.checkResult(nominals, result, this);
	}
}