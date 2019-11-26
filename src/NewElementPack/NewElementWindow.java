package NewElementPack;

import java.io.IOException;
import java.sql.SQLException;
import AboutMessageForm.AboutMessageWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Element;
import Exceptions.SavingException;
import GUIpack.guiWindow;
import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.TimeType;
import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationPack.MeasResult;
import YesNoDialogPack.YesNoWindow;

public class NewElementWindow extends guiWindow {

	public NewElementWindow() throws IOException {
		super("", "NewElementForm.fxml");
		NewElementController ctrl = (NewElementController) loader.getController();
		//��������� �������� �� �������� ��������� ���������� ����� ���������
		stage.setOnCloseRequest( event -> {
			
			ctrl.saveValues();			
			int countOfControlledParams = 0;
			if (ctrl.getTwoPoleRB().isSelected())
				countOfControlledParams = 1;
			else 
				countOfControlledParams = 4;
			if (!ctrl.getPrimaryParamsTable().isFull(countOfControlledParams)) {
				int answer = YesNoWindow.createYesNoWindow("��������", "�� �� ��������� ��������� �������\n� ����������� ��������� �������.\n������� ���������� ��������������?").showAndWait();
				if (answer == 0) {
					event.consume();					
				}
				return;
			}				
			if (!ctrl.getPeriodicParamsTable().isFull(countOfControlledParams)) {
				int answer = YesNoWindow.createYesNoWindow("��������", "�� �� ��������� ��������� �������\n� ����������� ������������� �������.\n������� ���������� ��������������?").showAndWait();
				if (answer == 0) {
					event.consume();
				}
			}
		});
	}
	
	public NewElementWindow(Element elm) throws IOException {		
		super(elm.getType(), "NewElementForm.fxml");
		NewElementController ctrl = (NewElementController) loader.getController();
		ctrl.setElement(elm);		
		stage.setOnCloseRequest( event -> {
			
			ctrl.saveValues();
			int answer = YesNoWindow.createYesNoWindow("��������� ���������", "��������� ��������� ���� ���������?").showAndWait();
			//���� ��������� ���������� ���������:
			if (answer == 0) {
				ToleranceParametrs newModulePrimaryTP = new ToleranceParametrs(TimeType.PRIMARY, MeasUnitPart.MODULE, ctrl, elm);
				ToleranceParametrs newModulePeriodicTP = new ToleranceParametrs(TimeType.PERIODIC, MeasUnitPart.MODULE, ctrl, elm);
				ToleranceParametrs newPhasePrimaryTP = new ToleranceParametrs(TimeType.PRIMARY, MeasUnitPart.PHASE, ctrl, elm);				
				ToleranceParametrs newPhasePeriodicTP = new ToleranceParametrs(TimeType.PERIODIC, MeasUnitPart.PHASE, ctrl, elm);
				MeasResult newNominals = new MeasResult(ctrl, elm);
				try {
					DataBaseManager.getDB().BeginTransaction();
					elm.rewriteParams(newModulePrimaryTP, newModulePeriodicTP, newPhasePrimaryTP, newPhasePeriodicTP);
					if (!elm.getNominal().equals(newNominals)) {
						elm.getNominal().deleteFromDB();
						newNominals.saveInDB();
						newNominals.setNominalStatus();
					}
					DataBaseManager.getDB().Commit();
					AboutMessageWindow.createWindow("�������", "��������� ������� ���������").show();		
				} 
				catch (SQLException sqlExp) {
					DataBaseManager.getDB().RollBack();
					AboutMessageWindow.createWindow("������", "������ ������� � ���� ������").show();										
				} catch (SavingException sExp) {
					sExp.printStackTrace();
					AboutMessageWindow.createWindow("������", sExp.getMessage()).show();
				}								
			}	
		});		
	}	
	
	public void setTitle(String newTitle) {
		title = newTitle;
	}
		
	public void showAndWait() {
		stage.showAndWait();
	}
}