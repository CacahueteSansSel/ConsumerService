package studios.nightek.consume;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;

public class ConsumerSounds {
    public static final SoundEvent ALARM_BEEP = newSE("alarm_beep");
    public static final SoundEvent ATM_LOGIN_SUCCESS = newSE("atm.login.success");
    public static final SoundEvent ATM_LOGIN_FAILED = newSE("atm.login.failed");
    public static final SoundEvent ATM_LOGOFF_SUCCESS = newSE("atm.logoff.success");

    public static SoundEvent newSE(String name) {
        return new SoundEvent(new ResourceLocation("consume", name)).setRegistryName(name);
    }

    public static SoundEvent[] all() {
        return new SoundEvent[] {
                ALARM_BEEP,
                ATM_LOGIN_SUCCESS,
                ATM_LOGIN_FAILED,
                ATM_LOGOFF_SUCCESS
        };
    }

    public static void register(final RegistryEvent.Register<SoundEvent> evt) {
        for (SoundEvent se : all()) {
            evt.getRegistry().register(se);
        }
    }
}
