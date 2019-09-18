package VerificationForm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import FileManagePack.FileManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServerService extends Service<Integer> {
	@Override
	protected Task<Integer> createTask() {
		return new Task<Integer>(){
			@Override
			public Integer call() throws IOException {
				InetAddress iaLocal = InetAddress.getLocalHost();
				String strAddr = iaLocal.getHostAddress();
				ArrayList<String> ipList = new ArrayList<String>();
				ipList.add(strAddr);
				FileManager.ItemsToLines(new File(".").getAbsolutePath() + "\\files\\ip.txt", ipList);
				
				ServerSocket serverSocket = new ServerSocket(5051, 1, iaLocal);
				Socket socket = null;
			    while(true){
			    	socket = serverSocket.accept();
			    	InputStream inStream = socket.getInputStream();
			        byte[] line = new byte[1024];
			        inStream.read(line);
			        String msg = new String(line);
			        msg = msg.trim();
			        System.out.println("Message is: " + msg + ". It's length is: " + msg.length());
			        if (msg.equals("ready")){
			        	System.out.println("It was flag for stopping");
			            break;
			        }
			        else{
			        	System.out.println("It was not flag!");
			        }
			   }
			   socket.close();
			   serverSocket.close();
			   return 0;
			}	
		};
	}
}
