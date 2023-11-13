package net.rebbystuff.yumecraft.setup;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import net.rebbystuff.yumecraft.YumeCraftMod;
import net.rebbystuff.yumecraft.util.processer.BlockProcessor;
import net.rebbystuff.yumecraft.util.processer.BlockRemovalProcessor;
import net.rebbystuff.yumecraft.world.pool.SetTemplatePool;

import java.util.function.Function;
import java.util.function.Supplier;


public class Registration {


    public static final DeferredRegister<SetTemplatePool> SET_TEMPLATE_POOL = DeferredRegister.create(
            new ResourceLocation(YumeCraftMod.MOD_ID, "worldgen/set_template_pool"), YumeCraftMod.MOD_ID);

    public static final DeferredRegister<Codec<? extends BlockProcessor>> BLOCK_PROCESSORS = DeferredRegister.create(
            new ResourceLocation(YumeCraftMod.MOD_ID, "worldgen/custom_processors"), YumeCraftMod.MOD_ID);









    public static final class Registries{

        public static final Supplier<IForgeRegistry<Codec<? extends BlockProcessor>>> BLOCK_PROCESSOR =
                BLOCK_PROCESSORS.makeRegistry(() -> new RegistryBuilder<Codec<? extends BlockProcessor>>().allowModification());

    }

    public static final class RegistryObjects {

    }

    public static void init(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        SET_TEMPLATE_POOL.register(bus);
        BLOCK_PROCESSORS.register(bus);
    }
}
