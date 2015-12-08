
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;





import org.json.simple.parser.ParseException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;


public class SyncServer {

	public static void main(String[] args) throws ParseException, CmdLineException {
		
		CmdOptions cmdOptions = new CmdOptions();
		CmdLineParser parser = new CmdLineParser(cmdOptions);
		parser.parseArgument(args);
		String folder_path= cmdOptions.get_folder_path();
		int port=cmdOptions.get_port();
		
		
//		String folder_path="/Users/Larry/Desktop/DStest/ToFolder";
//		int port=4444;
		
		try (ServerSocket server = new ServerSocket(port)){
			while(true){

				Socket socket = server.accept();
				Thread sync=new Thread(new SyncFile(socket,folder_path));
				sync.setDaemon(true);
				sync.start();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
