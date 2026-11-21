package net.minecraft.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LegacyStuffWrapper {
   /** @deprecated */
   @Deprecated
   public static int[] getPixels(ResourceManager p_118727_, ResourceLocation p_118728_) throws IOException {
      InputStream $$2 = p_118727_.open(p_118728_);

      int[] var4;
      try (NativeImage $$3 = NativeImage.read($$2)) {
         var4 = $$3.makePixelArray();
      } catch (Throwable var9) {
         if ($$2 != null) {
            try {
               $$2.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }
         }

         throw var9;
      }

      if ($$2 != null) {
         $$2.close();
      }

      return var4;
   }
}
