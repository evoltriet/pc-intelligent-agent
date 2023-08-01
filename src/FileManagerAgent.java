
/*****************************************************************
The “FileManagerAgent” will check the specified directory every 1 second to see how many files there are.
It will delete files older than x amount of time if there are more than 10 files in the directory.
 *****************************************************************/

import java.io.File;
import java.util.Date;

import jade.core.Agent;
import jade.core.behaviours.*;

public class FileManagerAgent extends Agent {
	//Agent's beliefs
	private File[] fileList;
	private Integer numberOfFiles;
	
	//Agent's knowledge
	private static String directoryPath;
	private static Integer maxFiles;
	private static Integer fileDeleteAge;

	protected void setup() {
		// Printout a welcome message
		System.out.println("Hallo! File-manager agent " + getAID().getName() + " is ready.");
		// set the directory environment the agent will manage.
		directoryPath = "/Users/tpham103/Desktop/tmp";
		
		// The goal state the agent tries to achieve. The max number of files we want to
		// maintain in the directory.
		maxFiles = 10;
		// We are giving the agent the knowledge that files who are older than x
		// milliseconds should be deleted.
		fileDeleteAge = 20000;
		
		
		// Add a TickerBehaviour to observe the environment (in this case the directory)
		// every 1 second.
		addBehaviour(new CountFiles(this, 1000));
		// Add a TickerBehaviour that will delete files older than x amount of time if
		// there are more than 10 files.
		addBehaviour(new ConditionalRemoveFiles(this, 1000));

	} // End of agent setup

	/**
	 * Inner class CountFiles. This is the behavior used by FileManagerAgent to get
	 * the list of files and count the number of files in the directory.
	 */
	private class CountFiles extends TickerBehaviour {

		// standard constructor for ticker behaviors
		public CountFiles(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			// Get the list of files
			fileList = new File(directoryPath).listFiles();
			// Count the number of files in the directory and update the agent's beliefs.
			numberOfFiles = fileList.length;
			System.out.println("Number of Files: " + numberOfFiles.toString());
		}

	} // End of inner class CountFiles

	
	/**
	 * Inner class ConditonalRemoveFiles. This is the behavior used by
	 * FileManagerAgent to delete files older than x amount of time if there are
	 * more than 10 files.
	 */
	private class ConditionalRemoveFiles extends TickerBehaviour {

		// standard constructor for ticker behaviors
		public ConditionalRemoveFiles(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			// If there are more than 10 files
			if (numberOfFiles > maxFiles) {
				// loop through the list of files and delete files beyond a certain age
				for (int x = 0; x < fileList.length; x++) {
					long diff = new Date().getTime() - fileList[x].lastModified();
					if (diff > fileDeleteAge) {
						try {
							fileList[x].delete();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

	} // End of inner class ConditionalRemoveFiles

}
