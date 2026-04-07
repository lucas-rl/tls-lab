import java.io.*;
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

            InputStream clientInputStream = clientSocket.getInputStream();
            OutputStream clientOutputStream = clientSocket.getOutputStream();

            Socket serverSocket = new Socket("localhost", 8080);
            InputStream serverInputStream = serverSocket.getInputStream();
            OutputStream serverOutputStream = serverSocket.getOutputStream();


            new Thread(() -> forward(clientInputStream, serverOutputStream)).start();
            new Thread(() -> forward(serverInputStream, clientOutputStream)).start();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void forward(InputStream in, OutputStream out) {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
        } catch (IOException e) {
            // connection closed
        }
    }
}
