package com.technovision.civilization.data.objects;

import com.technovision.civilization.data.enums.Technology;

/**
 * POJO object that stores data on a civilization's research progress.
 *
 * @author TechnoVision
 */
public class Research {

    private String technology;
    private double progress;
    private double cost;

    public Research() { }

    public Research(Technology technology) {
        this.technology = technology.toString();
        this.progress = 0;
        this.cost = technology.getScienceCost();
    }

    public Research(String technology, double progress, double cost) {
        this.technology = technology;
        this.progress = progress;
        this.cost = cost;
    }

    public double tick(double science) {
        progress += science;
        return progress;
    }

    public boolean isComplete() {
        return progress >= cost;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
