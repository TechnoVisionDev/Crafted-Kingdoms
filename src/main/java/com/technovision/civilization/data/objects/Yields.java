package com.technovision.civilization.data.objects;

import com.technovision.civilization.data.enums.BiomeYields;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * POJO object that stores chunk yields for a town or civ.
 */
public class Yields {

    private double production;
    private double science;
    private double happiness;
    private double growth;
    private double culture;

    @BsonIgnore
    public Map<String, Integer> biomes;

    public Yields() {
        this.production = 0;
        this.science = 0;
        this.happiness = 0;
        this.growth = 0;
        this.culture = 0;
        this.biomes = new HashMap<>();
    }

    public Yields(double production, double science, double happiness, double growth, double culture) {
        this.production = production;
        this.science = science;
        this.happiness = happiness;
        this.growth = growth;
        this.culture = culture;
    }

    public Yields(HashMap<String, Integer> biomes, double production, double science, double happiness, double growth) {
        this.biomes = biomes;
        this.production = production;
        this.science = science;
        this.happiness = happiness;
        this.growth = growth;
        this.culture = 0;
    }

    public void addBiome(String biomeName) {
        if (biomes.containsKey(biomeName)) {
            biomes.put(biomeName, biomes.get(biomeName)+1);
        } else {
            biomes.put(biomeName, 1);
        }
    }

    public Yields add(Yields yield) {
        setProduction(getProduction() + yield.getProduction());
        setScience(getScience() + yield.getScience());
        setHappiness(getHappiness() + yield.getHappiness());
        setGrowth(getGrowth() + yield.getGrowth());
        setCulture(getCulture() + yield.getCulture());
        this.biomes = yield.biomes;
        return this;
    }

    public Yields add(BiomeYields yield) {
        setProduction(getProduction() + yield.getProduction());
        setScience(getScience() + yield.getScience());
        setHappiness(getHappiness() + yield.getHappiness());
        setGrowth(getGrowth() + yield.getGrowth());
        return this;
    }

    /** Getters */

    public double getProduction() {
        return production;
    }

    public double getScience() {
        return science;
    }

    public double getHappiness() {
        return happiness;
    }

    public double getGrowth() {
        return growth;
    }

    public double getCulture() {
        return culture;
    }

    /** Setters */

    public void setProduction(double production) {
        this.production = production;
    }

    public void setScience(double science) {
        this.science = science;
    }

    public void setHappiness(double happiness) {
        this.happiness = happiness;
    }

    public void setGrowth(double growth) {
        this.growth = growth;
    }

    public void setCulture(double culture) {
        this.culture = culture;
    }
}
