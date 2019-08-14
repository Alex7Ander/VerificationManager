package VerificationPack;

import java.util.HashMap;

import ToleranceParamPack.ToleranceParametrs;

//Verificatable олжен быть класс MeasResult.
//В метод isSutable должны передаваться параметры годности и на основе этого устанавливается пригодность

public interface Verificatable {
	boolean isSuitable(MeasResult result, HashMap<Double, Double> report);
}
