package com.ioc.springcoredemo.common;

import org.springframework.stereotype.Component;

@Component

public class BaseballCoach implements Coach {
    public BaseballCoach(){
        System.out.println(">> Constructeur : " + getClass().getSimpleName());
    }
    @Override
    public String getDailyWorkout() {
        return "Practice fast bowling for 15 minutes";
    }

}

