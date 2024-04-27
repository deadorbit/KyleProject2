import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.*;

/**
 * Implements a scheduler
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
    ArrayList<Job> sortedList;

    public MyScheduler(int numJobs, String property) {
        this.numJobs = numJobs;
        this.property = property;
        this.inQueue = new LinkedBlockingQueue<>(numJobs);
        this.outQueue = new LinkedBlockingQueue<>(1);
        this.sortedList = new ArrayList<Job>();
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

    public synchronized void schedulingAlgorithm(LinkedBlockingQueue<Job> Jobs) {
        String errormessage;
        if (Jobs.size() == 0) {
            // No Jobs
            return;
        }

        try {
            ArrayList<Job> tempArray = new ArrayList<Job>(); // Temporary Array

            long batchWaitTime = 0;
            this.closestDeadline = -1;

            for (Job s : Jobs)
                tempArray.add(s);

            // Do math to get statistics
            /*for (int i = 0; i < tempArray.size(); i++) {
                Job job = tempArray.get(i);

                long jobDeadline = job.getDeadline() - job.getTimeCreated();
                long jobWaitTime = job.getWaitTime();
                long jobLength = job.getLength();

                // Job is about to expire and deadline can still be finished in time?
                if (jobDeadline < this.closestDeadline && jobLength + jobWaitTime < jobDeadline) {
                    this.closestDeadline = jobDeadline;
                }


                batchWaitTime += jobWaitTime;
                //.out.println(jobDeadline + " " +  this.closestDeadline + " "+ jobLength + " "+ jobWaitTime + " "+ jobDeadline + " " + batchWaitTime);
            }

            batchWaitTime = batchWaitTime / tempArray.size();*/

            

            // Use statistics to predict which algorithm needs to be used

            
            //System.out.println(this.property);

            switch (this.property) {
                case "deadlines":
                    tempArray = shortestDeadlineFirst(tempArray);
                    //System.out.println("deadlines");
                    break;
                case "combined":
                    tempArray = shortestJobFirst(tempArray);
                    //System.out.println("combined");
                    break;
                case "avg wait":
                    tempArray = averageWait(tempArray);
                    //System.out.println("combined");
                    break;
                case "max wait":
                    tempArray = longestWaitTimeFirst(tempArray);
                    //System.out.println("max wait");
                    break;
                
                default:
                    break;
            }

            //this.avgWaitTime = (int) ((this.avgWaitTime + batchWaitTime) / 2.0);

            this.sortedList = tempArray;
        } catch (Exception e) {
            System.out.println("SCHEDULING EXCEPTION: " + e);
        }
    }

    /*public ArrayList<Job> shortestDeadlineFirst(ArrayList<Job> Jobs) {
        Comparator<Job> sorter = new Comparator<Job>() {
            public int compare(Job left, Job right) {
                return Math.toIntExact(
                        (left.getDeadline() - left.getTimeCreated()) - (right.getDeadline()) - right.getTimeCreated());
            }
        };
        Jobs.sort(sorter);
        return Jobs;
    }*/
    public ArrayList<Job> shortestDeadlineFirst(ArrayList<Job> Jobs) {
        Comparator<Job> sorter = new Comparator<Job>() {
            public int compare(Job left, Job right) {
                long deadlineDiff = left.getDeadline() - right.getDeadline();
                long timeCreatedDiff = left.getTimeCreated() - right.getTimeCreated();
                return Long.compare(deadlineDiff - timeCreatedDiff, 0L);
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
    public ArrayList<Job> averageWait(ArrayList<Job> Jobs) {
        Comparator<Job> sorter = new Comparator<Job>() {
            
            public int compare(Job left, Job right) {
                
                return (int) left.getLength() - (int) right.getLength();  
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
        new Thread(() -> {
            while (this.numJobs > 0) {
                schedulingAlgorithm(this.inQueue);
            }
        }).start();
        while (numJobs > 0) {
            try {
                Job job;
                boolean takeSorted = false;

                if (!takeSorted) {
                    job = inQueue.peek();
                } else {
                    job = sortedList.getFirst();
                }

                if (job != null) {
                    if (outQueue.offer(job)) {
                        if (!takeSorted) {
                            inQueue.poll();
                        } else {
                            System.out.println("took sorted");
                            sortedList.removeFirst();
                        }
                        numJobs--;
                    }
                }
            } catch (Exception e) {
                System.out.println("RUN EXECEPTION: " + e);
            }
        }
    }
}
