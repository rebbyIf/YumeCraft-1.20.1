package net.rebbystuff.yumecraft.world;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.CloneCommands;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.rebbystuff.yumecraft.exceptions.FailureException;
import net.rebbystuff.yumecraft.exceptions.world.AreaTooLargeException;
import net.rebbystuff.yumecraft.exceptions.world.UnloadedChunkException;
import net.rebbystuff.yumecraft.exceptions.world.VolumeOverlappingException;

import javax.annotation.Nullable;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;

public class Clone {

    public static int clone(BlockPos pBegin, BlockPos pEnd, BlockPos pTarget, ServerLevel pBeginDimension, ServerLevel pEndDimension, boolean canOverlap, boolean move, boolean force) {
        BoundingBox boundingbox = BoundingBox.fromCorners(pBegin, pEnd);
        BlockPos targetEnd = pTarget.offset(boundingbox.getLength());
        BoundingBox boundingbox1 = BoundingBox.fromCorners(pTarget, targetEnd);
        if (canOverlap && pBeginDimension == pEndDimension && boundingbox1.intersects(boundingbox)) {
            throw new VolumeOverlappingException();
        } else {
            int i = boundingbox.getXSpan() * boundingbox.getYSpan() * boundingbox.getZSpan();
            int j = pBeginDimension.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
            if (i > j) {
                throw new AreaTooLargeException(i, j);
            }

            if (pBeginDimension.hasChunksAt(pBegin, pEnd) && (pEndDimension.hasChunksAt(pTarget, targetEnd) || force)) {

                List<Clone.CloneBlockInfo> list = Lists.newArrayList();
                List<Clone.CloneBlockInfo> list1 = Lists.newArrayList();
                List<Clone.CloneBlockInfo> list2 = Lists.newArrayList();
                Deque<BlockPos> deque = Lists.newLinkedList();
                BlockPos blockpos4 = new BlockPos(boundingbox1.minX() - boundingbox.minX(), boundingbox1.minY() - boundingbox.minY(), boundingbox1.minZ() - boundingbox.minZ());

                for(int k = boundingbox.minZ(); k <= boundingbox.maxZ(); ++k) {
                    for(int l = boundingbox.minY(); l <= boundingbox.maxY(); ++l) {
                        for(int i1 = boundingbox.minX(); i1 <= boundingbox.maxX(); ++i1) {
                            BlockPos blockpos5 = new BlockPos(i1, l, k);
                            BlockPos blockpos6 = blockpos5.offset(blockpos4);
                            BlockInWorld blockinworld = new BlockInWorld(pBeginDimension, blockpos5, force);
                            BlockState blockstate = blockinworld.getState();
                            BlockEntity blockentity = pBeginDimension.getBlockEntity(blockpos5);
                            if (blockentity != null) {
                                CompoundTag compoundtag = blockentity.saveWithoutMetadata();
                                list1.add(new Clone.CloneBlockInfo(blockpos6, blockstate, compoundtag));
                                deque.addLast(blockpos5);
                            } else if (!blockstate.isSolidRender(pBeginDimension, blockpos5) && !blockstate.isCollisionShapeFullBlock(pBeginDimension, blockpos5)) {
                                list2.add(new Clone.CloneBlockInfo(blockpos6, blockstate, (CompoundTag)null));
                                deque.addFirst(blockpos5);
                            } else {
                                list.add(new Clone.CloneBlockInfo(blockpos6, blockstate, (CompoundTag) null));
                                deque.addLast(blockpos5);
                            }

                        }
                    }
                }

                if (move) {
                    for(BlockPos blockpos7 : deque) {
                        BlockEntity blockentity1 = pBeginDimension.getBlockEntity(blockpos7);
                        Clearable.tryClear(blockentity1);
                        pBeginDimension.setBlock(blockpos7, Blocks.BARRIER.defaultBlockState(), 2);
                    }

                    for(BlockPos blockpos8 : deque) {
                        pBeginDimension.setBlock(blockpos8, Blocks.AIR.defaultBlockState(), 3);
                    }
                }

                List<Clone.CloneBlockInfo> list3 = Lists.newArrayList();
                list3.addAll(list);
                list3.addAll(list1);
                list3.addAll(list2);
                List<Clone.CloneBlockInfo> list4 = Lists.reverse(list3);

                for(Clone.CloneBlockInfo clonecommands$cloneblockinfo : list4) {
                    BlockEntity blockentity2 = pEndDimension.getBlockEntity(clonecommands$cloneblockinfo.pos);
                    Clearable.tryClear(blockentity2);
                    pEndDimension.setBlock(clonecommands$cloneblockinfo.pos, Blocks.BARRIER.defaultBlockState(), 2);
                }

                int j1 = 0;

                for(Clone.CloneBlockInfo clonecommands$cloneblockinfo1 : list3) {
                    boolean added;

                    if (force) {
                        added = pEndDimension.setBlockAndUpdate(clonecommands$cloneblockinfo1.pos, clonecommands$cloneblockinfo1.state);
                    } else {
                        added = pEndDimension.setBlock(clonecommands$cloneblockinfo1.pos, clonecommands$cloneblockinfo1.state, 2);
                    }

                    if (added) {
                        ++j1;
                    }
                }

                for(Clone.CloneBlockInfo clonecommands$cloneblockinfo2 : list1) {
                    BlockEntity blockentity3 = pEndDimension.getBlockEntity(clonecommands$cloneblockinfo2.pos);
                    if (clonecommands$cloneblockinfo2.tag != null && blockentity3 != null) {
                        blockentity3.load(clonecommands$cloneblockinfo2.tag);
                        blockentity3.setChanged();
                    }

                    if (force) {
                        pEndDimension.setBlockAndUpdate(clonecommands$cloneblockinfo2.pos, clonecommands$cloneblockinfo2.state);
                    } else {
                        pEndDimension.setBlock(clonecommands$cloneblockinfo2.pos, clonecommands$cloneblockinfo2.state, 2);
                    }
                }

                for(Clone.CloneBlockInfo clonecommands$cloneblockinfo3 : list4) {
                    pEndDimension.blockUpdated(clonecommands$cloneblockinfo3.pos, clonecommands$cloneblockinfo3.state.getBlock());
                }

                pEndDimension.getBlockTicks().copyAreaFrom(pBeginDimension.getBlockTicks(), boundingbox, blockpos4);
                if (j1 == 0) {
                    throw new FailureException("Cloning Failed");
                } else {
                    return j1;
                }
            } else {
                throw new UnloadedChunkException();
            }
        }
    }

    private static class CloneBlockInfo {
        private final BlockPos pos;
        private final BlockState state;
        @Nullable
        private final CompoundTag tag;

        private CloneBlockInfo(BlockPos pPos, BlockState pState, @Nullable CompoundTag pTag) {
            this.pos = pPos;
            this.state = pState;
            this.tag = pTag;
        }
    }
}
