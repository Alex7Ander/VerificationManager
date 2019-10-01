package NewElementPack;

import java.io.IOException;
import java.sql.SQLException;

import AboutMessageForm.AboutMessageWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Element;
import GUIpack.guiWindow;
import YesNoDialogPack.YesNoWindow;

public class NewElementWindow extends guiWindow {

	public NewElementWindow() throws IOException {
		super("", "NewElementForm.fxml");
		NewElementController ctrl = (NewElementController) loader.getController();
		ctrl.setWindow(this);
		//��������� �������� �� �������� ��������� ���������� ����� ���������
		this.stage.setOnCloseRequest( event -> {
			ctrl.remeberTables();
			int checkParamsResult = checkParams();
			if (checkParamsResult == 1) {
				event.consume();
			}					
			//���� ������������ ��� �������������, �� � ���� ������ ���������� ��������� ������ � �������������� ��������
			ctrl.initializeElement();
		});
	}
	
	public NewElementWindow(Element elm) throws IOException {		
		super(elm.getType(), "NewElementForm.fxml");
		NewElementController ctrl = (NewElementController) loader.getController();
		ctrl.setWindow(this);
		ctrl.setElement(elm);
				
		this.stage.setOnCloseRequest( event -> {
			int answer = 1;
			try {
				YesNoWindow qWin = new YesNoWindow("��������� ���������", "��������� ��������� ���� ���������?");
				answer = qWin.showAndWait();
			} catch (IOException ioExp) {
				ioExp.getStackTrace();
			}
			if (answer == 0) {
				int checkParamsResult = checkParams();
				if (checkParamsResult == 1) {
					event.consume();
				}		
				try {
					DataBaseManager.getDB().BeginTransaction();
					ctrl.getElement().rewriteParams(ctrl.getPrimaryTP(), ctrl.getPeriodicTP(), ctrl.getNominals());
					DataBaseManager.getDB().Commit();
					try {
						AboutMessageWindow errorMsg = new AboutMessageWindow("�������", "��������� ������� ���������");
						errorMsg.show();
					} catch (IOException ioExp) {
						ioExp.getStackTrace();
					}		
				} catch (SQLException sqlExp) {
					DataBaseManager.getDB().RollBack();
					try {
						AboutMessageWindow errorMsg = new AboutMessageWindow("������", "������ ������� � ���� ������");
						errorMsg.show();
					} catch (IOException ioExp) {
						ioExp.getStackTrace();
					}											
				}				
			}
		});			
	}	

	private int checkParams() {
		NewElementController ctrl = (NewElementController) loader.getController();
		//�������� �������
		if (!ctrl.checkfreqTable()) {
			try {
				YesNoWindow qWin = new YesNoWindow("������������ �������� �������", "���� �� ��������� ���� �������� ������� �����������.\n������� ���������� ��������������?");
				int answer = qWin.showAndWait();
				if (answer == 0) {
					return 1;
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
					return 1;
					
				}
			} catch(IOException ioExp) {
				ioExp.getStackTrace();
			}
		}
		return 0;
	}
	
	public void setTitle(String newTitle) {
		title = newTitle;
	}
}