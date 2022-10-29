import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Dm.Petrov
 * DATE: 29.10.2022
 */
public class Server {
     private ServerSocket serverSocket;
     private Socket socket;
     private int port = 9999;
     public void start(){
         try {
             serverSocket = new ServerSocket(port);
         } catch (IOException e) {
             e.printStackTrace();
         }
         while (true){
             try {
                 socket = serverSocket.accept();
             } catch (IOException e) {
                 e.printStackTrace();
             }
              connectionProcessing();

         }
     }
     public void connectionProcessing(){
         ExecutorService service = Executors.newFixedThreadPool(64);
         service.submit(new ClientHandler(socket));
     }
}
