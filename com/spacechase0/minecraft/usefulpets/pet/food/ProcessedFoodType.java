package com.spacechase0.minecraft.usefulpets.pet.food;

import static net.minecraft.init.Items.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ProcessedFoodType extends SpecificFoodType
{
	public ProcessedFoodType()
	{
		super();
		
		foods.add( new ItemStack( cookie ) );
		foods.add( new ItemStack( pumpkin_pie ) );
		foods.add( new ItemStack( bread ) );
		foods.add( new ItemStack( mushroom_stew ) );
	}
}
