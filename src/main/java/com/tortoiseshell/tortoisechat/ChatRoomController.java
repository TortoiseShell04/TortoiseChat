package com.tortoiseshell.tortoisechat;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatRoomController {
    @FXML
    private Button SendButton;
    @FXML
    private TextArea ChatField;
    @FXML
    private TextField ChatInput;
    @FXML
    private Button StartButton;
    private Socket client = null;

    private DataOutputStream outputStream;
    private String Username;


    public void initialize() throws IOException
    {
        client = LoginController.clienSocket;
        outputStream = new DataOutputStream(client.getOutputStream());
        Username = LoginController.Username;
        Parent p = StartButton.getParent();
        new Thread(new readClientMessages(new DataInputStream(client.getInputStream()), p)).start();
        StartButton.setText("Exit");
    }
    
    @FXML
    protected void onSendButtonClick(KeyEvent k) throws IOException {
        if (ChatInput.getText() != null && (k == null || k.getCode() == KeyCode.ENTER)) {
            outputStream.writeUTF(Username + " : " + ChatInput.getText());
            outputStream.flush();
            ChatInput.setText("");
        }
    }
    @FXML
    protected void onStart() throws IOException {
            outputStream.writeUTF("!exit");
            outputStream.flush();
            client.close();
            outputStream.close();
            System.exit(0);
        
    }


}
class readClientMessages implements Runnable
{
    DataInputStream i;
    TextArea ChatField;

    public readClientMessages(DataInputStream i,Parent root) {
        this.i = i;
        ChatField = (TextArea) root.lookup("#ChatField");
    }

    @Override
    public void run() {
        int count = 0;
        while (true)
        {
            try
            {
                String newMess = i.readUTF();
                ChatField.setText(ChatField.getText() + "\n" + newMess);
                ChatField.appendText("");
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
                count++;
                if (count > 10) System.exit(0);

            }
            catch (NullPointerException ne)
            {
                ne.printStackTrace();
            }
        }
    }
}
