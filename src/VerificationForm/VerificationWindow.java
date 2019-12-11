package VerificationForm;

import java.io.IOException;
import GUIpack.guiWindow;
import YesNoDialogPack.YesNoWindow;

public class VerificationWindow extends guiWindow{

	private static VerificationWindow instanceVerificationWindow;
		
	private VerificationWindow() throws IOException{
		super("�������", "VerificationForm.fxml");
		VerificationController ctrl = (VerificationController)loader.getController();
		ctrl.setStage(stage);
		stage.setOnCloseRequest(event -> {			
			if (!ctrl.resultIsSaved()) {
				int answer = YesNoWindow.createYesNoWindow("���������� ������� �� ���������", "���������� ������� �� ���������� � ��.\n�� ������� ��� ������ ��������� �������\n��� ���������� �����������?").showAndWait();
				if (answer == 1) {
					event.consume();
				}
			}
		});
	}
		
	public static VerificationWindow getVerificationWindow() throws IOException {
		if (instanceVerificationWindow == null) {
			instanceVerificationWindow = new VerificationWindow();			
		}
		return instanceVerificationWindow;
	}
	
	public VerificationController getController() {
		return this.loader.getController();
	}
	
}