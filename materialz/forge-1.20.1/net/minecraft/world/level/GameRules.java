package net.minecraft.world.level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public class GameRules {
   public static final int DEFAULT_RANDOM_TICK_SPEED = 3;
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<Key<?>, Type<?>> GAME_RULE_TYPES = Maps.newTreeMap(Comparator.comparing((p_46218_) -> p_46218_.id));
   public static final Key<BooleanValue> RULE_DOFIRETICK;
   public static final Key<BooleanValue> RULE_MOBGRIEFING;
   public static final Key<BooleanValue> RULE_KEEPINVENTORY;
   public static final Key<BooleanValue> RULE_DOMOBSPAWNING;
   public static final Key<BooleanValue> RULE_DOMOBLOOT;
   public static final Key<BooleanValue> RULE_DOBLOCKDROPS;
   public static final Key<BooleanValue> RULE_DOENTITYDROPS;
   public static final Key<BooleanValue> RULE_COMMANDBLOCKOUTPUT;
   public static final Key<BooleanValue> RULE_NATURAL_REGENERATION;
   public static final Key<BooleanValue> RULE_DAYLIGHT;
   public static final Key<BooleanValue> RULE_LOGADMINCOMMANDS;
   public static final Key<BooleanValue> RULE_SHOWDEATHMESSAGES;
   public static final Key<IntegerValue> RULE_RANDOMTICKING;
   public static final Key<BooleanValue> RULE_SENDCOMMANDFEEDBACK;
   public static final Key<BooleanValue> RULE_REDUCEDDEBUGINFO;
   public static final Key<BooleanValue> RULE_SPECTATORSGENERATECHUNKS;
   public static final Key<IntegerValue> RULE_SPAWN_RADIUS;
   public static final Key<BooleanValue> RULE_DISABLE_ELYTRA_MOVEMENT_CHECK;
   public static final Key<IntegerValue> RULE_MAX_ENTITY_CRAMMING;
   public static final Key<BooleanValue> RULE_WEATHER_CYCLE;
   public static final Key<BooleanValue> RULE_LIMITED_CRAFTING;
   public static final Key<IntegerValue> RULE_MAX_COMMAND_CHAIN_LENGTH;
   public static final Key<IntegerValue> RULE_COMMAND_MODIFICATION_BLOCK_LIMIT;
   public static final Key<BooleanValue> RULE_ANNOUNCE_ADVANCEMENTS;
   public static final Key<BooleanValue> RULE_DISABLE_RAIDS;
   public static final Key<BooleanValue> RULE_DOINSOMNIA;
   public static final Key<BooleanValue> RULE_DO_IMMEDIATE_RESPAWN;
   public static final Key<BooleanValue> RULE_DROWNING_DAMAGE;
   public static final Key<BooleanValue> RULE_FALL_DAMAGE;
   public static final Key<BooleanValue> RULE_FIRE_DAMAGE;
   public static final Key<BooleanValue> RULE_FREEZE_DAMAGE;
   public static final Key<BooleanValue> RULE_DO_PATROL_SPAWNING;
   public static final Key<BooleanValue> RULE_DO_TRADER_SPAWNING;
   public static final Key<BooleanValue> RULE_DO_WARDEN_SPAWNING;
   public static final Key<BooleanValue> RULE_FORGIVE_DEAD_PLAYERS;
   public static final Key<BooleanValue> RULE_UNIVERSAL_ANGER;
   public static final Key<IntegerValue> RULE_PLAYERS_SLEEPING_PERCENTAGE;
   public static final Key<BooleanValue> RULE_BLOCK_EXPLOSION_DROP_DECAY;
   public static final Key<BooleanValue> RULE_MOB_EXPLOSION_DROP_DECAY;
   public static final Key<BooleanValue> RULE_TNT_EXPLOSION_DROP_DECAY;
   public static final Key<IntegerValue> RULE_SNOW_ACCUMULATION_HEIGHT;
   public static final Key<BooleanValue> RULE_WATER_SOURCE_CONVERSION;
   public static final Key<BooleanValue> RULE_LAVA_SOURCE_CONVERSION;
   public static final Key<BooleanValue> RULE_GLOBAL_SOUND_EVENTS;
   public static final Key<BooleanValue> RULE_DO_VINES_SPREAD;
   private final Map<Key<?>, Value<?>> rules;

   public static <T extends Value<T>> Key<T> register(String p_46190_, Category p_46191_, Type<T> p_46192_) {
      Key<T> $$3 = new Key<T>(p_46190_, p_46191_);
      Type<?> $$4 = (Type)GAME_RULE_TYPES.put($$3, p_46192_);
      if ($$4 != null) {
         throw new IllegalStateException("Duplicate game rule registration for " + p_46190_);
      } else {
         return $$3;
      }
   }

   public GameRules(DynamicLike<?> p_46160_) {
      this();
      this.loadFromTag(p_46160_);
   }

   public GameRules() {
      this.rules = (Map)GAME_RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (p_46210_) -> ((Type)p_46210_.getValue()).createRule()));
   }

   private GameRules(Map<Key<?>, Value<?>> p_46162_) {
      this.rules = p_46162_;
   }

   public <T extends Value<T>> T getRule(Key<T> p_46171_) {
      return (T)(this.rules.get(p_46171_));
   }

   public CompoundTag createTag() {
      CompoundTag $$0 = new CompoundTag();
      this.rules.forEach((p_46197_, p_46198_) -> $$0.putString(p_46197_.id, p_46198_.serialize()));
      return $$0;
   }

   private void loadFromTag(DynamicLike<?> p_46184_) {
      this.rules.forEach((p_46187_, p_46188_) -> {
         Optional var10000 = p_46184_.get(p_46187_.id).asString().result();
         Objects.requireNonNull(p_46188_);
         var10000.ifPresent(p_46188_::deserialize);
      });
   }

   public GameRules copy() {
      return new GameRules((Map)this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (p_46194_) -> ((Value)p_46194_.getValue()).copy())));
   }

   public static void visitGameRuleTypes(GameRuleTypeVisitor p_46165_) {
      GAME_RULE_TYPES.forEach((p_46205_, p_46206_) -> callVisitorCap(p_46165_, p_46205_, p_46206_));
   }

   private static <T extends Value<T>> void callVisitorCap(GameRuleTypeVisitor p_46167_, Key<?> p_46168_, Type<?> p_46169_) {
      p_46167_.visit(p_46168_, p_46169_);
      p_46169_.callVisitor(p_46167_, p_46168_);
   }

   public void assignFrom(GameRules p_46177_, @Nullable MinecraftServer p_46178_) {
      p_46177_.rules.keySet().forEach((p_46182_) -> this.assignCap(p_46182_, p_46177_, p_46178_));
   }

   private <T extends Value<T>> void assignCap(Key<T> p_46173_, GameRules p_46174_, @Nullable MinecraftServer p_46175_) {
      T $$3 = p_46174_.getRule(p_46173_);
      this.getRule(p_46173_).setFrom($$3, p_46175_);
   }

   public boolean getBoolean(Key<BooleanValue> p_46208_) {
      return ((BooleanValue)this.getRule(p_46208_)).get();
   }

   public int getInt(Key<IntegerValue> p_46216_) {
      return ((IntegerValue)this.getRule(p_46216_)).get();
   }

   static {
      RULE_DOFIRETICK = register("doFireTick", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
      RULE_MOBGRIEFING = register("mobGriefing", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
      RULE_KEEPINVENTORY = register("keepInventory", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
      RULE_DOMOBSPAWNING = register("doMobSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
      RULE_DOMOBLOOT = register("doMobLoot", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));
      RULE_DOBLOCKDROPS = register("doTileDrops", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));
      RULE_DOENTITYDROPS = register("doEntityDrops", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));
      RULE_COMMANDBLOCKOUTPUT = register("commandBlockOutput", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
      RULE_NATURAL_REGENERATION = register("naturalRegeneration", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
      RULE_DAYLIGHT = register("doDaylightCycle", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
      RULE_LOGADMINCOMMANDS = register("logAdminCommands", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
      RULE_SHOWDEATHMESSAGES = register("showDeathMessages", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
      RULE_RANDOMTICKING = register("randomTickSpeed", GameRules.Category.UPDATES, GameRules.IntegerValue.create(3));
      RULE_SENDCOMMANDFEEDBACK = register("sendCommandFeedback", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
      RULE_REDUCEDDEBUGINFO = register("reducedDebugInfo", GameRules.Category.MISC, GameRules.BooleanValue.create(false, (p_46212_, p_46213_) -> {
         byte $$2 = (byte)(p_46213_.get() ? 22 : 23);

         for(ServerPlayer $$3 : p_46212_.getPlayerList().getPlayers()) {
            $$3.connection.send(new ClientboundEntityEventPacket($$3, $$2));
         }

      }));
      RULE_SPECTATORSGENERATECHUNKS = register("spectatorsGenerateChunks", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
      RULE_SPAWN_RADIUS = register("spawnRadius", GameRules.Category.PLAYER, GameRules.IntegerValue.create(10));
      RULE_DISABLE_ELYTRA_MOVEMENT_CHECK = register("disableElytraMovementCheck", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
      RULE_MAX_ENTITY_CRAMMING = register("maxEntityCramming", GameRules.Category.MOBS, GameRules.IntegerValue.create(24));
      RULE_WEATHER_CYCLE = register("doWeatherCycle", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
      RULE_LIMITED_CRAFTING = register("doLimitedCrafting", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
      RULE_MAX_COMMAND_CHAIN_LENGTH = register("maxCommandChainLength", GameRules.Category.MISC, GameRules.IntegerValue.create(65536));
      RULE_COMMAND_MODIFICATION_BLOCK_LIMIT = register("commandModificationBlockLimit", GameRules.Category.MISC, GameRules.IntegerValue.create(32768));
      RULE_ANNOUNCE_ADVANCEMENTS = register("announceAdvancements", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
      RULE_DISABLE_RAIDS = register("disableRaids", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
      RULE_DOINSOMNIA = register("doInsomnia", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
      RULE_DO_IMMEDIATE_RESPAWN = register("doImmediateRespawn", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false, (p_46200_, p_46201_) -> {
         for(ServerPlayer $$2 : p_46200_.getPlayerList().getPlayers()) {
            $$2.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.IMMEDIATE_RESPAWN, p_46201_.get() ? 1.0F : 0.0F));
         }

      }));
      RULE_DROWNING_DAMAGE = register("drowningDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
      RULE_FALL_DAMAGE = register("fallDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
      RULE_FIRE_DAMAGE = register("fireDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
      RULE_FREEZE_DAMAGE = register("freezeDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
      RULE_DO_PATROL_SPAWNING = register("doPatrolSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
      RULE_DO_TRADER_SPAWNING = register("doTraderSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
      RULE_DO_WARDEN_SPAWNING = register("doWardenSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
      RULE_FORGIVE_DEAD_PLAYERS = register("forgiveDeadPlayers", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
      RULE_UNIVERSAL_ANGER = register("universalAnger", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
      RULE_PLAYERS_SLEEPING_PERCENTAGE = register("playersSleepingPercentage", GameRules.Category.PLAYER, GameRules.IntegerValue.create(100));
      RULE_BLOCK_EXPLOSION_DROP_DECAY = register("blockExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));
      RULE_MOB_EXPLOSION_DROP_DECAY = register("mobExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));
      RULE_TNT_EXPLOSION_DROP_DECAY = register("tntExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanValue.create(false));
      RULE_SNOW_ACCUMULATION_HEIGHT = register("snowAccumulationHeight", GameRules.Category.UPDATES, GameRules.IntegerValue.create(1));
      RULE_WATER_SOURCE_CONVERSION = register("waterSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
      RULE_LAVA_SOURCE_CONVERSION = register("lavaSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanValue.create(false));
      RULE_GLOBAL_SOUND_EVENTS = register("globalSoundEvents", GameRules.Category.MISC, GameRules.BooleanValue.create(true));
      RULE_DO_VINES_SPREAD = register("doVinesSpread", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
   }

   public static enum Category {
      PLAYER("gamerule.category.player"),
      MOBS("gamerule.category.mobs"),
      SPAWNING("gamerule.category.spawning"),
      DROPS("gamerule.category.drops"),
      UPDATES("gamerule.category.updates"),
      CHAT("gamerule.category.chat"),
      MISC("gamerule.category.misc");

      private final String descriptionId;

      private Category(String p_46273_) {
         this.descriptionId = p_46273_;
      }

      public String getDescriptionId() {
         return this.descriptionId;
      }

      // $FF: synthetic method
      private static Category[] $values() {
         return new Category[]{PLAYER, MOBS, SPAWNING, DROPS, UPDATES, CHAT, MISC};
      }
   }

   public interface GameRuleTypeVisitor {
      default <T extends Value<T>> void visit(Key<T> p_46278_, Type<T> p_46279_) {
      }

      default void visitBoolean(Key<BooleanValue> p_46280_, Type<BooleanValue> p_46281_) {
      }

      default void visitInteger(Key<IntegerValue> p_46282_, Type<IntegerValue> p_46283_) {
      }
   }

   public static final class Key<T extends Value<T>> {
      final String id;
      private final Category category;

      public Key(String p_46326_, Category p_46327_) {
         this.id = p_46326_;
         this.category = p_46327_;
      }

      public String toString() {
         return this.id;
      }

      public boolean equals(Object p_46334_) {
         if (this == p_46334_) {
            return true;
         } else {
            return p_46334_ instanceof Key && ((Key)p_46334_).id.equals(this.id);
         }
      }

      public int hashCode() {
         return this.id.hashCode();
      }

      public String getId() {
         return this.id;
      }

      public String getDescriptionId() {
         return "gamerule." + this.id;
      }

      public Category getCategory() {
         return this.category;
      }
   }

   public static class Type<T extends Value<T>> {
      private final Supplier<ArgumentType<?>> argument;
      private final Function<Type<T>, T> constructor;
      final BiConsumer<MinecraftServer, T> callback;
      private final VisitorCaller<T> visitorCaller;

      Type(Supplier<ArgumentType<?>> p_46342_, Function<Type<T>, T> p_46343_, BiConsumer<MinecraftServer, T> p_46344_, VisitorCaller<T> p_46345_) {
         this.argument = p_46342_;
         this.constructor = p_46343_;
         this.callback = p_46344_;
         this.visitorCaller = p_46345_;
      }

      public RequiredArgumentBuilder<CommandSourceStack, ?> createArgument(String p_46359_) {
         return Commands.argument(p_46359_, (ArgumentType)this.argument.get());
      }

      public T createRule() {
         return (T)(this.constructor.apply(this));
      }

      public void callVisitor(GameRuleTypeVisitor p_46354_, Key<T> p_46355_) {
         this.visitorCaller.call(p_46354_, p_46355_, this);
      }
   }

   public abstract static class Value<T extends Value<T>> {
      protected final Type<T> type;

      public Value(Type<T> p_46362_) {
         this.type = p_46362_;
      }

      protected abstract void updateFromArgument(CommandContext<CommandSourceStack> var1, String var2);

      public void setFromArgument(CommandContext<CommandSourceStack> p_46371_, String p_46372_) {
         this.updateFromArgument(p_46371_, p_46372_);
         this.onChanged(((CommandSourceStack)p_46371_.getSource()).getServer());
      }

      protected void onChanged(@Nullable MinecraftServer p_46369_) {
         if (p_46369_ != null) {
            this.type.callback.accept(p_46369_, this.getSelf());
         }

      }

      protected abstract void deserialize(String var1);

      public abstract String serialize();

      public String toString() {
         return this.serialize();
      }

      public abstract int getCommandResult();

      protected abstract T getSelf();

      protected abstract T copy();

      public abstract void setFrom(T var1, @Nullable MinecraftServer var2);
   }

   public static class IntegerValue extends Value<IntegerValue> {
      private int value;

      public static Type<IntegerValue> create(int p_46295_, BiConsumer<MinecraftServer, IntegerValue> p_46296_) {
         return new Type<IntegerValue>(IntegerArgumentType::integer, (p_46293_) -> new IntegerValue(p_46293_, p_46295_), p_46296_, GameRuleTypeVisitor::visitInteger);
      }

      public static Type<IntegerValue> create(int p_46313_) {
         return create(p_46313_, (p_46309_, p_46310_) -> {
         });
      }

      public IntegerValue(Type<IntegerValue> p_46286_, int p_46287_) {
         super(p_46286_);
         this.value = p_46287_;
      }

      protected void updateFromArgument(CommandContext<CommandSourceStack> p_46304_, String p_46305_) {
         this.value = IntegerArgumentType.getInteger(p_46304_, p_46305_);
      }

      public int get() {
         return this.value;
      }

      public void set(int p_151490_, @Nullable MinecraftServer p_151491_) {
         this.value = p_151490_;
         this.onChanged(p_151491_);
      }

      public String serialize() {
         return Integer.toString(this.value);
      }

      protected void deserialize(String p_46307_) {
         this.value = safeParse(p_46307_);
      }

      public boolean tryDeserialize(String p_46315_) {
         try {
            this.value = Integer.parseInt(p_46315_);
            return true;
         } catch (NumberFormatException var3) {
            return false;
         }
      }

      private static int safeParse(String p_46318_) {
         if (!p_46318_.isEmpty()) {
            try {
               return Integer.parseInt(p_46318_);
            } catch (NumberFormatException var2) {
               GameRules.LOGGER.warn("Failed to parse integer {}", p_46318_);
            }
         }

         return 0;
      }

      public int getCommandResult() {
         return this.value;
      }

      protected IntegerValue getSelf() {
         return this;
      }

      protected IntegerValue copy() {
         return new IntegerValue(this.type, this.value);
      }

      public void setFrom(IntegerValue p_46298_, @Nullable MinecraftServer p_46299_) {
         this.value = p_46298_.value;
         this.onChanged(p_46299_);
      }

      // $FF: synthetic method
      protected Value copy() {
         return this.copy();
      }

      // $FF: synthetic method
      protected Value getSelf() {
         return this.getSelf();
      }
   }

   public static class BooleanValue extends Value<BooleanValue> {
      private boolean value;

      public static Type<BooleanValue> create(boolean p_46253_, BiConsumer<MinecraftServer, BooleanValue> p_46254_) {
         return new Type<BooleanValue>(BoolArgumentType::bool, (p_46242_) -> new BooleanValue(p_46242_, p_46253_), p_46254_, GameRuleTypeVisitor::visitBoolean);
      }

      public static Type<BooleanValue> create(boolean p_46251_) {
         return create(p_46251_, (p_46236_, p_46237_) -> {
         });
      }

      public BooleanValue(Type<BooleanValue> p_46221_, boolean p_46222_) {
         super(p_46221_);
         this.value = p_46222_;
      }

      protected void updateFromArgument(CommandContext<CommandSourceStack> p_46231_, String p_46232_) {
         this.value = BoolArgumentType.getBool(p_46231_, p_46232_);
      }

      public boolean get() {
         return this.value;
      }

      public void set(boolean p_46247_, @Nullable MinecraftServer p_46248_) {
         this.value = p_46247_;
         this.onChanged(p_46248_);
      }

      public String serialize() {
         return Boolean.toString(this.value);
      }

      protected void deserialize(String p_46234_) {
         this.value = Boolean.parseBoolean(p_46234_);
      }

      public int getCommandResult() {
         return this.value ? 1 : 0;
      }

      protected BooleanValue getSelf() {
         return this;
      }

      protected BooleanValue copy() {
         return new BooleanValue(this.type, this.value);
      }

      public void setFrom(BooleanValue p_46225_, @Nullable MinecraftServer p_46226_) {
         this.value = p_46225_.value;
         this.onChanged(p_46226_);
      }

      // $FF: synthetic method
      protected Value copy() {
         return this.copy();
      }

      // $FF: synthetic method
      protected Value getSelf() {
         return this.getSelf();
      }
   }

   interface VisitorCaller<T extends Value<T>> {
      void call(GameRuleTypeVisitor var1, Key<T> var2, Type<T> var3);
   }
}
