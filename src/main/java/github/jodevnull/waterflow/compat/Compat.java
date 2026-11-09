package github.jodevnull.waterflow.compat;

import github.jodevnull.waterflow.compat.fluidlogged.FluidLoggedCompat;

public final class Compat
{
    public static FluidLoggedCompat Fluidlogged = new FluidLoggedCompat() {};

    public static void init() {
        Fluidlogged = ModCompat.tryLoading(Fluidlogged);
    }
}
