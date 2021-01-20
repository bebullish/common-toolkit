package cn.bebullish.common.toolkit.utils;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

import static org.apache.logging.log4j.Level.DEBUG;
import static org.apache.logging.log4j.Level.ERROR;
import static org.apache.logging.log4j.Level.INFO;
import static org.apache.logging.log4j.Level.TRACE;
import static org.apache.logging.log4j.Level.WARN;

/**
 * <h3>Log wrapper class</h3>
 *
 * <p>Support label and print rate control by number and time</p>
 *
 * @author Marlon
 * @since 1.0.0
 */
@Slf4j
public class Logs {

    protected static final String[] EMPTY_STRING_ARRAY = new String[]{};
    protected static final Object[] EMPTY_OBJECT_ARRAY = new Object[]{};

    protected static final ConcurrentHashMap<String, Long> RATE_COUNT_MAP = new ConcurrentHashMap<>();
    protected static final ConcurrentHashMap<String, Long> RATE_TIME_MAP = new ConcurrentHashMap<>();

    protected String format;
    protected String[] labels;
    protected Object[] arguments;
    protected Level level;
    protected int rateCount;
    protected Duration rateTime;

    public Logs(String format, String[] labels, Object[] arguments, Level level, int rateCount, Duration rateTime) {
        this.format = format;
        this.labels = labels;
        this.arguments = arguments;
        this.level = level;
        this.rateCount = rateCount;
        this.rateTime = rateTime;
    }

    public static void trace(String format, Object... arguments) {
        Logs.builder().trace(format, arguments);
    }

    public static void debug(String format, Object... arguments) {
        Logs.builder().debug(format, arguments);
    }

    public static void info(String format, Object... arguments) {
        Logs.builder().info(format, arguments);
    }

    public static void warn(String format, Object... arguments) {
        Logs.builder().warn(format, arguments);
    }

    public static void error(String format, Object... arguments) {
        Logs.builder().error(format, arguments);
    }

    public static LogsBuilder rateCount(int count) {
        return Logs.builder().rateCount(count);
    }

    public static LogsBuilder rateTime(Duration rateTime) {
        return Logs.builder().rateTime(rateTime);
    }

    public static LogsBuilder labels(String... labels) {
        return Logs.builder().labels(labels);
    }

    public static Logs.LogsBuilder builder() {
        return new Logs.LogsBuilder();
    }

    public static class LogsBuilder {
        private String format;
        private String[] labels;
        private Object[] arguments;
        private Level level;
        private int rateCount;
        private Duration rateTime;

        LogsBuilder() {
        }

        public Logs.LogsBuilder format(final String format) {
            this.format = format;
            return this;
        }

        public Logs.LogsBuilder labels(final String[] labels) {
            this.labels = labels;
            return this;
        }

        public Logs.LogsBuilder arguments(final Object[] arguments) {
            this.arguments = arguments;
            return this;
        }

        public Logs.LogsBuilder level(final Level level) {
            this.level = level;
            return this;
        }

        public Logs.LogsBuilder rateCount(final int rateCount) {
            this.rateCount = rateCount;
            return this;
        }

        public LogsBuilder rateTime(Duration rateTime) {
            this.rateTime = rateTime;
            return this;
        }

        public void trace(String format, Object... arguments) {
            printLog(format, TRACE, arguments);
        }

        public void debug(String format, Object... arguments) {
            printLog(format, DEBUG, arguments);
        }

        public void info(String format, Object... arguments) {
            printLog(format, INFO, arguments);
        }

        public void warn(String format, Object... arguments) {
            printLog(format, WARN, arguments);
        }

        public void error(String format, Object... arguments) {
            printLog(format, ERROR, arguments);
        }

        private void printLog(String format, Level level, Object... arguments) {
            if (rateCount != 0) {
                String mapKey = getRateKey(format, level, arguments);
                Long currentCount = RATE_COUNT_MAP.put(mapKey, RATE_COUNT_MAP.getOrDefault(mapKey, 0L) + 1);
                if (currentCount != null && currentCount % rateCount != 0) return;
            }
            if (rateTime != null) {
                String mapKey = getRateKey(format, level, arguments);
                long currentTimeMillis = System.currentTimeMillis();
                long lastTime = RATE_TIME_MAP.getOrDefault(mapKey, 0L);
                if (lastTime != 0 && currentTimeMillis < lastTime + rateTime.toMillis()) return;
                RATE_TIME_MAP.put(mapKey, currentTimeMillis);
            }
            print(Logs.builder()
                    .rateCount(rateCount)
                    .rateTime(rateTime)
                    .labels(labels)
                    .level(level)
                    .format(format == null ? Strings.EMPTY : format)
                    .arguments(arguments == null ? EMPTY_OBJECT_ARRAY : arguments)
                    .build());
        }

        private String getRateKey(String format, Level level, Object... arguments) {
            List<String> argumentsList = Arrays.stream(arguments).map(Object::toString).collect(Collectors.toList());
            argumentsList.add(format);
            argumentsList.add(level.name());
            return Base64.getEncoder().encodeToString(String.join("", argumentsList).getBytes(StandardCharsets.UTF_8));
        }

        public Logs build() {
            return new Logs(this.format, this.labels, this.arguments, this.level, this.rateCount, this.rateTime);
        }

        protected static void print(Logs logs) {
            if (!StringUtils.hasLength(logs.format)) return;
            String labels = resolve(logs.labels);
            String rateCountLabel = logs.rateCount != 0 ? String.format("[rateCount-%d] ", logs.rateCount) : Strings.EMPTY;
            String rateTimeLabel = logs.rateTime != null ? String.format("[rateTime-%dms] ", logs.rateTime.toMillis()) : Strings.EMPTY;
            String format = rateCountLabel + rateTimeLabel + labels + logs.format;
            if (DEBUG.equals(logs.level)) log.debug(format, logs.arguments);
            else if (INFO.equals(logs.level)) log.info(format, logs.arguments);
            else if (WARN.equals(logs.level)) log.warn(format, logs.arguments);
            else if (ERROR.equals(logs.level)) log.error(format, logs.arguments);
            else if (TRACE.equals(logs.level)) log.trace(format, logs.arguments);
            else throw new IllegalStateException("Unexpected value: " + logs.level);
        }

        private static String resolve(String[] labels) {
            labels = labels == null ? EMPTY_STRING_ARRAY : labels;
            if (labels.length == 0) return Strings.EMPTY;
            return Stream.of(labels).collect(Collectors.joining("] [", "[", "] "));
        }
    }

}
