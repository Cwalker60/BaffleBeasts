package com.Taco.BaffleBeasts.flight;

import net.minecraft.nbt.CompoundTag;

public class AmaroFlight {
    // Flightpower is the amount of times the amaro can use the jump key to boost their height, or to accelerate with
    // the Amaro glide.

    // flight power will be represented in a GUI with 6 feathers.
    private final int MIN_FLIGHTPOWER = 0;
    private int MAX_FLIGHTPOWER = 6;
    private int flightPower;

    public void setFlightPower(int max) {
        this.MAX_FLIGHTPOWER = max;
        flightPower = max;
    }

    public int getFlightPower() {
        return this.flightPower;
    }

    public int getMaXFlightPower() {
        return this.MAX_FLIGHTPOWER;
    }

    public void addFlightPower(int power) {
        this.flightPower = Math.min(flightPower + power, MAX_FLIGHTPOWER);
    }

    public void subFlightPower(int power) {
        this.flightPower = Math.max(flightPower - power, MIN_FLIGHTPOWER);
    }

    public void copyFrom(AmaroFlight source) {
        this.flightPower = source.flightPower;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("flightpower", flightPower);
    }

    public void loadNBTData(CompoundTag nbt) {
        flightPower = nbt.getInt("flightpower");
    }
}
