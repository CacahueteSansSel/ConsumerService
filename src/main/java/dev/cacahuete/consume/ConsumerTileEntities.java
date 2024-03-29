package dev.cacahuete.consume;

import dev.cacahuete.consume.entities.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;

public class ConsumerTileEntities {
    public static final TileEntityType<?> PROTECTIVE_SHELF_TILE_ENTITY = TileEntityType.Builder.create(ProtectiveShelfTileEntity::new,
            ConsumerBlocks.PROTECTIVE_SHOP_SHELF_START, ConsumerBlocks.PROTECTIVE_SHOP_SHELF_MIDDLE,
            ConsumerBlocks.PROTECTIVE_SHOP_SHELF_END, ConsumerBlocks.PROTECTIVE_SHOWCASE).build(null).setRegistryName("shop_shelf_te");
    public static final TileEntityType<?> CASH_MACHINE_TILE_ENTITY = TileEntityType.Builder.create(CashMachineTileEntity::new,
            ConsumerBlocks.PROTECTIVE_CASH_MACHINE_BLOCK).build(null).setRegistryName("cash_machine_te");
    public static final TileEntityType<?> BLOCKCHAIN_TILE_ENTITY = TileEntityType.Builder.create(BankAccountControllerBlockTileEntity::new,
            ConsumerBlocks.BANK_ACCOUNT_CONTROLLER_BLOCK).build(null).setRegistryName("blockchain_te");
    public static final TileEntityType<?> RADIO_TILE_ENTITY = TileEntityType.Builder.create(RadioBlockTileEntity::new,
            ConsumerBlocks.RADIO).build(null).setRegistryName("radio_te");
    public static final TileEntityType<?> JUKEBOX_SPEAKER_TILE_ENTITY = TileEntityType.Builder.create(JukeboxSpeakerTileEntity::new,
            ConsumerBlocks.JUKEBOX_SPEAKER).build(null).setRegistryName("jukebox_speaker_te");
    public static final TileEntityType<?> PACKAGER_TILE_ENTITY = TileEntityType.Builder.create(PackagerTileEntity::new,
            ConsumerBlocks.PACKAGER).build(null).setRegistryName("packager_te");

    public static TileEntityType<?>[] all() {
        return new TileEntityType<?>[] {
                PROTECTIVE_SHELF_TILE_ENTITY,
                CASH_MACHINE_TILE_ENTITY,
                BLOCKCHAIN_TILE_ENTITY,
                RADIO_TILE_ENTITY,
                JUKEBOX_SPEAKER_TILE_ENTITY,
                PACKAGER_TILE_ENTITY
        };
    }

    public static void register(final RegistryEvent.Register<TileEntityType<?>> evt) {
        for (TileEntityType<?> te : all()) {
            evt.getRegistry().register(te);
        }
    }
}
