package GUIpack.StringGridFXPack;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

public class StringGridPosition {
	private int width;
	private int height;
	private ScrollPane scrollContainer;
	private AnchorPane anchorContainer;
	
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}
	public ScrollPane getScrollContainer() {
		return this.scrollContainer;
	}
	public AnchorPane getAnchorContainer() {
		 return this.anchorContainer;
	}
	public StringGridPosition(int Width, int Height, ScrollPane ScrollContainer, AnchorPane AnchorContainer){
		width = Width;
		height = Height;
		scrollContainer = ScrollContainer;
		anchorContainer = AnchorContainer;
	}

}