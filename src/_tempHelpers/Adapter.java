package _tempHelpers;

import java.util.ArrayList;
import java.util.HashMap;

public class Adapter {

	public static <T1, T2, T3> ArrayList<T3> HashMapToArrayList(ArrayList<T1> keys, HashMap<T2, T3> hashMap){
		int l = hashMap.size();
		ArrayList<T3> arrayList = new ArrayList<T3>();
		for (int i=0; i<l; i++) {
			arrayList.add(hashMap.get(keys.get(i)));
		}
		return arrayList;
	}
}
