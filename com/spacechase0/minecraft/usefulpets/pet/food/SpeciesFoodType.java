package com.spacechase0.minecraft.usefulpets.pet.food;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.spacechase0.minecraft.usefulpets.pet.PetType;

public class SpeciesFoodType extends FoodType
{
	@Override
	public boolean doesMatch( PetType type, ItemStack stack )
	{
		for ( ItemStack food : type.defaultFoodChoices )
		{
			if ( OreDictionary.itemMatches( food, stack, false ) )
			{
				return true;
			}
		}
		
		return false;
	}
}
