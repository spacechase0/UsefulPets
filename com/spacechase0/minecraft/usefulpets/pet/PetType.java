package com.spacechase0.minecraft.usefulpets.pet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityWolf;
import static net.minecraft.init.Items.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;

public enum PetType
{
	CAT( "cat", EntityOcelot.class,
	     new int[]
	     {
		 	Skill.HUNGER.id,
		 	Skill.REPELLANT.id,
		 	Skill.DEFENSE_FEATHERFALL.id,
		 },
		 new String[]
		 {
	 		"textures/entity/cat/red.png",
	 		"textures/entity/cat/siamese.png",
	 		"textures/entity/cat/black.png",
	 		"textures/entity/cat/ocelot.png",
		 } ),
	DOG( "dog", EntityWolf.class,
		 new int[]
		 {
		 	Skill.HUNGER.id,
		 	Skill.COMBAT.id,
		 	Skill.HUNGER_GROSS.id,
		 },
		 new String[]
		 {
			"textures/entity/wolf/wolf_tame.png",
			"textures/entity/wolf/wolf.png",
			"textures/entity/wolf/wolf_angry.png",
		 } ),
	PIG( "pig", EntityPig.class,
		 new int[]
		 {
			Skill.HUNGER.id,
			Skill.TRAVEL.id,
			Skill.TRAVEL_MOUNTABLE.id,
		 },
		 new String[]
		 {
			"textures/entity/pig/pig.png",
		 } ),
	SLIME( "slime", EntitySlime.class,
	       new int[]
	       {
	       	Skill.HUNGER.id,
	       	Skill.INVENTORY.id,
	       	Skill.INVENTORY_PICKUP.id,
	       },
	       new String[]
	       {
	       	"textures/entity/slime/slime.png",
	       	"textures/entity/slime/magmacube.png",
	       } ),
	SILVERFISH( "silverfish", EntitySilverfish.class,
	            new int[]
	            {
	            	Skill.HUNGER.id,
	            	Skill.COMBAT.id,
	            	Skill.DEFENSE.id,
	            },
	            new String[]
	            {
	            	"textures/entity/silverfish.png",
	            } );
	PetType( String theName, Class toConvertFrom, int[] theDefaultSkills, String[] theTextures )
	{
		name = theName;
		convertFrom = toConvertFrom;
		for ( int id : theDefaultSkills )
		{
			defaultSkills.add( id );
		}
		for ( String tex : theTextures )
		{
			textures.add( tex );
		}
	}
	
	public String getLivingSound()
	{
		if ( this.equals( CAT ) )
		{
			return ( ( rand.nextInt( 4 ) == 0 ) ? "mob.cat.purreow" : "mob.cat.meow" );
		}
		else if ( this.equals( DOG ) )
		{
			return "mob.wolf.bark";
		}
		else if ( this.equals( PIG ) )
		{
			return "mob.pig.say";
		}
		else if ( this.equals( SLIME ) )
		{
			return "mob.slime.small";
		}
		else if ( this.equals( SILVERFISH ) )
		{
			return "mob.silverfish.say";
		}
		
		return null;
	}
	
	public final String name;
	public final Class convertFrom;
	public final float sizeX = 0.6f;
	public final float sizeY = 0.8f;
	public final List< ItemStack > defaultFoodChoices = new ArrayList< ItemStack >();
	public final List< Integer > defaultSkills = new ArrayList< Integer >();
	public final List< String > textures = new ArrayList< String >();
	
	public static final Map< String, PetType > types = new HashMap< String, PetType >();
	private static final Random rand = new Random();
	
	public static PetType forName( String name )
	{
		return types.get( name );
	}
	
	static
	{
		CAT.defaultFoodChoices.add( new ItemStack( fish ) );
		CAT.defaultFoodChoices.add( new ItemStack( cooked_fished ) );
		CAT.defaultFoodChoices.add( new ItemStack( chicken ) );
		CAT.defaultFoodChoices.add( new ItemStack( cooked_chicken ) );

		DOG.defaultFoodChoices.add( new ItemStack( porkchop ) );
		DOG.defaultFoodChoices.add( new ItemStack( cooked_porkchop ) );
		DOG.defaultFoodChoices.add( new ItemStack( beef ) );
		DOG.defaultFoodChoices.add( new ItemStack( cooked_beef ) );

		PIG.defaultFoodChoices.add( new ItemStack( carrot ) );
		PIG.defaultFoodChoices.add( new ItemStack( golden_carrot ) );

		SLIME.defaultFoodChoices.add( new ItemStack( cookie ) );
		SLIME.defaultFoodChoices.add( new ItemStack( potato ) );
		
		SILVERFISH.defaultFoodChoices.add( new ItemStack( cookie ) );
		SILVERFISH.defaultFoodChoices.add( new ItemStack( potato ) );
		
		types.put( CAT.name, CAT );
		types.put( DOG.name, DOG );
		types.put( PIG.name, PIG );
		types.put( SLIME.name, SLIME );
		types.put( SILVERFISH.name, SILVERFISH );
	}
}
