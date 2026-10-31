package github.jodevnull.waterflow.utils;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RegistryList<R, T extends R>
{
    private final DeferredRegister<R> owner;
    private final List<RegistryObject<T>> elements = new ArrayList<>();

    public RegistryList(DeferredRegister<R> owner) {
        this.owner = owner;
    }

    public List<RegistryObject<T>> list() {
        return this.elements;
    }

    public RegistryObject<T> get(int i) {
        return this.elements.get(i);
    }

    public int size() {
        return this.elements.size();
    }

    public RegistryObject<T> add(String name, Supplier<T> element) {
        final RegistryObject<T> object = this.owner.register(name, element);
        this.elements.add(object);
        return object;
    }
}
