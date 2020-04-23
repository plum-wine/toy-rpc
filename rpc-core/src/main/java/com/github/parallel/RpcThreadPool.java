package com.github.parallel;

import com.github.core.RpcSystemConfig;
import com.github.jmx.ThreadPoolMonitorProvider;
import com.github.jmx.ThreadPoolStatus;
import com.github.parallel.policy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;


public class RpcThreadPool {

    private static final Timer TIMER = new Timer("ThreadPoolMonitor", true);

    private static long monitorDelay = 100L;

    private static long monitorPeriod = 300L;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static RejectedExecutionHandler createPolicy() {
        RejectedPolicyType rejectedPolicyType = RejectedPolicyType.fromString(System.getProperty(RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_REJECTED_POLICY_ATTR, "AbortPolicy"));

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
        BlockingQueueType queueType = BlockingQueueType.fromString(System.getProperty(RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NAME_ATTR, "LinkedBlockingQueue"));

        switch (queueType) {
            case LINKED_BLOCKING_QUEUE:
                return new LinkedBlockingQueue<>();
            case ARRAY_BLOCKING_QUEUE:
                return new ArrayBlockingQueue<>(RpcSystemConfig.SYSTEM_PROPERTY_PARALLEL * queues);
            case SYNCHRONOUS_QUEUE:
                return new SynchronousQueue<>();
            default:
                throw new RuntimeException();
        }
    }

    public static Executor getExecutor(int threads, int queues) {
        LOGGER.info("ThreadPool Core [threads:{},queues:{}]", threads, queues);
        String name = "RpcThreadPool";
        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
                createBlockingQueue(queues),
                new NamedThreadFactory(name, true), createPolicy());
    }

    public static Executor getExecutorWithJmx(int threads, int queues) {
        final ThreadPoolExecutor executor = (ThreadPoolExecutor) getExecutor(threads, queues);
        TIMER.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ThreadPoolStatus status = new ThreadPoolStatus();
                status.setPoolSize(executor.getPoolSize());
                status.setActiveCount(executor.getActiveCount());
                status.setCorePoolSize(executor.getCorePoolSize());
                status.setMaximumPoolSize(executor.getMaximumPoolSize());
                status.setLargestPoolSize(executor.getLargestPoolSize());
                status.setTaskCount(executor.getTaskCount());
                status.setCompletedTaskCount(executor.getCompletedTaskCount());

                try {
                    ThreadPoolMonitorProvider.monitor(status);
                } catch (IOException | MalformedObjectNameException | ReflectionException | MBeanException | InstanceNotFoundException e) {
                    LOGGER.error("getExecutorWithJmx error", e);
                }
            }
        }, monitorDelay, monitorDelay);
        return executor;
    }
}

