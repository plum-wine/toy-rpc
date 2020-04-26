package com.github.parallel;

import com.github.core.SystemConfig;
import com.github.parallel.policy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.*;


public class RpcThreadPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static RejectedExecutionHandler createPolicy() {
        RejectedPolicyType rejectedPolicyType = RejectedPolicyType.fromString(System.getProperty(SystemConfig.SYSTEM_PROPERTY_THREADPOOL_REJECTED_POLICY_ATTR, "AbortPolicy"));

        switch (rejectedPolicyType) {
            case BLOCKING_POLICY:
                return new BlockingPolicy();
            case CALLER_RUNS_POLICY:
                return new CallerRunsPolicy();
            case ABORT_POLICY:
                return new AbortPolicy();
            case REJECTED_POLICY:
                return new RejectedPolicy();
            case DISCARDED_POLICY:
                return new DiscardedPolicy();
            default:
                throw new RuntimeException();
        }
    }

    private static BlockingQueue<Runnable> createBlockingQueue(int queues) {
        BlockingQueueType queueType = BlockingQueueType.fromString(System.getProperty(SystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NAME_ATTR, "LinkedBlockingQueue"));

        switch (queueType) {
            case LINKED_BLOCKING_QUEUE:
                return new LinkedBlockingQueue<>();
            case ARRAY_BLOCKING_QUEUE:
                return new ArrayBlockingQueue<>(SystemConfig.SYSTEM_PROPERTY_PARALLEL * queues);
            case SYNCHRONOUS_QUEUE:
                return new SynchronousQueue<>();
            default:
                throw new RuntimeException();
        }
    }

    public static Executor getExecutor(int threads, int queues) {
        LOGGER.info("Toy-ThreadPool Core [threads:{},queues:{}]", threads, queues);
        String name = "Toy-ThreadPool";
        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
                createBlockingQueue(queues),
                new NamedThreadFactory(name, true), createPolicy());
    }

}

