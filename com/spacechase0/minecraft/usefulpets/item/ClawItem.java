package com.spacechase0.minecraft.usefulpets.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ClawItem extends Item
{
	public ClawItem( String theType, int theDamage )
	{
		super();
		type = theType;
		Type = ( type.substring( 0, 1 ).toUpperCase() + type.substring( 1 ) );
		damage = theDamage;
		
		setUnlocalizedName( "claw" + Type );
		setCreativeTab( CreativeTabs.tabCombat );
		setMaxStackSize( 1 );
	}
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		itemIcon = register.registerIcon( "usefulpets:claw" + Type );
	}
	
	public final String type;
	public final String Type;
	public final int damage;
}
