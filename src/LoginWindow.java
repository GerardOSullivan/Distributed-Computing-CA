import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;

public class LoginWindow {
    public LoginWindow()
    {
        //Make the window
        Frame mainWindow = new Frame("Client SMP");

        //Setup the username Text and username text area
        JLabel usernameHeader = new JLabel("Username:");
        Dimension size = usernameHeader.getPreferredSize();
        usernameHeader.setBounds(150, 100, size.width, size.height);
        mainWindow.add(usernameHeader);

        JTextField usernameInput = new JTextField(30);
        size = usernameInput.getPreferredSize();
        usernameInput.setBounds(150, 140, size.width, size.height);
        mainWindow.add(usernameInput);

        //Setup the password Text and password text area
        JLabel passwordText = new JLabel("Password:");
        size = passwordText.getPreferredSize();
        passwordText.setBounds(150, 180, size.width, size.height);
        mainWindow.add(passwordText);

        JPasswordField passwordInput = new JPasswordField(30);
        size = passwordInput.getPreferredSize();
        passwordInput.setBounds(150, 220, size.width, size.height);
        mainWindow.add(passwordInput);

        //Add the submit button
        JButton submitButton = new JButton("Login");
        size = submitButton.getPreferredSize();
        submitButton.setBounds(150, 260, size.width, size.height);
        mainWindow.add(submitButton);

        //Check username and password
        submitButton.addActionListener(e -> {
            attemptToLogin(mainWindow, usernameInput, passwordInput);
        });

        //added 2 more listeners for pressing "enter" when on either of the other TextFields
        passwordInput.addActionListener(e -> {
            attemptToLogin(mainWindow, usernameInput, passwordInput);
        });

        usernameInput.addActionListener(e -> {
            attemptToLogin(mainWindow, usernameInput, passwordInput);
        });
        mainWindow.setVisible(true);
    }

    private void attemptToLogin(Frame mainWindow, JTextField usernameInput, JPasswordField passwordInput) {
        String username = usernameInput.getText();
        char[] password = passwordInput.getPassword();


        try {
            String hostName = JOptionPane.showInputDialog(null,"What is the name of the server host?");
            if (hostName.length() == 0) // if user did not enter a name
                hostName = "localhost";  //   use the default host name

            String portNum = JOptionPane.showInputDialog(null,"What is the port number of the server host?");
            if (portNum.length() == 0)
                portNum = "7";          // default port number

            ClientHelper helper = new ClientHelper(hostName, portNum);
            helper.verifyCredentials(username, password,mainWindow);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "There was an error when attempting to connect to the server. Please try again.","Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }
}