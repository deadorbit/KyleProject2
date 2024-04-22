import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.*;

/**
 * Implements a scheudler
 * 
 * @author: Joshua Hammond, Austin Jackman
 */

public class MyScheduler {
    int numJobs;
    String property;

    int avgWaitTime = 0;
    int maxWait = 0;
    int combined = 0;
    ArrayList<Long> deadlines = new ArrayList<Long>();

    LinkedBlockingQueue<Job> outQueue;
    LinkedBlockingQueue<Job> inQueue;
    Semaphore jobAdder;

    public MyScheduler(int numJobs, String property) {
        this.numJobs = numJobs;
        this.property = property;
        this.inQueue = new LinkedBlockingQueue<>(numJobs);
        this.outQueue = new LinkedBlockingQueue<>(numJobs);
        this.jobAdder = new Semaphore(1);

    }

    /*
     * This is the queue where your code will put each Job that it has selected
     * to run on the CPU in the order it has chosen.
     * You will likely want to create this queue in your constructor.
     */
    public LinkedBlockingQueue<Job> getOutgoingQueue() {
        return this.outQueue;
    }

    // We recieve jobs as they are created and pass them to the outgoing queue
    public LinkedBlockingQueue<Job> getIncomingQueue() {
        return this.inQueue;
    }

    public LinkedBlockingQueue<Job> scheudlingAlgorithm(LinkedBlockingQueue<Job> Jobs) {
        ArrayList<Job> tempArray = new ArrayList<Job>(); // Temporary Array
        LinkedBlockingQueue<Job> tempQueue = new LinkedBlockingQueue<Job>(numJobs);
        this.deadlines.clear();

        for (Job s : Jobs)
            tempArray.add(s);

        // Do math to get statistics
        for (int i = 0; i <= tempArray.size(); i++) {
            this.numJobs++;

            this.avgWaitTime += tempArray.get(i).getWaitTime();
            deadlines.add(tempArray.get(i).getDeadline());
        }

        // Use statistics to predict which algorithm needs to be used
        this.property = "SDF";

        switch (this.property) {
            case "SDF":
                tempArray = shortestDeadlineFirst(tempArray);
                break;

            default:
                break;
        }

        for (int i = 0; i <= tempArray.size(); i++) {

        }

        return tempQueue;

    }

    public ArrayList<Job> shortestDeadlineFirst(ArrayList<Job> Jobs) {
        Comparator<Job> sorter = new Comparator<Job>() {
            public int compare(Job left, Job right) {
                return Math.toIntExact(left.getDeadline() - right.getDeadline());
            }
        };
        Jobs.sort(sorter);
        return Jobs;
    }

    /*
     * You will take jobs from the incoming queue (and probably store them), and
     * You will put jobs on the outgoing queue in the order of your choosing.
     */
    public void run() {
        while (numJobs >= 0) {
            try {
                Job job = inQueue.poll();
                // System.out.println(job.getLength());
                scheudlingAlgorithm(inQueue);
                outQueue.put(job);
                numJobs--;
            } catch (Exception e) {
                // pass
            }
        }
    }
}