import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import filesync.BlockUnavailableException;
import filesync.EndUpdateInstruction;
import filesync.Instruction;
import filesync.InstructionFactory;
import filesync.SynchronisedFile;


public class SyncFile implements Runnable{

	String folder_path;
	DataInputStream in;
	DataOutputStream out;
	
	private static final JSONParser parser = new JSONParser();

	SyncFile(Socket socket,String folder){

		try{
			in = new DataInputStream(socket.getInputStream());
			out= new DataOutputStream(socket.getOutputStream());
			folder_path=folder;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
		while(true){

			String header_json;
			
				header_json = in.readUTF();
				JSONObject header = (JSONObject) parser.parse(header_json);
				System.err.println("Receving header:"+header);
				String file_name=(String) header.get("Name");
				String type=(String) header.get("Type");
				Path folder=Paths.get(folder_path);
				Path file_path = folder.resolve(file_name);

				if (type.equals("delete")){
					File file = file_path.toFile();
					file.delete();
				}
				else if (type.equals("create")){
					File file = file_path.toFile();  
					file.createNewFile();
					filesync(in,out,file_path.toString());
				}else{
					filesync(in,out,file_path.toString());
				}
			} 
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("The client was shut down mannually.");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void filesync(DataInputStream in, DataOutputStream out,String file_path) throws IOException{
		SynchronisedFile server_file=new SynchronisedFile(file_path.toString());
		while(true){
			String ins_json= in.readUTF();
			System.err.println("Receieving:"+ins_json);

			InstructionFactory ins_fac=new InstructionFactory();
			Instruction ins=ins_fac.FromJSON(ins_json);

			try{
				server_file.ProcessInstruction(ins);
				out.writeUTF(pass_json());
				out.flush();
			}catch (BlockUnavailableException e) {
				// TODO Auto-generated catch block
				try{
					out.writeUTF(error_json());
					out.flush();
					String feedback =in.readUTF();
					System.err.println("Receieving:"+feedback);
					Instruction receivedInst2 = ins_fac.FromJSON(feedback);
					server_file.ProcessInstruction(receivedInst2);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.exit(-1);
				} catch (BlockUnavailableException e1) {
					assert(false); // a NewBlockInstruction can never throw this exception
				}
			}

			if (ins instanceof EndUpdateInstruction){
				break;
			}
		} 
	}

	private static String  pass_json(){
		JSONObject obj=new JSONObject();
		obj.put("Type", "pass");
		return obj.toJSONString();

	}

	private static String error_json(){
		JSONObject obj=new JSONObject();
		obj.put("Type", "error");
		return obj.toJSONString();
	}



}

