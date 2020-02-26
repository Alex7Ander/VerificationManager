package _tempHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Adapter {

    public static ArrayList<String> ListDoubleToListString(ArrayList<Double> listD){
        ArrayList<String> listS = new ArrayList<String>();
        for (Double d : listD) {
            String s = d.toString();
            listS.add(s);
        }
        return listS;
    }

    public static <T1, T2> List<T2> MapToArrayList(Map<T1, T2> map){
        List<T2> arrayList = new ArrayList<T2>();
        for (T1 key : map.keySet()) {
            arrayList.add(map.get(key));
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
