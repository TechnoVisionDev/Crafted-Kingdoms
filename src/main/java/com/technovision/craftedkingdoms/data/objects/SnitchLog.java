package com.technovision.craftedkingdoms.data.objects;

import java.util.Date;

/**
 * POJO object that stores data for a snitch log.
 *
 * @author TechnoVision
 */
public class SnitchLog {

    private String message;
    private Date date;

    public SnitchLog() { }

    public SnitchLog(String message) {
        this.message = message;
        this.date = new Date();
    }

    public SnitchLog(String message, Date date) {
        this.message = message;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
