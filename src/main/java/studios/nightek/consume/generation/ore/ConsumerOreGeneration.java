package studios.nightek.consume.generation.ore;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class ConsumerOreGeneration {
    public static void generate(final BiomeLoadingEvent event) {
        for (ConsumerOreType ore : ConsumerOreType.values()) {
            OreFeatureConfig featureConfig = new OreFeatureConfig(
                    OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
                    ore.getBlock().get().getDefaultState(),
                    ore.getMaxVeinSize()
            );

            ConfiguredPlacement configuredPlacement = Placement.RANGE.configure(
                    new TopSolidRangeConfig(
                            ore.getMinHeight(),
                            ore.getMinHeight(),
                            ore.getMaxHeight()
                    )
            );

            ConfiguredFeature<?, ?> feature = Registry.register(
                    WorldGenRegistries.CONFIGURED_FEATURE,
                    ore.getBlock().get().getRegistryName(),
                    Feature.ORE.withConfiguration(featureConfig).withPlacement(configuredPlacement)
                            .square().count(ore.getMaxVeinSize()));

            event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, feature);
        }
    }
}
