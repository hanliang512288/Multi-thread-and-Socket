import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestUpdateFilesConcurrenlty {

	public static void main(String[] args) throws IOException, InterruptedException {
		TestUtil.testDescription(TestUpdateFilesConcurrenlty.class.getSimpleName(), 
				"Periodically (every 10 secs) and concurrently updates two files (test1.txt and test2.txt).");
		
		File sourceFolder = TestUtil.getSourceFolder();
		File backupFolder = TestUtil.getBackupFolder();

		// Put two files in the source directory before starting
		TestUtil.cleanFolders(sourceFolder, backupFolder);
		TestUtil.writeToFile(new File( sourceFolder, "test1.txt"), "TestValue1", true);
		TestUtil.writeToFile(new File( sourceFolder, "test2.txt"), "TestValue2", true);
		
		boolean result = true;
		Process[] processes = new Process[0];
		try{
			processes = TestUtil.startServerAndClientLocally(sourceFolder, backupFolder);
	
			// Edit the files several times ...
			for (int i = 0; i < 5; i++) {
				TestUtil.statusMessage("Editing the files (test1.txt and test2.txt) ...");
				
				// "Simultaneously" (i.e. about the same time) edit the files
				for (int j = 0; j < 5; j++) {
					TestUtil.writeToFile(new File( sourceFolder, "test1.txt"), "\nUpdated content1 " + j, true); 
					TestUtil.writeToFile(new File( sourceFolder, "test2.txt"), "\nUpdated content2 " + j, true); 
				}

				// Wait for 10 seconds and compare the folders
				Thread.sleep(10000);
				System.out.flush();
				System.err.flush();
				TestUtil.statusMessage("Checking the status of the source folder and the backup ...");
				List<String> messages = new ArrayList<>();
				result &= TestUtil.compareFolders(sourceFolder, backupFolder, messages);
				for (String message : messages) {
					TestUtil.statusMessage(message);
				}
			}
		} catch (Throwable t) {
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
