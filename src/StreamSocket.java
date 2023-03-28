import javax.net.ssl.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper class of Socket which contains 
 * methods for sending and receiving messages
 * @author M. L. Liu
 */
public class StreamSocket extends Socket {
   private SSLSocket socket;
   private BufferedReader input;
   private PrintWriter output;

   StreamSocket(InetAddress acceptorHost, int acceptorPort ) throws IOException{
      //This code establishes an SSL connection from the client side.
      SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
      socket = (SSLSocket) sf.createSocket(acceptorHost, acceptorPort);

      //When the socket is created the Cipher suite used to encrypt messages between the server
      // and the client must be accepted. The setEnabledCipherSuites takes a list of
      // all acceptable Cipher suites e.g {"TLS_RSA_WITH_AES_128_CBC_SHA"};
      String[] cipherSuites = socket.getSupportedCipherSuites();
      socket.setEnabledCipherSuites(cipherSuites);
      socket.startHandshake();
      setStreams( );
   }

   StreamSocket(SSLSocket socket)  throws IOException {
      this.socket = socket;
      setStreams( );
   }

   private void setStreams( ) throws IOException{
      // get an input stream for reading from the data socket
      input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      OutputStream outStream = socket.getOutputStream();
      // create a PrinterWriter object for character-mode output
      output = new PrintWriter(new OutputStreamWriter(outStream));
   }

   boolean checkCredentials() throws IOException {
      if (receiveMessage().equals("admin")) {
         sendMessage("OK");
         if (receiveMessage().equals("password")) {
            sendMessage("OK");
            return true;
         }else{
            sendMessage("FAIL");
            socket.close();
         }
      }else{
         sendMessage("FAIL");
         socket.close();
      }
      return false;
   }

   public void sendMessage(String message) {
      output.println(message);
      //The ensuing flush method call is necessary for the data to
      // be written to the socket data stream before the
      // socket is closed.
      output.flush();               
   } // end sendMessage

   public String receiveMessage( ) throws IOException {
      // read a line from the data stream
      String message = input.readLine( );
      return message;
   } //end receiveMessage

   //This is a nice bit of code just to check if there was a successful SSL handshake.
   //I originally printed it to screen and used it for debugging but i taught that this
   //type of message is not seen from a users perspective so i commented it out
//   public String verifySSLHandshake(){
//      // check if the SSL handshake has completed successfully
//      if (socket.getSession().isValid()) {
//         return "SSL handshake completed successfully.";
//      } else {
//         return "SSL handshake failed.";
//      }
//   }

   public List<String> getAllServerMessages() throws IOException {
      // Unblock the thread to send messages to the input stream
      sendMessage("SEND_ALL_MESSAGES");

      //Because the readLine function blocks calls i had to set a timer to interrupt the socket
      //Before adding SSL I used the input.ready() function. This Boolean value would check if there was data
      // in the input stream. This function doesnt work with the
      //SSL socket because the SSL Socket uses a buffered stream that sits on top of the
      //socket input stream.

      //gather all the messages in the arraylist
      List<String> messages = new ArrayList<>();
      String message;
      while (!(message = input.readLine()).equals("LAST_MESSAGE")) {
         messages.add(message);
      }
      //give the list back to the client window to display the messages
      return messages;
   }

   public void close( ) throws IOException {
      socket.close( );
   }
} //end class
