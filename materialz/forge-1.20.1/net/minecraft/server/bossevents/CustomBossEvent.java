package net.minecraft.server.bossevents;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.UnaryOperator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

public class CustomBossEvent extends ServerBossEvent {
   private final ResourceLocation id;
   private final Set<UUID> players = Sets.newHashSet();
   private int value;
   private int max = 100;

   public CustomBossEvent(ResourceLocation p_136261_, Component p_136262_) {
      super(p_136262_, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS);
      this.id = p_136261_;
      this.setProgress(0.0F);
   }

   public ResourceLocation getTextId() {
      return this.id;
   }

   public void addPlayer(ServerPlayer p_136267_) {
      super.addPlayer(p_136267_);
      this.players.add(p_136267_.getUUID());
   }

   public void addOfflinePlayer(UUID p_136271_) {
      this.players.add(p_136271_);
   }

   public void removePlayer(ServerPlayer p_136281_) {
      super.removePlayer(p_136281_);
      this.players.remove(p_136281_.getUUID());
   }

   public void removeAllPlayers() {
      super.removeAllPlayers();
      this.players.clear();
   }

   public int getValue() {
      return this.value;
   }

   public int getMax() {
      return this.max;
   }

   public void setValue(int p_136265_) {
      this.value = p_136265_;
      this.setProgress(Mth.clamp((float)p_136265_ / (float)this.max, 0.0F, 1.0F));
   }

   public void setMax(int p_136279_) {
      this.max = p_136279_;
      this.setProgress(Mth.clamp((float)this.value / (float)p_136279_, 0.0F, 1.0F));
   }

   public final Component getDisplayName() {
      return ComponentUtils.wrapInSquareBrackets(this.getName()).withStyle((UnaryOperator)((p_136276_) -> p_136276_.withColor(this.getColor().getFormatting()).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(this.getTextId().toString()))).withInsertion(this.getTextId().toString())));
   }

   public boolean setPlayers(Collection<ServerPlayer> p_136269_) {
      Set<UUID> $$1 = Sets.newHashSet();
      Set<ServerPlayer> $$2 = Sets.newHashSet();

      for(UUID $$3 : this.players) {
         boolean $$4 = false;

         for(ServerPlayer $$5 : p_136269_) {
            if ($$5.getUUID().equals($$3)) {
               $$4 = true;
               break;
            }
         }

         if (!$$4) {
            $$1.add($$3);
         }
      }

      for(ServerPlayer $$6 : p_136269_) {
         boolean $$7 = false;

         for(UUID $$8 : this.players) {
            if ($$6.getUUID().equals($$8)) {
               $$7 = true;
               break;
            }
         }

         if (!$$7) {
            $$2.add($$6);
         }
      }

      for(UUID $$9 : $$1) {
         for(ServerPlayer $$10 : this.getPlayers()) {
            if ($$10.getUUID().equals($$9)) {
               this.removePlayer($$10);
               break;
            }
         }

         this.players.remove($$9);
      }

      for(ServerPlayer $$11 : $$2) {
         this.addPlayer($$11);
      }

      return !$$1.isEmpty() || !$$2.isEmpty();
   }

   public CompoundTag save() {
      CompoundTag $$0 = new CompoundTag();
      $$0.putString("Name", Component.Serializer.toJson(this.name));
      $$0.putBoolean("Visible", this.isVisible());
      $$0.putInt("Value", this.value);
      $$0.putInt("Max", this.max);
      $$0.putString("Color", this.getColor().getName());
      $$0.putString("Overlay", this.getOverlay().getName());
      $$0.putBoolean("DarkenScreen", this.shouldDarkenScreen());
      $$0.putBoolean("PlayBossMusic", this.shouldPlayBossMusic());
      $$0.putBoolean("CreateWorldFog", this.shouldCreateWorldFog());
      ListTag $$1 = new ListTag();

      for(UUID $$2 : this.players) {
         $$1.add(NbtUtils.createUUID($$2));
      }

      $$0.put("Players", $$1);
      return $$0;
   }

   public static CustomBossEvent load(CompoundTag p_136273_, ResourceLocation p_136274_) {
      CustomBossEvent $$2 = new CustomBossEvent(p_136274_, Component.Serializer.fromJson(p_136273_.getString("Name")));
      $$2.setVisible(p_136273_.getBoolean("Visible"));
      $$2.setValue(p_136273_.getInt("Value"));
      $$2.setMax(p_136273_.getInt("Max"));
      $$2.setColor(BossEvent.BossBarColor.byName(p_136273_.getString("Color")));
      $$2.setOverlay(BossEvent.BossBarOverlay.byName(p_136273_.getString("Overlay")));
      $$2.setDarkenScreen(p_136273_.getBoolean("DarkenScreen"));
      $$2.setPlayBossMusic(p_136273_.getBoolean("PlayBossMusic"));
      $$2.setCreateWorldFog(p_136273_.getBoolean("CreateWorldFog"));
      ListTag $$3 = p_136273_.getList("Players", 11);

      for(int $$4 = 0; $$4 < $$3.size(); ++$$4) {
         $$2.addOfflinePlayer(NbtUtils.loadUUID($$3.get($$4)));
      }

      return $$2;
   }

   public void onPlayerConnect(ServerPlayer p_136284_) {
      if (this.players.contains(p_136284_.getUUID())) {
         this.addPlayer(p_136284_);
      }

   }

   public void onPlayerDisconnect(ServerPlayer p_136287_) {
      super.removePlayer(p_136287_);
   }
}
