package net.shirojr.sheetsreader.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.shirojr.sheetsreader.network.SheetsS2CNetworking;

import java.util.Collection;

public class ForceListReloadCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("sheets").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(CommandManager.literal("refresh").then(CommandManager.argument("targets", EntityArgumentType.players())
                        .executes(context -> ForceListReloadCommand.runRefresh(context,
                                EntityArgumentType.getPlayers(context, "targets"))
                        )
                )));
    }

    private static int runRefresh(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets) throws CommandSyntaxException {
        if (targets.size() < 1) {
            context.getSource().sendFeedback(new TranslatableText("feedback.sheetsreader.player.error"), true);
            return -1;
        }
        for (var target : targets) {
            PacketByteBuf buf = PacketByteBufs.create();
            ServerPlayNetworking.send(target, SheetsS2CNetworking.REFRESH_SOURCE_CHANNEL, buf);
        }

        context.getSource().sendFeedback(new TranslatableText("feedback.sheetsreader.player.sent"), true);
        return 1;
    }
}
