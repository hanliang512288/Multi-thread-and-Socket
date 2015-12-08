import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;


public class SyncClient {
	public static void main(String[] args) throws  IOException, InterruptedException, CmdLineException{
		
		CmdOptions cmdOptions = new CmdOptions();
		CmdLineParser parser = new CmdLineParser(cmdOptions);
		parser.parseArgument(args);
		String folder_path= cmdOptions.get_folder_path();
		int port=cmdOptions.get_port();
		String hostname= cmdOptions.get_hostname();
	
//		int port=4444;
//		String hostname="localhost";
//		String folder_path="/Users/Larry/Desktop/DStest/FromFolder/";

		try(Socket clientsocket= new Socket(hostname,port);){
			
			DataInputStream in= new DataInputStream(clientsocket.getInputStream());
			DataOutputStream out = new DataOutputStream(clientsocket.getOutputStream());

			Transmit transmit=new Transmit();
			File file_folder = new File(folder_path);
			File[] files = file_folder.listFiles();

			for(File file : files){
				String file_name=file.getName();
				String file_path=file.getAbsolutePath();
				Thread exist=new Thread(new FileThread(in,out,folder_path,file_path, file_name,transmit));
				exist.setDaemon(true);
				exist.start();
			}

			while(true){
				Path path_folder = Paths.get(folder_path);
				WatchService watcher = FileSystems.getDefault().newWatchService();
				WatchKey key= path_folder.register(watcher,StandardWatchEventKinds.ENTRY_CREATE);
				ArrayList<WatchEvent<?>> watchEvents = (ArrayList<WatchEvent<?>>) watcher.take().pollEvents();
				if (!watchEvents.isEmpty()){
					for (WatchEvent<?> watchEvent : watchEvents){
						//					String file_action=watchEvent.kind().toString();
						String file_name = watchEvent.context().toString();
						String file_path = path_folder.resolve(file_name).toString();
						Thread create= new Thread(new FileThread(in,out, folder_path,file_path, file_name,transmit));
						create.setDaemon(true);
						create.start();	
					}
				}
				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}
		}
	}
}

