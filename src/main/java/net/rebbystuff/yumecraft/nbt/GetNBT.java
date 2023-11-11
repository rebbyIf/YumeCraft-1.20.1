package net.rebbystuff.yumecraft.nbt;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.rebbystuff.yumecraft.YumeCraftMod;
import net.rebbystuff.yumecraft.block.BuildingBlock;

import java.util.ArrayList;
import java.util.Optional;

public class GetNBT {
    public static ArrayList<BuildingBlock> getBuildingBlocks(String structureName, LevelAccessor levelAccessor, Level level) {
        ResourceManager resourceManager;
        if (levelAccessor.isClientSide())
            resourceManager = Minecraft.getInstance().getResourceManager();
        else
            resourceManager = levelAccessor.getServer().getResourceManager();

        CompoundTag nbt = getBuildingNbt(structureName, resourceManager);
        ArrayList<BuildingBlock> blocks = new ArrayList<>();

        // load in blocks (list of blockPos and their palette index)
        ListTag blocksNbt = nbt.getList("blocks", 10);

        ArrayList<BlockState> palette = getBuildingPalette(nbt, level);

        for(int i = 0; i < blocksNbt.size(); i++) {
            CompoundTag blockNbt = blocksNbt.getCompound(i);
            ListTag blockPosNbt = blockNbt.getList("pos", 3);

            blocks.add(new BuildingBlock(
                    new BlockPos(
                            blockPosNbt.getInt(0),
                            blockPosNbt.getInt(1),
                            blockPosNbt.getInt(2)
                    ),
                    palette.get(blockNbt.getInt("state"))
            ));
        }
        return blocks;
    }

    public static CompoundTag getBuildingNbt(String structureName, ResourceManager resManager) {
        try {
            ResourceLocation rl = new ResourceLocation(YumeCraftMod.MOD_ID, "structures/" + structureName + ".nbt");
            Optional<Resource> rs = resManager.getResource(rl);
            return NbtIo.readCompressed(rs.get().open());
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static ArrayList<BlockState> getBuildingPalette(CompoundTag nbt, Level level) {
        ArrayList<BlockState> palette = new ArrayList<>();
        // load in palette (list of unique blockstates)
        ListTag paletteNbt = nbt.getList("palette", 10);
        for (int i = 0; i < paletteNbt.size(); i++) {
            CompoundTag tag = paletteNbt.getCompound(i);
            String name = tag.getString("Name");
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
            HolderGetter<Block> holdergetter = (HolderGetter<Block>)(level != null ? level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup());
            palette.add(NbtUtils.readBlockState(holdergetter, paletteNbt.getCompound(i)));
        }
        return palette;
    }
}
