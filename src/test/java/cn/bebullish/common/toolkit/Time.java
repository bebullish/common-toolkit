package cn.bebullish.common.toolkit;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import cn.bebullish.common.toolkit.utils.JSONUtils;
import lombok.Data;

@Data
public class Time implements Serializable {

    private Date date = new Date();
    private LocalDate localDate = LocalDate.now();
    private LocalDateTime localDateTime = LocalDateTime.now();
    private LocalTime localTime = LocalTime.now();
    private Integer mInteger = 123;
    private Long mLong = 456L;
    private Double mDouble1 = 789D;
    private Double mDouble2 = 789.0D;
    private Float mFloat1 = 123.4F;
    private Float mFloat2 = 123F;

    @Override
    public String toString() {
        return JSONUtils.write2str(this).orElse("");
    }

}
