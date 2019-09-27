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
		
		//Установим действия по проверке введенных параметров перед закрытием
		this.stage.setOnCloseRequest( event->{
			ctrl.remeberTables();
			//Проверим частоты
			if (!ctrl.checkfreqTable()) {
				try {
					YesNoWindow qWin = new YesNoWindow("Некорректное значение частоты", "Одно из введенных вами значений частоты некорректно.\nЖелаете продолжить редактирование?");
					int answer = qWin.showAndWait();
					if (answer == 0) {
						event.consume();
					}
				} catch(IOException ioExp) {
					ioExp.getStackTrace();
				}
			}	
			
			//Проверим прочие параметры
			int errorsCount = ctrl.checkInfo();
			if (errorsCount != 0) {
				try {
					YesNoWindow qWin2 = new YesNoWindow("Некорректное значение параметров", Integer.toString(errorsCount) + " значений введенных вами параметров некорректны.\n"
							+ "Это может привести к неправльному определению\nпригодности поверяемго прибора.\nЖелаете продолжить редактирование?");
					int answerParams = qWin2.showAndWait();
					if (answerParams == 0) {
						event.consume();
					}
				} catch(IOException ioExp) {
					ioExp.getStackTrace();
				}
			}			
			//если пользователя все удовлетворяет, то в этом случае записываем внесенные данные в характеристики элемента
			ctrl.initializeElement();
		});
	}	
	public void setTitle(String newTitle) {
		title = newTitle;
	}	
}