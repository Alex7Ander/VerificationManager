package GUIpack.Tables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Line {
	protected Map<Integer, String> values = new HashMap<Integer, String>();
	
	public Line(int count){
		for (int i = 0; i < count; i++) {
			values.put(i, "-");
		}
	}
	
	public Line(List<String> textLine) {		
		for (int i = 0; i < textLine.size(); i++) {
			String value = textLine.get(i);
			values.put(i, value);	
		}
	}
	
	public void deleteValue(int index) {
		Set<Integer> keys = (TreeSet<Integer>) values.keySet();
		Integer key = (Integer) keys.toArray()[index];
		values.remove(key);
		values.put(key, "-");
	}
	
	public void edit(List<String> textLine) {
		for (int i = 0; i < values.size(); i++) {
			try {
				String value = textLine.get(i);
				values.remove(i);
				values.put(i, value);
			}
			catch(IndexOutOfBoundsException iExp) {
				break;
			}
		}
	}

}