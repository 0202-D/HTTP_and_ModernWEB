import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Dm.Petrov
 * DATE: 29.10.2022
 */
public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private static final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();

    public void listen(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectionProcessing();

        }
    }

    public void connectionProcessing() {
        ExecutorService service = Executors.newFixedThreadPool(64);
        service.submit(new ClientHandler(socket));
    }

    public void addHandler(String requestMethod, String path, Handler handler) {
        if (handlers.containsKey(requestMethod)) {
            handlers.get(requestMethod).put(path, handler);
        } else {
            handlers.put(requestMethod, new ConcurrentHashMap<>(Map.of(path, handler)));
        }

    }

    public static Map<String, Map<String, Handler>> getHandlers() {
        return handlers;
    }
}
