package net.rebbystuff.yumecraft.world.dimension.chunkgen;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.*;
import net.rebbystuff.yumecraft.world.pool.SetTemplatePool;

public abstract class StructureBasedChunkGenerator extends ChunkGenerator {


    protected SetTemplatePool templatePool;

    public StructureBasedChunkGenerator(BiomeSource biomeSource, SetTemplatePool templatePool) {
        super(biomeSource);
        this.templatePool = templatePool;
    }


}
