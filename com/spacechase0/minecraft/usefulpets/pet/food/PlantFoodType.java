package com.spacechase0.minecraft.usefulpets.pet.food;

import static net.minecraft.init.Items.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PlantFoodType extends SpecificFoodType
{
	public PlantFoodType()
	{
		super();
		
		foods.add( new ItemStack( apple ) );
		foods.add( new ItemStack( golden_apple ) );
		foods.add( new ItemStack( potato ) );
		foods.add( new ItemStack( baked_potato ) );
		foods.add( new ItemStack( poisonous_potato ) );
		foods.add( new ItemStack( carrot ) );
		foods.add( new ItemStack( golden_carrot ) );
		foods.add( new ItemStack( melon ) );
	}
}
