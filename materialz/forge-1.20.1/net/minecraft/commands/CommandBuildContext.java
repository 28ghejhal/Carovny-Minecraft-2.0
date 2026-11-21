package net.minecraft.commands;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;

public interface CommandBuildContext {
   <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> var1);

   static CommandBuildContext simple(final HolderLookup.Provider p_255702_, final FeatureFlagSet p_255968_) {
      return new CommandBuildContext() {
         public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> p_255791_) {
            return p_255702_.<T>lookupOrThrow(p_255791_).filterFeatures(p_255968_);
         }
      };
   }

   static Configurable configurable(final RegistryAccess p_255925_, final FeatureFlagSet p_255945_) {
      return new Configurable() {
         MissingTagAccessPolicy missingTagAccessPolicy;

         {
            this.missingTagAccessPolicy = CommandBuildContext.MissingTagAccessPolicy.FAIL;
         }

         public void missingTagAccessPolicy(MissingTagAccessPolicy p_256626_) {
            this.missingTagAccessPolicy = p_256626_;
         }

         public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> p_256616_) {
            Registry<T> $$1 = p_255925_.<T>registryOrThrow(p_256616_);
            HolderLookup.RegistryLookup<T> $$2 = $$1.asLookup();
            HolderLookup.RegistryLookup<T> $$3 = $$1.asTagAddingLookup();
            HolderLookup.RegistryLookup<T> $$4 = new HolderLookup.RegistryLookup.Delegate<T>($$2, $$3) {
               // $FF: synthetic field
               final HolderLookup.RegistryLookup val$originalLookup;
               // $FF: synthetic field
               final HolderLookup.RegistryLookup val$originalTagAddingLookup;
               // $FF: synthetic field
               final <undefinedtype> this$0;

               {
                  this.this$0 = p_256109_;
                  this.val$originalLookup = var2;
                  this.val$originalTagAddingLookup = var3;
               }

               protected HolderLookup.RegistryLookup<T> parent() {
                  HolderLookup.RegistryLookup var10000;
                  switch (this.this$0.missingTagAccessPolicy) {
                     case FAIL -> var10000 = this.val$originalLookup;
                     case CREATE_NEW -> var10000 = this.val$originalTagAddingLookup;
                     default -> throw new IncompatibleClassChangeError();
                  }

                  return var10000;
               }
            };
            return $$4.filterFeatures(p_255945_);
         }
      };
   }

   public static enum MissingTagAccessPolicy {
      CREATE_NEW,
      FAIL;

      // $FF: synthetic method
      private static MissingTagAccessPolicy[] $values() {
         return new MissingTagAccessPolicy[]{CREATE_NEW, FAIL};
      }
   }

   public interface Configurable extends CommandBuildContext {
      void missingTagAccessPolicy(MissingTagAccessPolicy var1);
   }
}
