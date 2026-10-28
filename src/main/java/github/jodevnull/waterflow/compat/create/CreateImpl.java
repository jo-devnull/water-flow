package github.jodevnull.waterflow.compat.create;

import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import github.jodevnull.waterflow.ModCreativeTabs;
import github.jodevnull.waterflow.WaterFlow;
import github.jodevnull.waterflow.compat.create.block.CopyCatChannelBlock;
import github.jodevnull.waterflow.compat.create.block.CopyCatChannelModel;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

// TODO: implement this
public final class CreateImpl implements ICreate
{
    public static final Registrate REGISTRATE = Registrate.create(WaterFlow.MODID);

    static { REGISTRATE.defaultCreativeTab(ModCreativeTabs.MAIN_TAB); }

    @Override
    public boolean isLoaded() {
        return true;
    }

    public static final BlockEntry<CopyCatChannelBlock> COPYCAT_CHANNEL = REGISTRATE
        .block("copycat_channel", CopyCatChannelBlock::new)
        .properties(BlockBehaviour.Properties::forceSolidOn)
        .transform(BuilderTransformers.copycat())
        .onRegister(CreateRegistrate.blockModel(() -> CopyCatChannelModel::new))
        .item()
        .recipe((c, p) -> p.stonecutting(DataIngredient.tag(CommonMetal.ZINC.ingots),
            RecipeCategory.BUILDING_BLOCKS, c, 4))
        .transform(customItemModel("copycat_base", "step"))
        .register();
}
