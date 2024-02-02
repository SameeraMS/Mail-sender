package com.example.mailsender.controller;

import com.example.mailsender.smtp.Mail;
import com.example.mailsender.util.SystemAlert;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;

public class MailFormController {
    public JFXTextField txtTo;
    public JFXTextField txtSubject;
    public TextArea txtMsg;
    public TextField txtFile;

    public void attachmentOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        txtFile.setText(fileChooser.showOpenDialog(null).getAbsolutePath());
    }

    public void sendOnAction(ActionEvent actionEvent) {

        if (!txtTo.getText().isEmpty()) {
            if (txtTo.getText().matches("(^[a-zA-Z0-9_.-]+)@([a-zA-Z]+)([\\.])([a-zA-Z]+)$")) {
                sendMail(txtTo.getText(), txtSubject.getText(), txtMsg.getText(), txtFile.getText());
            } else {
                try {
                    new SystemAlert(Alert.AlertType.INFORMATION, "Error", "Enter valid email address", ButtonType.OK).show();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            try {
                new SystemAlert(Alert.AlertType.INFORMATION, "Error", "Enter email address", ButtonType.OK).show();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMail(String to, String subj, String msg, String path) {
        String email = to;
        String message = msg;
        String subject = subj;
        File file = new File(path);
        Mail mail = null;

        if (path == null) {
             mail = new Mail(email, message, subject);
        } else {
            mail = new Mail(email, subject, message, file);
        }

        Thread thread = new Thread(mail);

        mail.valueProperty().addListener((a, oldValue, newValue) -> {
            if (newValue) {
                try {
                    new SystemAlert(Alert.AlertType.INFORMATION, "Email", "Mail sent successfully", ButtonType.OK).show();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                clear();
                System.out.println("sent successfully");
            } else {
                try {
                    new SystemAlert(Alert.AlertType.NONE, "Connection Error", "Connection Error!", ButtonType.OK).show();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    public void clear() {
        txtTo.clear();
        txtSubject.clear();
        txtMsg.clear();
        txtFile.clear();
    }
}
