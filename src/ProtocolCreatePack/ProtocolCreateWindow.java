package ProtocolCreatePack;

import java.io.IOException;

import GUIpack.guiWindow;

public class ProtocolCreateWindow extends guiWindow{

	private static ProtocolCreateWindow instanceProtocolCreateWindow;
	
	private ProtocolCreateWindow(String[] docTypes) throws IOException {
		super("Создание протокола", "ProtocolCreateForm.fxml");
		ProtocolCreateController ctrl = (ProtocolCreateController) this.loader.getController();
		ctrl.setDocTypes(docTypes);
	}
	
	public static ProtocolCreateWindow getProtocolCreateWindow(String[] docTypes) throws IOException {
		if (instanceProtocolCreateWindow == null) {
			instanceProtocolCreateWindow = new ProtocolCreateWindow(docTypes);
		}
		return instanceProtocolCreateWindow;
	}
	
	public static void deleteProtocolCreateWindow() {
		instanceProtocolCreateWindow = null;
	}

}