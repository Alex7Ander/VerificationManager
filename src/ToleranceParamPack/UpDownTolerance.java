package ToleranceParamPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DataBasePack.DataBaseManager;
import DevicePack.Element;
import NewElementPack.NewElementController;
import VerificationPack.MeasResult;

public class UpDownTolerance extends ToleranceParametrs {
	
	UpDownTolerance(Element ParamsOwnerElement, ArrayList<Double> ParametrsNames, ArrayList<ArrayList<Double>> Parametrs, String TypeByTime) {
		super(ParamsOwnerElement, ParametrsNames, Parametrs, TypeByTime);
	}
	
	public UpDownTolerance(String TypeByTime, NewElementController elCtrl, Element ownerElement){
		super(TypeByTime, elCtrl, ownerElement);
	}
	
	public UpDownTolerance(String TypeByTime, Element ownerElement) throws SQLException{
		super(TypeByTime, ownerElement);
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
}
