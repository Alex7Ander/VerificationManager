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
		tableHeads.add("�������, ���");
		tableHeads.add("������ ������");
		tableHeads.add("�������");
		tableHeads.add("������� ������");
		tableHeads.add("������ ������");
		tableHeads.add("�������");
		tableHeads.add("������� ������");
	}
		
	public NewElementStringGridFX(StringGridPosition position) {
		super(7, 10, position, tableHeads);
	}


}
