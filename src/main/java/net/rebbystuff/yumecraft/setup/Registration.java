package net.rebbystuff.yumecraft.setup;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import net.rebbystuff.yumecraft.YumeCraftMod;
import net.rebbystuff.yumecraft.world.dimension.chunkgen.DimensionalLibraryChunkGenerator;
import net.rebbystuff.yumecraft.world.pool.SetTemplatePool;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;


public class Registration {


    public static final DeferredRegister<SetTemplatePool> SET_TEMPLATE_POOL = DeferredRegister.create(
            new ResourceLocation(YumeCraftMod.MOD_ID, "set_template_pool"), YumeCraftMod.MOD_ID);





    public static void init(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        SET_TEMPLATE_POOL.register(bus);
    }

}
