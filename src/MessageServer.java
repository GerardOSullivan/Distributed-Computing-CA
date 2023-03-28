import javax.net.ssl.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

public class MessageServer{
   static List<String> serverMessages = new ArrayList<>();
   private static Boolean disconnected =false;
   public static void main(String[] args) {
      String ksName = "src/keystore.jks";
      char ksPass[] = "123456".toCharArray();
      char ctPass[] = "123456".toCharArray();
      int serverPort = 7;    // default port

      if (args.length == 1 )
         serverPort = Integer.parseInt(args[0]);
      try {
         Frame messageWindow = new Frame("SMP server window");
         messageWindow.setLayout(new BorderLayout());

         //Add a box for holding the messages
         JPanel messagePanel = new JPanel();
         messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

         //add the scroll section
         JScrollPane scrollPane = new JScrollPane(messagePanel);
         messageWindow.add(scrollPane, BorderLayout.CENTER);

         JPanel inputPanel = new JPanel();
         inputPanel.setLayout(new FlowLayout());

         //Add the disconnect button
         JButton disconnectButton = new JButton("Disconnect Server");
         disconnectButton.setBounds(150, 260,20,40);

         inputPanel.add(disconnectButton);
         messageWindow.add(inputPanel, BorderLayout.SOUTH);

         messageWindow.setVisible(true);

         //Disconnect button functionality
         disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               if(JOptionPane.showConfirmDialog(null, "Are you sure you wish to disconnect the server?", "Disconnect the server",
                       JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                  disconnected = true;
                  System.exit(0);
               }
            }
         });

         // instantiates a stream socket for accepting
         //   connections
         //Here the original server socket was commented out and the SSL Server socket was put in
   	     //ServerSocket myConnectionSocket = new ServerSocket(serverPort);

         //This code establishes an SSL connection from the server side.
         KeyStore ks = KeyStore.getInstance("JKS");
         ks.load(new FileInputStream(ksName), ksPass);

         KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
         kmf.init(ks, ctPass);

         TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
         tmf.init(ks);

         SSLContext sc = SSLContext.getInstance("TLS");
         sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);


         SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
         SSLServerSocket myConnectionSocket = (SSLServerSocket) ssf.createServerSocket(serverPort);

         //When the server socket is created the Cipher suite used to encrypt messages between the server
         // and the client must be accepted. The setEnabledCipherSuites takes a list of
         // all acceptable Cipher suites e.g {"TLS_RSA_WITH_AES_128_CBC_SHA"};
         String[] cipherSuites = myConnectionSocket.getSupportedCipherSuites();
         myConnectionSocket.setEnabledCipherSuites(cipherSuites);

         messagePanel.add(new JLabel("Message server ready."));
         while (true) {  // forever loop
            // wait to accept a connection
            addMessageToPanel(messagePanel,"Waiting for a connection.");

            //accept the incoming SSL connection
            StreamSocket streamSocket = new StreamSocket
                ((SSLSocket) myConnectionSocket.accept());

            addMessageToPanel(messagePanel,"Connection accepted. Verifying credentials .....");

            Boolean correctLoginDetails=streamSocket.checkCredentials();

            if(correctLoginDetails){
               addMessageToPanel(messagePanel,"Credentials accepted.");
            }else {
               addMessageToPanel(messagePanel,"Credentials invalid.");
            }

            // Start a thread to handle this client's session
            Thread theThread =
               new Thread(new ServerThread(streamSocket,messagePanel, disconnected));
            theThread.start();
            // and go on to the next client
            } //end while forever
       } // end try
	    catch (Exception ex) {
           JOptionPane.showMessageDialog(null,"There was an error starting the server. Check that the server is not already running and restart the server.","ERROR",JOptionPane.ERROR_MESSAGE);
          ex.printStackTrace( );
	    } // end catch
   } //end main

   private static void addMessageToPanel(JPanel messagePanel, String message) {
      messagePanel.add(new JLabel(message));
      messagePanel.revalidate();
      messagePanel.repaint();
   }

   public static void storeServerMessages(String message){
      serverMessages.add(message);
   }

   public static List<String> getServerMessages(){
      return serverMessages;
   }


} // end class
