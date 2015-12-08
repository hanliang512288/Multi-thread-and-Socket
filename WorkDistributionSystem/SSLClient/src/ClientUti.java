import java.util.Base64;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ClientUti {
	
private static final JSONParser parser = new JSONParser();
	
	public static void sendFile(Socket s, DataInputStream in, DataOutputStream out, String fileAddress){
		File sendFile = new File(fileAddress);
		int size = (int) sendFile.length();
		String fileName = fileNameJson(sendFile.getName());
		String bodyEncode = getFileString(fileAddress);
		try {
			//send file name in json
			out.writeUTF(fileName);
			System.err.println("Sending file name and size: " + fileName);
			out.flush();
				String[] bodies = bodyEncode.split(System.getProperty("line.separator"));
				System.err.println("File body size: " + bodies.length);
				//send file body size in json
				out.writeUTF(fileBodySizeJson(Integer.toString(bodies.length)));
				out.flush();
				for (int i = 0; i < bodies.length; i++) {
					String fileBody = fileBodyJson(bodies[i]);
					try {
						out.writeUTF(fileBody);
						out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.err.println("Finished");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private synchronized static String fileNameJson(String fileName) {
		JSONObject obj=new JSONObject();
		obj.put("FileName", fileName);
		return obj.toJSONString();
	}
	
	private synchronized static String fileBodySizeJson(String fileBodySize) {
		// TODO Auto-generated method stub
		JSONObject obj=new JSONObject();
		obj.put("FileBodySize", fileBodySize);
		return obj.toJSONString();
	}

	private synchronized static String fileBodyJson(String fileBody) {
		JSONObject obj=new JSONObject();
		obj.put("FileBody", fileBody);
		return obj.toJSONString();
	}
	
	public static String getFileString(String filePath){
		InputStream in = null;
		byte[] fileData = null;
		try 
		{
			in = new FileInputStream(filePath);        
			fileData = new byte[in.available()];
			in.read(fileData);
			in.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		Base64Coder encoder = new Base64Coder();  
		String encodedFileString = encoder.encodeLines(fileData);
		return encodedFileString;
	}


	public static void initializeMaster(WorkerList wl){
		
		ArrayList<Worker> workerlist = wl.workerList;
		SocketFactory factory=SSLSocketFactory.getDefault();

		for(Worker w:workerlist){
			try {
				Socket socket=factory.createSocket(w.ip,w.port);
				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				out.writeUTF(request("Initialize"));
				out.flush();
				String response=in.readUTF();
				JSONObject response_json = (JSONObject) parser.parse(response);
//				String reply=(String)response_json.get("Reply");
				String workload_s=(String)response_json.get("Workload");
				float workload_f=Float.parseFloat(workload_s);
				w.setState("RUNNING");
				w.setWorkLoad(workload_f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				w.state="DOWN";
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private  static  String request(String request) {
		JSONObject obj=new JSONObject();
		obj.put("Request", request);
		return obj.toJSONString();
	}
	
}
