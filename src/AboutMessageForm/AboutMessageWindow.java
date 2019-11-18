package AboutMessageForm;

import java.io.IOException;
import GUIpack.guiWindow;

public class AboutMessageWindow extends guiWindow{

    private AboutMessageWindow(String title, String message) throws IOException {
    	super(title, "AboutMessageForm.fxml");
        MessageController controller = (MessageController) loader.getController();
        controller.setMessage(message);
    }

    public static AboutMessageWindow createWindow(String title, String message) {
    	AboutMessageWindow aboutMessageWindow = null;
    	try {
    		aboutMessageWindow = new AboutMessageWindow(title, message);
    	}
    	catch (IOException ioExp) {
    		System.out.println("Ошибка при создании диалогового окна (" + message + ")" + ioExp.getMessage());
    	}
    	return aboutMessageWindow;
    }
}
