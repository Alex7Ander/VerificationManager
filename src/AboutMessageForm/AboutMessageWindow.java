package AboutMessageForm;

import java.io.IOException;
import GUIpack.guiWindow;

public class AboutMessageWindow extends guiWindow{

    public String message;

    public AboutMessageWindow(String title, String Message) throws IOException {
        super(title, "AboutMessageForm.fxml");
        System.out.println("1");
        message = Message;
        MessageController controller = (MessageController) loader.getController();
        controller.setMessage(Message);
    }

}
