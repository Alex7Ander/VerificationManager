package ProtocolCreatePack;

import java.io.IOException;
import java.util.List;

import DevicePack.Device;
import GUIpack.guiWindow;
import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationPack.VerificationProcedure;
import VerificationPack.MeasResult;

public class ProtocolCreateWindow extends guiWindow{

	private static ProtocolCreateWindow instanceProtocolCreateWindow;
	private static ProtocolCreateController ctrl;
	
	private ProtocolCreateWindow(Device device, 
								String[] docTypes, 
								List<MeasResult> results, 
								List<MeasResult> nominals, 
								List<ToleranceParametrs> protocoledModuleToleranceParams, 
								List<ToleranceParametrs> protocoledPhaseToleranceParams,
								VerificationProcedure verification) throws IOException {
		super("Создание протокола", "ProtocolCreateForm.fxml");
		ctrl = (ProtocolCreateController) this.loader.getController();
		ctrl.setDevice(device);
		ctrl.setDocTypes(docTypes);
		ctrl.setResults(results);
		ctrl.setNominals(nominals);
		ctrl.setModuleToleranceParams(protocoledModuleToleranceParams);
		ctrl.setPhaseToleranceParams(protocoledPhaseToleranceParams);
		ctrl.setVerificationProcedure(verification);
		stage.setOnCloseRequest(event -> {
			delete();
		});
	}
	
	public static ProtocolCreateWindow getProtocolCreateWindow(Device device,
																String[] docTypes, 
																List<MeasResult> results, 
																List<MeasResult> nominals, 
																List<ToleranceParametrs> protocoledModuleToleranceParams, 
																List<ToleranceParametrs> protocoledPhaseToleranceParams,
																VerificationProcedure verification) throws IOException {
		if (instanceProtocolCreateWindow == null) {
			instanceProtocolCreateWindow = new ProtocolCreateWindow(device, docTypes, results, nominals, protocoledModuleToleranceParams, protocoledPhaseToleranceParams, verification);
		}
		return instanceProtocolCreateWindow;
	}
	
	public static void delete() {
		instanceProtocolCreateWindow = null;
	}
	
	public static void closeInstanceWindow() {
		instanceProtocolCreateWindow.close();
	}
	
	public static ProtocolCreateController getController() {
		return ctrl;
	}

}