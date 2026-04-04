import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleProxy {

    public static void main(String[] args) throws Exception{
        int proxyPort = 9090;
        ServerSocket serverSocket = new ServerSocket(proxyPort);

        System.out.println("Proxy running on port " + proxyPort);

        while (true){
            Socket socket = serverSocket.accept();
            new Thread(() -> {
                try {
                    handle(socket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

    }

    private static void handle(Socket clientSocket) throws IOException {
        try{
            System.out.println("\n=== New Proxy Connection ===");

            BufferedReader clientReader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            );
            //Connect to real server
            Socket serverSocket = new Socket("localhost", 8080);
            PrintWriter serverWriter = new PrintWriter(serverSocket.getOutputStream());
            //Forward request line + headers
            String line;
            int contentLength = 0;
            while ((line = clientReader.readLine()) != null && !line.isEmpty()) {
                System.out.println("CLIENT -> " + line);
                serverWriter.print(line + "\r\n");
                if (line.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }
            serverWriter.print("\r\n");

            //Read and foward body
            char[] bodyChars = new char[contentLength];
            if (contentLength > 0) {
                clientReader.read(bodyChars, 0, contentLength);
                String body = new String(bodyChars);
                System.out.println("CLIENT BODY -> " + body);

                // Forward body (we will modify next)
                serverWriter.print(body);
            }

            serverWriter.flush();


            //Foward back response back to client
            BufferedReader serverReader = new BufferedReader(
                    new InputStreamReader(serverSocket.getInputStream())
            );
            PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream());
            line = null;
            while ((line = serverReader.readLine()) !=null && !line.isEmpty()){
                System.out.println("SERVER -> " + line);
                clientWriter.println(line);
            }
            clientWriter.flush();

            //Close connections
            serverSocket.close();
            clientSocket.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
