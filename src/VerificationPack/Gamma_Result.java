package VerificationPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DevicePack.Element;
import NewElementPack.NewElementController;

public class Gamma_Result extends MeasResult {
	
	public Gamma_Result(NewElementController elCtrl, Element ownerElement){
		super(elCtrl, ownerElement);
	}
	
	public Gamma_Result(Element ownerElement, int index) throws SQLException {
		super(ownerElement, index);
	}

}
