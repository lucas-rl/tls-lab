import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleHttpClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 8080);

        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        writer.print("GET /hello HTTP/1.1\r\n");
        writer.print("Host: localhost\r\n");
        writer.print("\r\n");
        writer.flush();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        System.out.println("Connection Finished");

        socket.close();
    }
}