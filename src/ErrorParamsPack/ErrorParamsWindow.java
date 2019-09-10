package ErrorParamsPack;

import java.io.IOException;
import GUIpack.guiWindow;

public class ErrorParamsWindow extends guiWindow {

	private static ErrorParamsWindow instanceErrorParamsWindow;
	
	private ErrorParamsWindow() throws IOException {
		super("Параметры погрешности", "ErrorParamsForm.fxml");
	}
	
	public static ErrorParamsWindow getErrorParamsWindow() throws IOException{
		if (instanceErrorParamsWindow == null) {
			instanceErrorParamsWindow = new ErrorParamsWindow();
		}
		return instanceErrorParamsWindow;
	}

}
