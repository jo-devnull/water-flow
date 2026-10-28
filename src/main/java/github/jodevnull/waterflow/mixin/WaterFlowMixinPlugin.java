package github.jodevnull.waterflow.mixin;

import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class WaterFlowMixinPlugin implements IMixinConfigPlugin
{
    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("github.jodevnull.waterflow.mixin.compat.fluidlogged")) {
            return isModLoaded("fluidlogged");
        }

        else if (mixinClassName.startsWith("github.jodevnull.waterflow.mixin.compat.embeddium")) {
            return isModLoaded("rubidium");
        }

        else if (mixinClassName.startsWith("github.jodevnull.waterflow.mixin.compat.create")) {
            return isModLoaded("create");
        }

        else if (mixinClassName.equals("github.jodevnull.waterflow.mixin.block.IForgeBlockMixin")) {
            return !isModLoaded("fluidlogged");
        }

        else if (mixinClassName.equals("github.jodevnull.waterflow.mixin.item.BlockItemMixin")) {
            return !isModLoaded("fluidlogged");
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    private static boolean isModLoaded(String name) {
        return FMLLoader.getLoadingModList().getModFileById(name) != null;
    }
}
