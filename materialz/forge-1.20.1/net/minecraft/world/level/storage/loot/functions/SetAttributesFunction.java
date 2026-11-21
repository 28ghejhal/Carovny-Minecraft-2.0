package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetAttributesFunction extends LootItemConditionalFunction {
   final List<Modifier> modifiers;

   SetAttributesFunction(LootItemCondition[] p_80833_, List<Modifier> p_80834_) {
      super(p_80833_);
      this.modifiers = ImmutableList.copyOf(p_80834_);
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_ATTRIBUTES;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return (Set)this.modifiers.stream().flatMap((p_279080_) -> p_279080_.amount.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
   }

   public ItemStack run(ItemStack p_80840_, LootContext p_80841_) {
      RandomSource $$2 = p_80841_.getRandom();

      for(Modifier $$3 : this.modifiers) {
         UUID $$4 = $$3.id;
         if ($$4 == null) {
            $$4 = UUID.randomUUID();
         }

         EquipmentSlot $$5 = (EquipmentSlot)Util.getRandom($$3.slots, $$2);
         p_80840_.addAttributeModifier($$3.attribute, new AttributeModifier($$4, $$3.name, (double)$$3.amount.getFloat(p_80841_), $$3.operation), $$5);
      }

      return p_80840_;
   }

   public static ModifierBuilder modifier(String p_165236_, Attribute p_165237_, AttributeModifier.Operation p_165238_, NumberProvider p_165239_) {
      return new ModifierBuilder(p_165236_, p_165237_, p_165238_, p_165239_);
   }

   public static Builder setAttributes() {
      return new Builder();
   }

   public static class ModifierBuilder {
      private final String name;
      private final Attribute attribute;
      private final AttributeModifier.Operation operation;
      private final NumberProvider amount;
      @Nullable
      private UUID id;
      private final Set<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);

      public ModifierBuilder(String p_165263_, Attribute p_165264_, AttributeModifier.Operation p_165265_, NumberProvider p_165266_) {
         this.name = p_165263_;
         this.attribute = p_165264_;
         this.operation = p_165265_;
         this.amount = p_165266_;
      }

      public ModifierBuilder forSlot(EquipmentSlot p_165269_) {
         this.slots.add(p_165269_);
         return this;
      }

      public ModifierBuilder withUuid(UUID p_165271_) {
         this.id = p_165271_;
         return this;
      }

      public Modifier build() {
         return new Modifier(this.name, this.attribute, this.operation, this.amount, (EquipmentSlot[])this.slots.toArray(new EquipmentSlot[0]), this.id);
      }
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final List<Modifier> modifiers = Lists.newArrayList();

      protected Builder getThis() {
         return this;
      }

      public Builder withModifier(ModifierBuilder p_165246_) {
         this.modifiers.add(p_165246_.build());
         return this;
      }

      public LootItemFunction build() {
         return new SetAttributesFunction(this.getConditions(), this.modifiers);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetAttributesFunction> {
      public void serialize(JsonObject p_80891_, SetAttributesFunction p_80892_, JsonSerializationContext p_80893_) {
         super.serialize(p_80891_, p_80892_, p_80893_);
         JsonArray $$3 = new JsonArray();

         for(Modifier $$4 : p_80892_.modifiers) {
            $$3.add($$4.serialize(p_80893_));
         }

         p_80891_.add("modifiers", $$3);
      }

      public SetAttributesFunction deserialize(JsonObject p_80883_, JsonDeserializationContext p_80884_, LootItemCondition[] p_80885_) {
         JsonArray $$3 = GsonHelper.getAsJsonArray(p_80883_, "modifiers");
         List<Modifier> $$4 = Lists.newArrayListWithExpectedSize($$3.size());

         for(JsonElement $$5 : $$3) {
            $$4.add(SetAttributesFunction.Modifier.deserialize(GsonHelper.convertToJsonObject($$5, "modifier"), p_80884_));
         }

         if ($$4.isEmpty()) {
            throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
         } else {
            return new SetAttributesFunction(p_80885_, $$4);
         }
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }

   static class Modifier {
      final String name;
      final Attribute attribute;
      final AttributeModifier.Operation operation;
      final NumberProvider amount;
      @Nullable
      final UUID id;
      final EquipmentSlot[] slots;

      Modifier(String p_165250_, Attribute p_165251_, AttributeModifier.Operation p_165252_, NumberProvider p_165253_, EquipmentSlot[] p_165254_, @Nullable UUID p_165255_) {
         this.name = p_165250_;
         this.attribute = p_165251_;
         this.operation = p_165252_;
         this.amount = p_165253_;
         this.id = p_165255_;
         this.slots = p_165254_;
      }

      public JsonObject serialize(JsonSerializationContext p_80866_) {
         JsonObject $$1 = new JsonObject();
         $$1.addProperty("name", this.name);
         $$1.addProperty("attribute", BuiltInRegistries.ATTRIBUTE.getKey(this.attribute).toString());
         $$1.addProperty("operation", operationToString(this.operation));
         $$1.add("amount", p_80866_.serialize(this.amount));
         if (this.id != null) {
            $$1.addProperty("id", this.id.toString());
         }

         if (this.slots.length == 1) {
            $$1.addProperty("slot", this.slots[0].getName());
         } else {
            JsonArray $$2 = new JsonArray();

            for(EquipmentSlot $$3 : this.slots) {
               $$2.add(new JsonPrimitive($$3.getName()));
            }

            $$1.add("slot", $$2);
         }

         return $$1;
      }

      public static Modifier deserialize(JsonObject p_80863_, JsonDeserializationContext p_80864_) {
         String $$2 = GsonHelper.getAsString(p_80863_, "name");
         ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString(p_80863_, "attribute"));
         Attribute $$4 = (Attribute)BuiltInRegistries.ATTRIBUTE.get($$3);
         if ($$4 == null) {
            throw new JsonSyntaxException("Unknown attribute: " + $$3);
         } else {
            AttributeModifier.Operation $$5 = operationFromString(GsonHelper.getAsString(p_80863_, "operation"));
            NumberProvider $$6 = (NumberProvider)GsonHelper.getAsObject(p_80863_, "amount", p_80864_, NumberProvider.class);
            UUID $$7 = null;
            EquipmentSlot[] $$8;
            if (GsonHelper.isStringValue(p_80863_, "slot")) {
               $$8 = new EquipmentSlot[]{EquipmentSlot.byName(GsonHelper.getAsString(p_80863_, "slot"))};
            } else {
               if (!GsonHelper.isArrayNode(p_80863_, "slot")) {
                  throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
               }

               JsonArray $$9 = GsonHelper.getAsJsonArray(p_80863_, "slot");
               $$8 = new EquipmentSlot[$$9.size()];
               int $$11 = 0;

               for(JsonElement $$12 : $$9) {
                  $$8[$$11++] = EquipmentSlot.byName(GsonHelper.convertToString($$12, "slot"));
               }

               if ($$8.length == 0) {
                  throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
               }
            }

            if (p_80863_.has("id")) {
               String $$14 = GsonHelper.getAsString(p_80863_, "id");

               try {
                  $$7 = UUID.fromString($$14);
               } catch (IllegalArgumentException var13) {
                  throw new JsonSyntaxException("Invalid attribute modifier id '" + $$14 + "' (must be UUID format, with dashes)");
               }
            }

            return new Modifier($$2, $$4, $$5, $$6, $$8, $$7);
         }
      }

      private static String operationToString(AttributeModifier.Operation p_80861_) {
         switch (p_80861_) {
            case ADDITION -> {
               return "addition";
            }
            case MULTIPLY_BASE -> {
               return "multiply_base";
            }
            case MULTIPLY_TOTAL -> {
               return "multiply_total";
            }
            default -> throw new IllegalArgumentException("Unknown operation " + p_80861_);
         }
      }

      private static AttributeModifier.Operation operationFromString(String p_80870_) {
         switch (p_80870_) {
            case "addition" -> {
               return AttributeModifier.Operation.ADDITION;
            }
            case "multiply_base" -> {
               return AttributeModifier.Operation.MULTIPLY_BASE;
            }
            case "multiply_total" -> {
               return AttributeModifier.Operation.MULTIPLY_TOTAL;
            }
            default -> throw new JsonSyntaxException("Unknown attribute modifier operation " + p_80870_);
         }
      }
   }
}
