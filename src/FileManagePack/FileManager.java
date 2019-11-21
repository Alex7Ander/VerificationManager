package FileManagePack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

public class FileManager {

	public static int LinesToItems(String filePath, List<String> collection) throws FileNotFoundException, IOException {
		collection.clear();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
        String item = null;
        while ((item = reader.readLine()) != null) {
        	collection.add(item);
        }
        reader.close();
		return collection.size();		
	}	
	
	public static int LinesToItems(String filePath, int count, List<String> collection) throws FileNotFoundException, IOException {
		collection.clear();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
        String item = null;
        for (int i=0; i<count; i++) {
        	item = reader.readLine();
        	collection.add(item);
        }
        reader.close();
		return collection.size();		
	}
	
	public static int LinesToDouble(String filePath, List<Double> collection) throws FileNotFoundException, IOException{
		collection.clear();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
        String item = null;
        while ((item = reader.readLine()) != null) {     	
        	double currentValue = 0;
        	try {
        		currentValue = Double.parseDouble(item);
        	}
        	catch(NumberFormatException nfExp) {
        		//
        	}
        	collection.add(currentValue);
        }
        reader.close();
		return collection.size();		
	}	
	
	public static void ItemsToLines(String filePath, List<String> collection) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(
			new OutputStreamWriter(new FileOutputStream(filePath, false), "UTF8"))){ 
				for (int i=0; i<collection.size(); i++) {
					bw.write(collection.get(i)); 
					if (i != collection.size()-1) {bw.write("\n");
				}
			}
		}
	}
	
	public static void WriteFile(String filePath, String coding, List<String> collection) throws IOException {
		try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, false), coding))){
				for (int i=0; i<collection.size(); i++) {
					bw.write(collection.get(i));
				}
		}
	}
	
}
