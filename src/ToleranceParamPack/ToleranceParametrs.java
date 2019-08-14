package ToleranceParamPack;

import java.util.ArrayList;
import java.util.HashMap;

import DataBasePack.dbStorable;
import DevicePack.Element;
import DevicePack.Includable;
import DevicePack.SavingException;
import VerificationPack.MeasResult;

public abstract class ToleranceParametrs implements Includable<Element>, dbStorable {
	
	ToleranceParametrs(Element ParamsOwnerElement, ArrayList<String> ParametrsNames, ArrayList<ArrayList<Double>> Parametrs){
		this.myElement = ParamsOwnerElement;
		this.countOfParams = ParametrsNames.size();
		this.countOfFreq = Parametrs.get(0).size();
		paramsValues = new HashMap<String, HashMap<Double, Double>>();
		for (int i=0; i < countOfParams; i++) {
			HashMap<Double, Double> tMap = new HashMap<Double, Double>();
			for (int j=0; j < countOfFreq; j++) {
				tMap.put(Parametrs.get(j).get(0), Parametrs.get(j).get(i+1));
			}
			paramsValues.put(ParametrsNames.get(i), tMap);
		}
	}
	
	protected int countOfParams;
	protected int countOfFreq;
	
	public int getCountOfParams() {return this.countOfParams;}
	public int getCountOfFreq() {return this.countOfFreq;}
	public HashMap<String, HashMap<Double, Double>> paramsValues;
	
	public abstract boolean checkResult(MeasResult result, HashMap<Double, Double> report);
	
//Includable<Element>	
	private Element myElement;
	@Override
	public Element getMyOwner() {return myElement;}
				
//dbStorable	
	@Override
	public void saveInDB() throws SavingException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteFromDB() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void editInfoInDB() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void getData() {
		// TODO Auto-generated method stub
		
	}
	
}
