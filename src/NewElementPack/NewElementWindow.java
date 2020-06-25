package NewElementPack;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

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
		//Установим действия по проверке введенных параметров перед закрытием
		stage.setOnCloseRequest( event -> {
			
			ctrl.saveValues();			
			int countOfControlledParams = 0;
			if (ctrl.getTwoPoleRB().isSelected())
				countOfControlledParams = 1;
			else 
				countOfControlledParams = 4;
			
			if (!ctrl.getNominalsTable().isFull(countOfControlledParams)) {
				int answer = YesNoWindow.createYesNoWindow("Внимание", "Вы не полностью заполнили таблицу\n номинальных значений\n(значений предыдущей поверки).\nЖелаете продолжить редактирование?").showAndWait();
				if (answer == 0) {
					event.consume();
				}
			}
			if (!ctrl.getPrimaryToleranceParamsTable().isFull(countOfControlledParams)) {
				int answer = YesNoWindow.createYesNoWindow("Внимание", "Вы не полностью заполнили таблицу\nс критериями годности для первичной поверки.\nЖелаете продолжить редактирование?").showAndWait();
				if (answer == 0) {
					event.consume();					
				}
				return;
			}				
			if (!ctrl.getPeriodicToleranceParamsTable().isFull(countOfControlledParams)) {
				int answer = YesNoWindow.createYesNoWindow("Внимание", "Вы не полностью заполнили таблицу\nс критериями годности для периодической поверки.\nЖелаете продолжить редактирование?").showAndWait();
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
			int answer = YesNoWindow.createYesNoWindow("Сохранить изменения", "Сохранить внесенные вами изменения?").showAndWait();
			//Если изменения необходимо сохранить:
			if (answer == 0) {
				//Проверим базовую информацию
				HashMap<String, String> editingValues = new HashMap<String, String>();				
				editingValues.put("type", ctrl.getType());
				editingValues.put("serNumber", ctrl.getSerNum());
				editingValues.put("serNumber", ctrl.getSerNum());
				editingValues.put("measUnit", ctrl.getMeasUnit());
				editingValues.put("moduleToleranceType", ctrl.getModuleToleranceType());
				editingValues.put("phaseToleranceType", ctrl.getPhaseToleranceType());
								
				ToleranceParametrs newModulePrimaryTP = new ToleranceParametrs(TimeType.PRIMARY, MeasUnitPart.MODULE, ctrl, elm);
				ToleranceParametrs newModulePeriodicTP = new ToleranceParametrs(TimeType.PERIODIC, MeasUnitPart.MODULE, ctrl, elm);
				ToleranceParametrs newPhasePrimaryTP = new ToleranceParametrs(TimeType.PRIMARY, MeasUnitPart.PHASE, ctrl, elm);				
				ToleranceParametrs newPhasePeriodicTP = new ToleranceParametrs(TimeType.PERIODIC, MeasUnitPart.PHASE, ctrl, elm);
				MeasResult newNominals = new MeasResult(ctrl, elm);
				try {
					DataBaseManager.getDB().BeginTransaction();
					elm.editInfoInDB(editingValues);
					if(elm.getPoleCount() != ctrl.getPoleCount()) {
						elm.changePoleCount(ctrl.getPoleCount());
					}
					elm.rewriteParams(newModulePrimaryTP, newModulePeriodicTP, newPhasePrimaryTP, newPhasePeriodicTP);
					if (!elm.getNominal().equals(newNominals)) {
						elm.getNominal().deleteFromDB();
						newNominals.saveInDB();
						newNominals.setNominalStatus();
					}
					DataBaseManager.getDB().Commit();
					AboutMessageWindow.createWindow("Успешно", "Изменения успешно сохранены").show();		
				} 
				catch (SQLException sqlExp) {
					DataBaseManager.getDB().RollBack();
					AboutMessageWindow.createWindow("Ошибка", "Ошибка доступа к базе данных").show();										
				} catch (SavingException sExp) {
					sExp.printStackTrace();
					AboutMessageWindow.createWindow("Ошибка", sExp.getMessage()).show();
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