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

    int avgWaitTime;
    int maxWait;
    int combined;
    int deadlines;

    LinkedBlockingQueue<Job> blockingQueue;

    public MyScheduler(int numJobs, String property) {
        this.numJobs = numJobs;
        this.property = property;
        this.blockingQueue = new LinkedBlockingQueue<>(numJobs);

    }

    /*
     * This is the queue where your code will put each Job that it has selected
     * to run on the CPU in the order it has chosen.
     * You will likely want to create this queue in your constructor.
     */
    public LinkedBlockingQueue<Job> getOutgoingQueue() {
        return new LinkedBlockingQueue<>();
    }

    // We recieve jobs as they are created and pass them to the outgoing queue
    public LinkedBlockingQueue<Job> getIncomingQueue() {
        return this.blockingQueue;
    }

    /*
     * You will take jobs from the incoming queue (and probably store them), and
     * You will put jobs on the outgoing queue in the order of your choosing.
     */
    public void run() {
        while (true) {
            try {
                Thread.sleep(1);
                Job job = this.getIncomingQueue().poll();
                System.out.println(job.getLength());
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}