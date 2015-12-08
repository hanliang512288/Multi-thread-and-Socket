import org.kohsuke.args4j.Option;


public class CmdOptions {
	@Option(name="-f", required=true)
	private String folder_path;
	
	@Option(name="-p", required=false)
	private int port = 4444;
	
	public String get_folder_path() {
		return folder_path;
	}
	
	public int get_port() {
		return port;
	}
}
