import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;


public class TestUtil {

	public static final int SUCCESS_STATUS_CODE = 0;
	
	public static void testDescription(String name, String descr) {
		for (int i = 0; i < 10; i++) {
			System.err.println();
		}
		System.err.println("=========================================================================================================");
		System.err.println(String.format("-------------------------------- TEST: \"%s\" --------------------------------", name));
		System.err.println(String.format("** Description: %s **", descr));
		System.err.println("Details: \n\n");
	}
	
	public static void statusMessage(String message) {
		System.err.println(">>>> " + message);
	}
	
	public static void cleanFolders(File ... folders) throws IOException {
		for (File folder : folders) {
			for (File child : folder.listFiles()) {
				if(child.exists() && child.isDirectory()) {
					cleanFolders(child);
				} 
				
				if(child.exists()){
					child.delete();
				}
			}
		}
	}
	
	public static void writeToFile(File file, String value, boolean append) throws IOException {
		if(!file.exists()){
			file.createNewFile();
		}
		try(FileWriter fileWritter = new FileWriter(file, append);
    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter)){
			bufferWritter.write(value);
			bufferWritter.flush();
		}
	}

	public static boolean compareFolders(File source, File backup, List<String> messages) {
		boolean result = true;
		for (File sourceFile : source.listFiles()) {
			if(sourceFile.isFile()){
				messages.add(String.format("Comparing File \"%s\" ...", sourceFile.getName()));
				File backupFile = new File(backup, sourceFile.getName());
				if(!backupFile.exists()){
					result &= false;
					messages.add(String.format("ERROR: File \"%s\" does not exist in the backup", sourceFile.getName()));
				} else if(!backupFile.isFile()){
					result &= false;
					messages.add(String.format("ERROR: File \"%s\" is a directory in the backup", sourceFile.getName()));
				} else if(!compareFiles(sourceFile, backupFile)) {
					result &= false;
					messages.add(String.format("ERROR: File \"%s\" in the backup is different from the source", sourceFile.getName()));
				} else{
					messages.add(String.format("File \"%s\" matches", sourceFile.getName()));
				}
			}
		}
		for (File backupFile : backup.listFiles()) {
			File sourceFile = new File(source, backupFile.getName());
			if(!sourceFile.exists()){
				result &= false;
				messages.add(String.format("ERROR: File \"%s\" does not exist in the source", backupFile.getName()));
			}
		}
		
		return result;
	}
	
	public static boolean compareFiles(File source, File backup) {
		String cSum1 = checksum(source);
		String cSum2 = checksum(backup);
		return cSum1.equals(cSum2);
	}

	public static String checksum(File file) {
		if(file.isDirectory()){
			throw new IllegalArgumentException("Can't compute checksum of a directory");
		}
		
		try (InputStream in = new FileInputStream(file)){
			java.security.MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int read;
			do {
				read = in.read(buffer);
				if (read > 0){
					md5.update(buffer, 0, read);
				}
			} while (read != -1);
			byte[] digest = md5.digest();
			String strDigest = "0x";
			for (int i = 0; i < digest.length; i++) {
				strDigest += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1).toUpperCase();
			}
			return strDigest;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static File getBackupFolder() {
		File backupFolder = new File("./serverfolder");
		if (!backupFolder.exists()) {
			backupFolder.mkdirs();
		}
		return backupFolder;
	}

	public static File getSourceFolder() {
		File sourceFolder = new File("./clientfolder");
		if (!sourceFolder.exists()) {
			sourceFolder.mkdirs();
		}
		return sourceFolder;
	}

	public static Process[] startServerAndClientLocally(File sourceFolder, File backupFolder) throws IOException, InterruptedException {
		// Find where is the current java exe
		String javaExePath = Paths.get(System.getProperty("java.home"), "bin", "java").toAbsolutePath().toString();
				
		// Start a process for the server ...
		ProcessBuilder serverProcessBuilder = 
                new ProcessBuilder(javaExePath, "-jar", 
                "syncserver.jar",
                "-f",
                backupFolder.getCanonicalPath(),
                "-p",
                "5555");
		statusMessage("Starting server with: " + toString(serverProcessBuilder));
		
		serverProcessBuilder.redirectErrorStream(true);
		serverProcessBuilder.inheritIO();		
		Process serverProcess = serverProcessBuilder.start();
		
		// Give the server some time to start
		Thread.sleep(2000);
		
		// Start a process for the client ...
		ProcessBuilder clientProcessBuilder = 
                new ProcessBuilder(javaExePath, "-jar", 
                "syncclient.jar",
                "-f",
                sourceFolder.getCanonicalPath(),
                "-h",
                "127.0.0.1",
                "-p",
                "5555");
		statusMessage("Starting client with: " + toString(clientProcessBuilder));
		clientProcessBuilder.redirectErrorStream(true);
		clientProcessBuilder.inheritIO();		
		Process clientProcess = clientProcessBuilder.start();

		// Give the client some time to start and connect to the server
		Thread.sleep(2000);
		
		// Return process handles.
		return new Process [] {clientProcess, serverProcess};
	}

	public static String toString(ProcessBuilder processBuilder) {
		StringBuffer buffer = new StringBuffer();
		for (String c : processBuilder.command()) {
			buffer.append(c + " ");
		}
		return buffer.toString();
	}
	
	public static void stopProcesses(Process ...processes){
		for (Process process : processes) {
			try{
				process.destroy();
			} catch(Throwable t) {
				t.printStackTrace();
				continue;
			}
		}
	}
	
}
