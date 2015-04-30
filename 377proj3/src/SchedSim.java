import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.io.BufferedInputStream;
import java.io.BufferedReader;


class SchedSim {

	public static int maxProcesses; // cap on total processes for simulation
	public static int maxCPUbursts; // cap on total CPU bursts per process
	public static double time = 0; // current time in simulation, starting at zero
	public static int quantum = 1; //TODO: what's a good default quantum
	
	
	//cpu process and io process initialized
	public static Process cpuProcess = null;
	public static Process ioProcess = null;
	
	//Event heap
	public static ArrayList<Event> eventHeap;
	
	//Process table: A collection of Processes, in some arbitrary 
	//order which can be thought of as equivalent to PID. 
	//It would be prudent to consider this table's growth in memory as the 
	//DES loop runs. Its size should be controlled by setting 
	//unused pointers to NULL when processes are terminated.
	
	//FIFO queue that handles events
	public static LinkedList<Process> ioQueue;
	
	//A priority queue for the CPU. 
	//The comparator function for this queue should be 
	//modifiable. You should write one function for each 
	//scheduling algorithm.
	public static PriorityQueue<Process> readyQueue;


	public enum Algorithm { // algorithm to use for entire run of simulation
		FCFS, SJF, SRTF, RR
	}	

	public static Algorithm algorithm;


	//ADDED
	//Algoritm Comparator methods
	//FCFS
	public static class FCFS implements Comparator<Process> {
		@Override
		public int compare(Process p1, Process p2) {
			//TODO: fix dis
			System.out.println("FCFS");
			return -1;
		}
	}

	//SJF
	public static class SJF implements Comparator<Process> {

		@Override
		public int compare(Process p1, Process p2) {
			System.out.println("SJF");
			System.out.println("p1.currentCPUBurst=" + p1.currentCPUBurst);
			System.out.println("p2.currentCPUBurst=" + p2.currentCPUBurst);
			System.out.println("p1.cpuBurstSize[p1.currentCPUBurst] =" + p1.cpuBurstSize[p1.currentCPUBurst]);
			System.out.println("p2.cpuBurstSize[p2.currentCPUBurst] =" + p2.cpuBurstSize[p1.currentCPUBurst]);

			//process1 larger than process2
			if (p1.cpuBurstSize[p1.currentCPUBurst] > p2.cpuBurstSize[p2.currentCPUBurst]) {
				return 1;
				//process1 smaller than process2
			} else if (p1.cpuBurstSize[p1.currentCPUBurst] < p2.cpuBurstSize[p2.currentCPUBurst])
				return -1; 
			else //equal
				return 0;
		}
	}

	//SRTF
	public static class SRTF implements Comparator<Process> {
		@Override
		public int compare(Process p1, Process p2) {
			System.out.println("SRTF");
			System.out.println("p1.completedTime=" + p1.completedTime);
			System.out.println("p2.completedTime=" + p2.completedTime);
			//process1 runtime greater than process2
			if(p1.completedTime > p2.completedTime)
				return 1;
			//process1 runtime less than process2
			else if(p1.completedTime < p2.completedTime)
				return -1;
			//equal
			else
				return 0;
		}
	}
	//TODO: RR
	//RR - Round Robin. Processes get an even time slice and are then descheduled. 
	//You will need to take the quantum to use as an additional optional argument (have a default):
	//TODO: MLFQ
	//MLFQ - Multi Level Feedback Queues. There are multiple tiered process queues 
	//with various time slices. See the book or lecture slides for algorithm 
	//details. You will need to introduce additional data structures to simulate this. 
	//There will need to be numerous optional appended arguments as well controlling 
	//the amount and behavior of the queues (have defaults for them).
	public static class MLFQ implements Comparator<Process>{
		//make queues for each level of priority
			//each queue gets certain slice of CPU time
		//how2store? use existing readyqueue?
		//which algorithms to use for each?
		//scheduling must be done between the queues
		
		//unsure where2handle MLFQ functionality
		
