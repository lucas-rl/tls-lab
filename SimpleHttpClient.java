import java.io.*;
import java.net.Socket;

public class SimpleHttpClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 9090);


        String request =
                "POST /login HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: 18\r\n" +
                        "\r\n" +
                        "{\"user\":\"admin\"}";

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(request.getBytes());
        outputStream.flush();

        //Get response
        InputStream input = socket.getInputStream();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        byte[] chunk = new byte[1024];
        int bytesRead;

        while ((bytesRead = input.read(chunk)) != -1) {
            buffer.write(chunk, 0, bytesRead);

            if (buffer.size() > 0) break; // simple for now
        }
        byte[] responseData = buffer.toByteArray();
        String response = new String(responseData);

        System.out.println("RESPONSE:");
        System.out.println(response);


        System.out.println("Connection Finished");

        socket.close();
    }
}