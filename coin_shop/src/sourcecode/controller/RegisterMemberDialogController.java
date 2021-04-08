package sourcecode.controller;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sourcecode.controller.DBConnection;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;


import sourcecode.model.*;

import sourcecode.util.DateUtil;


public class RegisterMemberDialogController implements Initializable {
	
	@FXML private Label lblTitle;
    @FXML private JFXTextField tfID;
    @FXML private JFXPasswordField pfPW;
    @FXML private JFXPasswordField pfPW_check;
    @FXML private JFXTextField tfPhoneNumber;
    @FXML private JFXTextField tfZipCode;
    @FXML private JFXTextField tfVolunteerTime;
    @FXML private Text textPoint;
    
    private boolean bIsCheckedID;
    private boolean bIsCheckedPW;
    private boolean bIsCheckedPoint;
    private int nPoint;
    
    private String title;
    private String strPW;
    private String strPW_check;
   
    private Stage currentStage;
    private boolean okClicked = false;
    private String insertionType;
    
    private CustomerMySelf customerMyself;
    
    //dialog init
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	tfVolunteerTime.setOnKeyPressed(event -> {
    		bIsCheckedPoint = false;
    		String volunteerTime = "";
    		
    		String value = event.getText();
    		try {
    			if(Character.isDigit(value.charAt(0))) {
        			return;
        		} else {
        			Alert alert = new Alert(AlertType.WARNING);
            		alert.setTitle("Warning!!");
            		alert.setHeaderText("���ڸ� �Է��ϼ���");
            		alert.showAndWait();
            		volunteerTime = tfVolunteerTime.getText();
            		volunteerTime = volunteerTime.substring(0, volunteerTime.length() - 1);

        		}
    		} catch(NullPointerException e) {
    			
    		} finally {
    			
    			if(volunteerTime.length() == 0)
    				return;
        		tfVolunteerTime.setText(volunteerTime);
    		}
    		
    	});
    	
