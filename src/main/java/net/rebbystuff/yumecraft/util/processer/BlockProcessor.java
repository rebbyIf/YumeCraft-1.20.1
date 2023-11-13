package net.rebbystuff.yumecraft.util.processer;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.rebbystuff.yumecraft.setup.Registration;

import java.util.function.Function;

public abstract class BlockProcessor {




    protected final HolderSet<Block> filter;

    public static ResourceLocation getRegistryName(){
        return Registration.BLOCK_PROCESSORS.getRegistryName();
    }

    public BlockProcessor(HolderSet<Block> filter) {
        this.filter = filter;
    }
    public abstract BlockState process(BlockState object, RandomSource random);

    public abstract Codec<? extends BlockProcessor> type();
    public abstract BlockProcessor toSuper();

    public <T extends BlockProcessor> T toInstance(){
        return (T) this;
    }
}
