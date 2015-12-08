public class Worker {
	public int port;
	public String ip;
	public String state;
	public float workload;
	
	public Worker(int port, String ip){
		this.port=port;
		this.ip=ip;
		workload=-1;
		state="Y";
		
	}
	
	public void setState(String s){
		state=s;
	}
	
	public void setWorkLoad(float load){
		workload=load;
	}
	
}