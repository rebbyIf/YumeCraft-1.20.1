package net.rebbystuff.yumecraft.world.pool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.rebbystuff.yumecraft.util.BuildingBlock;
import net.rebbystuff.yumecraft.util.GetNBT;
import net.rebbystuff.yumecraft.util.processer.BlockProcessor;
import net.rebbystuff.yumecraft.util.processer.BlockRemovalProcessor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SetTemplatePool {

    public static Codec<SetTemplatePool> CODEC = RecordCodecBuilder.create(
    instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("templates").forGetter(instance2 -> {
                return instance2.templates;
            }),
            BlockRemovalProcessor.CODEC.listOf().fieldOf("processors").forGetter(instance2 -> {
                return instance2.processors;
            })).apply(instance, SetTemplatePool::new)
        );


    private final Map<String, String> templates;
    private final Map<String, List<BuildingBlock>> structues;

    private final List<BlockRemovalProcessor> processors;

    public static void prepCodec(){

    }
    public SetTemplatePool(Map<String, String> templates, List<BlockRemovalProcessor> processors){
        this.templates = templates;
        this.processors = processors;
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

    @Nullable
    public BlockState process(BlockState block, RandomSource random) {
        BlockState currentBlock = block;
        for (BlockProcessor processor : processors){
            currentBlock = processor.process(currentBlock, random);
            if (currentBlock == null){
                break;
            }
        }
        return currentBlock;
    }


}
