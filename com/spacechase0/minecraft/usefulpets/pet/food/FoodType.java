package com.spacechase0.minecraft.usefulpets.pet.food;

import net.minecraft.item.ItemStack;

import com.spacechase0.minecraft.usefulpets.pet.PetType;

public abstract class FoodType
{
	public abstract boolean doesMatch( PetType type, ItemStack stack );
	
	public static final FoodType SPECIES = new SpeciesFoodType();
	public static final FoodType OTHER_SPECIES = new OtherSpeciesFoodType();
	public static final FoodType PLANTS = new PlantFoodType();
	public static final FoodType PROCESSED = new ProcessedFoodType();
	public static final FoodType GROSS = new GrossFoodType();
}
