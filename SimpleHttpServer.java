import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleHttpServer {
    public static void main(String[] args) throws Exception{
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("Server running on port " + port);

        while(true){
            Socket clientSocket = serverSocket.accept();
            System.out.println("\n==== New Connection ===");

            //Get request
            InputStream input = clientSocket.getInputStream();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] chunk = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(chunk)) != -1) {
                buffer.write(chunk, 0, bytesRead);

                if (buffer.size() > 0) break; // simple for now
            }
            byte[] requestData = buffer.toByteArray();
            String request = new String(requestData);

            System.out.println("REQUEST:");
            System.out.println(request);


            //Send response
            String response =
                    "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "\r\n" +
                    "Hello from server";


            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(response.getBytes());
            outputStream.flush();

            System.out.println("\n=== Connection Finished ====");

            clientSocket.close();
        }
    }
}
