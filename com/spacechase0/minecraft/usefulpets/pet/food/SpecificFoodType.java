package com.spacechase0.minecraft.usefulpets.pet.food;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.spacechase0.minecraft.usefulpets.pet.PetType;

public class SpecificFoodType extends FoodType
{
	public SpecificFoodType()
	{
	}
	
	@Override
	public boolean doesMatch( PetType type, ItemStack stack )
	{
		for ( ItemStack food : foods )
		{
			if ( OreDictionary.itemMatches( food, stack, false ) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	protected List< ItemStack > foods = new ArrayList< ItemStack >();
}
