package net.minecraft.client.searchtree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SearchRegistry implements ResourceManagerReloadListener {
   public static final Key<ItemStack> CREATIVE_NAMES = new Key<ItemStack>();
   public static final Key<ItemStack> CREATIVE_TAGS = new Key<ItemStack>();
   public static final Key<RecipeCollection> RECIPE_COLLECTIONS = new Key<RecipeCollection>();
   private final Map<Key<?>, TreeEntry<?>> searchTrees = new HashMap();

   public void onResourceManagerReload(ResourceManager p_119948_) {
      for(TreeEntry<?> $$1 : this.searchTrees.values()) {
         $$1.refresh();
      }

   }

   public <T> void register(Key<T> p_235233_, TreeBuilderSupplier<T> p_235234_) {
      this.searchTrees.put(p_235233_, new TreeEntry(p_235234_));
   }

   private <T> TreeEntry<T> getSupplier(Key<T> p_235239_) {
      TreeEntry<T> $$1 = (TreeEntry)this.searchTrees.get(p_235239_);
      if ($$1 == null) {
         throw new IllegalStateException("Tree builder not registered");
      } else {
         return $$1;
      }
   }

   public <T> void populate(Key<T> p_235236_, List<T> p_235237_) {
      this.getSupplier(p_235236_).populate(p_235237_);
   }

   public <T> SearchTree<T> getTree(Key<T> p_235231_) {
      return this.getSupplier(p_235231_).tree;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Key<T> {
   }

   @OnlyIn(Dist.CLIENT)
   static class TreeEntry<T> {
      private final TreeBuilderSupplier<T> factory;
      RefreshableSearchTree<T> tree = RefreshableSearchTree.<T>empty();

      TreeEntry(TreeBuilderSupplier<T> p_235243_) {
         this.factory = p_235243_;
      }

      void populate(List<T> p_235246_) {
         this.tree = (RefreshableSearchTree)this.factory.apply(p_235246_);
         this.tree.refresh();
      }

      void refresh() {
         this.tree.refresh();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface TreeBuilderSupplier<T> extends Function<List<T>, RefreshableSearchTree<T>> {
   }
}
