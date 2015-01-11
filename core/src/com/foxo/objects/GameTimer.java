package com.foxo.objects;

import com.badlogic.gdx.Gdx;


public class GameTimer {

    private int hours = 0, minutes = 0, seconds = 0;
    private float milliseconds = 0, totalTime = 0;
    private String ts;

    public GameTimer(float time) {
        this.totalTime = time;
        hours =  (int) (time / 3600);
        minutes = (int) ((time - hours * 3600 ) / 60);
        seconds = (int) (time - hours * 3600 - minutes * 60);
        milliseconds = time - (int) time;
    }

    public GameTimer() {}

    public void update() {
        milliseconds += Gdx.graphics.getDeltaTime();
        totalTime += Gdx.graphics.getDeltaTime();

        if(milliseconds >= 1) {
            milliseconds = milliseconds - 1f;
            seconds++;
        }
        if(seconds == 60) {
            seconds = 0;
            minutes++;
        }
        if(minutes == 60) {
            minutes = 0;
            hours++;
        }

        String sec = "";
        if(seconds < 10)
            sec = "0" + seconds;
        else
            sec = "" + seconds;

        String min = "";
        if(minutes < 10)
            min = "0" + minutes + ":";
        else
            min = minutes + ":";

        ts = "";
        if(hours == 0)
            ts = min + sec;
        else
            ts = hours + ":" + min + sec;
    }

    public static String prettyTime(float seconds) {
        int hours2 =  (int) (seconds / 3600);
        int min2 = (int) ((seconds - hours2 * 3600 ) / 60);
        int sec2 = (int) (seconds - hours2 * 3600 - min2 * 60);

        String sec = "";
        if(sec2 < 10)
            sec = "0" + sec2;
        else
            sec = "" + sec2;

        String min = "";
        if(min2 < 10)
            min = "0" + min2 + ":";
        else
            min = min2 + ":";

        String ts = "";
        if(hours2 == 0)
            ts = min + sec;
        else
            ts = hours2 + ":" + min + sec;

        return ts;
    }

    public String getTime() {
        return ts;
    }

    public float getTimeFloat() {
        return totalTime;
    }
}