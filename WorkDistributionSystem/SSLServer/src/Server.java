import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;


public class Server {

	public static void main(String[] args){

		System.setProperty("javax.net.ssl.keyStore", "/Users/bragilee/DS/Project/Resources/sslmasterkey.cer");
		System.setProperty("javax.net.ssl.keyStorePassword","135790");

		int port=4444;
		ServerSocketFactory factory=SSLServerSocketFactory.getDefault();
//		ServerSocket server =factory.createServerSocket(port);
		try(ServerSocket server = new ServerSocket(4444)){
			while(true){
				Socket socket=server.accept();
				
				Thread receiveJob = new Thread(new WorkThread(socket));
				receiveJob.setDaemon(true);
				receiveJob.start();

//				DataInputStream in = new DataInputStream(socket.getInputStream());
//				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//
//				String msg = in.readUTF();
//				System.out.println("Received:"+msg);
//				out.writeUTF("Received:"+msg);
//				out.flush();
				
				
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
