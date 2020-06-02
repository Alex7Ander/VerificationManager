package ProtocolCreatePack;

import java.io.IOException;
import java.util.List;

import GUIpack.guiWindow;
import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationPack.VerificationProcedure;
import VerificationPack.MeasResult;

public class ProtocolCreateWindow extends guiWindow{

	private static ProtocolCreateWindow instanceProtocolCreateWindow;
	
	private ProtocolCreateWindow(String[] docTypes, 
								List<MeasResult> results, 
								List<MeasResult> nominals, 
								List<ToleranceParametrs> protocoledModuleToleranceParams, 
								List<ToleranceParametrs> protocoledPhaseToleranceParams,
								VerificationProcedure verification) throws IOException {
		super("Создание протокола", "ProtocolCreateForm.fxml");
		ProtocolCreateController ctrl = (ProtocolCreateController) this.loader.getController();
		ctrl.setDocTypes(docTypes);
		ctrl.setResults(results);
		ctrl.setNominals(nominals);
		ctrl.setModuleToleranceParams(protocoledModuleToleranceParams);
		ctrl.setPhaseToleranceParams(protocoledPhaseToleranceParams);
		ctrl.setVerificationProcedure(verification);
	}
	
	public static ProtocolCreateWindow getProtocolCreateWindow(String[] docTypes, 
																List<MeasResult> results, 
																List<MeasResult> nominals, 
																List<ToleranceParametrs> protocoledModuleToleranceParams, 
																List<ToleranceParametrs> protocoledPhaseToleranceParams,
																VerificationProcedure verification) throws IOException {
		if (instanceProtocolCreateWindow == null) {
			instanceProtocolCreateWindow = new ProtocolCreateWindow(docTypes, results, nominals, protocoledModuleToleranceParams, protocoledPhaseToleranceParams, verification);
		}
		return instanceProtocolCreateWindow;
	}
	
	public static void deleteProtocolCreateWindow() {
		instanceProtocolCreateWindow = null;
	}
	
	public static void closeInstanceWindow() {
		instanceProtocolCreateWindow.close();
	}

}