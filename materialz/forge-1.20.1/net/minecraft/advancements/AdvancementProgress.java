package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class AdvancementProgress implements Comparable<AdvancementProgress> {
   final Map<String, CriterionProgress> criteria;
   private String[][] requirements = new String[0][];

   private AdvancementProgress(Map<String, CriterionProgress> p_144358_) {
      this.criteria = p_144358_;
   }

   public AdvancementProgress() {
      this.criteria = Maps.newHashMap();
   }

   public void update(Map<String, Criterion> p_8199_, String[][] p_8200_) {
      Set<String> $$2 = p_8199_.keySet();
      this.criteria.entrySet().removeIf((p_8203_) -> !$$2.contains(p_8203_.getKey()));

      for(String $$3 : $$2) {
         if (!this.criteria.containsKey($$3)) {
            this.criteria.put($$3, new CriterionProgress());
         }
      }

      this.requirements = p_8200_;
   }

   public boolean isDone() {
      if (this.requirements.length == 0) {
         return false;
      } else {
         for(String[] $$0 : this.requirements) {
            boolean $$1 = false;

            for(String $$2 : $$0) {
               CriterionProgress $$3 = this.getCriterion($$2);
               if ($$3 != null && $$3.isDone()) {
                  $$1 = true;
                  break;
               }
            }

            if (!$$1) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean hasProgress() {
      for(CriterionProgress $$0 : this.criteria.values()) {
         if ($$0.isDone()) {
            return true;
         }
      }

      return false;
   }

   public boolean grantProgress(String p_8197_) {
      CriterionProgress $$1 = (CriterionProgress)this.criteria.get(p_8197_);
      if ($$1 != null && !$$1.isDone()) {
         $$1.grant();
         return true;
      } else {
         return false;
      }
   }

   public boolean revokeProgress(String p_8210_) {
      CriterionProgress $$1 = (CriterionProgress)this.criteria.get(p_8210_);
      if ($$1 != null && $$1.isDone()) {
         $$1.revoke();
         return true;
      } else {
         return false;
      }
   }

   public String toString() {
      Map var10000 = this.criteria;
      return "AdvancementProgress{criteria=" + var10000 + ", requirements=" + Arrays.deepToString(this.requirements) + "}";
   }

   public void serializeToNetwork(FriendlyByteBuf p_8205_) {
      p_8205_.writeMap(this.criteria, FriendlyByteBuf::writeUtf, (p_144360_, p_144361_) -> p_144361_.serializeToNetwork(p_144360_));
   }

   public static AdvancementProgress fromNetwork(FriendlyByteBuf p_8212_) {
      Map<String, CriterionProgress> $$1 = p_8212_.<String, CriterionProgress>readMap(FriendlyByteBuf::readUtf, CriterionProgress::fromNetwork);
      return new AdvancementProgress($$1);
   }

   @Nullable
   public CriterionProgress getCriterion(String p_8215_) {
      return (CriterionProgress)this.criteria.get(p_8215_);
   }

   public float getPercent() {
      if (this.criteria.isEmpty()) {
         return 0.0F;
      } else {
         float $$0 = (float)this.requirements.length;
         float $$1 = (float)this.countCompletedRequirements();
         return $$1 / $$0;
      }
   }

   @Nullable
   public String getProgressText() {
      if (this.criteria.isEmpty()) {
         return null;
      } else {
         int $$0 = this.requirements.length;
         if ($$0 <= 1) {
            return null;
         } else {
            int $$1 = this.countCompletedRequirements();
            return $$1 + "/" + $$0;
         }
      }
   }

   private int countCompletedRequirements() {
      int $$0 = 0;

      for(String[] $$1 : this.requirements) {
         boolean $$2 = false;

         for(String $$3 : $$1) {
            CriterionProgress $$4 = this.getCriterion($$3);
            if ($$4 != null && $$4.isDone()) {
               $$2 = true;
               break;
            }
         }

         if ($$2) {
            ++$$0;
         }
      }

      return $$0;
   }

   public Iterable<String> getRemainingCriteria() {
      List<String> $$0 = Lists.newArrayList();

      for(Map.Entry<String, CriterionProgress> $$1 : this.criteria.entrySet()) {
         if (!((CriterionProgress)$$1.getValue()).isDone()) {
            $$0.add((String)$$1.getKey());
         }
      }

      return $$0;
   }

   public Iterable<String> getCompletedCriteria() {
      List<String> $$0 = Lists.newArrayList();

      for(Map.Entry<String, CriterionProgress> $$1 : this.criteria.entrySet()) {
         if (((CriterionProgress)$$1.getValue()).isDone()) {
            $$0.add((String)$$1.getKey());
         }
      }

      return $$0;
   }

   @Nullable
   public Date getFirstProgressDate() {
      Date $$0 = null;

      for(CriterionProgress $$1 : this.criteria.values()) {
         if ($$1.isDone() && ($$0 == null || $$1.getObtained().before($$0))) {
            $$0 = $$1.getObtained();
         }
      }

      return $$0;
   }

   public int compareTo(AdvancementProgress p_8195_) {
      Date $$1 = this.getFirstProgressDate();
      Date $$2 = p_8195_.getFirstProgressDate();
      if ($$1 == null && $$2 != null) {
         return 1;
      } else if ($$1 != null && $$2 == null) {
         return -1;
      } else {
         return $$1 == null && $$2 == null ? 0 : $$1.compareTo($$2);
      }
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((AdvancementProgress)var1);
   }

   public static class Serializer implements JsonDeserializer<AdvancementProgress>, JsonSerializer<AdvancementProgress> {
      public JsonElement serialize(AdvancementProgress p_8226_, Type p_8227_, JsonSerializationContext p_8228_) {
         JsonObject $$3 = new JsonObject();
         JsonObject $$4 = new JsonObject();

         for(Map.Entry<String, CriterionProgress> $$5 : p_8226_.criteria.entrySet()) {
            CriterionProgress $$6 = (CriterionProgress)$$5.getValue();
            if ($$6.isDone()) {
               $$4.add((String)$$5.getKey(), $$6.serializeToJson());
            }
         }

         if (!$$4.entrySet().isEmpty()) {
            $$3.add("criteria", $$4);
         }

         $$3.addProperty("done", p_8226_.isDone());
         return $$3;
      }

      public AdvancementProgress deserialize(JsonElement p_8230_, Type p_8231_, JsonDeserializationContext p_8232_) throws JsonParseException {
         JsonObject $$3 = GsonHelper.convertToJsonObject(p_8230_, "advancement");
         JsonObject $$4 = GsonHelper.getAsJsonObject($$3, "criteria", new JsonObject());
         AdvancementProgress $$5 = new AdvancementProgress();

         for(Map.Entry<String, JsonElement> $$6 : $$4.entrySet()) {
            String $$7 = (String)$$6.getKey();
            $$5.criteria.put($$7, CriterionProgress.fromJson(GsonHelper.convertToString((JsonElement)$$6.getValue(), $$7)));
         }

         return $$5;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((AdvancementProgress)var1, var2, var3);
      }
   }
}
