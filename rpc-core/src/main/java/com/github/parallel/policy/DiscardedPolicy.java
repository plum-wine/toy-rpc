package com.github.parallel.policy;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscardedPolicy implements RejectedExecutionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String threadName;

    public DiscardedPolicy() {
        this(null);
    }

    public DiscardedPolicy(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        if (threadName != null) {
            LOGGER.error("RPC Thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
        }

        if (!executor.isShutdown()) {
            BlockingQueue<Runnable> queue = executor.getQueue();
            int discardSize = queue.size() >> 1;
            for (int i = 0; i < discardSize; i++) {
                queue.poll();
            }
            queue.offer(runnable);
        }
    }
}
