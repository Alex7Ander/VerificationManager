package DataBasePack;

import java.sql.SQLException;

import DevicePack.SavingException;
import VerificationPack.MeasResult;

public interface dbStorable {

	public DataBaseManager AksolDataBase = DataBaseManager.getDB();

	void saveInDB() throws SQLException;
	void deleteFromDB() throws SQLException;
	void editInfoInDB() throws SQLException;
	void getData();
}
