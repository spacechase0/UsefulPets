package com.spacechase0.minecraft.usefulpets.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.item.ClawItem;
import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PetWeaponSlot extends Slot
{
    public PetWeaponSlot( PetInventoryContainer theContainer, IInventory inv, int par3, int par4, int par5, PetEntity thePet )
    {
        super(inv, par3, par4, par5);
        this.container = theContainer;
        this.pet = thePet;
    }
    
    @Override
    public boolean isItemValid( ItemStack stack )
    {
    	if ( !pet.hasSkill( Skill.INVENTORY_WEAPON.id ) )
    	{
    		return false;
    	}
    	
        return super.isItemValid( stack ) && ( stack == null || stack.getItem() instanceof ClawItem );
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean func_111238_b()
    {
        return this.pet.hasSkill( Skill.INVENTORY_WEAPON.id );
    }
    
    public final PetEntity pet;
    public final PetInventoryContainer container;
}