		//priority queues: q1 and q2 (how many to have?) q1 being higher priority stored as linkedlist
		//q1 uses more CPU than q2 to be handled faster, all of q1 
			//handled before start handling q2
		//process p comes in
		//put p on q1
		//q1 uses RR: if p does not finish before quantum expires, drop to q2
		//q2 uses RR (with larger quantum) to handle processes that have been waiting the longest first
		//handle all of q1 then switch to q2 (best approach?)
		//not sure how to handle this behavior in the MLFQ comparator 
		
	}

	
	
	//Run method
	public static void run(Process p) {
		System.out.println("Running");
		p.state = Process.State.RUNNING;
		Event newEvent = new Event();
		newEvent.type = Event.Type.CPU_DONE; //should this be set to this?
		//TODO: not sure if this should be time or arrival time or wut
		newEvent.time = time+(int)(p.cpuBurstSize[p.currentCPUBurst]&0xff); //read in only positive byte value

		//TODO: not sure if this is being set correctly
		p.completedTime = (int)(p.cpuBurstSize[p.currentCPUBurst])&0xff; //read in only positive byte value

		//add event to heap
		eventHeap.add(newEvent);

		//set current process
		cpuProcess = p;
	}

	//Main functionality of SchedSim
	public static void main(String [] args) throws IOException {
		// parse arguments
		// you might want to open the binary input file here

		//parse arguments
		String filename = args[0];
		int maxProcesses = Integer.parseInt(args[1]);
		int maxCPUBursts = Integer.parseInt(args[2]);
		
		//optional parameter to set quantum
		if(args[4]!=null){ 
			int quantum = Integer.parseInt(args[4]);
		}//if quantum not specified, stays as default value (=1)

		//test output
		System.out.println("maxProcesses= "+ maxProcesses);
		System.out.println("maxCPUBursts= "+ maxCPUBursts);
		
		//changed
		System.out.println("quantum = " + quantum);

		//algorithm to use
		algorithm = Enum.valueOf(Algorithm.class, args[3]);

		System.out.println("Algorithm =" + algorithm);

		//input file
		FileInputStream stream = null; //initialize
		try {
			//open binary file
			stream = new FileInputStream(filename);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		System.out.println("Comparator created");
		Comparator<Process> comp;
		//Comparator function set according to input
		if (algorithm == Algorithm.FCFS)
			comp = new FCFS();
		else if (algorithm == Algorithm.SJF)
			comp = new SJF();
		else 
			comp = new SRTF();
		//TODO: add other algorithms

		//Initialize Data structres
		//c returns true if 1st argument < 2nd argument
		readyQueue =new PriorityQueue<Process>(50000, comp); //TODO: what size?
		ioQueue =new LinkedList<Process>(); //TODO: check if good storage types
		eventHeap =new ArrayList<Event>();

		//test output
		System.out.println("First event created");

		//First event created + added to empty heap
		Event init = new Event();
		init.time = 0;
		init.type = Event.Type.ARRIVAL;
		eventHeap.add(init);
		double totalRuntime =0;
		double totalCompletionTime =0;
		int totalProcesses =0;
	

		//DES Loop
		while (!eventHeap.isEmpty())
		{
			//e = last event added to heap 
			Event e =eventHeap.remove(eventHeap.size()-1);
			time = e.time; //TODO: should this be same variable or wut? time maybe overloaded. might be causing problems

			//Switch that handles each event type
			switch (e.type) {

			//Case 1: Arrival
			case ARRIVAL:
				//test output
				System.out.println("Arrival");

				//run while file not end of input and less than max number of processes
				for (int a = 0; a <maxProcesses; a++) {

					//still more input
					if (stream.available() > 0) {
						Process p = new Process();

						//arrivalTimeIncrement- yields time at which the next process
						//arrives after the arrival of the previous process
						//TODO: double vs float?

						//time at which next process arrives after arrival of this process
						float nextProcessTime = (float)(stream.read()/10.0); 

						//number of CPU bursts process actually uses
						int numCPUBursts = stream.read()%maxCPUBursts+1; 

						//series of nCPUbursts bytes, each divided by 25.6
						//= total time taken by each IO burst done by the process
						p.cpuBurstSize = new byte[numCPUBursts];
						stream.read(p.cpuBurstSize); //TODO: does this read all the way or go too far or wut?
						for (int i = 0; i < numCPUBursts; i++) 
							p.cpuBurstSize[i] /= 25.6;

						//series of nCPUbursts-1 bytes, each divided by 25.6
						//= total time taken by each IO burst done by process
						p.ioBurstSize = new byte[numCPUBursts - 1];
						stream.read(p.ioBurstSize);
						for (int i = 0; i < numCPUBursts - 1; i++)
							p.ioBurstSize[i] /= 25.6;

						//no cpuProcess yet
						if (cpuProcess == null){
							System.out.println("if cpuProcess == null. Supposedly first run");
							run(p);

						}else //there exists cpuProcess
						{ 
							System.out.println("compare p and cpuProcess: "+ readyQueue.comparator().compare(p,  cpuProcess));
							//compare processes on readyQueue
							//add to readyQueue based on comparator function that determines which
							//process is "less than"
							if (readyQueue.comparator().compare(p, cpuProcess) > 0) {//CHANGED
								System.out.println(" readyQueue.comparator().compare(p, cpuProcess)>0");
								//p is larger than cpuProcess
								//TODO: check to make sure > logic is solid
								
								cpuProcess.state = Process.State.READY;

								cpuProcess.completedTime -=cpuProcess.completedTime
										- time; //TODO:maybe this just be cpuProcessCompletedTime-=time?

								//add cpuProcess to ready queue
								readyQueue.add(cpuProcess);

								//remove last event in event heap
								eventHeap.remove(eventHeap.size() - 1);
								run(p);
								//TODO: quantum slice
							} else { //cpuProcess is larger than p
								System.out.println("cpuProcess > p");

								//add p to ready queue
								p.state = Process.State.READY;
								readyQueue.add(p);
								//TODO: should stuff happen here, like run(cpuProcess)?
							}
						}

						//increment number of processes
						totalProcesses++;
						//Create new event to handle next process
						Event newEvent = new Event();
						newEvent.type = Event.Type.ARRIVAL;
						System.out.println("time = " + time);
						System.out.println("next event time = " + nextProcessTime);

						//time of new event
						newEvent.time = time + nextProcessTime; //TODO: not sure if this should just be nextProcessTime vs adding to time
						eventHeap.add(newEvent);
					}
				}
				break;

				//Case 2: CPU Burse Completion Case
				//TODO: name
			case CPU_DONE:
				//test output
				System.out.println("current CPUBurst (before) = "+ cpuProcess.currentCPUBurst);
				System.out.println("CPU Done");
				System.out.println("currentCPUBurst =" + cpuProcess.currentCPUBurst);

				cpuProcess.currentCPUBurst++; //null pointer exception here for SJF for input > 1
				//not sure if currentCPUBurst should be incremented here or what
				//if this was last of CPU bursts for process, set state to terminated
				//Else, if IO device is idle can be placed on IO device w/ IO status and IOCompletion event added to event queue (wut
				//Else put on IO queue with status waiting
				//If ready queue not empty, then a new process is moved from

				System.out.println("After increment CPU burst");
				//TODO: not sure what this logic is

				if (cpuProcess.currentCPUBurst == cpuProcess.cpuBurstSize.length) {
					System.out.println("currentCPUBurst == cpuProcess.cpuBurstSize.length");
					System.out.println("state now terminated + cpuProcess is null");

					//terminate process
					cpuProcess.state = Process.State.TERMINATED;
					//TODO: decrement total number of processes, handle in ready queue
					
					//reinitialize cpuProcess
					cpuProcess = null;

					//calculate total time to complete
					totalCompletionTime += time;

					//readyQueue not empty
					if (!readyQueue.isEmpty()){
						System.out.println("Ready Queue is not empty");

						//pop off top of queue and return
						run(readyQueue.poll());
					} // TODO: else break if no processes in ready queue

					//currentCPUBUrst not equal to the size of the cpuBurstSize
					
				} else 
				{
					System.out.println("currentCPUBurst != cpuProcess.cpuBurstSize.length");

					//if currentIOBurst is less than size of current process ioBurstSize
					
					if (cpuProcess.currentIOBurst < cpuProcess.ioBurstSize.length) {
						System.out.println("cpuProcess.currentIOBurst <cpuProcess.ioBurstSize.length");

						//no current ioProcess
						if (ioProcess == null) {
							System.out.println("ioProcess == null");

							//ioProcess inialized to cpuProcess
							ioProcess=cpuProcess;

							//create new event for io
							Event ioEvent = new Event();
							//initialize io process and event
							ioProcess.state = Process.State.IO;
							ioEvent.type = Event.Type.IO_DONE;

							//TODO: check when this should happen
							ioEvent.time = time+(int)(ioProcess.ioBurstSize[ioProcess.currentIOBurst]&0xff); //read in only positive byte value

							//add event to event heap
							eventHeap.add(ioEvent);

							//reinitialize cpuProcess
							cpuProcess = null;
						} 
						else //There is an ioProcess and cpuProcess is waiting
						{
							//test output
							System.out.println("ioProcess != null");

							//wait
							cpuProcess.state=Process.State.WAITING;

							//TODO: cpuProcess added to ioQueue? or ioProcess?
							ioQueue.add(cpuProcess);

							//reinitialize cpuProcess
							cpuProcess = null;
						}
						
					} 
					else //cpuProcess.currentIOBurst not less than size of IO burst container thing
					{
						System.out.println("cpuProcess.currentIOBurst not less than IOBuffer.length");
						//run process
						run(cpuProcess);
					}
				}
				break;

			//Case 3: IO Completion
			case IO_DONE:
				System.out.println("IO Completion");

				//TODO: incrememnt IO burst here?
				ioProcess.currentIOBurst++;

				//add ioProcess to ready queue
				//TODO: check to see if end of line, if so no more CPUBurst needed, terminate
				readyQueue.add(ioProcess);

				//reinitialize ioProcess
				ioProcess = null;

				//if no current cpuProcess, pop from ready queue
				if (cpuProcess == null) {
					//run next process in line
					run(readyQueue.poll());
				}

				//ioQueue still has things
				if (!ioQueue.isEmpty()) 
				{
					//set ioProcess to next in queue
					ioProcess=ioQueue.poll();

					//create new Event for io
					//code repetition but whatever, fix later (TODO)
					Event ioEvent = new Event();
					ioEvent.type = Event.Type.IO_DONE;
					ioEvent.time =time+ioProcess.ioBurstSize[ioProcess.currentIOBurst]; //TODO: again, check time logic here
					//add event to heap
					eventHeap.add(ioEvent);
				}
			}

			//cpuProcess has value
			if (cpuProcess != null) {
				System.out.println("cpuProcess != null");
				//calculate runtime
				totalRuntime+=cpuProcess.completedTime;
			}
		}

		
		
		//Output
		double averageRuntime=totalRuntime/totalProcesses;
		double averageCompletionTime=totalCompletionTime/totalProcesses;

		System.out.println("Average run time for " + algorithm + "is " + averageRuntime);
		System.out.println("Average completion time for " + algorithm + "is " + averageCompletionTime);
		System.out.println("Total runtime = "+ totalCompletionTime);
		
	}

}


//INSTRUCTIONS DUMP

/* DES loop */
// see psudeocode in the assignment
// all of your input reading occurs when processing the Arrival event
//The general algorithm of a DES is a while loop which continues 
//as long as there are events remaining in the heap to be 
//processed. ( The heap must obviously be populated with at 
//least one initial event before the while loop commences. )

//The while loop extracts the next event from the event heap, 
//determines what type of event it is, and processes it 
//accordingly, ( generally with a switch on the event type. ) 
//Depending on the type of event and other conditions, new ( future ) 
//events may be generated, which are then added into the 
//event heap to be processed when their time arrives.

//time variable updated whenever an event is extracted 
//from the event heap
//-->Since each event has an associated time at which 
//it occurs, we can update the time variable to match the time 
//of the event which was just extracted from the heap.

//priority_queue template used
//priority_queue<Type, Container, ComparatorFunction>
//-->ComparatorFunction= function used to order items in priority queue
//-->takes two arguments of type Type and returns true if first argument is less than second argument
//author determines what "less than" means


//case 1: default
//Prints all the information about the 
//Event, and nothing more. This will handle any Event 
//types that have not yet had their cases written. 
//Initially it will report the Arrival event used to 
//initialize the event queue, which you can use to test 
//that the queue was properly initialized.

//case 2:Arrival

// output statistics



