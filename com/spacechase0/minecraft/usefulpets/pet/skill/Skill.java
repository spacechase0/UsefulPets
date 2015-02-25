package com.spacechase0.minecraft.usefulpets.pet.skill;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureAttribute;
import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import com.spacechase0.minecraft.usefulpets.pet.food.FoodType;

public class Skill
{
	public Skill( int theId, String theName, float px, float py, ItemStack theIcon )
	{
		id = theId;
		name = theName;
		levelReq = 1;
		x = px;
		y = py;
		icon = theIcon;
		
		if ( skills.containsKey( id ) )
		{
			throw new IllegalArgumentException( "Skill " + id + " already exists." );
		}
		skills.put( id, this );
	}
	
	public Skill( int theId, String theName, float px, float py, ItemStack theIcon, int theLevelReq, int[] theSkillReqs )
	{
		this( theId, theName, px, py, theIcon );
		levelReq = theLevelReq;
		skillReqs = theSkillReqs;
	}
	
	public final int id;
	public final String name;
	public final float x;
	public final float y;
	public int levelReq;
	public int[] skillReqs;
	public /*final*/ ItemStack icon;
	
	public static final Map< Integer, Skill > skills = new HashMap< Integer, Skill >();
	
	public static Skill forId( int id )
	{
		return skills.get( id  );
	}
	
	public static void configure( Configuration config )
	{
		for ( Skill skill : skills.values() )
		{
			skill.levelReq = config.get( "skillLevelReqs", skill.name, skill.levelReq ).getInt( skill.levelReq );
		}
	}
	
	public static Skill HUNGER = new FoodSkill( 0, "", FoodType.SPECIES, new ItemStack( porkchop ) );
	public static Skill HUNGER_OTHER = new FoodSkill( 1, "eatOther", FoodType.OTHER_SPECIES, new ItemStack( fish ) );
	public static Skill HUNGER_PLANTS = new FoodSkill( 2, "eatPlants", FoodType.PLANTS, new ItemStack( carrot ) );
	public static Skill HUNGER_PROCESSED = new FoodSkill( 3, "eatProcessed", 5, FoodType.PROCESSED, new ItemStack( cookie ) );
	public static Skill HUNGER_GROSS = new FoodSkill( 4, "eatGross", 10, FoodType.GROSS, new ItemStack( rotten_flesh ) );

	public static Skill COMBAT = new AttackSkill( 5, "", 4, new ItemStack( wooden_sword ) );
	public static Skill COMBAT_UPGRADE1 = new AttackSkill( 6, "upgrade1", 1, new ItemStack( stone_sword ) );
	public static Skill COMBAT_UPGRADE2 = new AttackSkill( 7, "upgrade2", 5, 1, new ItemStack( iron_sword ) );
	public static Skill COMBAT_UPGRADE3 = new AttackSkill( 8, "upgrade3", 10, 1, new ItemStack( diamond_sword ) );
	public static Skill COMBAT_HOSTILE = new Skill( 34, "combat.hostile", 1.5f, 0.4f, new ItemStack( blaze_powder ), 10, new int[] { 5 } );

	public static Skill REPELLANT = new RepellantSkill( 9, "", new ItemStack( net.minecraft.init.Items.skull, 1, 4 ) );
	public static Skill REPELLANT_RADIUS = new RepellantSkill( 10, "larger", 10, 4.f, new ItemStack( stick ) );
	public static Skill REPELLANT_UNDEAD = new RepellantSkill( 11, "undead", 6, EnumCreatureAttribute.UNDEAD, new ItemStack( net.minecraft.init.Items.skull, 1, 0 ) );
	public static Skill REPELLANT_SPIDERS = new RepellantSkill( 12, "spiders", 3, EnumCreatureAttribute.ARTHROPOD, new ItemStack( spider_eye ) );

