import javax.swing.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * This class is a module which provides the application logic
 * for an Echo client using stream-mode socket.
 * @author M. L. Liu
 */

public class ClientHelper {

   private StreamSocket mySocket;
   private InetAddress serverHost;
   private int serverPort;

   ClientHelper(String hostName, String portNum) throws IOException {
                                     
  	   this.serverHost = InetAddress.getByName(hostName);
  	   this.serverPort = Integer.parseInt(portNum);

      // Set truststore properties
      System.setProperty("javax.net.ssl.trustStore", "public.jks");
      System.setProperty("javax.net.ssl.trustStorePassword", "123456");

  	   //Instantiates a stream-mode socket and wait for a connection.
       this.mySocket = new StreamSocket(this.serverHost, this.serverPort);
   } // end constructor

   public void verifyCredentials(String username,char[] password,Frame mainWindow ) throws IOException{
      mySocket.sendMessage(username);
      if (mySocket.receiveMessage().equals("OK")) {
         mySocket.sendMessage(toString(password));
         if (mySocket.receiveMessage().equals("OK")) {
            JOptionPane.showMessageDialog(null, "Login successful.");
            mainWindow.setVisible(false);
            new MessageWindow(this);
         }else{
            JOptionPane.showMessageDialog(null, "Invalid username or password.");
            mySocket.close();
         }
      }else{
         JOptionPane.showMessageDialog(null, "Invalid username or password.");
         mySocket.close();
      }
   } // end done

   private String toString(char[] password) {

      StringBuilder sb = new StringBuilder();
      for(int i =0; i<password.length;i++){
         sb.append(password[i]);
      }
      return sb.toString();
   }

   public void closeConnection( ) throws IOException{
      mySocket.sendMessage("LOG_OFF");
      mySocket.close( );
   } // end done 

   public void sendMessageToServer(String message){
      if(message.getBytes(StandardCharsets.UTF_8).length<=1024)
      {
         mySocket.sendMessage(message);
      }else{
         JOptionPane.showMessageDialog(null, "The message is too long. Shorten the message and try again!");
      }
   }

   public List<String> receiveMessagesFromServer() throws IOException {
      return mySocket.getAllServerMessages();
   }
} //end class
