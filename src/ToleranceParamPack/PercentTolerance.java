package ToleranceParamPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DevicePack.Element;
import NewElementPack.NewElementController;
import VerificationPack.MeasResult;

public class PercentTolerance extends ToleranceParametrs {

	PercentTolerance(Element ParamsOwnerElement, ArrayList<Double> Freqs,
			ArrayList<ArrayList<Double>> Parametrs, String TypeByTime) {
		super(ParamsOwnerElement, Freqs, Parametrs, TypeByTime);
	}
	
	public PercentTolerance(String TypeByTime, NewElementController elCtrl, Element ownerElement){
		super(TypeByTime, elCtrl, ownerElement);
	}
	
	public PercentTolerance(String TypeByTime, Element ownerElement) throws SQLException {
		super(TypeByTime, ownerElement);
	}

	@Override
	public boolean checkResult(MeasResult result) {
		boolean resultOfCheck = true;
		int currentCountOfFreq = result.getCountOfFreq();
		int countOfParams = result.getMyOwner().getPoleCount();
		if (countOfParams == 2) countOfParams = 1;
		String[] cKeys = {"S11", "S12", "S21", "S22"};
		for (int i=0; i < countOfParams; i++) {
			HashMap<Double, String> decisionsG = new HashMap<Double, String>();
			HashMap<Double, String> decisionsPhi = new HashMap<Double, String>();
			
			for (int j=0; j < currentCountOfFreq; j++) {
				double cFreq = result.freqs.get(j);
				
				double resG = result.values.get("m_" + cKeys[i]).get(cFreq);
				double downG = this.values.get("d_m_" + cKeys[i]).get(cFreq) * resG / 100;
				double upG = this.values.get("u_m_" + cKeys[i]).get(cFreq) * resG / 100;; 
				if(resG > upG || resG < downG) {
					decisionsG.put(cFreq, "Не годен");
					resultOfCheck = false;
				}
				else {
					decisionsG.put(cFreq, "Годен");
				}
				
				double resPhi = result.values.get("p_" + cKeys[i]).get(cFreq);
				double downPhi = this.values.get("d_p_" + cKeys[i]).get(cFreq) * resPhi / 100;
				double upPhi = this.values.get("u_p_" + cKeys[i]).get(cFreq) * resPhi / 100; 
				if(resPhi > upPhi || resPhi < downPhi) {
					decisionsPhi.put(cFreq, "Не годен");
					resultOfCheck = false;
				}
				else {
					decisionsPhi.put(cFreq, "Годен");
				}
			}
			
			result.suitabilityDecision.put("m_" + cKeys[i], decisionsG);
			result.suitabilityDecision.put("p_" + cKeys[i], decisionsPhi);
		}
		return resultOfCheck;
	}

}
