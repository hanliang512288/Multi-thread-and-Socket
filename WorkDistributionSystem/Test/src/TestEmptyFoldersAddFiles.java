import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestEmptyFoldersAddFiles {

	public static void main(String[] args) throws IOException, InterruptedException {
		TestUtil.testDescription(TestEmptyFoldersAddFiles.class.getSimpleName(), 
				"Starts with an empty source folder and adds two files (test1.txt and test2.txt) afterwards.");
		
		File sourceFolder = TestUtil.getSourceFolder();
		File backupFolder = TestUtil.getBackupFolder();
		TestUtil.cleanFolders(sourceFolder, backupFolder);
		
		boolean result = true;
		Process[] processes = new Process[0];
		try{
			processes = TestUtil.startServerAndClientLocally(sourceFolder, backupFolder);
			
			// Add new files after 5 seconds
			Thread.sleep(5000);
			TestUtil.statusMessage("Adding two files (test1.txt abd test2.txt) in the source directory ...");
			TestUtil.writeToFile(new File( sourceFolder, "test1.txt"), "TestValue", true);
			TestUtil.writeToFile(new File( sourceFolder, "test2.txt"), "TestValue", true);
			
			// Wait 10 seconds and check if they were copied
			Thread.sleep(10000);
			System.out.flush();
			System.err.flush();
			TestUtil.statusMessage("Checking the status of the source folder and the backup ...");
			List<String> messages = new ArrayList<>();
			result &= TestUtil.compareFolders(sourceFolder, backupFolder, messages);
			for (String message : messages) {
				TestUtil.statusMessage(message);
			}
		}catch (Throwable t) {
			result = false;
			TestUtil.statusMessage("A critical error ocurred during the test:");
			t.printStackTrace(System.err);
		} finally {
			TestUtil.stopProcesses(processes);
		}
	
		TestUtil.statusMessage(String.format(" Test Status: %s", result ? "Passed" : "Failed"));
		System.exit(result ? TestUtil.SUCCESS_STATUS_CODE : 1);
	}

}
