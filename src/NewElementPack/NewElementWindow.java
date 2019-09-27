package NewElementPack;

import java.io.IOException;

import DevicePack.Element;
import GUIpack.guiWindow;
import YesNoDialogPack.YesNoWindow;

public class NewElementWindow extends guiWindow {

	public NewElementWindow(Element elm) throws IOException {		
		super("", "NewElementForm.fxml");
		NewElementController ctrl = (NewElementController) loader.getController();
		ctrl.setWindow(this);
		ctrl.setElement(elm);
		
		//��������� �������� �� �������� ��������� ���������� ����� ���������
		this.stage.setOnCloseRequest( event -> {
			ctrl.remeberTables();
			//�������� �������
			if (!ctrl.checkfreqTable()) {
				try {
					YesNoWindow qWin = new YesNoWindow("������������ �������� �������", "���� �� ��������� ���� �������� ������� �����������.\n������� ���������� ��������������?");
					int answer = qWin.showAndWait();
					if (answer == 0) {
						//event.consume();
					}
				} catch(IOException ioExp) {
					ioExp.getStackTrace();
				}
			}	
			
			//�������� ������ ���������
			int errorsCount = ctrl.checkInfo();
			if (errorsCount != 0) {
				try {
					YesNoWindow qWin2 = new YesNoWindow("������������ �������� ����������", Integer.toString(errorsCount) + " �������� ��������� ���� ���������� �����������.\n"
							+ "��� ����� �������� � ������������ �����������\n����������� ���������� �������.\n������� ���������� ��������������?");
					int answerParams = qWin2.showAndWait();
					if (answerParams == 0) {
					//	event.consume();
					}
				} catch(IOException ioExp) {
					ioExp.getStackTrace();
				}
			}			
			//���� ������������ ��� �������������, �� � ���� ������ ���������� ��������� ������ � �������������� ��������
			ctrl.initializeElement();
		});
	}	
	public void setTitle(String newTitle) {
		title = newTitle;
	}	
}