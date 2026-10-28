package github.jodevnull.waterflow.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public final class WaterEffects
{
    public static void pushWater(ServerLevel world, BlockPos pos) {
        world.sendParticles(ParticleTypes.FALLING_WATER,
            pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
            4 + world.random.nextInt(4), world.random.nextFloat(), 0.5D, world.random.nextFloat(), 0.05D
        );

        world.playSound(null, pos, SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundSource.PLAYERS,
            0.8f + world.random.nextFloat() * 0.2f,
            0.9f + world.random.nextFloat() * 0.2f
        );
    }

    public static void fizz(ServerLevel world, BlockPos pos) {
        final int amount = 3 + world.random.nextInt(2);

        world.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
            amount, 0D, 0.5D, 0D, 0.05D
        );

        world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,
            0.8f + world.random.nextFloat() * 0.2f,
            0.9f + world.random.nextFloat() * 0.2f
        );
    }
}
