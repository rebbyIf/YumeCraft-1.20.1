package net.rebbystuff.yumecraft.world.pool;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.rebbystuff.yumecraft.block.BuildingBlock;
import net.rebbystuff.yumecraft.nbt.GetNBT;
import net.rebbystuff.yumecraft.setup.Registration;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SetTemplatePool {

    public static final Codec<SetTemplatePool> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("templates").forGetter(instance2 -> {
                        return instance2.templates;
                    })
            ).apply(instance, SetTemplatePool::new)
    );


    private Map<String, String> templates;
    private Map<String, List<BuildingBlock>> structues;
    public SetTemplatePool(Map<String, String> templates){
        this.templates = templates;
        structues = new HashMap<>();
    }

    @Nullable
    public List<BuildingBlock> getSetStructure(String key, LevelAccessor levelAccessor, Level level) {
        if (structues.containsKey(key)){
            return structues.get(key);
        } else if (templates.containsKey(key)) {
            ArrayList<BuildingBlock> generatedStructure = GetNBT.getBuildingBlocks(templates.get(key), levelAccessor, level);
            List<BuildingBlock> unmodifiableStructure = Collections.unmodifiableList(generatedStructure);
            structues.put(key, unmodifiableStructure);
            return unmodifiableStructure;
        }
        return null;
    }


}
