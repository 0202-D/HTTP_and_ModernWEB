import java.io.InputStream;
import java.util.Map;

/**
 * @author Dm.Petrov
 * DATE: 29.10.2022
 */
public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream body;

    public Request(String method, String path, Map<String, String> headers, InputStream body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';

    }
}
