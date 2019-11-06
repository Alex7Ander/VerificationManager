package FileManagePack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import _tempHelpers.Adapter;

public class ResultReaderManager {
	private ArrayList<String> fileStrings;
	private String filePath;
	
	public ResultReaderManager(String Path) throws IOException {
		filePath = Path;
		fileStrings = new ArrayList<String>();
		try(BufferedReader bf = new BufferedReader(new FileReader(filePath))){
			String str;
			while((str=bf.readLine()) != null) {
				fileStrings.add(str);
			}
		}
	}
	
	private void findResult(int numberOfResult, ArrayList<String> resStrings) throws IOException{
		String str = "";	
		//Если файл не был пуст, то обрабатываем прочитанные строки
		int stringNumber = 0;
		int currentResult = 0;
		while (stringNumber < fileStrings.size()){
			str = fileStrings.get(stringNumber);
			stringNumber++;
			if (str.startsWith("---")) {
				//если мы нашли ---, то значит далее идут результаты для какого-либо элемента
				currentResult++;
		    	if (currentResult == numberOfResult) {
		    		//если это нужные нам результаты, то
		    		stringNumber += 3;
		    		str = fileStrings.get(stringNumber);
		    		while(! (str.equals("end"))) {
		    			resStrings.add(str);
		    			stringNumber++;
		    			str = fileStrings.get(stringNumber);
		    		}	
		    		break;
		    	}
		    }
		}//end while
	}
	
	private void parseResult(ArrayList<String> res, ArrayList<Double> freqs, HashMap<String, HashMap<Double, Double>> values) {

		//Узнать, сколько параметров измеренно
		String firstStr = res.get(0);
		String[] firstStringValues = firstStr.split("\t");
		int countOfFreqs = res.size();
		int countOfParams = firstStringValues.length;	

		//Считываем значения
		ArrayList<ArrayList<Double>> tempArrays = new ArrayList<ArrayList<Double>>();
		for (int j=0; j<countOfParams; j++) {
			tempArrays.add(new ArrayList<Double>());			
		}

		for (int i=0; i<countOfFreqs; i++) {			
			String[] line = res.get(i).split("\t");	
			int column = 0;
			for (String st: line) {
				/*
				String text = st.replace(',', '.');
				tempArrays.get(column).add(Double.parseDouble(text));
				*/
				double cValue = Adapter.textToDouble(st, 0);
				tempArrays.get(column).add(cValue);
				column++;
			}
		}
		for (double val : tempArrays.get(0)) freqs.add(val);

		String keys[] = {"MODULE_S11", "ERROR_MODULE_S11", "PHASE_S11", "ERROR_PHASE_S11", 
				 		 "MODULE_S12", "ERROR_MODULE_S12", "PHASE_S12", "ERROR_PHASE_S12",
				 		 "MODULE_S21", "ERROR_MODULE_S21", "PHASE_S21", "ERROR_PHASE_S21", 
				 		 "MODULE_S22", "ERROR_MODULE_S22", "PHASE_S22", "ERROR_PHASE_S22"};
		
		for (int i=1; i<countOfParams; i++) {
			HashMap<Double, Double> oneFreqHM = new HashMap<Double, Double>();
			for (int j=0; j<countOfFreqs; j++) {
				oneFreqHM.put(freqs.get(j), tempArrays.get(i).get(j));
			}
			values.put(keys[i-1], oneFreqHM);
		}
		
	}

	public void readResult(int numberOfResult, ArrayList<Double> freqs, HashMap<String, HashMap<Double, Double>> values) throws IOException{		
		ArrayList<String> res = new ArrayList<String>();
		findResult(numberOfResult, res);
		parseResult(res, freqs, values);
	}
	
}