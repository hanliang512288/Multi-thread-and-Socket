import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;


public class Client {
	
	public static void main(String[] args){
		
		System.setProperty("javax.net.ssl.trustStore", "Users/bragilee/DS/Project/Resources/sslmasterkey.cer");
		System.setProperty("javax.net.ssl.trustStorePassword","135790");
		
		SocketFactory factory=SSLSocketFactory.getDefault();
		
		try{
//			Socket socket=factory.createSocket("144.6.227.232",4444);
			Socket socket =  new Socket("localhost",4444);

			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			String jarPath="/Users/bragilee/DS/Project/Resources/SampleJob/wordcount.jar";
			String inPath="/Users/bragilee/DS/Project/Resources/SampleJob/sample-input.txt";
			String outName="Output";
			
			File jarfile=new File(jarPath);
			
			Thread sendJob = new Thread(new JobThread(socket, jarPath, inPath));
			sendJob.setDaemon(true);
			sendJob.start();
			
			out.writeUTF("Hello Server");
			out.flush();
			String msg=in.readUTF();
			System.out.println(msg);
			Thread.sleep(3000);
//			socket.close();
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			System.err.println("Server is not av");
			System.err.println(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		}
	}
	
}
