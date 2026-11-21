package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class AttributeInstance {
   private final Attribute attribute;
   private final Map<AttributeModifier.Operation, Set<AttributeModifier>> modifiersByOperation = Maps.newEnumMap(AttributeModifier.Operation.class);
   private final Map<UUID, AttributeModifier> modifierById = new Object2ObjectArrayMap();
   private final Set<AttributeModifier> permanentModifiers = new ObjectArraySet();
   private double baseValue;
   private boolean dirty = true;
   private double cachedValue;
   private final Consumer<AttributeInstance> onDirty;

   public AttributeInstance(Attribute p_22097_, Consumer<AttributeInstance> p_22098_) {
      this.attribute = p_22097_;
      this.onDirty = p_22098_;
      this.baseValue = p_22097_.getDefaultValue();
   }

   public Attribute getAttribute() {
      return this.attribute;
   }

   public double getBaseValue() {
      return this.baseValue;
   }

   public void setBaseValue(double p_22101_) {
      if (p_22101_ != this.baseValue) {
         this.baseValue = p_22101_;
         this.setDirty();
      }
   }

   public Set<AttributeModifier> getModifiers(AttributeModifier.Operation p_22105_) {
      return (Set)this.modifiersByOperation.computeIfAbsent(p_22105_, (p_22124_) -> Sets.newHashSet());
   }

   public Set<AttributeModifier> getModifiers() {
      return ImmutableSet.copyOf(this.modifierById.values());
   }

   @Nullable
   public AttributeModifier getModifier(UUID p_22112_) {
      return (AttributeModifier)this.modifierById.get(p_22112_);
   }

   public boolean hasModifier(AttributeModifier p_22110_) {
      return this.modifierById.get(p_22110_.getId()) != null;
   }

   private void addModifier(AttributeModifier p_22134_) {
      AttributeModifier $$1 = (AttributeModifier)this.modifierById.putIfAbsent(p_22134_.getId(), p_22134_);
      if ($$1 != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         this.getModifiers(p_22134_.getOperation()).add(p_22134_);
         this.setDirty();
      }
   }

   public void addTransientModifier(AttributeModifier p_22119_) {
      this.addModifier(p_22119_);
   }

   public void addPermanentModifier(AttributeModifier p_22126_) {
      this.addModifier(p_22126_);
      this.permanentModifiers.add(p_22126_);
   }

   protected void setDirty() {
      this.dirty = true;
      this.onDirty.accept(this);
   }

   public void removeModifier(AttributeModifier p_22131_) {
      this.getModifiers(p_22131_.getOperation()).remove(p_22131_);
      this.modifierById.remove(p_22131_.getId());
      this.permanentModifiers.remove(p_22131_);
      this.setDirty();
   }

   public void removeModifier(UUID p_22121_) {
      AttributeModifier $$1 = this.getModifier(p_22121_);
      if ($$1 != null) {
         this.removeModifier($$1);
      }

   }

   public boolean removePermanentModifier(UUID p_22128_) {
      AttributeModifier $$1 = this.getModifier(p_22128_);
      if ($$1 != null && this.permanentModifiers.contains($$1)) {
         this.removeModifier($$1);
         return true;
      } else {
         return false;
      }
   }

   public void removeModifiers() {
      for(AttributeModifier $$0 : this.getModifiers()) {
         this.removeModifier($$0);
      }

   }

   public double getValue() {
      if (this.dirty) {
         this.cachedValue = this.calculateValue();
         this.dirty = false;
      }

      return this.cachedValue;
   }

   private double calculateValue() {
      double $$0 = this.getBaseValue();

      for(AttributeModifier $$1 : this.getModifiersOrEmpty(AttributeModifier.Operation.ADDITION)) {
         $$0 += $$1.getAmount();
      }

      double $$2 = $$0;

      for(AttributeModifier $$3 : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_BASE)) {
         $$2 += $$0 * $$3.getAmount();
      }

      for(AttributeModifier $$4 : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
         $$2 *= (double)1.0F + $$4.getAmount();
      }

      return this.attribute.sanitizeValue($$2);
   }

   private Collection<AttributeModifier> getModifiersOrEmpty(AttributeModifier.Operation p_22117_) {
      return (Collection)this.modifiersByOperation.getOrDefault(p_22117_, Collections.emptySet());
   }

   public void replaceFrom(AttributeInstance p_22103_) {
      this.baseValue = p_22103_.baseValue;
      this.modifierById.clear();
      this.modifierById.putAll(p_22103_.modifierById);
      this.permanentModifiers.clear();
      this.permanentModifiers.addAll(p_22103_.permanentModifiers);
      this.modifiersByOperation.clear();
      p_22103_.modifiersByOperation.forEach((p_22107_, p_22108_) -> this.getModifiers(p_22107_).addAll(p_22108_));
      this.setDirty();
   }

   public CompoundTag save() {
      CompoundTag $$0 = new CompoundTag();
      $$0.putString("Name", BuiltInRegistries.ATTRIBUTE.getKey(this.attribute).toString());
      $$0.putDouble("Base", this.baseValue);
      if (!this.permanentModifiers.isEmpty()) {
         ListTag $$1 = new ListTag();

         for(AttributeModifier $$2 : this.permanentModifiers) {
            $$1.add($$2.save());
         }

         $$0.put("Modifiers", $$1);
      }

      return $$0;
   }

   public void load(CompoundTag p_22114_) {
      this.baseValue = p_22114_.getDouble("Base");
      if (p_22114_.contains("Modifiers", 9)) {
         ListTag $$1 = p_22114_.getList("Modifiers", 10);

         for(int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            AttributeModifier $$3 = AttributeModifier.load($$1.getCompound($$2));
            if ($$3 != null) {
               this.modifierById.put($$3.getId(), $$3);
               this.getModifiers($$3.getOperation()).add($$3);
               this.permanentModifiers.add($$3);
            }
         }
      }

      this.setDirty();
   }
}
