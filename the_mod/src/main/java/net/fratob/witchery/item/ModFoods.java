package net.fratob.witchery.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties WHITE_BERRIES = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.1f)
            .effect(() -> new MobEffectInstance(MobEffects.INVISIBILITY, 100, 1), 1.0f)
            .build();

}
