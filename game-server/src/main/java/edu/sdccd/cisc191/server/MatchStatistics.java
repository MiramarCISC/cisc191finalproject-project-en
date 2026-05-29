package edu.sdccd.cisc191.server;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tracks server-wide match statistics shared by many gRPC request threads.
 */
public class MatchStatistics {

    // consider adding more statistics methods (average score, total losses) to increase function
    private final AtomicInteger joinedMatchCount = new AtomicInteger(0);
    private final AtomicInteger completedMatchCount = new AtomicInteger(0);


    public void recordJoin() {
        joinedMatchCount.addAndGet(1);
    }


    public void recordCompletion() {
        completedMatchCount.addAndGet(1);
    }

    public int getJoinedMatchCount() {
        return joinedMatchCount.get();
    }

    public int getCompletedMatchCount() {
        return completedMatchCount.get();
    }


    public String buildStatusLine() {
        return String.format("Server stats: %s joined, %s completed",
                joinedMatchCount.get(), completedMatchCount.get());
    }
}
