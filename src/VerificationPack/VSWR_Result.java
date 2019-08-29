package VerificationPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DevicePack.Element;
import NewElementPack.NewElementController;

public class VSWR_Result extends MeasResult {
		
	public VSWR_Result(NewElementController elCtrl, Element ownerElement){
		super(elCtrl, ownerElement);
	}
	
	public VSWR_Result(Element ownerElement, int index) throws SQLException {
		super(ownerElement, index);
	}
	/*
	public Gamma_Result toGamma() {
		Gamma_Result reCalculatedResult = new Gamma_Result();
		return reCalculatedResult;
	}
	*/
	
}
