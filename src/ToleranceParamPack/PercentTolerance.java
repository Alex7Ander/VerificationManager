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
	public boolean checkResult(MeasResult result, HashMap<Double, Double> report) {
		// TODO Auto-generated method stub
		return false;
	}

}
