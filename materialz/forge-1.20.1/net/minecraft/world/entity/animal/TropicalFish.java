package net.minecraft.world.entity.animal;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class TropicalFish extends AbstractSchoolingFish implements VariantHolder<Pattern> {
   public static final String BUCKET_VARIANT_TAG = "BucketVariantTag";
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT;
   public static final List<Variant> COMMON_VARIANTS;
   private boolean isSchool = true;

   public TropicalFish(EntityType<? extends TropicalFish> p_30015_, Level p_30016_) {
      super(p_30015_, p_30016_);
   }

   public static String getPredefinedName(int p_30031_) {
      return "entity.minecraft.tropical_fish.predefined." + p_30031_;
   }

   static int packVariant(Pattern p_262598_, DyeColor p_262618_, DyeColor p_262683_) {
      return p_262598_.getPackedId() & '\uffff' | (p_262618_.getId() & 255) << 16 | (p_262683_.getId() & 255) << 24;
   }

   public static DyeColor getBaseColor(int p_30051_) {
      return DyeColor.byId(p_30051_ >> 16 & 255);
   }

   public static DyeColor getPatternColor(int p_30053_) {
      return DyeColor.byId(p_30053_ >> 24 & 255);
   }

   public static Pattern getPattern(int p_262604_) {
      return TropicalFish.Pattern.byId(p_262604_ & '\uffff');
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
   }

   public void addAdditionalSaveData(CompoundTag p_30033_) {
      super.addAdditionalSaveData(p_30033_);
      p_30033_.putInt("Variant", this.getPackedVariant());
   }

   public void readAdditionalSaveData(CompoundTag p_30029_) {
      super.readAdditionalSaveData(p_30029_);
      this.setPackedVariant(p_30029_.getInt("Variant"));
   }

   private void setPackedVariant(int p_30057_) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, p_30057_);
   }

   public boolean isMaxGroupSizeReached(int p_30035_) {
      return !this.isSchool;
   }

   private int getPackedVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public DyeColor getBaseColor() {
      return getBaseColor(this.getPackedVariant());
   }

   public DyeColor getPatternColor() {
      return getPatternColor(this.getPackedVariant());
   }

   public Pattern getVariant() {
      return getPattern(this.getPackedVariant());
   }

   public void setVariant(Pattern p_262594_) {
      int $$1 = this.getPackedVariant();
      DyeColor $$2 = getBaseColor($$1);
      DyeColor $$3 = getPatternColor($$1);
      this.setPackedVariant(packVariant(p_262594_, $$2, $$3));
   }

   public void saveToBucketTag(ItemStack p_30049_) {
      super.saveToBucketTag(p_30049_);
      CompoundTag $$1 = p_30049_.getOrCreateTag();
      $$1.putInt("BucketVariantTag", this.getPackedVariant());
   }

   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.TROPICAL_FISH_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.TROPICAL_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.TROPICAL_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_30039_) {
      return SoundEvents.TROPICAL_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.TROPICAL_FISH_FLOP;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_30023_, DifficultyInstance p_30024_, MobSpawnType p_30025_, @Nullable SpawnGroupData p_30026_, @Nullable CompoundTag p_30027_) {
      p_30026_ = super.finalizeSpawn(p_30023_, p_30024_, p_30025_, p_30026_, p_30027_);
      if (p_30025_ == MobSpawnType.BUCKET && p_30027_ != null && p_30027_.contains("BucketVariantTag", 3)) {
         this.setPackedVariant(p_30027_.getInt("BucketVariantTag"));
         return p_30026_;
      } else {
         RandomSource $$5 = p_30023_.getRandom();
         Variant $$7;
         if (p_30026_ instanceof TropicalFishGroupData) {
            TropicalFishGroupData $$6 = (TropicalFishGroupData)p_30026_;
            $$7 = $$6.variant;
         } else if ((double)$$5.nextFloat() < 0.9) {
            $$7 = (Variant)Util.getRandom(COMMON_VARIANTS, $$5);
            p_30026_ = new TropicalFishGroupData(this, $$7);
         } else {
            this.isSchool = false;
            Pattern[] $$9 = TropicalFish.Pattern.values();
            DyeColor[] $$10 = DyeColor.values();
            Pattern $$11 = (Pattern)Util.getRandom($$9, $$5);
            DyeColor $$12 = (DyeColor)Util.getRandom($$10, $$5);
            DyeColor $$13 = (DyeColor)Util.getRandom($$10, $$5);
            $$7 = new Variant($$11, $$12, $$13);
         }

         this.setPackedVariant($$7.getPackedId());
         return p_30026_;
      }
   }

   public static boolean checkTropicalFishSpawnRules(EntityType<TropicalFish> p_218267_, LevelAccessor p_218268_, MobSpawnType p_218269_, BlockPos p_218270_, RandomSource p_218271_) {
      return p_218268_.getFluidState(p_218270_.below()).is(FluidTags.WATER) && p_218268_.getBlockState(p_218270_.above()).is(Blocks.WATER) && (p_218268_.getBiome(p_218270_).is(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT) || WaterAnimal.checkSurfaceWaterAnimalSpawnRules(p_218267_, p_218268_, p_218269_, p_218270_, p_218271_));
   }

   // $FF: synthetic method
   public Object getVariant() {
      return this.getVariant();
   }

   static {
      DATA_ID_TYPE_VARIANT = SynchedEntityData.<Integer>defineId(TropicalFish.class, EntityDataSerializers.INT);
      COMMON_VARIANTS = List.of(new Variant(TropicalFish.Pattern.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), new Variant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), new Variant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), new Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), new Variant(TropicalFish.Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), new Variant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE), new Variant(TropicalFish.Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), new Variant(TropicalFish.Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), new Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED), new Variant(TropicalFish.Pattern.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), new Variant(TropicalFish.Pattern.GLITTER, DyeColor.WHITE, DyeColor.GRAY), new Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), new Variant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.PINK), new Variant(TropicalFish.Pattern.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), new Variant(TropicalFish.Pattern.BETTY, DyeColor.RED, DyeColor.WHITE), new Variant(TropicalFish.Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED), new Variant(TropicalFish.Pattern.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), new Variant(TropicalFish.Pattern.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), new Variant(TropicalFish.Pattern.KOB, DyeColor.RED, DyeColor.WHITE), new Variant(TropicalFish.Pattern.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), new Variant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW), new Variant(TropicalFish.Pattern.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW));
   }

   public static enum Base {
      SMALL(0),
      LARGE(1);

      final int id;

      private Base(int p_262648_) {
         this.id = p_262648_;
      }

      // $FF: synthetic method
      private static Base[] $values() {
         return new Base[]{SMALL, LARGE};
      }
   }

   public static record Variant(Pattern pattern, DyeColor baseColor, DyeColor patternColor) {
      public Variant(Pattern pattern, DyeColor baseColor, DyeColor patternColor) {
         this.pattern = pattern;
         this.baseColor = baseColor;
         this.patternColor = patternColor;
      }

      public int getPackedId() {
         return TropicalFish.packVariant(this.pattern, this.baseColor, this.patternColor);
      }
   }

   public static enum Pattern implements StringRepresentable {
      KOB("kob", TropicalFish.Base.SMALL, 0),
      SUNSTREAK("sunstreak", TropicalFish.Base.SMALL, 1),
      SNOOPER("snooper", TropicalFish.Base.SMALL, 2),
      DASHER("dasher", TropicalFish.Base.SMALL, 3),
      BRINELY("brinely", TropicalFish.Base.SMALL, 4),
      SPOTTY("spotty", TropicalFish.Base.SMALL, 5),
      FLOPPER("flopper", TropicalFish.Base.LARGE, 0),
      STRIPEY("stripey", TropicalFish.Base.LARGE, 1),
      GLITTER("glitter", TropicalFish.Base.LARGE, 2),
      BLOCKFISH("blockfish", TropicalFish.Base.LARGE, 3),
      BETTY("betty", TropicalFish.Base.LARGE, 4),
      CLAYFISH("clayfish", TropicalFish.Base.LARGE, 5);

      public static final Codec<Pattern> CODEC = StringRepresentable.<Pattern>fromEnum(Pattern::values);
      private static final IntFunction<Pattern> BY_ID = ByIdMap.<Pattern>sparse(Pattern::getPackedId, values(), KOB);
      private final String name;
      private final Component displayName;
      private final Base base;
      private final int packedId;

      private Pattern(String p_262625_, Base p_262680_, int p_262584_) {
         this.name = p_262625_;
         this.base = p_262680_;
         this.packedId = p_262680_.id | p_262584_ << 8;
         this.displayName = Component.translatable("entity.minecraft.tropical_fish.type." + this.name);
      }

      public static Pattern byId(int p_262653_) {
         return (Pattern)BY_ID.apply(p_262653_);
      }

      public Base base() {
         return this.base;
      }

      public int getPackedId() {
         return this.packedId;
      }

      public String getSerializedName() {
         return this.name;
      }

      public Component displayName() {
         return this.displayName;
      }

      // $FF: synthetic method
      private static Pattern[] $values() {
         return new Pattern[]{KOB, SUNSTREAK, SNOOPER, DASHER, BRINELY, SPOTTY, FLOPPER, STRIPEY, GLITTER, BLOCKFISH, BETTY, CLAYFISH};
      }
   }

   static class TropicalFishGroupData extends AbstractSchoolingFish.SchoolSpawnGroupData {
      final Variant variant;

      TropicalFishGroupData(TropicalFish p_262626_, Variant p_262579_) {
         super(p_262626_);
         this.variant = p_262579_;
      }
   }
}
