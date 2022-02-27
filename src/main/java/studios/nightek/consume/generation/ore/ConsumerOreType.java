package studios.nightek.consume.generation.ore;

import net.minecraftforge.common.util.Lazy;
import studios.nightek.consume.ConsumerBlocks;
import studios.nightek.consume.blocks.ConsumerOreBlock;

public enum ConsumerOreType {

    PETROLEUM(Lazy.of(() -> ConsumerBlocks.PETROLEUM_CRYSTAL_ORE), 8, 4, 25);

    private final Lazy<ConsumerOreBlock> block;
    private final int maxVeinSize;
    private final int minHeight;
    private final int maxHeight;

    ConsumerOreType(Lazy<ConsumerOreBlock> block, int maxVeinSize, int minHeight, int maxHeight) {
        this.block = block;
        this.maxVeinSize = maxVeinSize;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    public Lazy<ConsumerOreBlock> getBlock() {
        return block;
    }

    public int getMaxVeinSize() {
        return maxVeinSize;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getMinHeight() {
        return minHeight;
    }
}
