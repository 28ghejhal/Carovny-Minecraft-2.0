package net.minecraft.world.item.crafting;

import net.minecraft.util.StringRepresentable;

public enum CookingBookCategory implements StringRepresentable {
   FOOD("food"),
   BLOCKS("blocks"),
   MISC("misc");

   public static final StringRepresentable.EnumCodec<CookingBookCategory> CODEC = StringRepresentable.<CookingBookCategory>fromEnum(CookingBookCategory::values);
   private final String name;

   private CookingBookCategory(String p_248549_) {
      this.name = p_248549_;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static CookingBookCategory[] $values() {
      return new CookingBookCategory[]{FOOD, BLOCKS, MISC};
   }
}
