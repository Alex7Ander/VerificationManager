package GUIpack.StringGridFXPack;

import java.util.ArrayList;

import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationPack.MeasResult;

public class NewElementStringGridFX extends StringGridFX {

	private static ArrayList<String> tableHeads;
	private MeasResult nominal;
	private ToleranceParametrs primaryModuleTP;
	private ToleranceParametrs primaryPhaseTP;
	private ToleranceParametrs periodicModuleTP;
	private ToleranceParametrs periodicPhaseTP;
	
	static {
		tableHeads = new ArrayList<String>();
		tableHeads.add("Частота, ГГц");
		tableHeads.add("Нижний допуск");
		tableHeads.add("Номинал");
		tableHeads.add("Верхний допуск");
		tableHeads.add("Нижний допуск");
		tableHeads.add("Номинал");
		tableHeads.add("Верхний допуск");
	}
		
	public NewElementStringGridFX(StringGridPosition position) {
		super(7, 10, position, tableHeads);
	}


}
