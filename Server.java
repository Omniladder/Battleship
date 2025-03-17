import java.io.*;
import java.net.*;
import javax.swing.*;

public class Server extends JFrame {
   private JTextArea displayArea; // Show who connects + other important info
   private ServerSocket server; // listen to connections
   private Socket[] clients = new Socket[2]; // Array of 2 clients
   private ObjectOutputStream[] outputs = new ObjectOutputStream[2];
   private ObjectInputStream[] inputs = new ObjectInputStream[2];
   private Controller c;

   public Server() {
      super("Battleship server");
      displayArea = new JTextArea();
      add(new JScrollPane(displayArea), "Center");
      setSize(300, 150);
      setVisible(true);
   }

   // Runs the server: waits for two clients and starts a thread for each.

   public void runServer() {
      try {
         // Create a server socket that listens on port 12345, with a backlog of 2
         // connections.
         server = new ServerSocket(12345, 2);
         displayMessage("Server started. Waiting for two players...\n");

         for (int i = 0; i < 2; i++) {
            clients[i] = server.accept();
            displayMessage(
                  "Player " + (i + 1) + " connected from: " + clients[i].getInetAddress().getHostName() + "\n");
            outputs[i] = new ObjectOutputStream(clients[i].getOutputStream());
            outputs[i].flush();
            inputs[i] = new ObjectInputStream(clients[i].getInputStream());
            int[] clientIndex = { i };
            outputs[i].writeObject(clientIndex);
            outputs[i].flush();
         }

         // Start threads to handle messages from both clients
         for (int i = 0; i < 2; i++) {
            final int clientIndex = i;
            new Thread(() -> {
               try {
                  while (true) {
                     // Read message from this client
                     Object message = inputs[clientIndex].readObject();

                     // Calculate opponent index (0->1, 1->0)
                     int opponentIndex = (clientIndex + 1) % 2;

                     // Send only to the opponent, not back to the sender
                     displayMessage(
                           "Player " + (clientIndex + 1) + " sent a message to Player " + (opponentIndex + 1) + "\n");
                     outputs[opponentIndex].writeObject(message);
                     outputs[opponentIndex].flush();
                  }
               } catch (IOException | ClassNotFoundException e) {
                  displayMessage("Error handling client " + (clientIndex + 1) + ": Error Type :" + e
                        + " Error Message: " + e.getMessage() + "\n");
               }
            }).start();
         }
      } catch (IOException e) {
         displayMessage("Server error: " + e.getMessage() + "\n");
      }
   }

   // Utility method to update the server GUI's display area.
   private void displayMessage(final String message) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            displayArea.append(message);
         }
      });
   }

   // Main method to launch the server.
   public static void main(String[] args) {
      Server serverApp = new Server();
      serverApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      serverApp.runServer();
   }
}

