package net.minecraft.world.entity.ai.attributes;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class Attributes {
   public static final Attribute MAX_HEALTH = register("generic.max_health", (new RangedAttribute("attribute.name.generic.max_health", (double)20.0F, (double)1.0F, (double)1024.0F)).setSyncable(true));
   public static final Attribute FOLLOW_RANGE = register("generic.follow_range", new RangedAttribute("attribute.name.generic.follow_range", (double)32.0F, (double)0.0F, (double)2048.0F));
   public static final Attribute KNOCKBACK_RESISTANCE = register("generic.knockback_resistance", new RangedAttribute("attribute.name.generic.knockback_resistance", (double)0.0F, (double)0.0F, (double)1.0F));
   public static final Attribute MOVEMENT_SPEED = register("generic.movement_speed", (new RangedAttribute("attribute.name.generic.movement_speed", (double)0.7F, (double)0.0F, (double)1024.0F)).setSyncable(true));
   public static final Attribute FLYING_SPEED = register("generic.flying_speed", (new RangedAttribute("attribute.name.generic.flying_speed", (double)0.4F, (double)0.0F, (double)1024.0F)).setSyncable(true));
   public static final Attribute ATTACK_DAMAGE = register("generic.attack_damage", new RangedAttribute("attribute.name.generic.attack_damage", (double)2.0F, (double)0.0F, (double)2048.0F));
   public static final Attribute ATTACK_KNOCKBACK = register("generic.attack_knockback", new RangedAttribute("attribute.name.generic.attack_knockback", (double)0.0F, (double)0.0F, (double)5.0F));
   public static final Attribute ATTACK_SPEED = register("generic.attack_speed", (new RangedAttribute("attribute.name.generic.attack_speed", (double)4.0F, (double)0.0F, (double)1024.0F)).setSyncable(true));
   public static final Attribute ARMOR = register("generic.armor", (new RangedAttribute("attribute.name.generic.armor", (double)0.0F, (double)0.0F, (double)30.0F)).setSyncable(true));
   public static final Attribute ARMOR_TOUGHNESS = register("generic.armor_toughness", (new RangedAttribute("attribute.name.generic.armor_toughness", (double)0.0F, (double)0.0F, (double)20.0F)).setSyncable(true));
   public static final Attribute LUCK = register("generic.luck", (new RangedAttribute("attribute.name.generic.luck", (double)0.0F, (double)-1024.0F, (double)1024.0F)).setSyncable(true));
   public static final Attribute SPAWN_REINFORCEMENTS_CHANCE = register("zombie.spawn_reinforcements", new RangedAttribute("attribute.name.zombie.spawn_reinforcements", (double)0.0F, (double)0.0F, (double)1.0F));
   public static final Attribute JUMP_STRENGTH = register("horse.jump_strength", (new RangedAttribute("attribute.name.horse.jump_strength", 0.7, (double)0.0F, (double)2.0F)).setSyncable(true));

   private static Attribute register(String p_22291_, Attribute p_22292_) {
      return (Attribute)Registry.register(BuiltInRegistries.ATTRIBUTE, (String)p_22291_, p_22292_);
   }
}
