package ErrorParamsPack;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import AboutMessageForm.AboutMessageWindow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ErrorParamsController {

	public ErrorParams erp;
	@FXML
	private Label label;
	@FXML
	private TextField a1TextField;
	@FXML
	private TextField b1TextField;
	@FXML
	private TextField c1TextField;
	@FXML
	private TextField d1TextField;
	@FXML
	private TextField e1TextField;
	@FXML
	private TextField a2TextField;
	@FXML
	private TextField b2TextField;
	@FXML
	private TextField c2TextField;
	@FXML
	private TextField d2TextField;
	
	@FXML
	private ComboBox<String> tractComboBox;
	
	@FXML
	private Button saveBtn;
	
	@FXML
	private void initialize() throws IOException {
		try {
			erp = new ErrorParams();
		}
		catch(SQLException sqlExp) {
			AboutMessageWindow msg = new AboutMessageWindow("Ошибка", "Не удалось найти параметры\nНет свзи с БД");
			msg.show();
		}
		
		tractComboBox.setItems(FXCollections.observableArrayList(new String("5,2"), 
																 new String("3,6"), 
																 new String("2,4"),  
																 new String("1,6")));
	}
	@FXML
	private void tractComboBoxClick() {
		int index = this.tractComboBox.getSelectionModel().getSelectedIndex();
		switch(index) {
			case 0:
				representErrorParams(erp.value.get("5,2"));
				break;
			case 1:
				representErrorParams(erp.value.get("3,6"));
				break;
			case 2:
				representErrorParams(erp.value.get("2,4"));
				break;
			case 3:
				representErrorParams(erp.value.get("1,6"));
				break;
		}
	}
	@FXML
	private void saveBtnClick() throws IOException {		
		try {
			ArrayList<Double> newParams = new ArrayList<Double>();
			newParams.add(Double.parseDouble(this.a1TextField.getText()));
			newParams.add(Double.parseDouble(this.b1TextField.getText()));
			newParams.add(Double.parseDouble(this.c1TextField.getText()));
			newParams.add(Double.parseDouble(this.d1TextField.getText()));
			newParams.add(Double.parseDouble(this.e1TextField.getText()));
			newParams.add(Double.parseDouble(this.a2TextField.getText()));
			newParams.add(Double.parseDouble(this.b2TextField.getText()));
			newParams.add(Double.parseDouble(this.c2TextField.getText()));
			newParams.add(Double.parseDouble(this.d2TextField.getText()));			
			int index = this.tractComboBox.getSelectionModel().getSelectedIndex();
			String tract = this.tractComboBox.getSelectionModel().getSelectedItem();
			switch(index) {
				case 0:
					this.erp.setTr5(newParams);
					break;
				case 1:
					this.erp.setTr3(newParams);
					break;
				case 2:
					this.erp.setTr2(newParams);
					break;
				case 3:
					this.erp.setTr1(newParams);	
					break;
			}
			erp.saveInDB();
			AboutMessageWindow msg = new AboutMessageWindow("Успешно", "Параметры для тракта " +tract+ "\nуспешно сохранены в БД");
			msg.show();
		}
		catch(NumberFormatException nfExp) {
			AboutMessageWindow msg = new AboutMessageWindow("Ошибка", "Введенные вами параметры\nне являются действительными числами");
			msg.show();
		}
		catch(SQLException sqlExp) {
			AboutMessageWindow msg = new AboutMessageWindow("Ошибка", "Не удалось сохранить изменения\nНет свзи с БД");
			msg.show();
		}
	}
	
	public void representErrorParams(ArrayList<Double> erParams) {
		this.a1TextField.setText(erParams.get(0).toString());	
		this.b1TextField.setText(erParams.get(1).toString());
		this.c1TextField.setText(erParams.get(2).toString());
		this.d1TextField.setText(erParams.get(3).toString());
		this.e1TextField.setText(erParams.get(4).toString());
		this.a2TextField.setText(erParams.get(5).toString());
		this.b2TextField.setText(erParams.get(6).toString());
		this.c2TextField.setText(erParams.get(7).toString());
		this.d2TextField.setText(erParams.get(8).toString());
	}
	
	
}
