import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestAddAFilePerSecond {

	public static void main(String[] args) throws IOException, InterruptedException {
		TestUtil.testDescription(TestAddAFilePerSecond.class.getSimpleName(), 
				"Adds a new file to the source folder every 1 second. "
				+ "Periodically checks that souce and back-up match.");
		
		File sourceFolder = TestUtil.getSourceFolder();
		File backupFolder = TestUtil.getBackupFolder();

		// Put one file in the source directory before starting
		TestUtil.cleanFolders(sourceFolder, backupFolder);
		TestUtil.writeToFile(new File( sourceFolder, "test.txt"), "TestValue", true);
		
		boolean result = true;
		Process[] processes = new Process[0];
		try{
			processes = TestUtil.startServerAndClientLocally(sourceFolder, backupFolder);
	
			// Create a file every 1 second
			for (int i = 0; i <= 40; i++) {
				String fileName = "test"+ i + ".txt";
				TestUtil.statusMessage("Creating file "+ fileName + " ...");
				TestUtil.writeToFile(new File( sourceFolder, fileName), "Updated content", true); 
				Thread.sleep(1000);
				
				// Every 20 secs check the status
				if(i > 0 && i % 20 == 0){
					// Give some time for synchronisation.
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
