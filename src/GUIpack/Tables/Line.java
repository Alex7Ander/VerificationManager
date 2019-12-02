package GUIpack.Tables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Line {
	protected Map<String, String> values;
	
	public Line(List<String> heads){
		values = new HashMap<String, String>();
		for (int i = 0; i < heads.size(); i++) {
			values.put(heads.get(i), "15");
		}
	}
	
	public void deleteValue(int index) {
		Set<String> keys = (TreeSet<String>) values.keySet();
		String key = keys.toArray()[index].toString();
		values.remove(key);
		values.put(key, "-");
	}

}
