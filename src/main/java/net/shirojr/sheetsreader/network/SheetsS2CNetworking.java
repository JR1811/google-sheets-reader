package net.shirojr.sheetsreader.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.shirojr.sheetsreader.SheetsReader;
import net.shirojr.sheetsreader.SheetsReaderClient;
import net.shirojr.sheetsreader.sheet.SheetsElement;
import net.shirojr.sheetsreader.sound.SheetsReaderSound;

public class SheetsS2CNetworking {
    public static final Identifier REFRESH_SOURCE_CHANNEL = new Identifier(SheetsReader.MODID, "refresh_sheets_source");

    private static void handleSheetSourceRefreshPacket(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler,
                                                       PacketByteBuf buf, PacketSender packetSender) {
        client.execute(() -> {
            SheetsReaderClient.clientTick.startTicking(3, false, () -> {
                SheetsReader.elementList = SheetsElement.getItemList();
                if (client.player != null) {
                    client.getSoundManager().play(PositionedSoundInstance.master(SheetsReaderSound.EVENT_REFRESH, 1f, 2f));
                }
            });
        });
    }

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(REFRESH_SOURCE_CHANNEL, SheetsS2CNetworking::handleSheetSourceRefreshPacket);
    }
}
