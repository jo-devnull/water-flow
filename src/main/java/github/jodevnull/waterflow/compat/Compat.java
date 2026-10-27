package github.jodevnull.waterflow.compat;

import github.jodevnull.waterflow.compat.fluidlogged.IFluidLogged;

public final class Compat
{
    public static IFluidLogged Fluidlogged = new IFluidLogged() {};

    public static void init() {
        Fluidlogged = IFluidLogged.loadClass(Fluidlogged);
    }
}
