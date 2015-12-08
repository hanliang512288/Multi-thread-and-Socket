import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;



public class FileThread implements Runnable {

	String file_path;
	String file_name;
	String folder_path;
	Transmit transmit;
	DataInputStream in;
	DataOutputStream out;

	FileThread(DataInputStream i,DataOutputStream o, String folder,String path, String name, Transmit trans){
		in=i;
		out=o;
		file_path=path;
		file_name=name;
		folder_path=folder;
		transmit=trans;
	}
	@Override
	public void run() {
		//		try (Socket socket=clientsocket){
		try {

			transmit.send_create(in, out, file_name, file_path);
			/*
			 * This while loop monitors the file in this thread with two conditions "Delete" and "Modify"
			 */
			ArrayList<WatchEvent<?>> watchEvents=new ArrayList<WatchEvent<?>>();
			Path folder = Paths.get(folder_path);
			WatchService watcher = FileSystems.getDefault().newWatchService();
			Label:
				while(true){
					WatchKey key= folder.register(watcher,StandardWatchEventKinds.ENTRY_DELETE,
							StandardWatchEventKinds.ENTRY_MODIFY);
					watchEvents = (ArrayList<WatchEvent<?>>) watcher.take().pollEvents();
					if (!watchEvents.isEmpty()){
						for (WatchEvent<?> event:watchEvents){
							String file_action=event.kind().toString();
							String file_eve_name = event.context().toString();
							if (file_eve_name.equals(file_name)&&file_action.equals("ENTRY_MODIFY")){
								
									transmit.send_modify(in, out, file_name, file_path);
								
							}
							
							if (file_eve_name.equals(file_name)&&file_action.equals("ENTRY_DELETE")){
								transmit.send_delete(out, file_name);
								break Label;
							}
						}
					}
					boolean valid = key.reset();
					if (!valid) {break;}
				}



		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				transmit.send_delete(out, file_name);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.err.println("The server was shut down mannually.");
			}

		}
	}













}
