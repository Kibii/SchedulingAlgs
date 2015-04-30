/* model for a process, you will need a collection of these */
class Process {

public enum State { // added IO to indicate a process is on the I/O device
    NEW, READY, RUNNING, IO, WAITING, TERMINATED
}	

  State state = State.NEW; // current state in the state machine from figure 3.2

/* Put data structures to hold size of CPU and I/O bursts here */
  	
  	//int currentBurst // indicates which of the series of bursts is currently being handled. state can be used to determine what kind of burst
  	int currentIOBurst; //current burst for IO
	int currentCPUBurst; //current burst for CPU
	public byte[] ioBurstSize; //total time taken by each io burst for process
	public byte[] cpuBurstSize; //total time taken by each cpu burst for process
	
	
  double completedTime = 0; // used to calculate remaining time till completion if burst is descheduled
  
}
