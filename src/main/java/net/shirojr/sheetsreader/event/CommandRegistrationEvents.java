package net.shirojr.sheetsreader.event;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.shirojr.sheetsreader.command.ForceListReloadCommand;

public class CommandRegistrationEvents {
    private CommandRegistrationEvents() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register(ForceListReloadCommand::register);
    }
}
