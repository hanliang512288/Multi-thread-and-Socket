
public class Test {
	public static void main(String[] args){
//		String path=ServerUti.createJobfolder("hanliang1");
//		System.out.println(path);
		String a =ServerUti.processJarFile("/Users/Larry/Desktop/workshop/SSLServer/hanliang1/wordcount.jar", "/Users/Larry/Desktop/workshop/SSLServer/hanliang1/sample-input.txt","abc.txt");	
		System.out.println(a);
	}	
}