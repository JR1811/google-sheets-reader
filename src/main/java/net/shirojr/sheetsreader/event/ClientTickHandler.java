package net.shirojr.sheetsreader.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.shirojr.sheetsreader.SheetsReader;

/**
 * Creates an instance which handles Client sided ticks
 */
@Environment(EnvType.CLIENT)
public class ClientTickHandler {
    private int tick;
    private boolean shouldTick = false, printValues = false;
    private Runnable executor;

    /**
     * Starts the ticking of the {@link ClientTickHandler} instance.<br>
     * Make sure to have the {@link ClientTickEvents#END_CLIENT_TICK Event} registered already,
     * using the {@link #registerCountdown()} method.
     * @param seconds specifies the time until the {@link #executor} will be executed
     * @param printValues print the countdown values into the chat of the current client
     * @param executor pass over a {@linkplain  Runnable Functional Interface} which will be executed
     *                 after the countdown has ended
     */
    public void startTicking(float seconds, boolean printValues, Runnable executor) {
        this.shouldTick = true;
        this.tick = (int)(seconds * 20.0f);
        this.printValues = printValues;
        this.executor = executor;
        SheetsReader.devLogger("Client ticker object is now ticking down");
    }

    /**
     * Registers a countdown in seconds on the client side.<br>
     * Use {@link #startTicking(float, boolean, Runnable)} to start the countdown.
     */
    public void registerCountdown() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!shouldTick || client.player == null) return;
            this.tick--;

            if (tick % 20 == 0 && tick > 0 && printValues) {
                client.player.sendMessage(new TranslatableText("chat.sheetsreader.refresh.countdown")
                        .append(new LiteralText(String.valueOf(tick / 20 + 1))), false);
            }

            if (tick <= 0) {
                this.shouldTick = false;
                this.executor.run();
            }
        });
    }
}
