package com.daom;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class LocalDateTimeTest {
    @Test
    void compare() {
        LocalDateTime now = LocalDateTime.now(); // 현재 시간

        LocalDateTime visitDay = LocalDateTime.parse("2022-01-20T10:11:50.000"); // 현재 시간
        LocalDateTime day3After = visitDay.plusDays(3); // 작성 시간에서 3일 후
        System.out.println(day3After);
        // 방문일 ~ 방문일 + 3

        if( now.isAfter(visitDay) && now.isBefore(day3After)){
            System.out.println("리뷰 작성 가능");
        }else{
            System.out.println("리뷰 작성 불가능");
        }
    }
}
