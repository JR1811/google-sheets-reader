package net.shirojr.sheetsreader.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.shirojr.sheetsreader.SheetsReader;

public class ClientTicker {
    private int tick;
    private boolean shouldTick = false, printValues = false;
    private Runnable executor;
    public void startTicking(float seconds, boolean printValues, Runnable executor) {
        this.shouldTick = true;
        this.tick = (int)(seconds * 20.0f);
        this.printValues = printValues;
        this.executor = executor;
        SheetsReader.devLogger("Client ticker object is now ticking down");
    }

    /**
     * Registers a countdown in seconds on the client side.<br>
     * Use {@link #startTicking(float, boolean, Runnable)} to initiate this method
     */
    public void registerCountdown() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || !shouldTick) return;
            this.tick--;
            if (tick <= 0) this.shouldTick = false;

            if (tick % 20 == 0 && tick > 0 && printValues) {
                client.player.sendMessage(new TranslatableText("chat.sheetsreader.refresh.countdown")
                        .append(new LiteralText(String.valueOf(tick / 20 + 1))), false);
            }
            if (tick <= 0) this.executor.run();
        });
    }
}
