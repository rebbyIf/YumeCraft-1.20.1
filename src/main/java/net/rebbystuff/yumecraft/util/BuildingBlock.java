package net.rebbystuff.yumecraft.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BuildingBlock {

    private BlockPos pos;

    private BlockState state;

    public BuildingBlock(BlockPos pos, BlockState state){
        this.pos = pos;
        this.state = state;
    }

    public BlockPos getPos() {
        return new BlockPos(pos);
    }

    public BlockState getState() {
        return state;
    }



}
