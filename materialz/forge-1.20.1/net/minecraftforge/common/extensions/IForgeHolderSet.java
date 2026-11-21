package net.minecraftforge.common.extensions;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;

public interface IForgeHolderSet<T> {
   default void addInvalidationListener(Runnable runnable) {
   }

   default SerializationType serializationType() {
      SerializationType var10000;
      if (this instanceof HolderSet.ListBacked<T> listBacked) {
         var10000 = (SerializationType)listBacked.unwrap().map((tag) -> IForgeHolderSet.SerializationType.STRING, (list) -> list.size() == 1 ? (SerializationType)((Holder)list.get(0)).unwrap().map((key) -> key == null ? IForgeHolderSet.SerializationType.OBJECT : IForgeHolderSet.SerializationType.STRING, (value) -> IForgeHolderSet.SerializationType.OBJECT) : IForgeHolderSet.SerializationType.LIST);
      } else {
         var10000 = IForgeHolderSet.SerializationType.UNKNOWN;
      }

      return var10000;
   }

   public static enum SerializationType {
      UNKNOWN,
      STRING,
      LIST,
      OBJECT;

      // $FF: synthetic method
      private static SerializationType[] $values() {
         return new SerializationType[]{UNKNOWN, STRING, LIST, OBJECT};
      }
   }
}
