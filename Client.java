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

   public Client(String serverIP, int port) {
      try {
         // Connect to Server
         socket = new Socket(serverIP, port);
         out = new ObjectOutputStream(socket.getOutputStream());
         in = new ObjectInputStream(socket.getInputStream());

         // Initialize Game
         Model gameState = new Model(10);
         BufferedImage bufferedImage = ImageIO.read(new File("./images/waterBackground.jpeg"));
         ImageIcon imageIcon = new ImageIcon(bufferedImage);

         // Create View and pass network streams
         gameView = new View(gameState, imageIcon, out);
         // Start listener thread to update the view
         startListeningForUpdates();

      }

      // // Start sending updates
      // startSendingUpdates();

      catch (Exception e) {
         e.printStackTrace();
      }
   }

   // private void startSendingUpdates() {
   // new Thread(() -> {
   // try {
   // while (true) {
   // Thread.sleep(100); // Prevents excessive network spam
   // int[] position = gameView.getShipSquarePosition(); // Pull from View

   // out.writeObject(position);
   // out.flush();
   // }
   // } catch (Exception e) {
   // e.printStackTrace();
   // }
   // }).start();
   // }

   private void startListeningForUpdates() {
      new Thread(() -> {
         try {
            while (true) {
               int[] position = (int[]) in.readObject();
               gameView.updateShipSquarePosition(position[0], position[1]); // Update View
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }).start();
   }

   public static void main(String[] args) {
      new Client("localhost", 12345); // Connect to server
   }
}
