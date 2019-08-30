package ProtocolCreatePack;

import java.io.IOException;
import java.util.ArrayList;

import GUIpack.guiWindow;
import VerificationPack.MeasResult;

public class ProtocolCreateWindow extends guiWindow{

	private static ProtocolCreateWindow instanceProtocolCreateWindow;
	
	private ProtocolCreateWindow(String[] docTypes, ArrayList<MeasResult> results) throws IOException {
		super("Создание протокола", "ProtocolCreateForm.fxml");
		ProtocolCreateController ctrl = (ProtocolCreateController) this.loader.getController();
		ctrl.setDocTypes(docTypes);
		ctrl.setResults(results);
	}
	
	public static ProtocolCreateWindow getProtocolCreateWindow(String[] docTypes, ArrayList<MeasResult> results) throws IOException {
		if (instanceProtocolCreateWindow == null) {
			instanceProtocolCreateWindow = new ProtocolCreateWindow(docTypes, results);
		}
		return instanceProtocolCreateWindow;
	}
	
	public static void deleteProtocolCreateWindow() {
		instanceProtocolCreateWindow = null;
	}

}