package DevicePack;

public class TwoPoleElement extends Element {

	TwoPoleElement(String Type, String SerialNumber, int PoleCount, String MeasUnit, String ToleranceType,
			Device MyDevice) {
		super(Type, SerialNumber, PoleCount, MeasUnit, ToleranceType, MyDevice);
	}
	
	@Override
	public void saveInDB() {
		String sqlString;
		String addStr = myDevice.getName() + " " + myDevice.getType() + " " + myDevice.getSerialNumber();
		String strElementsTable = "Elements of " + addStr;
		String ePerParamTable  = "Periodic for " + addStr + " " + this.type + " " + this.serialNumber;
		String ePrimParamTable = "Primary for " + addStr + " " + this.type + " " + this.serialNumber;
		try {
			//Внесли запись об элементе
			sqlString = "INSERT INTO ["+strElementsTable+"] (ElementType, ElementSerNumber, PoleCount, MeasUnit, ToleranceType, PeriodicParamTable, PrimaryParamTable) values ('"+type+"','"+serialNumber+"','"+poleCount+"','"+measUnit+"','"+toleranceType+"','"+ePerParamTable+"','"+ePrimParamTable+"')";
			AksolDataBase.sqlQueryUpdate(sqlString);
			//Создание таблиц для первичных и переодических параметров годности
			sqlString = "CREATE TABLE ["+ePerParamTable+"] (id INTEGER PRIMARY KEY AUTOINCREMENT, freq REAL, m_s11_d REAL, m_s11_n REAL, m_s11_u REAL, p_s11_d REAL, p_s11_n REAL,  p_s11_u REAL)";
			AksolDataBase.sqlQueryUpdate(sqlString);
			sqlString = "CREATE TABLE ["+ePrimParamTable+"] (id INTEGER PRIMARY KEY AUTOINCREMENT, freq REAL, m_s11_d REAL, m_s11_n REAL, m_s11_u REAL, p_s11_d REAL, p_s11_n REAL,  p_s11_u REAL)";
			AksolDataBase.sqlQueryUpdate(sqlString);
			//Заполняем таблицы с параметрами

		}
		catch(Exception exp) {
			
		}		
	}

	@Override
	public void deleteFromDB() {
	
	}

	@Override
	public void editInfoInDB() {
	
	}

	@Override
	public void getData() {
	
	}

}
