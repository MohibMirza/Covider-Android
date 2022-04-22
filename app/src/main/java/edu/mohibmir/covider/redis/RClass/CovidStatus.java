package edu.mohibmir.covider.redis.RClass;

import java.io.Serializable;
import java.util.Date;

import edu.mohibmir.covider.redis.RClass.Enums.Status;

public class CovidStatus implements Serializable {

    private Status status; // 0: Healthy, 1: Symptomatic, 2: Infected
    private Date date;

    public CovidStatus(Status status, Date date) {
        this.status = status;
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public Date getDate() {
        return date;
    }

}
