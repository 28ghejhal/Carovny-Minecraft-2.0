package net.minecraft.client.resources.model;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.IForgeModelBaker;

@OnlyIn(Dist.CLIENT)
public interface ModelBaker extends IForgeModelBaker {
   UnbakedModel getModel(ResourceLocation var1);

   /** @deprecated */
   @Deprecated
   @Nullable
   BakedModel bake(ResourceLocation var1, ModelState var2);
}
