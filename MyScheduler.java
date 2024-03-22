import java.util.*;
import java.util.concurrent.*;

/**
 * Implements a scheudler
 * 
 * @author: Joshua Hammond, Austin Jackman
 */


public class MyScheduler<T> {
    int numJobs;
    String property;

    int avgWaitTime;
    int maxWait;
    int combined;
    int deadlines;

    public MyScheduler(int numJobs, String property) {
        this.numJobs = numJobs;
        this.property = property;

    }

}

public static void main(String[] args) {

}
