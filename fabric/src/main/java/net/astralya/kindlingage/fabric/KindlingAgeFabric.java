package net.astralya.kindlingage.fabric;

import net.astralya.kindlingage.KindlingAge;
import net.fabricmc.api.ModInitializer;

public final class KindlingAgeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        KindlingAgeFabricConfig.init();
        KindlingAge.init();
    }
}
