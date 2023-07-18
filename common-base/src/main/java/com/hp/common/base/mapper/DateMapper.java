package com.hp.common.base.mapper;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public class DateMapper {
    public Long asLong(Instant date) {
        return Optional.ofNullable(date).map(Instant::toEpochMilli).orElse(null);
    }

    public Instant asInstant(Long date) {
        return Optional.ofNullable(date).map(Instant::ofEpochMilli).orElse(null);
    }

    public String instantToString(Instant instant) {
        return Optional.ofNullable(instant)
                .map(i -> LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .orElse(null);
    }

    public Instant stringToInstant(String datetime) {
        return Optional.ofNullable(datetime)
                .map(i -> LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.of("Asia/Shanghai")).toInstant())
                .orElse(null);
    }

    public String asString(LocalDate date) {
        return Optional.ofNullable(date).map(d -> d.format(DateTimeFormatter.ISO_LOCAL_DATE)).orElse(null);
    }

    public LocalDate asLocalDate(String date) {
        return Optional.ofNullable(date).map(LocalDate::parse).orElse(null);
    }

    public Long localDateAsLong(LocalDate date) {
        return Optional.ofNullable(date)
                .map(d -> d.atTime(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .orElse(null);
    }

    public LocalDate longAsLocalDate(Long date) {
        return Optional.ofNullable(date).map(d -> LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault()).toLocalDate())
                .orElse(null);
    }

    public String asString(LocalDateTime date) {
        return Optional.ofNullable(date).map(d -> d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .orElse(null);
    }

    public LocalDateTime asLocalDateTime(String date) {
        return Optional.ofNullable(date).map(d -> LocalDateTime.parse(d, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .orElse(null);
    }

    public Long localDateTimeAsLong(LocalDateTime date) {
        return Optional.ofNullable(date).map(d -> d.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .orElse(null);
    }

    public LocalDateTime longAsLocalDateTime(Long date) {
        return Optional.ofNullable(date).map(d -> LocalDateTime.ofInstant(Instant.ofEpochMilli(d), ZoneId.systemDefault()))
                .orElse(null);
    }

    public Date stringToDate(String string) {
        return Optional.ofNullable(string).map(s -> Date.from(stringToInstant(s))).orElse(null);
    }

    public String dateToString(Date date) {
        return Optional.ofNullable(date).map(d -> asString(longAsLocalDateTime(date.getTime()))).orElse(null);
    }

    public Date longToDate(Long l) {
        return Optional.ofNullable(l).map(i -> Date.from(Instant.ofEpochMilli(i))).orElse(null);
    }

    public Long dateToLong(Date date) {
        return Optional.ofNullable(date).map(Date::getTime).orElse(null);
    }
}
