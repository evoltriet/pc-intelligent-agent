
/*****************************************************************
The “ProcessManagerAgent” will check the processes on the os every 5 seconds to monitor their state.
It will also check for and attempt to kill any runaway processes that are consuming more CPU or Memory than normal.
 *****************************************************************/

import java.io.File;
import java.io.IOException;
import java.util.*;

import jade.core.Agent;
import jade.core.behaviours.*;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystem.ProcessSort;
import oshi.util.FormatUtil;

public class ProcessManagerAgent extends Agent {
	// Agent's beliefs
	private List<OSProcess> procs;

	// Agent's knowledge
	private static double ageThreshold;
	private static double memoryThreshold;
	private static double cpuThreshold;

	protected void setup() {
		// Printout a welcome message
		System.out.println("Hallo! agent " + getAID().getName() + " is ready.");

		// Agent's Knowledge of a runaway process. How you change these variables will
		// decide how aggressive the agent is.
		ageThreshold = 30.0;
		memoryThreshold = 50.0;
		cpuThreshold = 80.0;

		// Add a TickerBehaviour to observe the environment (in this case the os)
		// every 5 second.
		addBehaviour(new CheckProcesses(this, 5000));
		// Add a TickerBehaviour that will identify and attempt to kill runaway
		// processes.
		addBehaviour(new ConditionalKillProcesses(this, 5000));

	} // End of agent setup

	/**
	 * Inner class CheckProcesses. This is the behavior used by ProcessManagerAgent
	 * to get the list of processes and their CPU/memory consumption from the os by
	 * using the OSHI library.
	 */
	private class CheckProcesses extends TickerBehaviour {

		// standard constructor for ticker behaviors
		public CheckProcesses(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			// Getting the system's hardware and os layer
			SystemInfo si = new SystemInfo();
			HardwareAbstractionLayer hal = si.getHardware();
			OperatingSystem os = si.getOperatingSystem();
			GlobalMemory memory = hal.getMemory();

			// Information about total processes
			System.out.println("\nTotal Processes:" + os.getProcessCount() + ", Threads:" + os.getThreadCount()
					+ ", Printing top 10 CPU...");
			// Store a list of processes Sort by highest CPU top 10
			procs = Arrays.asList(os.getProcesses(10, ProcessSort.CPU));

			System.out.println(" PPID   PID  %CPU %MEM Age(sec)    Name");
			for (int i = 0; i < procs.size(); i++) {
				OSProcess p = procs.get(i);
				System.out.format("%5d %5d %5.1f %4.1f %5.1f     %s%n", p.getParentProcessID(), p.getProcessID(),
						100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
						100d * p.getResidentSetSize() / memory.getTotal(), .001d * p.getUpTime(), p.getName());
			}

		}

	}

	/**
	 * Inner class ConditonalKillProcesses. This is the behavior used by
	 * FileManagerAgent to attempt to kill processes that are consuming more CPU and
	 * Memory than normal.
	 */
	private class ConditionalKillProcesses extends TickerBehaviour {

		// standard constructor for ticker behaviors
		public ConditionalKillProcesses(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			// Getting the system's hardware layer
			SystemInfo si = new SystemInfo();
			HardwareAbstractionLayer hal = si.getHardware();
			GlobalMemory memory = hal.getMemory();

			// loop through the list of processes and kill runaway
			for (int i = 0; i < procs.size(); i++) {
				OSProcess p = procs.get(i);
				int currentPID = p.getProcessID();
				double currentAge = .001d * p.getUpTime();
				double currentMemoryConsumed = 100d * p.getResidentSetSize() / memory.getTotal();
				double currentCPUUsage = 100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime();

				// If Process has a parent pid of 1, this means there is no parent process thus
				// potentially being a runaway process.
				if (p.getParentProcessID() == 1) {
					// Find runaways that are consuming too much resources
					if (currentAge > ageThreshold
							&& (currentCPUUsage > cpuThreshold || currentMemoryConsumed > memoryThreshold)) {
						System.out.format("Found runaway process. PID: %5d  name: %s%n", currentPID, p.getName());
						// Kill the process
						Runtime rt = Runtime.getRuntime();
						if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
							try {
								rt.exec("taskkill /F /PID " + currentPID);
							} catch (IOException e) {
								e.printStackTrace();
							}
						else
							try {
								rt.exec("kill -9 " + currentPID);
							} catch (IOException e) {
								e.printStackTrace();
							}
						System.out.format("Killed PID: %5d  name: %s%n%n", currentPID, p.getName());
					}
				}

				// Find memory leakage
				if (currentAge > ageThreshold && currentMemoryConsumed > memoryThreshold) {
					System.out.format("Found memory leakage. PID: %5d  name: %s%n", currentPID, p.getName());
					// Kill the process
					Runtime rt = Runtime.getRuntime();
					if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
						try {
							rt.exec("taskkill /F /PID " + currentPID);
						} catch (IOException e) {
							e.printStackTrace();
						}
					else
						try {
							rt.exec("kill -9 " + currentPID);
						} catch (IOException e) {
							e.printStackTrace();
						}
					System.out.format("Killed PID: %5d  name: %s%n%n", currentPID, p.getName());

				}
			}
		}

	}

}
