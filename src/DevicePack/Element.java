package DevicePack;

import java.util.HashMap;

import DataBasePack.dbStorable;
import ToleranceParamPack.ToleranceParametrs;
import VerificationPack.MeasResult;
import VerificationPack.Verificatable;

public abstract class Element implements Includable<Device>, Verificatable, dbStorable{

	public Element(String ElementTableName, int index){
		//
	}
	
	public Element(String Type, String SerialNumber, int PoleCount, String MeasUnit, String ToleranceType, Device MyDevice){
		this.type = Type;
		this.serialNumber = SerialNumber;
		this.poleCount = PoleCount;
		this.measUnit = MeasUnit;
		this.toleranceType = ToleranceType;
		this.myDevice = MyDevice; 
	}
	
	protected String type;
	protected String serialNumber;
	protected int poleCount;
	protected String measUnit;
	protected String toleranceType;
	protected Device myDevice;
	
	public String getType() {return this.type;}
	public String getSerialNumber() {return this.serialNumber;}
	public int getPoleCount() {return this.poleCount;}
	public String getMeasUnit() {return this.measUnit;}
	public String getToleranceType() {return this.toleranceType;}
	
	@Override
	public Device getMyOwner() {return this.myDevice;}
	
//dbStorable		
	@Override
	public abstract void saveInDB();
	@Override
	public abstract void deleteFromDB();
	@Override
	public abstract void editInfoInDB();
	@Override
	public abstract void getData();
//----------------------------------------	
	//критерии годности
	public ToleranceParametrs periodicParametrs;
	public ToleranceParametrs primaryParametrs;
	//-----------------	
	@Override
	public boolean isSuitable(MeasResult result, HashMap<Double, Double> report) {
		boolean resultOfVerification = false;
		if (result.getPeriodType().equals("periodic")) resultOfVerification = periodicParametrs.checkResult(result, report);
		else if (result.getPeriodType().equals("primary")) resultOfVerification = primaryParametrs.checkResult(result, report);
		return resultOfVerification;
	}

}
