package ToleranceParamPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DevicePack.Element;
import VerificationPack.MeasResult;

public class PercentTolerance extends ToleranceParametrs {

	PercentTolerance(Element ParamsOwnerElement, ArrayList<Double> Freqs,
			ArrayList<ArrayList<Double>> Parametrs, String TypeByTime) {
		super(ParamsOwnerElement, Freqs, Parametrs, TypeByTime);
	}

	@Override
	public boolean checkResult(MeasResult result, HashMap<Double, Double> report) {
		// TODO Auto-generated method stub
		return false;
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
