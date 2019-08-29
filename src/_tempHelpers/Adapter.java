package _tempHelpers;

import java.util.ArrayList;
import java.util.HashMap;

public class Adapter {

	public static <T> ArrayList<T> HashMapToArrayList(ArrayList<T> keys, HashMap<T, T> hashMap){
		int l = hashMap.size();
		ArrayList<T> arrayList = new ArrayList<T>();
		for (int i=0; i<l; i++) {
			arrayList.add(hashMap.get(keys.get(i)));
		}
		return arrayList;
	}
}
