package GUIpack.Tables;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Line {
	protected Map<Integer, String> values;
	
	public Line(int count){
		values = new HashMap<Integer, String>();
		for (int i = 0; i < count; i++) {
			values.put(i, "-");
		}
	}
	
	public void deleteValue(int index) {
		Set<Integer> keys = (TreeSet<Integer>) values.keySet();
		Integer key = (Integer) keys.toArray()[index];
		values.remove(key);
		values.put(key, "-");
	}

}