package net.minecraftforge.client.event;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.Map;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus.Internal;

public class RegisterNamedRenderTypesEvent extends Event implements IModBusEvent {
   private final Map<ResourceLocation, RenderTypeGroup> renderTypes;

   @Internal
   public RegisterNamedRenderTypesEvent(Map<ResourceLocation, RenderTypeGroup> renderTypes) {
      this.renderTypes = renderTypes;
   }

   public void register(String name, RenderType blockRenderType, RenderType entityRenderType) {
      this.register(name, blockRenderType, entityRenderType, entityRenderType);
   }

   public void register(String name, RenderType blockRenderType, RenderType entityRenderType, RenderType fabulousEntityRenderType) {
      ResourceLocation key = new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), name);
      boolean var10000 = !this.renderTypes.containsKey(key);
      String var10001 = String.valueOf(key);
      Preconditions.checkArgument(var10000, "Render type already registered: " + var10001);
      Preconditions.checkArgument(blockRenderType.format() == DefaultVertexFormat.BLOCK, "The block render type must use the BLOCK vertex format.");
      Preconditions.checkArgument(blockRenderType.getChunkLayerId() >= 0, "Only chunk render types can be used for block rendering. Query RenderType#chunkBufferLayers() for a list.");
      Preconditions.checkArgument(entityRenderType.format() == DefaultVertexFormat.NEW_ENTITY, "The entity render type must use the NEW_ENTITY vertex format.");
      Preconditions.checkArgument(fabulousEntityRenderType.format() == DefaultVertexFormat.NEW_ENTITY, "The fabulous entity render type must use the NEW_ENTITY vertex format.");
      this.renderTypes.put(key, new RenderTypeGroup(blockRenderType, entityRenderType, fabulousEntityRenderType));
   }
}
