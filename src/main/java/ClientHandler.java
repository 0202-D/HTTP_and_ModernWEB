
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dm.Petrov
 * DATE: 29.10.2022
 */
public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())) {
            while (true) {

                Request request = createRequest(in, out);

                Handler handler = Server.getHandlers().get(request.getMethod()).get(request.getPath());

                if (handler == null) {
                    Path parent = Path.of(request.getPath()).getParent();
                    handler = Server.getHandlers().get(request.getMethod()).get(parent.toString());
                    if (handler == null) {
                        error404NotFound(out);
                        return;
                    }
                }
                handler.handle(request, out);
                responseOK(request, out);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Request createRequest(BufferedReader in, BufferedOutputStream out) throws IOException {
        final var requestLine = in.readLine();
        final var parts = requestLine.split(" ");

        if (parts.length != 3) {
            socket.close();
        }

        //  final var path = parts[1];
        final var pathAndQuery = parts[1];
        System.out.println("Параметры");
        var parsResultParams = Request.getQueryParams(pathAndQuery);
        var path = Request.getQueryParamsPath(pathAndQuery);
        System.out.println(parsResultParams);
        System.out.println(path);

      /* if (!FileDao.validPaths.contains(path)) {
            error404NotFound(out);
        }*/

        String line;
        Map<String, String> headers = new HashMap<>();
        while (!(line = in.readLine()).equals("")) {
            var indexOf = line.indexOf(":");
            var name = line.substring(0, indexOf);
            var value = line.substring(indexOf + 2);
            headers.put(name, value);
        }

        Request request = new Request(parts[0], parts[1], headers, socket.getInputStream());
        out.flush();
        return request;
    }


    static void error404NotFound(BufferedOutputStream responseStream) throws IOException {
        responseStream.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        responseStream.flush();
    }

    static void responseOK(Request request, BufferedOutputStream responseStream) throws IOException {
        if (request.getMethod().equals("POST")) {
            responseStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            responseStream.flush();
        }

        final var filePath = Path.of(".", "public", request.getPath());
        final var mimeType = Files.probeContentType(filePath);

        final var template = Files.readString(filePath);
        final var content = template.replace(
                "{time}",
                LocalDateTime.now().toString()
        ).getBytes();
        responseStream.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + content.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        responseStream.write(content);

        final var length = Files.size(filePath);
        responseStream.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, responseStream);
        responseStream.flush();
    }
}




