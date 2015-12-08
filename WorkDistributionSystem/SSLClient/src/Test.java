import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class Test {
	public static void main(String[] args) throws IOException{
		File f=new File("/Users/Larry/Desktop/syncclient.jar");
		BufferedInputStream br=new BufferedInputStream(new FileInputStream(f));
		System.out.println(br.read());
		
	}
}
