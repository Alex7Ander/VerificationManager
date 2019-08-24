package _tempHelpers;

import java.util.Random;

import GUIpack.StringGridFX;

public class Randomizer {
	
	public static void fillStringGrid(StringGridFX stringGrid, int from) {
		Random rand = new Random();
		
		for (int col=from; col<stringGrid.getColCount(); col++) {
			for (int row=0; row<stringGrid.getRowCount(); row++) {				
				Float value = rand.nextFloat();
				stringGrid.setCellValue(col, row, value.toString());
			}
		}
			
	}
		
}