package com.taco.bafflebeasts.flight;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlightPowerProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<FlightPower> AMARO_FLIGHT_POWER = CapabilityManager.get(new CapabilityToken<FlightPower>() { });

    private FlightPower flightPower = null;
    private final LazyOptional<FlightPower> optional = LazyOptional.of(this::createFlightPower);

    private FlightPower createFlightPower() {
        if(this.flightPower == null) {
            this.flightPower = new FlightPower();
        }

        return this.flightPower;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == AMARO_FLIGHT_POWER) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createFlightPower().saveNBTData(nbt);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createFlightPower().loadNBTData(nbt);
    }
}
