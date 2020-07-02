package ToleranceParamPack.StrategyPack;

import java.util.LinkedHashMap;
import java.util.Map;

import ToleranceParamPack.ParametrsPack.S_Parametr;
import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationPack.MeasResult;

public class percentStrategy implements StrategyOfSuitability {
	@Override
	public boolean checkResult(MeasResult result, ToleranceParametrs tolerance) {
		boolean resultOfCheck = true;
		int currentCountOfFreq = result.getCountOfFreq();
		for (int i=0; i < result.getMyOwner().getSParamsCout(); i++) {
			Map<Double, String> decisions = new LinkedHashMap<Double, String>();
			Map<Double, Double> difference = new LinkedHashMap<Double, Double>();
			
			for (int j=0; j < currentCountOfFreq; j++) {
				double cFreq = result.freqs.get(j);	
				String key = tolerance.measUnitPart + "_" + S_Parametr.values()[i];
				double res = result.values.get(key).get(cFreq);
				double nominal = tolerance.getMyOwner().getNominal().values.get(key).get(cFreq);
				
				double up = 0;
				double down = 0;
				double currentDifference = 0;
				
				//Если это S12 или S21
				if(S_Parametr.values()[i].equals(S_Parametr.S12) || S_Parametr.values()[i].equals(S_Parametr.S21)) {
					up = nominal + tolerance.values.get("UP_" + key).get(cFreq);
					down = nominal + tolerance.values.get("DOWN_" + key).get(cFreq);	
					currentDifference = java.lang.Math.round((res - nominal)*1000);
				}
				else {
					up = nominal + tolerance.values.get("UP_" + key).get(cFreq) * nominal / 100;
					down = nominal + tolerance.values.get("DOWN_" + key).get(cFreq) * nominal / 100; 
					currentDifference = java.lang.Math.round(((res - nominal)*100/nominal)*1000);					
				}
				difference.put(cFreq, currentDifference/1000);
				
				if(res > up || res < down) {
					decisions.put(cFreq, "Не соотв.");
					resultOfCheck = false;
				}
				else {
					decisions.put(cFreq, "Соотв.");
				}	 
				
			}			
			result.suitabilityDecision.put(tolerance.measUnitPart + "_" + S_Parametr.values()[i], decisions);
			result.differenceBetweenNominal.put(tolerance.measUnitPart + "_" + S_Parametr.values()[i], difference);
		}
		return resultOfCheck;
	}
}