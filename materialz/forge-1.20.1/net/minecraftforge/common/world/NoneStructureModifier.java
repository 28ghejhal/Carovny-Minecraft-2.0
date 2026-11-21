package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.ForgeMod;

public class NoneStructureModifier implements StructureModifier {
   public static final NoneStructureModifier INSTANCE = new NoneStructureModifier();

   public void modify(Holder<Structure> structure, StructureModifier.Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
   }

   public Codec<? extends StructureModifier> codec() {
      return ForgeMod.NONE_STRUCTURE_MODIFIER_TYPE.get();
   }
}
