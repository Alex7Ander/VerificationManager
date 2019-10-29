package _tempHelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Adapter {
	
	public static ArrayList<String> ListDoubleToListString(ArrayList<Double> listD){
		ArrayList<String> listS = new ArrayList<String>();
		for (Double d : listD) {
			String s = d.toString();
			listS.add(s);
		}
		return listS;
	}
	
	public static <T1, T2> ArrayList<T2> HashMapToArrayList(HashMap<T1, T2> hashMap){
		ArrayList<T2> arrayList = new ArrayList<T2>();		
		for (T1 key : hashMap.keySet()) {
			arrayList.add(hashMap.get(key));
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