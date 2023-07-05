package dev.cacahuete.consume.generation.surface;

import dev.cacahuete.consume.generation.surface.features.LostDeliveryBikeFeature;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.ArrayList;

public class ConsumerSurfaceGeneration {
    public static final ArrayList<Feature<NoFeatureConfig>> FEATURES = new ArrayList<>();

    public static final Feature<NoFeatureConfig> LOST_DELIVERY_BIKE_FEATURE = register(new LostDeliveryBikeFeature());

    public static void generate(final BiomeLoadingEvent event) {
        for (Feature<NoFeatureConfig> feature : FEATURES) {
            ConfiguredFeature<?, ?> confFeature = Registry.register(
                    WorldGenRegistries.CONFIGURED_FEATURE,
                    feature.getRegistryName().toString(),
                    feature.withConfiguration(new NoFeatureConfig())
            );

            event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, confFeature);
        }
    }

    static Feature<NoFeatureConfig> register(Feature<NoFeatureConfig> feature) {
        FEATURES.add(feature);
        return feature;
    }
}
