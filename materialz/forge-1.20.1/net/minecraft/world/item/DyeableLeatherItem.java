package net.minecraft.world.item;

import java.util.List;
import net.minecraft.nbt.CompoundTag;

public interface DyeableLeatherItem {
   String TAG_COLOR = "color";
   String TAG_DISPLAY = "display";
   int DEFAULT_LEATHER_COLOR = 10511680;

   default boolean hasCustomColor(ItemStack p_41114_) {
      CompoundTag $$1 = p_41114_.getTagElement("display");
      return $$1 != null && $$1.contains("color", 99);
   }

   default int getColor(ItemStack p_41122_) {
      CompoundTag $$1 = p_41122_.getTagElement("display");
      return $$1 != null && $$1.contains("color", 99) ? $$1.getInt("color") : 10511680;
   }

   default void clearColor(ItemStack p_41124_) {
      CompoundTag $$1 = p_41124_.getTagElement("display");
      if ($$1 != null && $$1.contains("color")) {
         $$1.remove("color");
      }

   }

   default void setColor(ItemStack p_41116_, int p_41117_) {
      p_41116_.getOrCreateTagElement("display").putInt("color", p_41117_);
   }

   static ItemStack dyeArmor(ItemStack p_41119_, List<DyeItem> p_41120_) {
      ItemStack $$2 = ItemStack.EMPTY;
      int[] $$3 = new int[3];
      int $$4 = 0;
      int $$5 = 0;
      Item $$7 = p_41119_.getItem();
      if ($$7 instanceof DyeableLeatherItem $$6) {
         $$2 = p_41119_.copyWithCount(1);
         if ($$6.hasCustomColor(p_41119_)) {
            int $$8 = $$6.getColor($$2);
            float $$9 = (float)($$8 >> 16 & 255) / 255.0F;
            float $$10 = (float)($$8 >> 8 & 255) / 255.0F;
            float $$11 = (float)($$8 & 255) / 255.0F;
            $$4 += (int)(Math.max($$9, Math.max($$10, $$11)) * 255.0F);
            $$3[0] += (int)($$9 * 255.0F);
            $$3[1] += (int)($$10 * 255.0F);
            $$3[2] += (int)($$11 * 255.0F);
            ++$$5;
         }

         for(DyeItem $$12 : p_41120_) {
            float[] $$13 = $$12.getDyeColor().getTextureDiffuseColors();
            int $$14 = (int)($$13[0] * 255.0F);
            int $$15 = (int)($$13[1] * 255.0F);
            int $$16 = (int)($$13[2] * 255.0F);
            $$4 += Math.max($$14, Math.max($$15, $$16));
            $$3[0] += $$14;
            $$3[1] += $$15;
            $$3[2] += $$16;
            ++$$5;
         }
      }

      if ($$6 == null) {
         return ItemStack.EMPTY;
      } else {
         int $$17 = $$3[0] / $$5;
         int $$18 = $$3[1] / $$5;
         int $$19 = $$3[2] / $$5;
         float $$20 = (float)$$4 / (float)$$5;
         float $$21 = (float)Math.max($$17, Math.max($$18, $$19));
         $$17 = (int)((float)$$17 * $$20 / $$21);
         $$18 = (int)((float)$$18 * $$20 / $$21);
         $$19 = (int)((float)$$19 * $$20 / $$21);
         int $$22 = ($$17 << 8) + $$18;
         $$22 = ($$22 << 8) + $$19;
         $$6.setColor($$2, $$22);
         return $$2;
      }
   }
}
