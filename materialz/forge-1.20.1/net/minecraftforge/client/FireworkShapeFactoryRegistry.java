package net.minecraftforge.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.world.item.FireworkRocketItem;
import org.jetbrains.annotations.Nullable;

public class FireworkShapeFactoryRegistry {
   private static final Map<FireworkRocketItem.Shape, Factory> factories = new HashMap();

   public static void register(FireworkRocketItem.Shape shape, Factory factory) {
      factories.put(shape, factory);
   }

   public static @Nullable Factory get(FireworkRocketItem.Shape shape) {
      return (Factory)factories.get(shape);
   }

   public interface Factory {
      void build(FireworkParticles.Starter var1, boolean var2, boolean var3, int[] var4, int[] var5);
   }
}
