# PC Intelligent Agent with JADE

This repo is a JADE multi-agent prototype for monitoring a PC and reacting to two simple problems: runaway processes and file buildup. It is a demo of observe/decide/act behavior, not a hardened system-management product.

## Problem

The project explores whether lightweight agents can watch local system state, keep a small amount of internal state, and apply rules when a threshold is crossed. The two scenarios are:

- processes that consume too much CPU or memory over time, and
- directories that accumulate too many stale files.

## Approach

- `ProcessManagerAgent.java` uses OSHI to sample running processes every 5 seconds, print the top CPU consumers, and attempt to kill long-lived processes that exceed CPU or memory thresholds.
- `FileManagerAgent.java` checks a target directory every second, counts files, and deletes files that are older than a configured age when the directory is over the file limit.
- `TestAgent.java` is a small file-generator used to create load in the watched directory.
- `simulation/generateCPULoad.py` can simulate CPU stress for process-monitoring experiments.
- `JADEAgentConfiguration.launch` starts `jade.Boot -gui`, which matches the repo's intended JADE startup path in Eclipse.

The agents keep a small amount of state so they can compare current observations against thresholds instead of reacting to a single snapshot.

## Outcome

The repo demonstrates a working multi-agent loop over local system resources:

- processes are sampled and reported,
- suspicious processes can be terminated,
- files can be added and cleaned up automatically, and
- the agent behavior is visible through console output and a JADE GUI launch.

This is useful as an early agentic-systems experiment, but it is still a prototype with aggressive actions and limited safeguards.

## How To Run

The project is built with Maven and targets Java 8.

```bash
mvn compile
```

Use the included Eclipse launch config or start JADE with `jade.Boot -gui`, then launch the agents you want to observe. Before running `FileManagerAgent` or `TestAgent`, update the hard-coded directory path in the source to match your machine.

## Limitations

- The process killer is intentionally aggressive and can terminate real processes.
- Directory paths are hard-coded to the original developer's machine.
- Thresholds are static and tuned for a demo environment.
- The behavior is rule-based, not adaptive or self-learning.
