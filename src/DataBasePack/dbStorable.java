package DataBasePack;

import java.sql.SQLException;
import java.util.HashMap;

public interface dbStorable {

	public DataBaseManager AksolDataBase = DataBaseManager.getDB();

	void saveInDB() throws SQLException;
	void deleteFromDB() throws SQLException;
	void editInfoInDB(HashMap<String, String> editingValues) throws SQLException;
}
