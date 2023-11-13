package net.rebbystuff.yumecraft.world.dimension;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.rebbystuff.yumecraft.YumeCraftMod;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.rebbystuff.yumecraft.util.Clone;
import net.rebbystuff.yumecraft.world.dimension.chunkgen.DimensionalLibraryChunkGenerator;


public class Dimensions {

    public static final ResourceKey<Level> THRESHOLD_DIM_KEY =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(YumeCraftMod.MOD_ID, "threshold"));

    public static final ResourceKey<DimensionType> THRESHOLD_DIM_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, THRESHOLD_DIM_KEY.location());

    public static final ResourceKey<Level> DIMENSIONAL_LIBRARY_DIM_KEY =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(YumeCraftMod.MOD_ID, "dimensional_library"));

    public static final ResourceKey<DimensionType> DIMENSIONAL_LIBRARY_DIM_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, THRESHOLD_DIM_KEY.location());


    public static void register() {
        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.addListener(Dimensions::playerWakeUpEvent);
        System.out.println("Registering ModDimension for " + YumeCraftMod.MOD_ID);
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, new ResourceLocation(YumeCraftMod.MOD_ID,
                "dimensional_library_chunkgen"), DimensionalLibraryChunkGenerator.CODEC);

    }

    public static void worldLoadEvent(GatherDataEvent event) {

    }

    @SubscribeEvent
    public static void playerWakeUpEvent(PlayerWakeUpEvent wakeUpEvent){
        Player player = wakeUpEvent.getEntity();

        try {
            if (player.level().dimension().equals(ServerLevel.OVERWORLD)) {
                ServerLevel startLevel = null;
                ServerLevel level = null;
                if (player.level().isClientSide) {
                    startLevel = Minecraft.getInstance().getSingleplayerServer().getLevel(ServerLevel.OVERWORLD);
                    level = Minecraft.getInstance().getSingleplayerServer().getLevel(THRESHOLD_DIM_KEY);
                } else {
                    startLevel = player.getServer().getLevel(ServerLevel.OVERWORLD);
                    level = player.getServer().getLevel(THRESHOLD_DIM_KEY);
                }

                int diff = 5;
                BlockPos begin = new BlockPos(player.getBlockX() - diff, player.getBlockY() - diff,
                        player.getBlockZ() - diff);
                BlockPos end = new BlockPos(player.getBlockX() + diff, player.getBlockY() + diff,
                        player.getBlockZ() + diff);
                BlockPos final_pos = new BlockPos(player.getBlockX() - diff, 32 - diff,
                        player.getBlockZ() - diff);

                Clone.clone(begin, end, final_pos, startLevel, level, true, false, true);

                ((ServerPlayer) player).teleportTo(level, player.getX(), 32.0,
                        player.getZ(), player.getYRot(), player.getXRot());

            }
        } catch (Exception e) {
            player.displayClientMessage(Component.literal(e.getClass().getName() + ": " + e.getMessage()), true);
        }
    }
}