    	customerMyself = CustomerMySelf.getInstance();
    	System.out.println(pfPW.getText());
    }  

    
    private boolean isValidInput() {
    	String str = "";
    	if(tfID.getText() != null && pfPW.getText() != null
    			&& tfPhoneNumber.getText() != null && tfZipCode.getText() != null
    			&& tfVolunteerTime.getText() != null && textPoint.getText() != null) {    		
    		if(bIsCheckedID && bIsCheckedPW && bIsCheckedPoint) {
    			customerMyself.getCustomer().setName(tfID.getText());
    			customerMyself.getCustomer().setPassword(pfPW.getText());
    			customerMyself.getCustomer().setPhone(tfPhoneNumber.getText());
    			customerMyself.getCustomer().setZipcode(tfZipCode.getText());
    			customerMyself.getCustomer().setVolunteer_time(Integer.parseInt(tfVolunteerTime.getText()));
    			customerMyself.getCustomer().setCoin(nPoint);
    			return true;
    		} else {
    			
    			Alert alert = new Alert(AlertType.WARNING);
        		alert.setTitle("Warning!!");
        		
        		if(!bIsCheckedID) {
        			str = "ID �ߺ�Ȯ���� �ϼ���";
        		} else if(!bIsCheckedPW) {
        			str = "PW�� Ȯ�� �ϼ���";
        		} else {
        			str = "����Ʈ�� Ȯ���ϼ���";
        		}
        		
        		alert.setHeaderText("�Է� ����");
        		alert.setContentText(str);
        		alert.show();
        		return false;
    		}
    	}
    	else {
    		str = "��� ���� �ۼ��ϼ���";
    		Alert alert = new Alert(AlertType.WARNING);
    		alert.setTitle("Warning!!");
    		alert.setHeaderText("�Է� ����");
    		alert.setContentText(str);
    		alert.show();
    		return false;
    	}
    }
    
    @FXML
    private void registerNewMember(ActionEvent event) {
    	
    	if(isValidInput()) {
    		//register member data callableStatement
    		procRegisterID(customerMyself.getCustomer());
    		currentStage.close();
    	}
    }
    
    
    // ID �ߺ� Ȯ�� �� ȸ�� ��� ���ν��� ȣ��
    private boolean procRegisterID(Customer customer) {
    	
		String runP = "{ call customer_insert_version2(?, ?, ?, ?, ?, ?, ?) }";
	
			try {
				Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				CallableStatement callableStatement = conn.prepareCall(runP.toString());
				callableStatement.setString(1, customer.getName());
				callableStatement.setString(2, customer.getPassword());
				callableStatement.setString(3, customer.getZipcode());
				callableStatement.setString(4, customer.getPhone());
				callableStatement.setInt(5, customer.getCoin());
				callableStatement.setInt(6, customer.getVolunteer_time());
				callableStatement.registerOutParameter(7, java.sql.Types.INTEGER);
				callableStatement.executeUpdate();	
				
				int check = callableStatement.getInt(7);
				if(check == 0) {
					System.out.println("���̵� �ߺ�");
					return false;	
				}
				else
					System.out.println("���� ����");
				
				
			} catch (SQLException e) {
				System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return true;
    }
    
    
    @FXML
    private void exitMemberRegisterForm(ActionEvent event) {
    	currentStage.close();
    }
    
    @FXML
    private boolean onBtnClickedCheckID(ActionEvent event) {
    	//is ID exists? callableStatement
    	Alert alert = new Alert(AlertType.WARNING);
    	String message = "";
    	customerMyself.getCustomer().setName(tfID.getText());
    	bIsCheckedID = procCheckID(customerMyself.getCustomer());
    	
    	if(bIsCheckedID == true) {
    		message = "��밡���� ID�Դϴ�";
    		alert.alertTypeProperty();
    		alert.setAlertType(AlertType.CONFIRMATION);
    	} else {
    		message = "�ߺ��� ID�Դϴ�";
        	alert.setTitle("Warning !!");
        	
    	}
		alert.setHeaderText(message);
		alert.showAndWait();
		
    	return bIsCheckedID;
    }
    
    //ID �ߺ� Ȯ�� ���ν��� ȣ��
    private boolean procCheckID(Customer customer) {
    	
		String runP = "{ call C_NAMECHECK(?, ?) }";
	
			try {
				Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				CallableStatement callableStatement = conn.prepareCall(runP.toString());
				callableStatement.setString(1, customer.getName());
				callableStatement.registerOutParameter(2, java.sql.Types.INTEGER);
				callableStatement.executeUpdate();	
				
				int check = callableStatement.getInt(2);
				if(check == 1) {
					System.out.println("���̵� �ߺ�");
					return false;	
				}
				else
					System.out.println("���� ����");
				
			} catch (SQLException e) {
				System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return true;
			
    }
   
    
    @FXML
    private boolean onBtnClickedCheckPassword(ActionEvent event) {
    	Alert alert = new Alert(AlertType.WARNING);
    	if(pfPW.getText() == null || pfPW.getText().length() < 8) {
    		alert.setTitle("Warning !!");
    		alert.setHeaderText("�Է� ����");
    		alert.setContentText("��й�ȣ�� 8�ڸ� �̻� �Է��ϼ���");
    		alert.show();
    		bIsCheckedPW = false;
    			return bIsCheckedPW;
    	}
    	
    	if(pfPW.getText().equals(pfPW_check.getText())) {
    		strPW = pfPW.getText();
    		alert.setAlertType(AlertType.CONFIRMATION);
    		alert.setTitle("Confirm");
    		alert.setHeaderText("��й�ȣ�� ��ġ�մϴ�");
    		alert.setContentText("");
    		alert.show();
    		bIsCheckedPW = true;
    	} else {
    		alert.setAlertType(AlertType.WARNING);
    		alert.setTitle("Confirm");
    		alert.setHeaderText("��й�ȣ�� ��ġ���� �ʽ��ϴ�");
    		alert.setContentText("");
    		alert.show();
    	}
    	return bIsCheckedPW;
    }
    
    @FXML
    private void onBtnClickedCheckVolunteerTime(ActionEvent event) {
    	String volunteerTime = tfVolunteerTime.getText();
    	nPoint = Integer.parseInt(volunteerTime) * 10;
    	customerMyself.getCustomer().setCoin(nPoint);
    	textPoint.setText(Integer.toString(nPoint));
    	bIsCheckedPoint = true;
    	return;
    }
    
    @FXML
    void handleCancel(ActionEvent event) {
        currentStage.close();
    }
    
     
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        lblTitle.setText("Register a New Member");
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.currentStage = dialogStage;
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }

}
