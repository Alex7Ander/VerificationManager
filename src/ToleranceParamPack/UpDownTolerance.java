package ToleranceParamPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DevicePack.Element;
import VerificationPack.MeasResult;

public class UpDownTolerance extends ToleranceParametrs {

	
	UpDownTolerance(Element ParamsOwnerElement, ArrayList<String> ParametrsNames,
			ArrayList<ArrayList<Double>> Parametrs) {
		super(ParamsOwnerElement, ParametrsNames, Parametrs);
	}

	@Override
	public boolean checkResult(MeasResult result, HashMap<Double, Double> report) {
		int currentCountOfFreq = result.getCountOfFreq();
		for (int i=0; i < this.getCountOfParams(); i++) {
			for (int j=0; j < currentCountOfFreq; j++) {
				double cFreq = result.freqs.get(j);
				double down = this.paramsValues.get("").get(cFreq);
				double up = this.paramsValues.get("").get(cFreq); 
				double res = result.resultValues.get("").get(cFreq);
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
		// TODO Auto-generated method stub
		
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
