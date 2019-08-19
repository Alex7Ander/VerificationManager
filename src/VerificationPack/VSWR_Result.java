package VerificationPack;

import java.sql.SQLException;
import java.util.HashMap;

public class VSWR_Result extends MeasResult {
		
	public VSWR_Result(int CountOfParams){
		super(CountOfParams);
		String[] paramKeys = {"VSWR1", "S12", "S21", "VSWR2"};
		for (int i=0; i<this.countOfParams; i++) {
			HashMap<Double, Double> mapOfValues = new HashMap<Double, Double>();
			resultValues.put(paramKeys[i], mapOfValues);
		}	
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
