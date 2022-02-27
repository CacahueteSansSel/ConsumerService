package studios.nightek.consume.generation.surface.features;

import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import studios.nightek.consume.ConsumerBlocks;
import studios.nightek.consume.blocks.DeliveryBikeBlock;

import java.util.Random;

// TODO: Replace this shit by a structure
public class LostDeliveryBikeFeature extends Feature<NoFeatureConfig> {
    BlockPos lastPosition = BlockPos.ZERO;

    public static final float MINIMUM_DISTANCE = 1024;

    public LostDeliveryBikeFeature() {
        super(NoFeatureConfig.CODEC);
        setRegistryName("lost_delivery_bike");
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {

        if (rand.nextFloat() >= 0.05f) return false;
        if (!lastPosition.withinDistance(pos, MINIMUM_DISTANCE)) return false;

        BlockPos randomPosInChunk = pos.add(rand.nextInt(16), 0, rand.nextInt(16));
        int height = reader.getHeight(Heightmap.Type.WORLD_SURFACE, randomPosInChunk.getX(), randomPosInChunk.getZ());
        BlockPos blockPos = new BlockPos(randomPosInChunk.getX(), height, randomPosInChunk.getZ());

        lastPosition = blockPos;

        if (!reader.getBlockState(blockPos).matchesBlock(Blocks.AIR)) return false;
        if (!reader.getBlockState(blockPos.down()).matchesBlock(Blocks.GRASS_BLOCK)) return false;

        if (reader.getBlockState(blockPos.west()).matchesBlock(Blocks.AIR)
            && reader.getBlockState(blockPos.west().down()).matchesBlock(Blocks.GRASS_BLOCK)) {
            if (rand.nextFloat() < 0.2f) {
                reader.setBlockState(blockPos.west(),
                        ConsumerBlocks.EMPTY_CARDBOARD.getDefaultState(),
                        0);
            }
        }

        if (reader.getBlockState(blockPos.east()).matchesBlock(Blocks.AIR)
                && reader.getBlockState(blockPos.east().down()).matchesBlock(Blocks.GRASS_BLOCK)) {
            if (rand.nextFloat() < 0.2f) {
                reader.setBlockState(blockPos.east(),
                        ConsumerBlocks.EMPTY_CARDBOARD.getDefaultState(),
                        0);
            }
        }

        if (reader.getBlockState(blockPos.north()).matchesBlock(Blocks.AIR)
                && reader.getBlockState(blockPos.north().down()).matchesBlock(Blocks.GRASS_BLOCK)) {
            if (rand.nextFloat() < 0.2f) {
                reader.setBlockState(blockPos.north(),
                        ConsumerBlocks.EMPTY_CARDBOARD.getDefaultState(),
                        0);
            }
        }

        if (reader.getBlockState(blockPos.south()).matchesBlock(Blocks.AIR)
                && reader.getBlockState(blockPos.south().down()).matchesBlock(Blocks.GRASS_BLOCK)) {
            if (rand.nextFloat() < 0.2f) {
                reader.setBlockState(blockPos.south(),
                        ConsumerBlocks.EMPTY_CARDBOARD.getDefaultState(),
                        0);
            }
        }

        reader.setBlockState(blockPos, ConsumerBlocks.DELIVERY_BIKE_BLOCK.getDefaultState()
                .with(DeliveryBikeBlock.Facing, Direction.byHorizontalIndex(rand.nextInt(4))), 0);

        return false;
    }
}
