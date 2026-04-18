package net.astralya.kindlingage.neoforge;

import net.astralya.kindlingage.KindlingAge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(KindlingAge.MOD_ID)
public final class KindlingAgeNeoForge {
    public KindlingAgeNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        KindlingAgeNeoForgeConfig.init(modContainer, modEventBus);
        KindlingAge.init();
        KindlingAgeNeoForgeClient.init(modEventBus);
    }
}
