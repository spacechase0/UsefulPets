package com.spacechase0.minecraft.usefulpets.pet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.spacechase0.minecraft.usefulpets.UsefulPets;
import com.spacechase0.minecraft.usefulpets.UsefulPetsLog;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;

public class Level
{
	public static int MAX_LEVEL = 25;
	
	public static int getLevelExperienceRequirements( int level )
	{
		return xpReqs.get( level );
	}
	
	public static List< ItemStack > getLevelItemRequirements( int level )
	{
		return itemReqs.get( level );
	}
	
	public static void configure( Configuration config )
	{
		Property prop = config.get( "general", "maxLevel", 25 );
		prop.comment = "If you change this, delete the 'levelReqs' category and let it re-generate.";
		MAX_LEVEL = prop.getInt( 25 );
		
		Map< Integer, List< ItemStack > > levelItemReqs = new HashMap< Integer, List< ItemStack > >();
		Map< Integer, Integer > levelXpReqs = new HashMap< Integer, Integer >();
		for ( int i = 2; i <= Level.MAX_LEVEL; ++i )
		{
			String coal = "minecraft:coal_block";
			String iron = "minecraft:iron_ingot";
			String redstone = "minecraft:redstone_block";
			String gold = "minecraft:gold_ingot";
			String diamond = "minecraft:diamond";
			
			String defaultItems = "";
			if ( i <= MAX_LEVEL * 0.2 )
			{
				defaultItems += "1x" + coal;
			}
			else if ( i <= MAX_LEVEL * 0.4 )
			{
				defaultItems += "5x" + iron;
			}
			else if ( i <= MAX_LEVEL * 0.6 )
			{
				defaultItems += "3x" + redstone;
			}
			else if ( i <= MAX_LEVEL * 0.8 )
			{
				defaultItems += "5x" + gold;
			}
			else
			{
				defaultItems += "2x" + diamond;
			}
			int defaultLevel = i - 1;

			String num = String.format( "%02d", i );
			Property itemProp = config.get( "levelReqs", num + "_items", defaultItems );
			Property xpProp = config.get( "levelReqs", num + "_xp", defaultLevel );
			if ( i == 2 )
			{
				itemProp.comment  =   "Items should be separated by commas, in the format: \"QUANTITY x MOD : NAME @ DATA_VALUE\"";
				itemProp.comment += "\n(DATA_VALUE is optional, but if you don't specify it, do not add the @.)";
				itemProp.comment += "\nFor a list of valid entries, add -Dfml.dumpRegistry to your launch options, and find itemStackRegistry.csv in your minecraft directory.";
			}
			
			List< ItemStack > stacks = new ArrayList< ItemStack >();
			String itemStr = itemProp.getString();
			StringTokenizer tokens = new StringTokenizer( itemStr, "," );
			while ( tokens.hasMoreTokens() )
			{
				String token = tokens.nextToken();
				Matcher matcher = item.matcher( token );
				if ( !matcher.matches() )
				{
					UsefulPetsLog.warning( "Invalid item requirement \"" + token + "\" for pet level " + i + "! Skipping..." );
					continue;
				}
				
				int amt = Integer.parseInt( matcher.group( 1 ) );
				String modId = matcher.group( 2 );
				String itemName = matcher.group( 3 );
				
				ItemStack stack = GameRegistry.findItemStack( modId, itemName, amt );
				if ( stack == null )
				{
					UsefulPetsLog.warning( "Unknown item " + itemName + " from " + modId + " for pet level " + i + " requirements! Skipping..." );
					continue;
				}
				if ( matcher.groupCount() > 3 && matcher.group( 5 ) != null && matcher.group( 5 ).length() > 0 )
				{
					stack.setItemDamage( Integer.parseInt( matcher.group( 5 ) ) );
				}
				stacks.add( stack );
			}
			levelItemReqs.put( i, stacks );
			
			levelXpReqs.put( i, xpProp.getInt( defaultLevel ) );
		}
		
		itemReqs = levelItemReqs;
		xpReqs = levelXpReqs;
	}
	
	private static Map< Integer, List< ItemStack > > itemReqs;
	private static Map< Integer, Integer > xpReqs;
	
	private static final Pattern item = Pattern.compile( "\\s*(\\d+)\\s*x\\s*([a-zA-Z0-9\\-_]+)\\s*:\\s*([a-zA-Z0-9\\-_]+)\\s*(@\\s*([0-9]+)\\s*)?" );
}
