package com.miryor.jawn.rest.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by royrim on 11/20/16.
 */

public class HourlyForecast {

    private int hour;
    private String hourPadded;
    private int min;
    private String minPadded;
    private int year;
    private int month;
    private String monthPadded;
    private int day;
    private String dayPadded;
    private int tempF;
    private int tempM;
    private String condition;
    private int feelsLikeF;
    private int feelsLikeM;
    private int humidity;
    private long epoch;

    @JsonProperty
    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    @JsonProperty
    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @JsonProperty
    public String getHourPadded() {
        return hourPadded;
    }

    public void setHourPadded(String hourPadded) {
        this.hourPadded = hourPadded;
    }

    @JsonProperty
    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @JsonProperty
    public String getMinPadded() {
        return minPadded;
    }

    public void setMinPadded(String minPadded) {
        this.minPadded = minPadded;
    }

    @JsonProperty
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @JsonProperty
    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    @JsonProperty
    public String getMonthPadded() {
        return monthPadded;
    }

    public void setMonthPadded(String monthPadded) {
        this.monthPadded = monthPadded;
    }

    @JsonProperty
    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @JsonProperty
    public String getDayPadded() {
        return dayPadded;
    }

    public void setDayPadded(String dayPadded) {
        this.dayPadded = dayPadded;
    }

    @JsonProperty
    public int getTempF() {
        return tempF;
    }

    public void setTempF(int tempF) {
        this.tempF = tempF;
    }

    @JsonProperty
    public int getTempM() {
        return tempM;
    }

    public void setTempM(int tempM) {
        this.tempM = tempM;
    }

    @JsonProperty
    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @JsonProperty
    public int getFeelsLikeF() {
        return feelsLikeF;
    }

    public void setFeelsLikeF(int feelsLikeF) {
        this.feelsLikeF = feelsLikeF;
    }

    @JsonProperty
    public int getFeelsLikeM() {
        return feelsLikeM;
    }

    public void setFeelsLikeM(int feelsLikeM) {
        this.feelsLikeM = feelsLikeM;
    }

    @JsonProperty
    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
    
    public HourlyForecast() {
    }

    public HourlyForecast(HourlyForecastBuilder b) {
        hour = b.hour;
        hourPadded = b.hourPadded;
        min = b.min;
        minPadded = b.minPadded;
        year = b.year;
        month = b.month;
        monthPadded = b.monthPadded;
        day = b.day;
        dayPadded = b.dayPadded;
        tempF = b.tempF;
        tempM = b.tempM;
        condition = b.condition;
        feelsLikeF = b.feelsLikeF;
        feelsLikeM = b.feelsLikeM;
        humidity = b.humidity;
        epoch = b.epoch;
    }

    @Override
    public String toString() {
        return monthPadded + "/" + dayPadded + " " + tempF;
    }

    public static class HourlyForecastBuilder {
        private int hour;
        private String hourPadded;
        private int min;
        private String minPadded;
        private int year;
        private int month;
        private String monthPadded;
        private int day;
        private String dayPadded;
        private int tempF;
        private int tempM;
        private String condition;
        private int feelsLikeF;
        private int feelsLikeM;
        private int humidity;
        private long epoch;

        public long getEpoch() {
            return epoch;
        }

        public void setEpoch(long epoch) {
            this.epoch = epoch;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public void setHourPadded(String hourPadded) {
            this.hourPadded = hourPadded;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public void setMinPadded(String minPadded) {
            this.minPadded = minPadded;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public void setMonthPadded(String monthPadded) {
            this.monthPadded = monthPadded;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public void setDayPadded(String dayPadded) {
            this.dayPadded = dayPadded;
        }

        public void setTempF(int tempF) {
            this.tempF = tempF;
        }

        public void setTempM(int tempM) {
            this.tempM = tempM;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public void setFeelsLikeF(int feelsLikeF) {
            this.feelsLikeF = feelsLikeF;
        }

        public void setFeelsLikeM(int feelsLikeM) {
            this.feelsLikeM = feelsLikeM;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public HourlyForecast build() {
            HourlyForecast f = new HourlyForecast(this);
            return f;
        }
    }
}
