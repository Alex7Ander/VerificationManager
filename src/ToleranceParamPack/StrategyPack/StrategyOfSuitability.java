package ToleranceParamPack.StrategyPack;

import ToleranceParamPack.Parametrspack.ToleranceParametrs;
import VerificationPack.MeasResult;

public interface StrategyOfSuitability {
	boolean checkResult(MeasResult result, ToleranceParametrs tolerance);
}