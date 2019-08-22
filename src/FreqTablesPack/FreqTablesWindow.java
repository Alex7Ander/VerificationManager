package FreqTablesPack;

import java.io.IOException;
import GUIpack.guiWindow;
import NewElementPack.NewElementController;

public class FreqTablesWindow extends guiWindow {

	private static FreqTablesWindow instanceFreqTablesWindow;
	
	private FreqTablesWindow(NewElementController elemntController) throws IOException {
		super("��������� �����", "FreqTablesForm.fxml");
		FreqTablesController ftContr = (FreqTablesController) this.loader.getController();
		ftContr.setMyOwner(elemntController);
	}

	public static FreqTablesWindow getFreqTablesWindow(NewElementController elemntController) throws IOException{
		if (instanceFreqTablesWindow == null) {
			instanceFreqTablesWindow = new FreqTablesWindow(elemntController);
		}
		return instanceFreqTablesWindow;
	}
	
}
