package cn.bebullish.common.toolkit;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import cn.bebullish.common.toolkit.utils.JSONUtils;
import cn.bebullish.common.toolkit.utils.Logs;
import cn.bebullish.common.toolkit.utils.TimeWatcher;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootApplication
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CommonKitTest {

    public static void main(String[] args) {
        SpringApplication.run(CommonKitTest.class, args);
    }

    @Test
    public void test() {
        watchTest();
        logTest();
        jacksonTest();
    }

    @Test
    public void jacksonTest() {
        Time time1 = new Time();
        Time time2 = JSONUtils.read2cls(time1, Time.class).orElseThrow(() -> new AssertionError("反序列化失败"));
        Map<String, Object> timeObj = JSONUtils.read2map(time1).orElseThrow(() -> new AssertionError("反序列化失败"));
        List<Map<String, Object>> timeListMap = JSONUtils.read2listMap(Lists.newArrayList(time1)).orElseThrow(() -> new AssertionError("反序列化失败"));

        log.info("[time1] {}", JSONUtils.write2str(time1).orElse(""));
        log.info("[time2] {}", JSONUtils.write2str(time2).orElse(""));
        log.info("[Map<String,Object>] {}", timeObj);
        log.info("[List<Map<String,Object>>] {}", timeListMap);
        assertEquals(time1.toString(), time2.toString());

        List<Map<String, Object>> timeListMap1 = JSONUtils.read2typeReference(time1, JSONUtils.LIST_MAP_TYPE).orElse(null);
        assertNull(timeListMap1);

        assertThrows(IllegalArgumentException.class, () -> JSONUtils.read2typeReferenceNonnull(time1, JSONUtils.LIST_MAP_TYPE));
    }

    @Test
    public void watchTest() {
        watchWithRunnable();
        watchWithSupplier();
    }

    private void watchWithSupplier() {
        int result = TimeWatcher.info(() -> 1, "watchWithSupplier", Duration.ofMillis(-1));
        assertEquals(result, 1);
    }

    private void watchWithRunnable() {
        TimeWatcher.info(this::task, "task", Duration.ofMillis(1));
        assertThrows(IllegalArgumentException.class, () -> TimeWatcher.info(this::task, null, Duration.ofMillis(1)));
        TimeWatcher.warn(this::task, "task", Duration.ofMillis(1));
        TimeWatcher.debug(this::task, "task", Duration.ofMillis(1));
        TimeWatcher.error(this::task, "task", Duration.ofMillis(1));
    }

    @SneakyThrows
    private void task() {
        Thread.sleep(5);
    }

    @Test
    public void logTest() {
        Logs.labels("我是个标记").warn("这是一条测试日志");
        Logs.info("这是一条测试日志");
        Logs.debug("这是一条测试日志");
        Logs.labels("label").error("这是一条测试日志");
        Logs.warn("这是一条测试日志 {} {}", 1.5, "哈哈");
        Logs.error("这是一条测试日志 {} {}", 1.5, "哈哈");
        Logs.labels("label 1", "label 2").error("这是一条测试日志 {} {}", 1.5, "哈哈");
    }

    @SneakyThrows
    @Test
    public void logRateLimitTest() {
        for (int i = 0; i < 20; i++) {
            Logs.rateCount(5).info("测试 rateCount");
        }
        for (int i = 0; i < 200; i++) {
            Thread.sleep(100);
            Logs.rateTime(Duration.ofMillis(1000)).info("测试 rateTime");
        }
    }
}
