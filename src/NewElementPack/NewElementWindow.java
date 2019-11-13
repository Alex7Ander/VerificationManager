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
		//Установим действия по проверке введенных параметров перед закрытием
		stage.setOnCloseRequest( event -> {
			ctrl.saveValues();
			/*
			if (!ctrl.checkPrimaryTable()) {
				try {
					YesNoWindow  qWin = new YesNoWindow("Внимание", "Вы не полностью заполнили таблицу\nс параметрами первичной поверки.\nЖелаете продолжить редактирование?");
					int answer = qWin.showAndWait();
					if (answer == 1) {	
						return;
					}
					else{
						event.consume();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				if (!ctrl.checkPeriodicTable()) {
					try {
						YesNoWindow  qWin = new YesNoWindow("Внимание", "Вы не полностью заполнили таблицу\nс параметрами периодической поверки.\nЖелаете продолжить редактирование?");
						int answer = qWin.showAndWait();
						if (answer == 1) {
							return;
						}
						else{
							event.consume();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			*/
		});
	}
	
	public NewElementWindow(Element elm) throws IOException {		
		super(elm.getType(), "NewElementForm.fxml");
		NewElementController ctrl = (NewElementController) loader.getController();
		ctrl.setElement(elm);		
		stage.setOnCloseRequest( event -> {
			/*
			ctrl.remeberTables();
			int answer = 1;
			try {
				YesNoWindow qWin = new YesNoWindow("Сохранить изменения", "Сохранить внесенный вами изменения?");
				answer = qWin.showAndWait();
			} catch (IOException ioExp) {
				ioExp.getStackTrace();
			}
			//Если изменения необходимо сохранить:
			if (answer == 0) {
				int checkParamsResult = checkParams();
				if (checkParamsResult == 1) {
					event.consume();
				}	
				else {
					try {
						DataBaseManager.getDB().BeginTransaction();
						//ctrl.getElement().rewriteParams(ctrl.getPrimaryTP(), ctrl.getPeriodicTP(), ctrl.getNominals());
						ctrl.getElement().rewriteParams();
						DataBaseManager.getDB().Commit();
						try {
							AboutMessageWindow errorMsg = new AboutMessageWindow("Успешно", "Изменения успешно сохранены");
							errorMsg.show();
						} catch (IOException ioExp) {
							ioExp.getStackTrace();
						}		
					} catch (SQLException sqlExp) {
						DataBaseManager.getDB().RollBack();
						try {
							AboutMessageWindow errorMsg = new AboutMessageWindow("Ошибка", "Ошибка доступа к базе данных");
							errorMsg.show();
						} catch (IOException ioExp) {
							ioExp.getStackTrace();
						}											
					}				
				}				
			}*/	
		});		
	}	
/*
	private int checkParams() {
		NewElementController ctrl = (NewElementController) loader.getController();
		//Проверим частоты
		if (!ctrl.checkfreqTable()) {
			try {
				YesNoWindow qWin = new YesNoWindow("Некорректное значение частоты", "Одно из введенных вами значений частоты некорректно.\nЖелаете продолжить редактирование?");
				int answer = qWin.showAndWait();
				if (answer == 1) {
					return 0;
				}
				else {
					return 1;
				}
			} catch(IOException ioExp) {
				ioExp.getStackTrace();
				return 0;
			}
		}				
		//Проверим прочие параметры
		int errorsCount = ctrl.checkInfo();
		if (errorsCount != 0) {
			try {
				YesNoWindow qWin2 = new YesNoWindow("Некорректное значение параметров", Integer.toString(errorsCount) + " значений введенных вами параметров некорректны.\n"
						+ "Это может привести к неправльному определению\nпригодности поверяемго прибора.\nЖелаете продолжить редактирование?");
				int answerParams = qWin2.showAndWait();
				if (answerParams == 1) {
					return 0;					
				}
				else {
					return 1;
				}
			} catch(IOException ioExp) {
				ioExp.getStackTrace();
				return 0;
			}
		}
		return 0;
	}
*/	
	public void setTitle(String newTitle) {
		title = newTitle;
	}
}