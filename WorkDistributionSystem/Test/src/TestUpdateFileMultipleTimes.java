import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestUpdateFileMultipleTimes {

	public static void main(String[] args) throws IOException, InterruptedException {
		TestUtil.testDescription(TestUpdateFileMultipleTimes.class.getSimpleName(), 
				"Periodically (every 10 secs) updates a file (test.txt).");
		
		File sourceFolder = TestUtil.getSourceFolder();
		File backupFolder = TestUtil.getBackupFolder();

		// Put one file in the client and server directories before start
		TestUtil.cleanFolders(sourceFolder, backupFolder);
		TestUtil.writeToFile(new File( sourceFolder, "test.txt"), "TestValue", true);
		TestUtil.writeToFile(new File( backupFolder, "test.txt"), "TestValue", true);
		
		boolean result = true;
		Process[] processes = new Process[0];
		try{
			processes = TestUtil.startServerAndClientLocally(sourceFolder, backupFolder);
			
			// Edit the file several times - every other time replace its entire content
			for (int i = 0; i < 5; i++) {
				TestUtil.statusMessage("Editing test.txt");
				TestUtil.writeToFile(new File( sourceFolder, "test.txt"), "Updated content", i % 2 == 0); 
				
				// Wait 10 seconds and check the folders
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
