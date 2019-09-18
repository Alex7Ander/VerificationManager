import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Program {
    public static void main(String[] args){
        Client client = new Client();
        try{
            client.connect();
            client.send("ready");
            client.disconnect();
        }
        catch(IOException ioExp){
            System.out.println(ioExp.getStackTrace());
        }
    }

    static class Client{
        Socket clSocket;
        OutputStream outStream;

        void connect() throws IOException {
            String host = null;
            String filePath = new File(".").getAbsolutePath() + "\\files\\ip.txt";
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
            host = reader.readLine();
            reader.close();
            System.out.println("\n" + host);

            clSocket = new Socket(host, 5051);
            outStream = clSocket.getOutputStream();
        }

        void send(String message) throws IOException {
            byte[] buffer = new byte[1024];
            buffer = message.getBytes();
            outStream.write(buffer);
        }

        void disconnect() throws IOException {
            clSocket.close();
            outStream.close();
        }
    }
}