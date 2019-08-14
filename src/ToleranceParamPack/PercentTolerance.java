package ToleranceParamPack;

import java.util.ArrayList;
import java.util.HashMap;

import DevicePack.Element;
import VerificationPack.MeasResult;

public class PercentTolerance extends ToleranceParametrs {

	PercentTolerance(Element ParamsOwnerElement, ArrayList<String> ParametrsNames,
			ArrayList<ArrayList<Double>> Parametrs) {
		super(ParamsOwnerElement, ParametrsNames, Parametrs);
	}

	@Override
	public boolean checkResult(MeasResult result, HashMap<Double, Double> report) {
		// TODO Auto-generated method stub
		return false;
	}

}
