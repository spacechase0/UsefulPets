package com.spacechase0.minecraft.usefulpets.pet.food;

import static net.minecraft.init.Items.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GrossFoodType extends SpecificFoodType
{
	public GrossFoodType()
	{
		super();
		
		foods.add( new ItemStack( spider_eye ) );
		foods.add( new ItemStack( rotten_flesh ) );
	}
}
