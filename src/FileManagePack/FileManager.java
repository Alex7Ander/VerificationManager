package FileManagePack;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class FileManager {

	public static int LinesToItems(String filePath, Collection<String> collection) throws FileNotFoundException, IOException {
		collection.clear();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
        String item = null;
        while ((item = reader.readLine()) != null) {
        	collection.add(item);
        }
        reader.close();
		return collection.size();		
	}	
	
	public static int LinesToItems(String filePath, int count, Collection<String> collection) throws FileNotFoundException, IOException {
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
}
