package VerificationPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DevicePack.Element;

public class Gamma_Result extends MeasResult {
	
	Gamma_Result(){};
	
	public Gamma_Result(Element ownerElement, ArrayList<Double> Freqs, ArrayList<ArrayList<Double>> Values){
		super(ownerElement, Freqs, Values);		
	}

}
