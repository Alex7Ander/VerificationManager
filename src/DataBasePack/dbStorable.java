package DataBasePack;

import DevicePack.SavingException;

public interface dbStorable {

	public DataBaseManager AksolDataBase = DataBaseManager.getDB();

	void saveInDB() throws SavingException;
	void deleteFromDB();
	void editInfoInDB();
	void getData();
}
