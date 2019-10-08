package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Sender {
	
	public void send() throws IOException {
		Client client = new Client();
        client.connect();
        System.out.println("Connected");
        client.send("ready");
        System.out.println("Send msg");
        client.disconnect();
        System.out.println("Disconnected");
	}
	
	static class Client{
        Socket clSocket;
        OutputStream outStream;

        void connect() throws IOException {
            String host = null;
            String startPath = new File(".").getAbsolutePath();
            int index = startPath.lastIndexOf("\\measurment");
            //int index = startPath.lastIndexOf("\\MsgSender");
            startPath = startPath.substring(0, index);
            String filePath = startPath + "\\files\\ip.txt";
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
