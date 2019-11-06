package GUIpack.StringGridFXPack;

import java.util.ArrayList;
import VerificationPack.MeasResult;

public class ResultsStringGridFX extends StringGridFX {

	private static ArrayList<String> tableHeads;
	public ResultsStringGridFX(StringGridPosition position) {
		super(5, 10, position, tableHeads);
	}

	static {
		tableHeads = new ArrayList<String>();
		tableHeads.add("Частота, ГГц");
		tableHeads.add("Изм. знач. модуля");
		tableHeads.add("Погрешность");
		tableHeads.add("Изм. знач. фазы");
		tableHeads.add("Погрешность");
	}
	
	public void setResult(MeasResult result) {
		
	}
}
