package net.rebbystuff.yumecraft.world.dimension.chunkgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.rebbystuff.yumecraft.util.BuildingBlock;
import net.rebbystuff.yumecraft.world.pool.SetTemplatePool;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DimensionalLibraryChunkGenerator extends StructureBasedChunkGenerator{

    private static final Codec<Settings> SETTINGS_CODEC = RecordCodecBuilder.create(
            instance -> {
                return instance.group(
                        Codec.INT.fieldOf("base").forGetter(Settings::base),
                        Codec.INT.fieldOf("floor").forGetter(Settings::floor),
                        Codec.INT.fieldOf("ceiling").forGetter(Settings::ceiling),
                        Codec.INT.fieldOf("height").forGetter(Settings::height)
                ).apply(instance, Settings::new);
            });

    public static final Codec<DimensionalLibraryChunkGenerator> CODEC = RecordCodecBuilder.create(
            (instance) -> {
                return instance.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((instance2) -> {
                    return instance2.biomeSource;
                }), SetTemplatePool.CODEC.fieldOf("set_template_pool").forGetter(instance3 -> {
                    return instance3.templatePool;
                }), SETTINGS_CODEC.fieldOf("settings").forGetter(instance4 -> {
                    return instance4.settings;
                }), NormalNoise.NoiseParameters.CODEC.fieldOf("structural_noise").forGetter(instance5 -> {
                    return instance5.structureNoiseParameters;
                })).apply(instance, instance.stable(DimensionalLibraryChunkGenerator::new));
            });
    private final Settings settings;
    private final Holder<NormalNoise.NoiseParameters> structureNoiseParameters;

    private NormalNoise structureNoise;




    public DimensionalLibraryChunkGenerator(BiomeSource biomeSource, SetTemplatePool templatePool,
            Settings settings, Holder<NormalNoise.NoiseParameters> structureNoiseParameters) {
        super(biomeSource, templatePool);
        this.settings = settings;
        this.structureNoiseParameters = structureNoiseParameters;
    }



    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion pLevel, long pSeed, RandomState pRandom, BiomeManager pBiomeManager, StructureManager pStructureManager, ChunkAccess pChunk, GenerationStep.Carving pStep) {
    }

    @Override
    public void buildSurface(WorldGenRegion pLevel, StructureManager pStructureManager, RandomState pRandom, ChunkAccess pChunk) {
        // Initializes noise
        if (structureNoise == null) {
            structureNoise = NormalNoise.create(pLevel.getRandom(), structureNoiseParameters.value());
        }

        BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
        BlockState oak_planks = Blocks.OAK_PLANKS.defaultBlockState();
        BlockState red_carpet = Blocks.RED_CARPET.defaultBlockState();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = 0; x < 16; x++){
            for (int z = 0; z < 16; z++){
                pChunk.setBlockState(pos.set(x, settings.base, z), bedrock, false);
                for (int y = settings.base + 1; y < settings.floor; y++){
                    pChunk.setBlockState(pos.set(x, y, z), oak_planks, false);
                }
                pChunk.setBlockState(pos.set(x, settings.floor, z), red_carpet, false);
                for (int y = settings.ceiling; y < settings.height; y++){
                    pChunk.setBlockState(pos.set(x, y, z), oak_planks, false);
                }
                pChunk.setBlockState(pos.set(x, settings.height, z), bedrock, false);
            }
        }


        String key = "0";

        boolean [][] isLoc = new boolean[3][3];
        boolean hasNeighbors = false;

        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                isLoc[x][z] = structureNoise.getValue(pChunk.getPos().x + x - 1, 0, pChunk.getPos().z + z -1) < 0;
                if (isLoc[x][z]) {
                    hasNeighbors = true;
                }
            }
        }

        if (isLoc[1][1]) {
            if (isLoc[1][0] && isLoc[1][2]) {
                key = "1";
            } else if (isLoc[1][0]) {
                key = "2";
            } else if (isLoc[1][2]) {
                key = "3";
            } else {
                key = "4";
            }
        } else if (!hasNeighbors && pChunk.getPos().x % 2 == 0 && pChunk.getPos().z % 2 == 0) {
            key = "4";
        }

        List<BuildingBlock> structure = templatePool.getSetStructure(key, pLevel.getLevel(), pLevel.getLevel());

        Vec3i startPos = new Vec3i(pChunk.getPos().getMinBlockX(), settings.floor, pChunk.getPos().getMinBlockZ());

        if (structure != null) {
            for (BuildingBlock block : structure){
                BlockState state = templatePool.process(block.getState(), pLevel.getRandom());
                if (state != null) {
                    pChunk.setBlockState(block.getPos().offset(startPos), state, false);
                }
            }
        }

    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion pLevel) {

    }

    @Override
    public int getGenDepth() {
        return settings.height;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, Blender pBlender, RandomState pRandom, StructureManager pStructureManager, ChunkAccess pChunk) {
        return CompletableFuture.completedFuture(pChunk);
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinY() {
        return settings.base;
    }

    @Override
    public int getBaseHeight(int pX, int pZ, Heightmap.Types pType, LevelHeightAccessor pLevel, RandomState pRandom) {
        return settings.floor;
    }

    @Override
    public NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor pHeight, RandomState pRandom) {
        int y = getBaseHeight(pX, pZ, Heightmap.Types.WORLD_SURFACE, pHeight, pRandom);
        BlockState planks = Blocks.OAK_WOOD.defaultBlockState();
        BlockState [] blocks = new BlockState[y];
        blocks[0] = Blocks.BEDROCK.defaultBlockState();
        for (int i = 1; i < y; i++){
            blocks[i] = planks;
        }
        return new NoiseColumn(pHeight.getMinBuildHeight(), blocks);
    }

    @Override
    public void addDebugScreenInfo(List<String> pInfo, RandomState pRandom, BlockPos pPos) {

    }

    private record Settings(int base, int floor, int ceiling, int height){

    }
}
