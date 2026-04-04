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


            //1. Request line
            String requestLine = reader.readLine();
            System.out.println("Request Line " + requestLine);




            //2. Headers
            String line;
            int contentLength = 0;
            System.out.println("Headers");
            while((line = reader.readLine()) != null && !line.isEmpty()){
                System.out.println(line);
                if(line.toLowerCase().startsWith("content-length:")){
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            //3. Body (if exists)
            char[] bodyChars = new char[contentLength];
            if(contentLength > 0){
                reader.read(bodyChars, 0, contentLength);
                String body = new String(bodyChars);
                System.out.println("Body: " + body);
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
