import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import filesync.CopyBlockInstruction;
import filesync.EndUpdateInstruction;
import filesync.Instruction;
import filesync.NewBlockInstruction;
import filesync.SynchronisedFile;


public class Transmit {
	private static final JSONParser parser = new JSONParser();
	
	public synchronized void send_create(DataInputStream in,DataOutputStream out,String file_name,String file_path) throws IOException, InterruptedException, ParseException{
			out.writeUTF(header(file_name, "create"));
			System.err.println("Sending header:"+header(file_name, "create"));
			out.flush();
			SynchronisedFile file=new SynchronisedFile(file_path);
			file.CheckFileState();
			while(true){
				Instruction ins=file.NextInstruction();
				String ins_json = ins.ToJSON();
				System.err.println("Sending Instruction:"+ins_json);
				out.writeUTF(ins_json);
				out.flush();

				String response_json=in.readUTF();
				JSONObject response = (JSONObject) parser.parse(response_json);
				String type=(String)response.get("Type");
				if(type.equals("error")){
					Instruction upgraded=new NewBlockInstruction((CopyBlockInstruction)ins);
					String msg2 = upgraded.ToJSON();
					out.writeUTF(msg2);
					out.flush();
					System.err.println("Sending CopyBlock Instruction:"+msg2);
				}

				if (ins instanceof EndUpdateInstruction){
					break;
				}
			}
		}
	
	private synchronized static  String header(String file_name, String type) {
		JSONObject obj=new JSONObject();
		obj.put("Type", type);
		obj.put("Name", file_name);
		return obj.toJSONString();
	}

	public synchronized  void send_modify(DataInputStream in,DataOutputStream out,String file_name,String file_path) throws IOException, InterruptedException, ParseException{
		
			out.writeUTF(header(file_name, "modify"));
			out.flush();
			System.err.println("Sending header:"+header(file_name, "modify"));
			SynchronisedFile file=new SynchronisedFile(file_path);
			file.CheckFileState();
			while(true){
				{
					
					Instruction ins=file.NextInstruction();
					String ins_json = ins.ToJSON();
					System.err.println("Sending:"+ins_json);
					out.writeUTF(ins_json);
					out.flush();

					
					String response_json=in.readUTF();
					JSONObject response = (JSONObject) parser.parse(response_json);
					String type=(String)response.get("Type");
					if(type.equals("error")){
						Instruction upgraded=new NewBlockInstruction((CopyBlockInstruction)ins);
						String msg2 = upgraded.ToJSON();
						out.writeUTF(msg2);
						out.flush();
						System.err.println("Sending:"+msg2);
					}

					if (ins instanceof EndUpdateInstruction){
						break;
					}
				}
			}
	}

	public synchronized void send_delete(DataOutputStream out,String file_name) throws IOException{
		out.writeUTF(header(file_name,"delete"));
		out.flush();
		System.err.println("Sending header:"+header(file_name,"delete"));
	}

}
