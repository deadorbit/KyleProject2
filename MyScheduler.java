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
    long closestDeadline = 0;
    ArrayList<Long> deadlines = new ArrayList<Long>();

    LinkedBlockingQueue<Job> outQueue;
    LinkedBlockingQueue<Job> inQueue;
    LinkedBlockingQueue<Job> tempQueue;

    public MyScheduler(int numJobs, String property) {
        this.numJobs = numJobs;
        this.property = property;
        this.inQueue = new LinkedBlockingQueue<>(numJobs);
        this.outQueue = new LinkedBlockingQueue<>(numJobs);
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

    public Job scheudlingAlgorithm(LinkedBlockingQueue<Job> Jobs) {
        if (Jobs.size() == 0)
            return null;
        else if (outQueue.peek() == null) {
            return Jobs.peek();
        }

        try {
            ArrayList<Job> tempArray = new ArrayList<Job>(); // Temporary Array

            long batchWaitTime = 0;
            this.closestDeadline = -1;

            for (Job s : Jobs)
                tempArray.add(s);

            // Do math to get statistics
            for (int i = 0; i < tempArray.size(); i++) {
                Job job = tempArray.get(i);

                long jobDeadline = job.getDeadline() - job.getTimeCreated();
                long jobWaitTime = job.getWaitTime();
                long jobLength = job.getLength();

                // Job is about to expire and deadline can still be finished in time?
                if (jobDeadline < this.closestDeadline && jobLength + jobWaitTime < jobDeadline) {
                    this.closestDeadline = jobDeadline;
                }

                batchWaitTime += jobWaitTime;
            }

            batchWaitTime = batchWaitTime / tempArray.size();

            // Use statistics to predict which algorithm needs to be used

            // Wait time is rising
            int waitThreshold = 5;
            if (batchWaitTime - this.avgWaitTime > waitThreshold) {
                this.property = "LWF";
            }

            if (this.closestDeadline > 0) {
                this.property = "SDF";
            }

            this.property = "SJF";

            switch (this.property) {
                case "SDF":
                    tempArray = shortestDeadlineFirst(tempArray);
                    break;
                case "SJF":
                    tempArray = shortestJobFirst(tempArray);
                case "LWF":
                    tempArray = longestWaitTimeFirst(tempArray);
                default:
                    break;
            }

            this.avgWaitTime = (int) ((this.avgWaitTime + batchWaitTime) / 2.0);

            return tempArray.get(0);
        } catch (Exception e) {
            System.out.println("SCHEUDLING EXECPTION: " + e);
            return null;
        }
    }

    public ArrayList<Job> shortestDeadlineFirst(ArrayList<Job> Jobs) {
        Comparator<Job> sorter = new Comparator<Job>() {
            public int compare(Job left, Job right) {
                return Math.toIntExact(
                        (left.getDeadline() - left.getTimeCreated()) - (right.getDeadline()) - right.getTimeCreated());
            }
        };
        Jobs.sort(sorter);
        return Jobs;
    }

    public ArrayList<Job> shortestJobFirst(ArrayList<Job> Jobs) {
        Comparator<Job> sorter = new Comparator<Job>() {
            public int compare(Job left, Job right) {
                return Math.toIntExact(left.getLength() - right.getLength());
            }
        };
        Jobs.sort(sorter);
        return Jobs;
    }

    public ArrayList<Job> longestWaitTimeFirst(ArrayList<Job> Jobs) {
        Comparator<Job> sorter = new Comparator<Job>() {
            public int compare(Job left, Job right) {
                return Math.toIntExact(right.getWaitTime() - left.getWaitTime());
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
        while (numJobs > 0) {
            // System.out.println("JOBS REMAINING: " + numJobs);
            try {
                if (inQueue.size() > 0) {
                    // System.out.println("INQUEUE SIZE: " + inQueue.size());
                    Job job = scheudlingAlgorithm(inQueue);
                    if (job != null) {
                        inQueue.poll();
                        outQueue.put(job);
                        numJobs--;
                    }
                }
            } catch (Exception e) {
                // System.out.println("RUN EXECEPTION: " + e);
            }
        }
    }
}