	public static Skill INVENTORY = new Skill( 13, "inventory", 4.5f, 0, new ItemStack( chest ), 1, null );
	public static Skill INVENTORY_UPGRADE1 = new Skill( 14, "inventory.upgrade1", 5.5f, 1.5f, new ItemStack( chest ), 5, new int[] { 13 } );
	public static Skill INVENTORY_UPGRADE2 = new Skill( 15, "inventory.upgrade2", 5.5f, 2.5f, new ItemStack( chest ), 10, new int[] { 14 } );
	public static Skill INVENTORY_UPGRADE3 = new Skill( 35, "inventory.upgrade3", 6.5f, 2.5f, new ItemStack( chest ), 15, new int[] { 15 } );
	public static Skill INVENTORY_UPGRADE4 = new Skill( 36, "inventory.upgrade4", 6.5f, 1.5f, new ItemStack( chest ), 20, new int[] { 35 } );
	public static Skill INVENTORY_FEEDING = new Skill( 16, "inventory.selfSufficient", 4.5f, -1.5f, new ItemStack( pumpkin_pie ), 10, new int[] { 0, 13 } );
	public static Skill INVENTORY_ARMOR = new Skill( 17, "inventory.armor", 3.5f, 1.5f, new ItemStack( golden_horse_armor ), 5, new int[] { 5, 13 } );
	public static Skill INVENTORY_WEAPON = new Skill( 18, "inventory.weapon", 3.5f, 2.5f, new ItemStack( iron_sword ), 17, new int[] { 5, 13 } );
	public static Skill INVENTORY_PICKUP = new Skill( 37, "inventory.pickup", 6, 0, new ItemStack( hopper ), 5, new int[] { 13 } );

	public static Skill DEFENSE = new DefenseSkill( 19, "", 1, 0.1f, new ItemStack( leather_chestplate ) );
	public static Skill DEFENSE_UPGRADE1 = new DefenseSkill( 20, "upgrade1", 5, 0.15f, new ItemStack( iron_chestplate ) );
	public static Skill DEFENSE_UPGRADE2 = new DefenseSkill( 21, "upgrade2", 12, 0.25f, new ItemStack( diamond_chestplate ) );
	public static Skill DEFENSE_FIRE = new DefenseSkill( 22, "fire", 10, new String[] { "inFire", "onFire", "lava" }, new ItemStack( fire ) );
	public static Skill DEFENSE_BREATHLESS = new DefenseSkill( 23, "breathless", 5, new String[] { "inWall", "drown" }, new ItemStack( water ) );
	public static Skill DEFENSE_FEATHERFALL = new DefenseSkill( 24, "featherFall", 7, new String[] { "fall" }, new ItemStack( feather ) );
	
	public static Skill TRAVEL = new SpeedSkill( 25, "", 3, 0.2f, new ItemStack( potionitem, 1, 8194 ) );
	public static Skill TRAVEL_UPGRADE1 = new SpeedSkill( 26, "upgrade1", 15, 0.2f, new ItemStack( potionitem, 1, 8194 ) );
	public static Skill TRAVEL_UPGRADE2 = new SpeedSkill( 27, "upgrade2", 17, 0.3f, new ItemStack( potionitem, 1, 8194 + 16384 ) );
	public static Skill TRAVEL_MOUNTABLE = new Skill( 28, "travel.mountable", 0.5f, 9.f, new ItemStack( saddle ), 10, new int[] { 25 } );
	public static Skill TRAVEL_SWIMMING = new Skill( 31, "travel.aquaticMount", 0.5f, 10.25f, new ItemStack( water ), 12, new int[] { 28 } );
	public static Skill TRAVEL_SWIMCONTROL = new Skill( 32, "travel.swimControl", 1.75f, 10.25f, new ItemStack( water ), 15, new int[] { 31, 23 } );
	public static Skill TRAVEL_MOUNTJUMP = new Skill( 33, "travel.mountJump", 1.75f, 9.f, new ItemStack( feather ), 12, new int[] { 28 } );
	
	public static Skill HEALTH = new HealthSkill( 29, "", 7, 10, new ItemStack( golden_apple ) );
	public static Skill HEALTH_UPGRADE1 = new HealthSkill( 30, "upgrade1", 14, 10, new ItemStack( golden_apple, 1, 1 ) );
}
