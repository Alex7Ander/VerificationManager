package VerificationPack;

import java.util.ArrayList;
import java.util.HashMap;
import DataBasePack.dbStorable;
import DevicePack.Element;
import DevicePack.Includable;
import DevicePack.SavingException;

public abstract class MeasResult implements Includable<Element>, dbStorable{
		
	MeasResult(int CountOfParams){
		freqs = new ArrayList<Double>();
		resultValues = new HashMap<String, HashMap<Double, Double>>();
		this.countOfParams = CountOfParams;
	}
	
	protected String periodType;
	public String getPeriodType() {return periodType;}
	protected int countOfFreq;
	public int getCountOfFreq() {return this.countOfFreq;}
	protected int countOfParams;
	public int getCountOfParams() {return this.countOfParams;}
	public HashMap<String, HashMap<Double, Double>> resultValues;
	public ArrayList<Double> freqs;
	
//Includable<Element>
	protected Element myElement;
	@Override
	public Element getMyOwner() {return myElement;}

}
