package net.shirojr.sheetsreader.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.sheetsreader.SheetsReader;

public class SheetsReaderSound {
    public static SoundEvent EVENT_REFRESH = registerSound("refresh");

    private static SoundEvent registerSound(String id) {
        SoundEvent sound = new SoundEvent(new Identifier(SheetsReader.MODID, id));
        return Registry.register(Registry.SOUND_EVENT, new Identifier(SheetsReader.MODID, id), sound);
    }

    public static void initializeSounds() { }
}
