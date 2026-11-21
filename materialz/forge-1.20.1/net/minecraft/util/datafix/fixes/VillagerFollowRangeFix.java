package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class VillagerFollowRangeFix extends NamedEntityFix {
   private static final double ORIGINAL_VALUE = (double)16.0F;
   private static final double NEW_BASE_VALUE = (double)48.0F;

   public VillagerFollowRangeFix(Schema p_17064_) {
      super(p_17064_, false, "Villager Follow Range Fix", References.ENTITY, "minecraft:villager");
   }

   protected Typed<?> fix(Typed<?> p_17066_) {
      return p_17066_.update(DSL.remainderFinder(), VillagerFollowRangeFix::fixValue);
   }

   private static Dynamic<?> fixValue(Dynamic<?> p_17068_) {
      return p_17068_.update("Attributes", (p_17071_) -> p_17068_.createList(p_17071_.asStream().map((p_145760_) -> p_145760_.get("Name").asString("").equals("generic.follow_range") && p_145760_.get("Base").asDouble((double)0.0F) == (double)16.0F ? p_145760_.set("Base", p_145760_.createDouble((double)48.0F)) : p_145760_)));
   }
}
