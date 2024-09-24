package net.shirojr.sheetsreader.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.shirojr.sheetsreader.network.SheetsS2CNetworking;
import net.shirojr.sheetsreader.sheet.SheetsElement;
import net.shirojr.sheetsreader.util.SheetsReaderUtil;

import java.util.Collection;
import java.util.List;

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
        if (targets.isEmpty()) {
            context.getSource().sendFeedback(new TranslatableText("feedback.sheetsreader.player.error"), true);
            return -1;
        }
        for (ServerPlayerEntity target : targets) {
            List<SheetsElement> elements = SheetsReaderUtil.getDataFromApi();
            if (elements == null) {
                context.getSource().sendFeedback(new TranslatableText("feedback.sheetsreader.data.empty", target.getName()), true);
                continue;
            }
            NbtCompound compound = SheetsElement.toNbt(elements, new NbtCompound());
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeNbt(compound);
            ServerPlayNetworking.send(target, SheetsS2CNetworking.REFRESH_SOURCE_CHANNEL, buf);
        }

        context.getSource().sendFeedback(new TranslatableText("feedback.sheetsreader.player.sent"), true);
        return 1;
    }
}
