import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

/**
 * This module contains the presentation logic of an Echo Client.
 * @author M. L. Liu
 */

public class MessageWindow {

    //Add a box for holding the messages
    private static JPanel messagePanel;

    public MessageWindow(ClientHelper helper)
    {
        messagePanel = new JPanel();

        Frame messageWindow = new Frame("SMP client window");
        messageWindow.setLayout(new BorderLayout());

        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

        messagePanel.add(new JLabel("Welcome to the message client!"));

        //add the scroll section
        JScrollPane scrollPane = new JScrollPane(messagePanel);
        messageWindow.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        //add the text field, send button and the log off button
        JTextField textField = new JTextField(20);
        JButton sendButton = new JButton("Send");
        JButton logOffButton = new JButton("Log Off");
        JButton downloadMessagesButton = new JButton("Download Messages");

        inputPanel.add(textField);
        inputPanel.add(sendButton);
        inputPanel.add(logOffButton);
        inputPanel.add(downloadMessagesButton);

        try {
//            String hostName = JOptionPane.showInputDialog(null,"What is the name of the server host?");
//            if (hostName.length() == 0) // if user did not enter a name
              String hostName = "localhost";  //   use the default host name

//            String portNum = JOptionPane.showInputDialog(null,"What is the port number of the server host?");
//            if (portNum.length() == 0)
              String portNum = "7";          // default port number

            //ClientHelper helper = new ClientHelper(hostName, portNum, messagePanel);

            //Send button functionality
            sendButton.addActionListener(e->{
                sendMessage(textField, helper);
            });

            textField.addActionListener(e->{
                sendMessage(textField, helper);
            });

            //Log off button functionality
            logOffButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(JOptionPane.showConfirmDialog(null, "Are you sure you wish to log off?", "Log Off",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                        try {
                            helper.closeConnection();
                            messageWindow.setVisible(false);
                            new LoginWindow();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            });

            //Download messages button functionality
            downloadMessagesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<String> serverMessages = null;
                    try {
                        serverMessages = helper.receiveMessagesFromServer();
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(null,"Failed to connect to server. Check if server is running and try again","Error",JOptionPane.ERROR_MESSAGE);
                        ioException.printStackTrace();
                        messageWindow.setVisible(false);
                        new LoginWindow();
                    }
                    if(serverMessages != null){
                        int index = 1;
                        addMessageToPanel("Server Messages: ");
                        for (String message:serverMessages) {
                            addMessageToPanel("Message "+index+" : " + message);
                            index++;
                        }
                    }
                }
            });

            messageWindow.add(inputPanel, BorderLayout.SOUTH);

            messageWindow.setVisible(true);

        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(null,"Failed to connect to server. Check if server is running and try again","Error",JOptionPane.ERROR_MESSAGE);
            new LoginWindow();
        }

    }

    private void sendMessage(JTextField textField, ClientHelper helper) {
        String message = textField.getText();
        if(!message.equals("")){
            textField.setText("");
            helper.sendMessageToServer(message);
        }
    }

    private static void addMessageToPanel(String message) {
        messagePanel.add(new JLabel(message));
        messagePanel.revalidate();
        messagePanel.repaint();
    }
}