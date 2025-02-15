package cc.cassian.pyrite.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class TorchLever extends LeverBlock {
    private final ParticleEffect particle;

    public TorchLever(Settings settings, ParticleEffect particle) {
        super(settings);
        this.particle = particle;
    }
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {

        double xPlus;
        double yPlus;
        double zPlus;
        switch (state.get(FACE)) {
            case FLOOR:
                if (state.get(POWERED))  {
                    zPlus = switch (state.get(FACING)) {
                        case WEST -> {
                            xPlus = (double) pos.getX() + 0.1;
                            yield (double) pos.getZ() + 0.5;
                        }
                        case EAST -> {
                            xPlus = (double) pos.getX() + 0.9;
                            yield (double) pos.getZ() + 0.5;
                        }
                        case NORTH -> {
                            xPlus = (double) pos.getX() + 0.4;
                            yield (double) pos.getZ() + 0.05;
                        }
                        default -> {
                            xPlus = (double) pos.getX() + 0.5;
                            yield (double) pos.getZ() + .9;
                        }
                    };
                    yPlus = (double)pos.getY() + 0.55;

                }
            else {
                xPlus = (double)pos.getX() + 0.5;
                yPlus = (double)pos.getY() + 0.65;
                zPlus = (double)pos.getZ() + 0.5;
                }
                break;
            case WALL:
                if (state.get(POWERED)) {
                    zPlus = switch (state.get(FACING)) {
                        case EAST -> {
                            xPlus = (double) pos.getX() + 0.3;
                            yield (double) pos.getZ() + 0.5;
                        }
                        case WEST -> {
                            xPlus = (double) pos.getX() + 0.7;
                            yield (double) pos.getZ() + 0.5;
                        }
                        case SOUTH -> {
                            xPlus = (double) pos.getX() + 0.5;
                            yield (double) pos.getZ() + 0.35;
                        }
                        default -> {
                            xPlus = (double) pos.getX() + 0.5;
                            yield (double) pos.getZ() + .6;
                        }
                    };
                    yPlus = (double)pos.getY() + 0.1;

                }
                else {
                    switch (state.get(FACING)) {
                        case EAST:
                            xPlus = (double)pos.getX() + 0.3;
                            yPlus = (double)pos.getY() + 0.9;
                            zPlus = (double)pos.getZ() + 0.5;
                            break;
                        case WEST:
                            xPlus = (double)pos.getX() + 0.7;
                            yPlus = (double)pos.getY() + 0.9;
                            zPlus = (double)pos.getZ() + 0.5;
                            break;
                        case SOUTH:
                            xPlus = (double)pos.getX() + 0.5;
                            yPlus = (double)pos.getY() + 0.9;
                            zPlus = (double)pos.getZ() + 0.25;
                            break;
                        default:
                            xPlus = (double)pos.getX() + 0.5;
                            yPlus = (double)pos.getY() + 0.9;
                            zPlus = (double)pos.getZ() +.8;
                            break;
                    }
                }
                break;
            //Ceiling
            default:
                if (state.get(POWERED))  {
                    zPlus = switch (state.get(FACING)) {
                        case WEST -> {
                            xPlus = (double) pos.getX() + 0.1;
                            yield (double) pos.getZ() + 0.5;
                        }
                        case EAST -> {
                            xPlus = (double) pos.getX() + 0.9;
                            yield (double) pos.getZ() + 0.5;
                        }
                        case NORTH -> {
                            xPlus = (double) pos.getX() + 0.4;
                            yield (double) pos.getZ() + 0.05;
                        }
                        default -> {
                            xPlus = (double) pos.getX() + 0.5;
                            yield (double) pos.getZ() + .9;
                        }
                    };
                    yPlus = (double)pos.getY() + 0.55;

                }
                else {
                    xPlus = (double)pos.getX() + 0.5;
                    yPlus = (double)pos.getY() + 0.4;
                    zPlus = (double)pos.getZ() + 0.5;
                }
                break;
        }



        world.addParticle(ParticleTypes.SMOKE, xPlus, yPlus, zPlus, 0.0, 0.0, 0.0);
        world.addParticle(particle, xPlus, yPlus, zPlus, 0.0, 0.0, 0.0);
    }
}
