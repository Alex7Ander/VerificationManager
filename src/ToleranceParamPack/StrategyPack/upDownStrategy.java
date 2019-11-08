package ToleranceParamPack.StrategyPack;

import java.util.HashMap;

import ToleranceParamPack.ParametrsPack.S_Parametr;
import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationPack.MeasResult;

public class upDownStrategy implements StrategyOfSuitability {
	@Override
	public boolean checkResult(MeasResult result, ToleranceParametrs tolerance) {
		boolean resultOfCheck = true;
		int currentCountOfFreq = result.getCountOfFreq();
		for (int i=0; i < result.getMyOwner().getSParamsCout(); i++) {
			HashMap<Double, String> decisions = new HashMap<Double, String>();
			for (int j=0; j < currentCountOfFreq; j++) {
				double cFreq = result.freqs.get(j);	
				String key = tolerance.measUnitPart + "_" + S_Parametr.values()[i];
				double res = result.values.get(key).get(cFreq);
				double down = tolerance.values.get("DOWN_" + key).get(cFreq);
				double up = tolerance.values.get("UP_" + key).get(cFreq);
				if(res > up || res < down) {
					decisions.put(cFreq, "�� �����");
					resultOfCheck = false;
				}
				else {
					decisions.put(cFreq, "�����");
				}
			}			
			result.suitabilityDecision.put(tolerance.measUnitPart + "_" + S_Parametr.values()[i], decisions);
		}
		return resultOfCheck;
	}

}