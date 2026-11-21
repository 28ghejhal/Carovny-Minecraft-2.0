package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.FriendlyByteBuf;

public class IntegerArgumentInfo implements ArgumentTypeInfo<IntegerArgumentType, Template> {
   public void serializeToNetwork(Template p_235551_, FriendlyByteBuf p_235552_) {
      boolean $$2 = p_235551_.min != Integer.MIN_VALUE;
      boolean $$3 = p_235551_.max != Integer.MAX_VALUE;
      p_235552_.writeByte(ArgumentUtils.createNumberFlags($$2, $$3));
      if ($$2) {
         p_235552_.writeInt(p_235551_.min);
      }

      if ($$3) {
         p_235552_.writeInt(p_235551_.max);
      }

   }

   public Template deserializeFromNetwork(FriendlyByteBuf p_235554_) {
      byte $$1 = p_235554_.readByte();
      int $$2 = ArgumentUtils.numberHasMin($$1) ? p_235554_.readInt() : Integer.MIN_VALUE;
      int $$3 = ArgumentUtils.numberHasMax($$1) ? p_235554_.readInt() : Integer.MAX_VALUE;
      return new Template($$2, $$3);
   }

   public void serializeToJson(Template p_235548_, JsonObject p_235549_) {
      if (p_235548_.min != Integer.MIN_VALUE) {
         p_235549_.addProperty("min", p_235548_.min);
      }

      if (p_235548_.max != Integer.MAX_VALUE) {
         p_235549_.addProperty("max", p_235548_.max);
      }

   }

   public Template unpack(IntegerArgumentType p_235540_) {
      return new Template(p_235540_.getMinimum(), p_235540_.getMaximum());
   }

   // $FF: synthetic method
   public ArgumentTypeInfo.Template deserializeFromNetwork(FriendlyByteBuf var1) {
      return this.deserializeFromNetwork(var1);
   }

   public final class Template implements ArgumentTypeInfo.Template<IntegerArgumentType> {
      final int min;
      final int max;

      Template(int p_235562_, int p_235563_) {
         this.min = p_235562_;
         this.max = p_235563_;
      }

      public IntegerArgumentType instantiate(CommandBuildContext p_235566_) {
         return IntegerArgumentType.integer(this.min, this.max);
      }

      public ArgumentTypeInfo<IntegerArgumentType, ?> type() {
         return IntegerArgumentInfo.this;
      }

      // $FF: synthetic method
      public ArgumentType instantiate(CommandBuildContext var1) {
         return this.instantiate(var1);
      }
   }
}
