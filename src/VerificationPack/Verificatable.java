package VerificationPack;

import java.util.HashMap;

//Verificatable ����� ���� ����� MeasResult.
//� ����� isSutable ������ ������������ ��������� �������� � �� ������ ����� ��������������� �����������

public interface Verificatable {
	boolean isSuitable(MeasResult result, HashMap<Double, Double> report);
}
