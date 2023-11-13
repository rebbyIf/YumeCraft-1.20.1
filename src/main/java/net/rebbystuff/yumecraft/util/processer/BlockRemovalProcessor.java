package net.rebbystuff.yumecraft.util.processer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockRemovalProcessor extends BlockProcessor {

    public static final Codec<BlockRemovalProcessor> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("filter").forGetter(instance2 -> {
                    return instance2.filter;
                }),
                Codec.FLOAT.fieldOf("chance").forGetter(instance2 -> {
                    return instance2.chance;
                })
        ).apply(instance, BlockRemovalProcessor::new);
    });


    private final float chance;

    public BlockRemovalProcessor(HolderSet<Block> filter, float chance) {
        super(filter);
        this.chance = chance;
    }

    @Override
    public BlockState process(BlockState object, RandomSource random) {
        if (object.is(filter) && Math.abs(random.nextFloat() % 1.0f) < chance) {
            return null;
        }
        return object;
    }

    @Override
    public Codec<? extends BlockProcessor> type() {
        return CODEC;
    }

    @Override
    public BlockProcessor toSuper(){
        BlockProcessor processor;
        processor = (BlockProcessor) this;
        return processor;
    }
}
