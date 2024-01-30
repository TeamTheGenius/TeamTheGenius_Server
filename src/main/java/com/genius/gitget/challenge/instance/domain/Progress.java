package com.genius.gitget.challenge.instance.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Progress {
    ALL,
    PREACTIVITY,
    ACTIVITY,
    DONE;

//    @JsonCreator
//    public static Progress from(String s) {
//        return Progress.valueOf(s.toUpperCase());
//    }
}
