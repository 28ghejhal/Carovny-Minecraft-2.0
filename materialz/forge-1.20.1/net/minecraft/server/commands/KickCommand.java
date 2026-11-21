package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class KickCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_137796_) {
      p_137796_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("kick").requires((p_137800_) -> p_137800_.hasPermission(3))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((p_137806_) -> kickPlayers((CommandSourceStack)p_137806_.getSource(), EntityArgument.getPlayers(p_137806_, "targets"), Component.translatable("multiplayer.disconnect.kicked")))).then(Commands.argument("reason", MessageArgument.message()).executes((p_137798_) -> kickPlayers((CommandSourceStack)p_137798_.getSource(), EntityArgument.getPlayers(p_137798_, "targets"), MessageArgument.getMessage(p_137798_, "reason"))))));
   }

   private static int kickPlayers(CommandSourceStack p_137802_, Collection<ServerPlayer> p_137803_, Component p_137804_) {
      for(ServerPlayer $$3 : p_137803_) {
         $$3.connection.disconnect(p_137804_);
         p_137802_.sendSuccess(() -> Component.translatable("commands.kick.success", $$3.getDisplayName(), p_137804_), true);
      }

      return p_137803_.size();
   }
}
