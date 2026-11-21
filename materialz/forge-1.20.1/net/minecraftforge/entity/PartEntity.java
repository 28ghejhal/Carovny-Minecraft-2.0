package net.minecraftforge.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;

public abstract class PartEntity<T extends Entity> extends Entity {
   private final T parent;

   public PartEntity(T parent) {
      super(((Entity)parent).getType(), parent.level());
      this.parent = parent;
   }

   public T getParent() {
      return this.parent;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      throw new UnsupportedOperationException();
   }
}
