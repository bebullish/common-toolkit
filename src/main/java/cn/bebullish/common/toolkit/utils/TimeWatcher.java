package cn.bebullish.common.toolkit.utils;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.time.Duration;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;


/**
 * <h3>StopWatch wrapper class</h3>
 *
 * <p>Supports different levels of log printing after a task has been executed for a specified
 * amount of time</p>
 *
 * @author Marlon
 * @since 1.0.0
 */
@Slf4j
public class TimeWatcher {

    private static final String TASK_NAME_NOT_EMPTY = "[TimeWatcher] taskName must not be empty";

    private TimeWatcher() {
    }

    public static <T> T debug(Supplier<T> supplier, @NonNull String taskName, @Nullable Duration duration) {
        Assert.hasLength(taskName, TASK_NAME_NOT_EMPTY);
        duration = duration == null ? Duration.ofNanos(0) : duration;
        StopWatch sw = new StopWatch(taskName);
        sw.start();
        T t = supplier.get();
        sw.stop();
        if (sw.getTotalTimeMillis() > duration.toMillis()) {
            log.debug(shortSummary(sw));
        }
        return t;
    }

    public static void debug(Runnable runnable, @NonNull String taskName, @Nullable Duration duration) {
        Assert.hasLength(taskName, TASK_NAME_NOT_EMPTY);
        duration = duration == null ? Duration.ofNanos(0) : duration;
        StopWatch sw = new StopWatch(taskName);
        sw.start();
        runnable.run();
        sw.stop();
        if (sw.getTotalTimeMillis() > duration.toMillis()) {
            log.debug(shortSummary(sw));
        }
    }

    public static <T> T info(Supplier<T> supplier, @NonNull String taskName, @Nullable Duration duration) {
        Assert.hasLength(taskName, TASK_NAME_NOT_EMPTY);
        duration = duration == null ? Duration.ofNanos(0) : duration;
        StopWatch sw = new StopWatch(taskName);
        sw.start();
        T t = supplier.get();
        sw.stop();
        if (sw.getTotalTimeMillis() > duration.toMillis()) {
            log.info(shortSummary(sw));
        }
        return t;
    }

    public static void info(Runnable runnable, @NonNull String taskName, @Nullable Duration duration) {
        Assert.hasLength(taskName, TASK_NAME_NOT_EMPTY);
        duration = duration == null ? Duration.ofNanos(0) : duration;
        StopWatch sw = new StopWatch(taskName);
        sw.start();
        runnable.run();
        sw.stop();
        if (sw.getTotalTimeMillis() > duration.toMillis()) {
            log.info(shortSummary(sw));
        }
    }

    public static <T> T warn(Supplier<T> supplier, @NonNull String taskName, @Nullable Duration duration) {
        Assert.hasLength(taskName, TASK_NAME_NOT_EMPTY);
        duration = duration == null ? Duration.ofNanos(0) : duration;
        StopWatch sw = new StopWatch(taskName);
        sw.start();
        T t = supplier.get();
        sw.stop();
        if (sw.getTotalTimeMillis() > duration.toMillis()) {
            log.warn(shortSummary(sw));
        }
        return t;
    }

    public static void warn(Runnable runnable, @NonNull String taskName, @Nullable Duration duration) {
        Assert.hasLength(taskName, TASK_NAME_NOT_EMPTY);
        duration = duration == null ? Duration.ofNanos(0) : duration;
        StopWatch sw = new StopWatch(taskName);
        sw.start();
        runnable.run();
        sw.stop();
        if (sw.getTotalTimeMillis() > duration.toMillis()) {
            log.warn(shortSummary(sw));
        }
    }

    public static <T> T error(Supplier<T> supplier, @NonNull String taskName, @Nullable Duration duration) {
        Assert.hasLength(taskName, TASK_NAME_NOT_EMPTY);
        duration = duration == null ? Duration.ofNanos(0) : duration;
        StopWatch sw = new StopWatch(taskName);
        sw.start();
        T t = supplier.get();
        sw.stop();
        if (sw.getTotalTimeMillis() > duration.toMillis()) {
            log.error(shortSummary(sw));
        }
        return t;
    }

    public static void error(Runnable runnable, @NonNull String taskName, @Nullable Duration duration) {
        Assert.hasLength(taskName, TASK_NAME_NOT_EMPTY);
        duration = duration == null ? Duration.ofNanos(0) : duration;
        StopWatch sw = new StopWatch(taskName);
        sw.start();
        runnable.run();
        sw.stop();
        if (sw.getTotalTimeMillis() > duration.toMillis()) {
            log.error(shortSummary(sw));
        }
    }

    private static String shortSummary(StopWatch stopWatch) {
        if (stopWatch.getTotalTimeMillis() == 0) {
            return String.format("[TimeWatcher] [%s] running time : %dus", stopWatch.getId(), stopWatch.getTotalTimeNanos() / 1000);
        }
        return String.format("[TimeWatcher] [%s] running time : %dms", stopWatch.getId(), stopWatch.getTotalTimeMillis());
    }

}
