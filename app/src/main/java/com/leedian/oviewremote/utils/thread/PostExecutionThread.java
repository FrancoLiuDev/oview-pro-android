package com.leedian.oviewremote.utils.thread;
import rx.Scheduler;

/**
 * PostExecutionThread
 *
 * @author Franco
 */
public interface PostExecutionThread {
    Scheduler getScheduler();
}
