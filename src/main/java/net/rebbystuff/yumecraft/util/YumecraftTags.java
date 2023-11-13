package net.rebbystuff.yumecraft.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.rebbystuff.yumecraft.YumeCraftMod;

public class YumecraftTags {

    public static class Blocks {
        public static final TagKey<Block> DETERIORATE = tag("deteriorate");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(YumeCraftMod.MOD_ID, name));
        }
    }
}
