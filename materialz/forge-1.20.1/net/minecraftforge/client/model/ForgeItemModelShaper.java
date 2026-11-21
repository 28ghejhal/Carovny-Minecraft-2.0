package net.minecraftforge.client.model;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForgeItemModelShaper extends ItemModelShaper {
   private final Map<Holder.Reference<Item>, ModelResourceLocation> locations = Maps.newHashMap();
   private final Map<Holder.Reference<Item>, BakedModel> models = Maps.newHashMap();

   public ForgeItemModelShaper(ModelManager manager) {
      super(manager);
   }

   public @Nullable BakedModel getItemModel(Item item) {
      return (BakedModel)this.models.get(ForgeRegistries.ITEMS.getDelegateOrThrow(item));
   }

   public void register(Item item, ModelResourceLocation location) {
      Holder.Reference<Item> key = ForgeRegistries.ITEMS.getDelegateOrThrow(item);
      this.locations.put(key, location);
      this.models.put(key, this.getModelManager().getModel(location));
   }

   public void rebuildCache() {
      ModelManager manager = this.getModelManager();

      for(Map.Entry<Holder.Reference<Item>, ModelResourceLocation> e : this.locations.entrySet()) {
         this.models.put((Holder.Reference)e.getKey(), manager.getModel((ModelResourceLocation)e.getValue()));
      }

   }

   public ModelResourceLocation getLocation(@NotNull ItemStack stack) {
      ModelResourceLocation location = (ModelResourceLocation)this.locations.get(ForgeRegistries.ITEMS.getDelegateOrThrow(stack.getItem()));
      return location == null ? ModelBakery.MISSING_MODEL_LOCATION : location;
   }
}
