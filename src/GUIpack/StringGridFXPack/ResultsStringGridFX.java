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
		tableHeads.add("�������, ���");
		tableHeads.add("���. ����. ������");
		tableHeads.add("�����������");
		tableHeads.add("���. ����. ����");
		tableHeads.add("�����������");
	}
	
	public void setResult(MeasResult result) {
		
	}
}
