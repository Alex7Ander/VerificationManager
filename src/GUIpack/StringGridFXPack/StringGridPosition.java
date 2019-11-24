package GUIpack.StringGridFXPack;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

public class StringGridPosition {
	private int width;
	private int height;
	private ScrollPane scrollContainer;
	private AnchorPane anchorContainer;
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public ScrollPane getScrollContainer() {
		return scrollContainer;
	}
	public AnchorPane getAnchorContainer() {
		 return anchorContainer;
	}
	public StringGridPosition(int Width, int Height, ScrollPane ScrollContainer, AnchorPane AnchorContainer){
		width = Width;
		height = Height;
		scrollContainer = ScrollContainer;
		anchorContainer = AnchorContainer;
	}

}