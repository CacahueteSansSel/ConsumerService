package dev.cacahuete.consume;

import dev.cacahuete.consume.ui.CashMachineContainer;
import dev.cacahuete.consume.ui.PackagerContainer;
import dev.cacahuete.consume.ui.ProtectiveShelfContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;

public class ConsumerContainers {

    public static ContainerType<?> ContainerProtectiveShelf;
    public static ContainerType<?> ContainerCashMachine;
    public static ContainerType<?> ContainerPackager;

    public static ContainerType<?>[] all() {
        return new ContainerType[]{
                ContainerProtectiveShelf,
                ContainerCashMachine,
                ContainerPackager
        };
    }

    public static void init() {
        ContainerProtectiveShelf = IForgeContainerType.create(ProtectiveShelfContainer::newClient);
        ContainerProtectiveShelf.setRegistryName("ui.shop_shelf");

        ContainerCashMachine = IForgeContainerType.create(CashMachineContainer::newClient);
        ContainerCashMachine.setRegistryName("ui.cash_machine");

        ContainerPackager = IForgeContainerType.create(PackagerContainer::newClient);
        ContainerPackager.setRegistryName("ui.packager");
    }

    public static void register(final RegistryEvent.Register<ContainerType<?>> evt) {
        for (ContainerType<?> ui : all()) {
            evt.getRegistry().register(ui);
        }
    }
}
