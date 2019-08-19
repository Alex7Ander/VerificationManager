package VerificationPack;

import java.sql.SQLException;
import java.util.HashMap;

public class Gamma_Result extends MeasResult {
	
	public Gamma_Result(int CountOfParams){
		super(CountOfParams);
		
		String[] paramKeys = {"Gamma1", "S12", "S21", "Gamma2"};
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
