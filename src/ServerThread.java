import javax.swing.*;
import java.io.IOException;

/**
 * This module is to be used with a concurrent Echo server.
 * Its run method carries out the logic of a client session.
 * @author M. L. Liu
 */

class ServerThread implements Runnable {
    private StreamSocket myDataSocket;
    private JPanel messagePanel;
    private Boolean disconnected;
    private boolean clientReadyForMessages=false;
    private String message;

    ServerThread(StreamSocket myDataSocket, JPanel messagePanel, Boolean disconnected) {
        this.myDataSocket = myDataSocket;
        this.messagePanel = messagePanel;
        this.disconnected = disconnected;
    }

    public void run( ) {
        boolean done = false;
        try {
            while (!done) {
                //Receive client message
                receiveMessagesFromClient();

                // Send server messages to the client
                sendAllServerMessagesToClient();

                //check if server disconnected
                done = isDone(done);

            } //end while !done
        }// end try
        catch (Exception ex) {
            System.out.println("Exception caught in thread: " + ex);
        } // end catch
    } //end run

    private boolean isDone(boolean done) throws IOException {
        if (disconnected || message.equals("LOG_OFF")){
            myDataSocket.close( );
            done = true;
        }
        return done;
    }

    private void sendAllServerMessagesToClient() {
        if(clientReadyForMessages && !disconnected){
            for (String serverMessage:MessageServer.getServerMessages()) {
                myDataSocket.sendMessage(serverMessage);
            }
            //Send last message to stop client from blocking
            myDataSocket.sendMessage("LAST_MESSAGE");
            clientReadyForMessages=false;
        }
    }

    private synchronized void receiveMessagesFromClient() throws IOException {
        //server block and wait for messages
        message = myDataSocket.receiveMessage();
        //check if disconnected while blocking
        if(!disconnected){
            //check if client wants messages
            if(!message.equals("SEND_ALL_MESSAGES")){
                //check if client tried to send a message to attempt to interrupt the server from sending all the messages
                //or the client attempted to try and stop the server thread.
                if(!message.equals("LAST_MESSAGE")&& !message.equals("LOG_OFF")){
                    addMessageToPanel(messagePanel,"Message received: "+ message);
                    MessageServer.storeServerMessages(message);
                }
            }
            else{
                clientReadyForMessages = true;
            }
        }
    }

    private static void addMessageToPanel(JPanel messagePanel, String message) {
        messagePanel.add(new JLabel(message));
        messagePanel.revalidate();
        messagePanel.repaint();
    }
} //end class 
