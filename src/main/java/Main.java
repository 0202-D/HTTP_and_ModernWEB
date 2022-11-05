import java.io.IOException;

/**
 * @author Dm.Petrov
 * DATE: 29.10.2022
 **/


public class Main {
    public static void main(String[] args) {
        final var server = new Server();

       for (String validPath : FileDao.validPaths) {
            server.addHandler("GET", validPath, (request, responseStream) -> {
                try {
                    ClientHandler.responseOK(request, responseStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
        server.addHandler("POST", "/", (request, responseStream) -> {
            try {
                ClientHandler.responseOK(request, responseStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        server.listen(9999);
    }


}


