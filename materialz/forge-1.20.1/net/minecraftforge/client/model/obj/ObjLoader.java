package net.minecraftforge.client.model.obj;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.FileNotFoundException;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public class ObjLoader implements IGeometryLoader<ObjModel>, ResourceManagerReloadListener {
   public static ObjLoader INSTANCE = new ObjLoader();
   private final Map<ObjModel.ModelSettings, ObjModel> modelCache = Maps.newConcurrentMap();
   private final Map<ResourceLocation, ObjMaterialLibrary> materialCache = Maps.newConcurrentMap();
   private ResourceManager manager = Minecraft.getInstance().getResourceManager();

   public void onResourceManagerReload(ResourceManager resourceManager) {
      this.modelCache.clear();
      this.materialCache.clear();
      this.manager = resourceManager;
   }

   public ObjModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
      if (!jsonObject.has("model")) {
         throw new JsonParseException("OBJ Loader requires a 'model' key that points to a valid .OBJ model.");
      } else {
         String modelLocation = jsonObject.get("model").getAsString();
         boolean automaticCulling = GsonHelper.getAsBoolean(jsonObject, "automatic_culling", true);
         boolean shadeQuads = GsonHelper.getAsBoolean(jsonObject, "shade_quads", true);
         boolean flipV = GsonHelper.getAsBoolean(jsonObject, "flip_v", false);
         boolean emissiveAmbient = GsonHelper.getAsBoolean(jsonObject, "emissive_ambient", true);
         String mtlOverride = GsonHelper.getAsString(jsonObject, "mtl_override", (String)null);
         return this.loadModel(new ObjModel.ModelSettings(new ResourceLocation(modelLocation), automaticCulling, shadeQuads, flipV, emissiveAmbient, mtlOverride));
      }
   }

   public ObjModel loadModel(ObjModel.ModelSettings settings) {
      return (ObjModel)this.modelCache.computeIfAbsent(settings, (data) -> {
         Resource resource = (Resource)this.manager.getResource(settings.modelLocation()).orElseThrow();

         try (ObjTokenizer tokenizer = new ObjTokenizer(resource.open())) {
            return ObjModel.parse(tokenizer, settings);
         } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find OBJ model", e);
         } catch (Exception e) {
            throw new RuntimeException("Could not read OBJ model", e);
         }
      });
   }

   public ObjMaterialLibrary loadMaterialLibrary(ResourceLocation materialLocation) {
      return (ObjMaterialLibrary)this.materialCache.computeIfAbsent(materialLocation, (location) -> {
         Resource resource = (Resource)this.manager.getResource(location).orElseThrow();

         try (ObjTokenizer rdr = new ObjTokenizer(resource.open())) {
            return new ObjMaterialLibrary(rdr);
         } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find OBJ material library", e);
         } catch (Exception e) {
            throw new RuntimeException("Could not read OBJ material library", e);
         }
      });
   }
}
