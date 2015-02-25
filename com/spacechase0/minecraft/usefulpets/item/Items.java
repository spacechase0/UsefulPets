package com.spacechase0.minecraft.usefulpets.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import com.spacechase0.minecraft.spacecore.BaseMod;
import com.spacechase0.minecraft.spacecore.util.ModObject;
import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;

public class Items extends com.spacechase0.minecraft.spacecore.item.Items
{
	@Override
	public void register( BaseMod mod, Configuration config )
	{
		super.register( mod, config );
		Skill.INVENTORY_WEAPON.icon = new ItemStack( ironClaws );
	}
	
	@ModObject
	public PetWandItem wand;
	
	@ModObject
	public ClawItem goldClaws;
	public Object[] goldClawsParams = new Object[] { "gold", 3 };
	
	@ModObject
	public ClawItem ironClaws;
	public Object[] ironClawsParams = new Object[] { "iron", 1 };
	
	@ModObject
	public ClawItem diamondClaws;
	public Object[] diamondClawsParams = new Object[] { "diamond", 5 };
	
	@ModObject
	public PetEggItem domesticEgg;
}
