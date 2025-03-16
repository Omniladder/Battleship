import java.io.*;
import java.net.Socket;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

class Client {
   private Socket socket;
   private ObjectOutputStream out; // passes from here: Client -> View -> ShipSquare
   private ObjectInputStream in;
   private View gameView;
   private Model gameState;

   public Client(String serverIP, int port) {
      try {
         // Connect to Server
         socket = new Socket(serverIP, port);
         out = new ObjectOutputStream(socket.getOutputStream());
         in = new ObjectInputStream(socket.getInputStream());

         int[] playerNumberContainer = (int[]) in.readObject();

         // Initialize Game
         gameState = new Model(10, playerNumberContainer[0], out, in);

         // Create Vie w and pass network streams
         gameView = new View(gameState, out);
         // Start listener thread to update the view
         // startListeningForUpdates();
         gameState.waitForOpponent();

      }

      // // Start sending updates
      // startSendingUpdates();

      catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {
      String serverIP;
      if (args.length > 0) {
         serverIP = args[0];
      } else {
         serverIP = "localhost";
      }
      new Client(serverIP, 12345); // Connect to server
   }
}
