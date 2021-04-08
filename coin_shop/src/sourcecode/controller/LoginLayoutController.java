package sourcecode.controller;

import java.io.IOException;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;
import sourcecode.MainApp;

import sourcecode.controller.DBConnection;
import sourcecode.model.CustomerMySelf;
import sourcecode.model.DAOCategory;
import sourcecode.model.DAOCompany;
import sourcecode.model.Category;
import sourcecode.model.Company;
import sourcecode.model.Customer;

public class LoginLayoutController implements Initializable {
	MainApp mainApp; 
	Scene thisScene;
	
	@FXML private TextField tfID;
	@FXML private PasswordField tfPW; 
	@FXML private Button btnLogin;
	@FXML private Button btnRegisterMember;
	@FXML private Button btnExit;
	
	private boolean bID;
	private boolean bPW;
	private boolean bLoginSuccess;
	private CustomerMySelf customerMyself;

	
	
	@FXML
	void onBtnClickedLogin(ActionEvent event) {
		System.out.println("�α��� ��ư Ŭ��");
		String id = tfID.getText();
		String pw = tfPW.getText();
		
		//if login success �� id, pw���� Ȯ���Ұ�
		// 1 ���Ͻ� �α��� ����, 0 ���Ͻ� �α��� ���� ����
		System.out.println(id + ", " + pw);
		bLoginSuccess = funcCheckLogin(id, pw);

		if(bLoginSuccess == false) {
			//ID �ߺ�
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("login Error");
			alert.setHeaderText("�α��� ���� : ���̵� �Ǵ� ��й�ȣ�� Ȯ�����ּ���.");
			alert.showAndWait();
		}
		else { // �α��� ����
			Stage pop = (Stage)btnLogin.getScene().getWindow();
			pop.close();
		}	
	}
	
	   private boolean funcCheckLogin(String strID, String strPW) {
	    	
			String runP = "{ call customer_login(?, ?, ?) }";
		
				try {
					Connection conn = DBConnection.getConnection();
					Statement stmt = conn.createStatement();
					CallableStatement callableStatement = conn.prepareCall(runP.toString());
					callableStatement.setString(1, strID);
					callableStatement.setString(2, strPW);
					callableStatement.registerOutParameter(3, java.sql.Types.INTEGER);
					callableStatement.executeUpdate();	
					
					int check = callableStatement.getInt(3);
					if(check == 0) {
						System.out.println("�α��� ����");
							return false;
					}
					else {
						System.out.println("�α��� ����");
						mainApp.procCallCustomerInfo(strID);
						return true;
					}
				} catch (SQLException e) {
					System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				return true; //Success
	    }
	   
	   
	   
	   

	@FXML
	void onBtnClickedRegisterMember(ActionEvent event) {
		System.out.println("ȸ������ ��ư Ŭ��");
		showRegisterMemberDialog("Register");
	}
	

	@FXML
	void onBtnClickedExit(ActionEvent event) {
		Stage pop = (Stage)tfID.getScene().getWindow();
		Platform.exit();
		pop.close();
	}
	
	public boolean isLoginSuccess() {
		return bLoginSuccess;
	}
	
	public MainApp getMainApp() {
		return mainApp;
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public boolean showRegisterMemberDialog(String title) {
		thisScene = tfID.getScene();

		try {
			
			FXMLLoader loader = new FXMLLoader();
	
			loader.setLocation(MainApp.class.getResource("view/fxml/RegisterMemberLayout.fxml"));

			AnchorPane page = (AnchorPane) loader.load();
			Stage stageRegMember = new Stage();
			stageRegMember.setTitle(title);
			stageRegMember.initModality(Modality.WINDOW_MODAL);
			stageRegMember.initOwner(thisScene.getWindow());
			
			Scene scene = new Scene(page);
			stageRegMember.setScene(scene);
			stageRegMember.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/adicionar.png")));

			RegisterMemberDialogController controller = loader.getController();
			controller.setTitle(title);
			controller.setDialogStage(stageRegMember);

			stageRegMember.showAndWait();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		

	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
}
