package ToleranceParamPack.StrategyPack;

import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationPack.MeasResult;

public interface StrategyOfSuitability {
	boolean checkResult(MeasResult result, ToleranceParametrs tolerance);
}