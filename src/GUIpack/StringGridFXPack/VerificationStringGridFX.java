package GUIpack.StringGridFXPack;

import java.util.ArrayList;

public class VerificationStringGridFX extends StringGridFX {
	
	private static ArrayList<String> tableHeads;
	
	public VerificationStringGridFX(StringGridPosition position){
		super(7, 10, position, tableHeads); //7, 10, 1110, 100, scrollPane, tablePane, heads
	}

	static {
		tableHeads = new ArrayList<String>();
		tableHeads.add("Частота, ГГц");	
		tableHeads.add("Измер. знач. модуля");	
		tableHeads.add("Погрешность");	
		tableHeads.add("Годен/не годен");	
		tableHeads.add("Измер. знач. фазы");	
		tableHeads.add("Погрешность");	
		tableHeads.add("Годен/не годен");
	}
	
}