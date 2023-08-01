
/*****************************************************************
The “TestAgent” will check the directory every 1 second to see how many files there are.
It will create a new file if there are less than 20 files.
 *****************************************************************/

import java.io.File;
import jade.core.Agent;
import jade.core.behaviours.*;

public class TestAgent extends Agent {
	private File[] fileList;
	private Integer numberOfFiles;
	private Integer tickCount;
	
	private static String directoryPath;
	private static Integer maxFiles;

	protected void setup() {
		tickCount = 0;
		// Printout a welcome message
		System.out.println("Hallo! Test-agent " + getAID().getName() + " is ready.");
		// set the directory environment the agent will manage.
		directoryPath = "/Users/tpham103/Desktop/tmp";
		// The goal state the agent tries to achieve. The max number of files the agent
		// creates up to.
		maxFiles = 20;

		// Add a TickerBehaviour to observe the environment (in this case the directory)
		addBehaviour(new CountFiles(this, 1000));
		// Add a TickerBehaviour that will add files if there are less than 20 files.
		addBehaviour(new ConditionalAddFiles(this, 1000));

	} // End of agent setup

	/**
	 * Inner class CountFiles. This is the behavior used by agent to get
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
			// Count the number of files in the directory and update the agent's knowledge.
			numberOfFiles = fileList.length;
		}

	} // End of inner class CountFiles
	

	/**
	 * Inner class ConditonalAddFiles. This is the behavior used by
	 * agent to add files if there are less than 20 files.
	 */
	private class ConditionalAddFiles extends TickerBehaviour {

		// standard constructor for ticker behaviors
		public ConditionalAddFiles(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			// If there are less than 20 files
			if (numberOfFiles < maxFiles) {
				//Add a new file
				try {
					String filePath = directoryPath + File.separator + "test" + tickCount + ".txt";
					File newFile = new File(filePath);
					newFile.getParentFile().mkdirs();
					newFile.createNewFile();
					System.out.println("New file created: " + filePath);
					tickCount++;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	} // End of inner class ConditionalRemoveFiles

}
