package VerificationPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DevicePack.Element;

public class VSWR_Result extends MeasResult {
		
	public VSWR_Result(Element ownerElement, ArrayList<Double> Freqs, ArrayList<ArrayList<Double>> Values){
		super(ownerElement, Freqs, Values);
	}
	
	public Gamma_Result toGamma() {
		Gamma_Result reCalculatedResult = new Gamma_Result();
		return reCalculatedResult;
	}
	
	
}
