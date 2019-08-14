package VerificationPack;

import java.util.ArrayList;
import java.util.HashMap;

import DataBasePack.dbStorable;
import DevicePack.Element;
import DevicePack.Includable;
import DevicePack.SavingException;

public class MeasResult implements Includable<Element>, dbStorable{
		
	MeasResult(){
		freqs = new ArrayList<Double>();
		resultValues = new HashMap<String, HashMap<Double, Double>>();
	}
	
	protected String periodType;
	public String getPeriodType() {return periodType;}
	protected int countOfFreq;
	public int getCountOfFreq() {return this.countOfFreq;}
	public HashMap<String, HashMap<Double, Double>> resultValues;
	public ArrayList<Double> freqs;
	
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
