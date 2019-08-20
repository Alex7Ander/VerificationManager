package ToleranceParamPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DataBasePack.DataBaseManager;
import DevicePack.Element;
import VerificationPack.MeasResult;

public class UpDownTolerance extends ToleranceParametrs {
	
	UpDownTolerance(Element ParamsOwnerElement, ArrayList<Double> ParametrsNames, ArrayList<ArrayList<Double>> Parametrs, String TypeByTime) {
		super(ParamsOwnerElement, ParametrsNames, Parametrs, TypeByTime);
	}

	@Override
	public boolean checkResult(MeasResult result, HashMap<Double, Double> report) {
		int currentCountOfFreq = result.getCountOfFreq();
		for (int i=0; i < this.getCountOfParams(); i++) {
			for (int j=0; j < currentCountOfFreq; j++) {
				double cFreq = result.freqs.get(j);
				double down = this.values.get("").get(cFreq);
				double up = this.values.get("").get(cFreq); 
				
				double res = result.values.get("").get(cFreq);
				
				if (res > up) {
					double delta = res - up;
					report.put(cFreq, delta);
				}
				else if (res < down) {
					double delta = res - down;
					report.put(cFreq, delta);
				}
			}
		}
		if (report.size()>0) return false;
		else return true;
	}

	@Override
	public void saveInDB() throws SQLException {
		String tableName = "";
		if (typeByTime.equals("periodic")) {
			tableName = this.getMyOwner().getPeriodicParamTable();
		}
		else if (typeByTime.equals("primary")) {
			tableName = this.getMyOwner().getPrimaryParamTable();
		}
		
		String sqlQuery = "";
		for (int i = 0; i < this.countOfFreq; i++) {
			sqlQuery = "INSERT INTO "+ tableName +" (freq, d_S11, u_S11, d_S12, u_S12, d_S21, u_S21, d_S22, u_S22) values ('','','','','','','','','')";
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
