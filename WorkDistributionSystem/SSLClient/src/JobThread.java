import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class JobThread implements Runnable{
	
		String jarAddress;
		String inAddress;
		ClientUti clientUti;
		Socket socket;
		
		public JobThread(Socket s, String jarAdd, String inAdd){
			socket = s;
			clientUti = new ClientUti();
			jarAddress = jarAdd;
			inAddress = inAdd;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				clientUti.sendFile(socket,in,out,jarAddress);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}

	}
