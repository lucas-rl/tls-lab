import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //Read HTTP request line by line
            String line;
            while((line = reader.readLine()) != null && !line.isEmpty()){
                System.out.println(line);
            }

            //Send basic HTTP response
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
            writer.print("HTTP/1.1 200 OK\r\n");
            writer.print("Content-Type: text/plain\r\n");
            writer.print("\r\n");
            writer.print("Hello from server");
            writer.flush();

            System.out.println("Connection Finished");

            clientSocket.close();
        }
    }
}
