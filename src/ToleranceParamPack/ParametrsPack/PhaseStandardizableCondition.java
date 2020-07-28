package ToleranceParamPack.ParametrsPack;

import java.util.function.Predicate;

import DevicePack.Element;

public class PhaseStandardizableCondition {

	public static Predicate<Double> getStandardizableCondition(Element element) {
		Predicate<Double> std = null;
		if(element.getMeasUnit().equals("vswr")) {
			std = (Double currentModuleValue) -> {
				if(currentModuleValue >= 1.05) {
					return true;
				}
				else {
					return false;
				}
			};
			
		}
		else {
			std = (Double currentModuleValue) -> {
				if(currentModuleValue >= 1.05) {
					return true;
				}
				else {
					return false;
				}
			};
		}
		return std;
	}
}
