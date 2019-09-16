package VerificationPack;

import java.sql.SQLException;

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
