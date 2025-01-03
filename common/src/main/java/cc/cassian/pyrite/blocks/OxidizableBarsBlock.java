package cc.cassian.pyrite.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class OxidizableBarsBlock extends PaneBlock implements Oxidizable {
	private final OxidationLevel oxidationLevel;

	public OxidizableBarsBlock(OxidationLevel oxidationLevel, Settings settings) {
		super(settings);
		this.oxidationLevel = oxidationLevel;
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.tickDegradation(state, world, pos, random);
	}

	@Override
	protected boolean hasRandomTicks(BlockState state) {
		return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
	}

	@Override
	public OxidationLevel getDegradationLevel() {
		return oxidationLevel;
	}
}