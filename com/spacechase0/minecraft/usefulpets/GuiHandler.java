package com.spacechase0.minecraft.usefulpets;

import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import com.spacechase0.minecraft.usefulpets.PetTrackingDataHandler.PetData;
import com.spacechase0.minecraft.usefulpets.client.gui.PetInventoryGui;
import com.spacechase0.minecraft.usefulpets.client.gui.PetSkillGui;
import com.spacechase0.minecraft.usefulpets.client.gui.PetTrackingGui;
import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.inventory.PetInventoryContainer;
import com.spacechase0.minecraft.usefulpets.network.TrackingDataPacket;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement( int id, EntityPlayer player, World world, int x, int y, int z )
	{
		if ( id == UsefulPets.PET_INVENTORY_GUI_ID )
		{
			PetEntity pet = ( PetEntity ) UsefulPets.proxy.getPendingPetForGui( Side.SERVER );
			return new PetInventoryContainer( player.inventory, pet.getInventory(), pet );
		}
		else if ( id == UsefulPets.PET_TRACKING_GUI_ID )
		{
			Map< UUID, PetData > data = UsefulPets.instance.petData.getPetData( player.getUniqueID().toString() );
			if ( data != null )
			{
				UsefulPets.network.sendToPlayer( ( EntityPlayerMP ) player, new TrackingDataPacket( data ) );
			}
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement( int id, EntityPlayer player, World world, int x, int y, int z )
	{
		if ( id == UsefulPets.PET_INVENTORY_GUI_ID )
		{
			PetEntity pet = ( PetEntity ) UsefulPets.proxy.getPendingPetForGui( Side.CLIENT );
			return new PetInventoryGui( player.inventory, pet.getInventory(), pet );
		}
		else if ( id == UsefulPets.PET_SKILLS_GUI_ID )
		{
			return new PetSkillGui( player, UsefulPets.proxy.getPendingPetForGui( Side.CLIENT ) );
		}
		else if ( id == UsefulPets.PET_TRACKING_GUI_ID )
		{
			return new PetTrackingGui();
		}
		
		return null;
	}
}
