import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class WorkThread implements Runnable {
	
	private Socket socket;
	private static final JSONParser parser = new JSONParser();
	
	public WorkThread(Socket s){
		socket=s;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			receiveFile(socket,in,out);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		try {
//			DataInputStream in = new DataInputStream(socket.getInputStream());
//			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//			String request=in.readUTF();
//			JSONObject response_json = (JSONObject) parser.parse(request);
//			String requestType=(String) response_json.get("Request");
//			if (requestType=="Initialize"){
//				float workload=ServerUti.getCPULoad();
//				
//			}
//			else if(requestType=="Transmit"){
//				receiveFile(socket,in,out);
//			}
//			
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
	}

	public static void receiveFile(Socket s, DataInputStream in, DataOutputStream out ){
		// TODO Auto-generated method stub
		try {
			String fileNameJson = in.readUTF();
			JSONObject fileInfo = (JSONObject) parser.parse(fileNameJson);
			String fileName = (String)fileInfo.get("FileName");
			System.err.println("Receiving file name and size: " + fileName);
			String path = "/Users/bragilee/DS/Project/Resources/SampleOutput/"+fileName;
			//		out.writeUTF(serverReady());
			//		out.flush();
			//		System.out.println("Ready to receive.");
			//		System.out.println(path);
			File receiveFile = new File(path);
			try {
				//create a new file if it doesn't exist already
				receiveFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String fileBodySizeReceived = in.readUTF();
			JSONObject fileBodySizeJson = (JSONObject) parser.parse(fileBodySizeReceived);
			String fileBodySizeString = (String) fileBodySizeJson.get("FileBodySize");
			int fileBodySize = Integer.parseInt(fileBodySizeString); 
			System.err.println("File body length: " + fileBodySize);
			String[] fileStringList = new String[fileBodySize];
			FileOutputStream fos = new FileOutputStream(receiveFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			for (int i = 0; i < fileBodySize; i++) {
				String fileEncodedString = in.readUTF();
				JSONObject fileBodyJson = (JSONObject) parser.parse(fileEncodedString);
				String fileBody = (String) fileBodyJson.get("FileBody");
				byte[] fileByte = generateFile(fileBody);
				bos.write(fileByte,0,fileByte.length);
				bos.flush();
			}
			System.err.println("Finished");		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public static byte[] generateFile(String fileEncoder){
		Base64Coder decoder = new Base64Coder();
		byte[] b = decoder.decodeLines(fileEncoder);
		return b;
	}
}
