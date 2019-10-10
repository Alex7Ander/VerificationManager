package ToleranceParamPack.StrategyPack;

import java.util.HashMap;

import ToleranceParamPack.Parametrspack.ToleranceParametrs;
import VerificationPack.MeasResult;

public class percentStrategy implements StrategyOfSuitability {

	@Override
	public boolean checkResult(MeasResult result, ToleranceParametrs tolerance) {
		boolean resultOfCheck = true;
		int currentCountOfFreq = result.getCountOfFreq();
		int countOfParams = result.getMyOwner().getPoleCount();
		if (countOfParams == 2) countOfParams = 1;
		String[] cKeys = {"S11", "S12", "S21", "S22"};
		for (int i=0; i < countOfParams; i++) {
			HashMap<Double, String> decisions = new HashMap<Double, String>();			
			for (int j=0; j < currentCountOfFreq; j++) {
				double cFreq = result.freqs.get(j);	
				String k1 = tolerance.getUnitPrefix() + cKeys[i];
				double res = result.values.get(k1).get(cFreq);
				double down = tolerance.values.get("d_" + k1).get(cFreq) * res / 100;
				double up = tolerance.values.get("u_" + k1).get(cFreq) * res / 100;; 
				if(res > up || res < down) {
					decisions.put(cFreq, "�� �����");
					resultOfCheck = false;
				}
				else {
					decisions.put(cFreq, "�����");
				}				
			}			
			result.suitabilityDecision.put("m_" + cKeys[i], decisions);
		}
		return resultOfCheck;
	}

}
