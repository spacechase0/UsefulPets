package com.spacechase0.minecraft.usefulpets.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spacechase0.minecraft.spacecore.util.TranslateUtils;
import com.spacechase0.minecraft.usefulpets.UsefulPets;
import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.pet.PetType;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class PetEggItem extends Item
{
	public PetEggItem()
	{
		super();
		
		setUnlocalizedName( "domesticEgg" );
		setCreativeTab( CreativeTabs.tabMisc );
		setMaxStackSize( 1 );
	}

	@Override
    public boolean itemInteractionForEntity( ItemStack stack, EntityPlayer player, EntityLivingBase entity )
    {
		if ( !( entity instanceof PetEntity ) ) return false;
        if ( stack.getTagCompound() != null && stack.getTagCompound().hasKey( "Pet" ) ) return false;
        
        if ( !player.worldObj.isRemote )
        {
	        if ( !( ( PetEntity ) entity ).func_152113_b().equals( player.getUniqueID().toString() ) )
	        {
	        	TranslateUtils.chat( player, "chat.pet.notYours" );
	        }
	        
	        NBTTagCompound pet = new NBTTagCompound();
	        entity.writeEntityToNBT( pet );
	        
	        NBTTagCompound tag = stack.getTagCompound();
	        if ( tag == null )
	        {
	        	tag = new NBTTagCompound();
	        }
	        tag.setTag( "Pet", pet );
	        stack.setTagCompound( tag );
	        
	        UsefulPets.instance.petData.removePetData( player.getUniqueID().toString(), entity.getPersistentID() );
	        entity.worldObj.removeEntity( entity );
        }
        
        return true;
    }
	
	@Override
    public boolean onItemUse( ItemStack stack, EntityPlayer player, World world, int blockX, int blockY, int blockZ, int side, float par8, float par9, float par10)
	{
		if ( stack.getTagCompound() == null || !stack.getTagCompound().hasKey( "Pet" ) ) return false;
		
		if ( !player.worldObj.isRemote )
		{
			ForgeDirection dir = ForgeDirection.getOrientation( side );
			int x = blockX + dir.offsetX;
			int y = blockY + dir.offsetY;
			int z = blockZ + dir.offsetZ;
			
			PetEntity pet = new PetEntity( world );
			pet.readEntityFromNBT( stack.getTagCompound().getCompoundTag( "Pet" ) );
			pet.setPosition( x, y, z );
			pet.setOwnerUUID( player.getUniqueID().toString() );
			world.spawnEntityInWorld( pet );
			
			stack.getTagCompound().removeTag( "Pet" );
		}
		return true;
	}
	
	@Override
	public void addInformation( ItemStack stack, EntityPlayer player, List list, boolean par4 )
	{
		if ( stack.getTagCompound() == null ) return;
		NBTTagCompound pet = stack.getTagCompound().getCompoundTag( "Pet" );
		
		if ( player.capabilities.isCreativeMode )
		{
			list.add( TranslateUtils.translate( "item.domesticEgg.tooltip.creative" ) );
			list.add( "" );
		}
		
		if ( !pet.hasKey( "Type" ) )
		{
			list.add( TranslateUtils.translate( "item.domesticEgg.tooltip.holding", "n/a" ) );
			return;
		}
		String type = pet.getString( "Type" );
		String typeStr = TranslateUtils.translate( "entity.pet." + type );

		list.add( TranslateUtils.translate( "item.domesticEgg.tooltip.holding", typeStr ) );
		if ( pet.hasKey( "CustomName" ) )
		{
			list.add( TranslateUtils.translate( "item.domesticEgg.tooltip.name", pet.getString( "CustomName" ) ) );
		}
		list.add( TranslateUtils.translate( "item.domesticEgg.tooltip.level", pet.getInteger( "Level" ) ) );
	}
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		icons = new HashMap< String, IIcon >();
		for ( String type : PetType.types.keySet() )
		{
			icons.put( type, register.registerIcon( "usefulpets:domesticEgg_" + type ) );
		}
		icons.put( "", register.registerIcon( "usefulpets:domesticEgg_none" ) );
		
		itemIcon = net.minecraft.init.Items.egg.getIconFromDamage( 0 );
	}
	
	@Override
	public boolean requiresMultipleRenderPasses()
	{
		return true;
	}
	
	@Override
	public IIcon getIcon( ItemStack stack, int pass )
	{
		if ( pass == 0 ) return itemIcon;
		if ( stack.getTagCompound() == null ) return icons.get( "" );
		
		String type = stack.getTagCompound().getCompoundTag( "Pet" ).getString( "Type" );
		return icons.get( type );
	}
	
	@Override
	public boolean hasEffect( ItemStack stack )
	{
		return true;
	}
	
	@Override
	public void getSubItems( Item item, CreativeTabs tab, List list )
	{
		for ( String type : PetType.types.keySet() )
		{
			list.add( getPetEgg( type ) );
		}
		
		list.add( new ItemStack( item ) );
	}
	
	public ItemStack getPetEgg( String petType )
	{
		PetType type = PetType.forName( petType );
		int[] skills = new int[ type.defaultSkills.size() ];
		for ( int i = 0; i < skills.length; ++i )
		{
			skills[ i ] = type.defaultSkills.get( i );
		}
		
		NBTTagCompound petTag = new NBTTagCompound();

		petTag.setString( "Type", type.name );
		petTag.setInteger( "Level", 1 );
		petTag.setInteger( "FreeSkillPoints", 1 );
		petTag.setTag( "Skills", new NBTTagIntArray( skills ) );
		petTag.setFloat( "Hunger", PetEntity.MAX_HUNGER );

		ItemStack stack = new ItemStack( this );
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag( "Pet", petTag );
		stack.setTagCompound( tag );
		
		return stack;
	}
	
	@SideOnly( Side.CLIENT )
	private Map< String, IIcon > icons;
}
