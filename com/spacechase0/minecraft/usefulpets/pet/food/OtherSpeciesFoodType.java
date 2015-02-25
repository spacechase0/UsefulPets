package com.spacechase0.minecraft.usefulpets.pet.food;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.spacechase0.minecraft.usefulpets.pet.PetType;

public class OtherSpeciesFoodType extends FoodType
{
	@Override
	public boolean doesMatch( PetType type, ItemStack stack )
	{
		for ( PetType other : PetType.types.values() )
		{
			if ( other.equals( type ) )
			{
				continue;
			}
			
			for ( ItemStack food : other.defaultFoodChoices )
			{
				if ( OreDictionary.itemMatches( food, stack, false ) )
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
