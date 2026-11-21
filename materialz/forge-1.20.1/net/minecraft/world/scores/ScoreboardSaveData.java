package net.minecraft.world.scores;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreboardSaveData extends SavedData {
   public static final String FILE_ID = "scoreboard";
   private final Scoreboard scoreboard;

   public ScoreboardSaveData(Scoreboard p_166101_) {
      this.scoreboard = p_166101_;
   }

   public ScoreboardSaveData load(CompoundTag p_166103_) {
      this.loadObjectives(p_166103_.getList("Objectives", 10));
      this.scoreboard.loadPlayerScores(p_166103_.getList("PlayerScores", 10));
      if (p_166103_.contains("DisplaySlots", 10)) {
         this.loadDisplaySlots(p_166103_.getCompound("DisplaySlots"));
      }

      if (p_166103_.contains("Teams", 9)) {
         this.loadTeams(p_166103_.getList("Teams", 10));
      }

      return this;
   }

   private void loadTeams(ListTag p_83525_) {
      for(int $$1 = 0; $$1 < p_83525_.size(); ++$$1) {
         CompoundTag $$2 = p_83525_.getCompound($$1);
         String $$3 = $$2.getString("Name");
         PlayerTeam $$4 = this.scoreboard.addPlayerTeam($$3);
         Component $$5 = Component.Serializer.fromJson($$2.getString("DisplayName"));
         if ($$5 != null) {
            $$4.setDisplayName($$5);
         }

         if ($$2.contains("TeamColor", 8)) {
            $$4.setColor(ChatFormatting.getByName($$2.getString("TeamColor")));
         }

         if ($$2.contains("AllowFriendlyFire", 99)) {
            $$4.setAllowFriendlyFire($$2.getBoolean("AllowFriendlyFire"));
         }

         if ($$2.contains("SeeFriendlyInvisibles", 99)) {
            $$4.setSeeFriendlyInvisibles($$2.getBoolean("SeeFriendlyInvisibles"));
         }

         if ($$2.contains("MemberNamePrefix", 8)) {
            Component $$6 = Component.Serializer.fromJson($$2.getString("MemberNamePrefix"));
            if ($$6 != null) {
               $$4.setPlayerPrefix($$6);
            }
         }

         if ($$2.contains("MemberNameSuffix", 8)) {
            Component $$7 = Component.Serializer.fromJson($$2.getString("MemberNameSuffix"));
            if ($$7 != null) {
               $$4.setPlayerSuffix($$7);
            }
         }

         if ($$2.contains("NameTagVisibility", 8)) {
            Team.Visibility $$8 = Team.Visibility.byName($$2.getString("NameTagVisibility"));
            if ($$8 != null) {
               $$4.setNameTagVisibility($$8);
            }
         }

         if ($$2.contains("DeathMessageVisibility", 8)) {
            Team.Visibility $$9 = Team.Visibility.byName($$2.getString("DeathMessageVisibility"));
            if ($$9 != null) {
               $$4.setDeathMessageVisibility($$9);
            }
         }

         if ($$2.contains("CollisionRule", 8)) {
            Team.CollisionRule $$10 = Team.CollisionRule.byName($$2.getString("CollisionRule"));
            if ($$10 != null) {
               $$4.setCollisionRule($$10);
            }
         }

         this.loadTeamPlayers($$4, $$2.getList("Players", 8));
      }

   }

   private void loadTeamPlayers(PlayerTeam p_83515_, ListTag p_83516_) {
      for(int $$2 = 0; $$2 < p_83516_.size(); ++$$2) {
         this.scoreboard.addPlayerToTeam(p_83516_.getString($$2), p_83515_);
      }

   }

   private void loadDisplaySlots(CompoundTag p_83531_) {
      for(int $$1 = 0; $$1 < 19; ++$$1) {
         if (p_83531_.contains("slot_" + $$1, 8)) {
            String $$2 = p_83531_.getString("slot_" + $$1);
            Objective $$3 = this.scoreboard.getObjective($$2);
            this.scoreboard.setDisplayObjective($$1, $$3);
         }
      }

   }

   private void loadObjectives(ListTag p_83529_) {
      for(int $$1 = 0; $$1 < p_83529_.size(); ++$$1) {
         CompoundTag $$2 = p_83529_.getCompound($$1);
         ObjectiveCriteria.byName($$2.getString("CriteriaName")).ifPresent((p_83523_) -> {
            String $$2 = $$2.getString("Name");
            Component $$3 = Component.Serializer.fromJson($$2.getString("DisplayName"));
            ObjectiveCriteria.RenderType $$4 = ObjectiveCriteria.RenderType.byId($$2.getString("RenderType"));
            this.scoreboard.addObjective($$2, p_83523_, $$3, $$4);
         });
      }

   }

   public CompoundTag save(CompoundTag p_83527_) {
      p_83527_.put("Objectives", this.saveObjectives());
      p_83527_.put("PlayerScores", this.scoreboard.savePlayerScores());
      p_83527_.put("Teams", this.saveTeams());
      this.saveDisplaySlots(p_83527_);
      return p_83527_;
   }

   private ListTag saveTeams() {
      ListTag $$0 = new ListTag();

      for(PlayerTeam $$2 : this.scoreboard.getPlayerTeams()) {
         CompoundTag $$3 = new CompoundTag();
         $$3.putString("Name", $$2.getName());
         $$3.putString("DisplayName", Component.Serializer.toJson($$2.getDisplayName()));
         if ($$2.getColor().getId() >= 0) {
            $$3.putString("TeamColor", $$2.getColor().getName());
         }

         $$3.putBoolean("AllowFriendlyFire", $$2.isAllowFriendlyFire());
         $$3.putBoolean("SeeFriendlyInvisibles", $$2.canSeeFriendlyInvisibles());
         $$3.putString("MemberNamePrefix", Component.Serializer.toJson($$2.getPlayerPrefix()));
         $$3.putString("MemberNameSuffix", Component.Serializer.toJson($$2.getPlayerSuffix()));
         $$3.putString("NameTagVisibility", $$2.getNameTagVisibility().name);
         $$3.putString("DeathMessageVisibility", $$2.getDeathMessageVisibility().name);
         $$3.putString("CollisionRule", $$2.getCollisionRule().name);
         ListTag $$4 = new ListTag();

         for(String $$5 : $$2.getPlayers()) {
            $$4.add(StringTag.valueOf($$5));
         }

         $$3.put("Players", $$4);
         $$0.add($$3);
      }

      return $$0;
   }

   private void saveDisplaySlots(CompoundTag p_83533_) {
      CompoundTag $$1 = new CompoundTag();
      boolean $$2 = false;

      for(int $$3 = 0; $$3 < 19; ++$$3) {
         Objective $$4 = this.scoreboard.getDisplayObjective($$3);
         if ($$4 != null) {
            $$1.putString("slot_" + $$3, $$4.getName());
            $$2 = true;
         }
      }

      if ($$2) {
         p_83533_.put("DisplaySlots", $$1);
      }

   }

   private ListTag saveObjectives() {
      ListTag $$0 = new ListTag();

      for(Objective $$2 : this.scoreboard.getObjectives()) {
         if ($$2.getCriteria() != null) {
            CompoundTag $$3 = new CompoundTag();
            $$3.putString("Name", $$2.getName());
            $$3.putString("CriteriaName", $$2.getCriteria().getName());
            $$3.putString("DisplayName", Component.Serializer.toJson($$2.getDisplayName()));
            $$3.putString("RenderType", $$2.getRenderType().getId());
            $$0.add($$3);
         }
      }

      return $$0;
   }
}
