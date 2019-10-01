package _tempHelpers;

import java.util.ArrayList;
import java.util.HashMap;

public class Adapter {
	
	public static ArrayList<String> ListDoubleToListString(ArrayList<Double> listD){
		ArrayList<String> listS = new ArrayList<String>();
		for (Double d : listD) {
			String s = d.toString();
			listS.add(s);
		}
		return listS;
	}
	
	public static <T1, T2, T3> ArrayList<T3> HashMapToArrayList(ArrayList<T1> keys, HashMap<T2, T3> hashMap){
		int l = hashMap.size();
		ArrayList<T3> arrayList = new ArrayList<T3>();
		for (int i=0; i<l; i++) {
			arrayList.add(hashMap.get(keys.get(i)));
		}
		return arrayList;
	}
	
	public static double textToDouble(String text, double defValue) {
		try {
			text = text.replace(',', '.');
			double val = Double.parseDouble(text); 
			return val;
		}
		catch(Exception exp){
			return defValue;
		}
	}
}