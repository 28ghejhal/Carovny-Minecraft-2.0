package net.minecraftforge.client.settings;

import net.minecraft.client.Minecraft;

public enum KeyConflictContext implements IKeyConflictContext {
   UNIVERSAL {
      public boolean isActive() {
         return true;
      }

      public boolean conflicts(IKeyConflictContext other) {
         return true;
      }
   },
   GUI {
      public boolean isActive() {
         return Minecraft.getInstance().screen != null;
      }

      public boolean conflicts(IKeyConflictContext other) {
         return this == other;
      }
   },
   IN_GAME {
      public boolean isActive() {
         return !GUI.isActive();
      }

      public boolean conflicts(IKeyConflictContext other) {
         return this == other;
      }
   };

   // $FF: synthetic method
   private static KeyConflictContext[] $values() {
      return new KeyConflictContext[]{UNIVERSAL, GUI, IN_GAME};
   }
}
