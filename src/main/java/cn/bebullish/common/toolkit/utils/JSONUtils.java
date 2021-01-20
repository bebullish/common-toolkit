package cn.bebullish.common.toolkit.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import static com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;

/**
 * <h3>Jackson serialization wrapper class</h3>
 *
 * <p>Supports serialization and deserialization of Date, DateTime, LocalDate, LocalDateTime, encapsulates some methods</p>
 *
 * @author Marlon
 * @since 1.0.0
 */
@Slf4j
public class JSONUtils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";

    public static final MapTypeReference MAP_TYPE = new MapTypeReference();
    public static final ListMapTypeReference LIST_MAP_TYPE = new ListMapTypeReference();

    static {
        // 对象的所有字段全部列入
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 取消默认转换 timestamps 形式
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 配置日期转换
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // Date
        javaTimeModule.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat(DATE_TIME_PATTERN)));
        javaTimeModule.addDeserializer(Date.class, new DateDeserializer(new DateDeserializer(), new SimpleDateFormat(DATE_TIME_PATTERN), DATE_TIME_PATTERN));
        // LocalDateTime
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        // LocalDate
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        // LocalTime
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_PATTERN)));
        OBJECT_MAPPER.registerModule(javaTimeModule);
        // 忽略空 Bean 转 json 的错误
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 忽略在 json 字符串中存在，但是在 java 对象中不存在对应属性的情况。防止错误
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @NonNull
    public static <T> String write2strNonnull(@NonNull T obj) {
        Assert.notNull(obj, "origin object must not be null");
        try {
            return obj instanceof String ? (String) obj : OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw exceptionHandleNonnull(e);
        }
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T> T read2clsNonnull(@NonNull Object obj, @NonNull Class<T> cls) {
        Assert.notNull(obj, "Origin object must not be null");
        Assert.notNull(cls, "Target class must not be null");

        try {
            String str = obj instanceof String ? (String) obj : OBJECT_MAPPER.writeValueAsString(obj);
            return cls.equals(String.class) ? (T) str : OBJECT_MAPPER.readValue(str, cls);
        } catch (JsonProcessingException e) {
            throw exceptionHandleNonnull(e);
        }
    }

    @NonNull
    public static Map<String, Object> read2mapNonnull(@NonNull Object obj) {
        return read2typeReferenceNonnull(obj, MAP_TYPE);
    }

    @NonNull
    public static List<Map<String, Object>> read2listMapNonnull(@NonNull List<Object> obj) {
        return read2typeReferenceNonnull(obj, LIST_MAP_TYPE);
    }

    @NonNull
    public static <T> T read2typeReferenceNonnull(@NonNull Object obj, @NonNull TypeReference<T> typeReference) {
        Assert.notNull(obj, "Origin object must not be null");
        Assert.notNull(typeReference, "Target class must not be null");

        try {
            String str = obj instanceof String ? (String) obj : OBJECT_MAPPER.writeValueAsString(obj);
            return OBJECT_MAPPER.readValue(str, typeReference);
        } catch (JsonProcessingException e) {
            throw exceptionHandleNonnull(e);
        }
    }

    @Nullable
    public static <T> String write2strNullable(@Nullable T obj) {
        if (Objects.isNull(obj)) return null;

        try {
            return obj instanceof String ? (String) obj : OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return exceptionHandleNullable(e);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T read2clsNullable(@Nullable Object obj, @Nullable Class<T> cls) {
        if (Objects.isNull(obj) || Objects.isNull(cls)) return null;

        try {
            String str = obj instanceof String ? (String) obj : OBJECT_MAPPER.writeValueAsString(obj);
            return cls.equals(String.class) ? (T) str : OBJECT_MAPPER.readValue(str, cls);
        } catch (JsonProcessingException e) {
            return exceptionHandleNullable(e);
        }
    }

    @Nullable
    public static Map<String, Object> read2mapNullable(@Nullable Object obj) {
        return read2typeReferenceNullable(obj, MAP_TYPE);
    }

    @Nullable
    public static List<Map<String, Object>> read2listMapNullable(@Nullable List<Object> obj) {
        return read2typeReferenceNullable(obj, LIST_MAP_TYPE);
    }

    @Nullable
    public static <T> T read2typeReferenceNullable(@Nullable Object obj, @Nullable TypeReference<T> typeReference) {
        if (Objects.isNull(obj) || Objects.isNull(typeReference)) return null;

        try {
            String str = obj instanceof String ? (String) obj : OBJECT_MAPPER.writeValueAsString(obj);
            return OBJECT_MAPPER.readValue(str, typeReference);
        } catch (JsonProcessingException e) {
            return exceptionHandleNullable(e);
        }
    }

    public static <T> Optional<String> write2str(T obj) {
        if (Objects.isNull(obj)) return Optional.empty();

        try {
            return Optional.ofNullable(obj instanceof String ? (String) obj : OBJECT_MAPPER.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            return exceptionHandle(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> read2cls(Object obj, Class<T> cls) {
        if (Objects.isNull(obj) || Objects.isNull(cls)) return Optional.empty();

        try {
            String str = obj instanceof String ? (String) obj : OBJECT_MAPPER.writeValueAsString(obj);
            return Optional.ofNullable(cls.equals(String.class) ? (T) str : OBJECT_MAPPER.readValue(str, cls));
        } catch (JsonProcessingException e) {
            return exceptionHandle(e);
        }
    }

    public static Optional<Map<String, Object>> read2map(Object obj) {
        return read2typeReference(obj, MAP_TYPE);
    }

    public static Optional<List<Map<String, Object>>> read2listMap(List<Object> obj) {
        return read2typeReference(obj, LIST_MAP_TYPE);
    }

    public static <T> Optional<T> read2typeReference(Object obj, TypeReference<T> typeReference) {
        if (Objects.isNull(obj) || Objects.isNull(typeReference)) return Optional.empty();

        try {
            String str = obj instanceof String ? (String) obj : OBJECT_MAPPER.writeValueAsString(obj);
            return Optional.ofNullable(OBJECT_MAPPER.readValue(str, typeReference));
        } catch (JsonProcessingException e) {
            return exceptionHandle(e);
        }
    }

    protected static <T> Optional<T> exceptionHandle(Exception e) {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        log(method, e.getMessage());
        return Optional.empty();
    }

    protected static <T> T exceptionHandleNullable(Exception e) {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        log(method, e.getMessage());
        return null;
    }

    protected static IllegalArgumentException exceptionHandleNonnull(Exception e) {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        log(method, e.getMessage());
        return new IllegalArgumentException(e.getMessage());
    }

    private static void log(String methodName, String errorMessage) {
        Logs.labels("JsonProcessingException").error("{} method failed, message : {}", methodName, errorMessage);
    }

    private static class MapTypeReference extends TypeReference<Map<String, Object>> {

    }

    protected static class ListMapTypeReference extends TypeReference<List<Map<String, Object>>> {

    }

}
