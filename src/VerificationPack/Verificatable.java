package VerificationPack;

import java.util.HashMap;

import ToleranceParamPack.ToleranceParametrs;

//Verificatable ����� ���� ����� MeasResult.
//� ����� isSutable ������ ������������ ��������� �������� � �� ������ ����� ��������������� �����������

public interface Verificatable {
	boolean isSuitable(MeasResult result, HashMap<Double, Double> report);
}
