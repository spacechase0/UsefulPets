package com.spacechase0.minecraft.usefulpets;

import java.util.List;

import net.minecraft.block.Block;
import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.spacechase0.minecraft.spacecore.BaseMod;
import com.spacechase0.minecraft.spacecore.StarterItemEventHandler;
import com.spacechase0.minecraft.spacecore.network.PacketInterceptor;
import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.item.Items;
import com.spacechase0.minecraft.usefulpets.item.PetEggItem;
import com.spacechase0.minecraft.usefulpets.network.JumpPacketMonitor;
import com.spacechase0.minecraft.usefulpets.network.PacketCodec;
import com.spacechase0.minecraft.usefulpets.pet.Level;
import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

// 1.3 - Added silverfish.
// 1.2.4 - Fixed crashing thing for pets. Updated for SpaceCore 0.7.9.
// 1.2.3 - Added domestic egg recipe (oops...).
// 1.2.2 - Added domestic eggs (for putting pets into items, which CAN be destroyed). Made the max level, skill level requirements, and leveling resource costs configurable.
// 1.2.1 - Moved over to new UUID stuff for owning pets. Fixed name tags not persisting when converting. Spiders running away is broken, not sure how to fix.
// 1.2.0 - Added slimes. Added a couple of skills. Made leveling easier (configurable). Increased max level to 25. Non-vanilla pets require a potion of weakness applied to tame. Updated for Minecraft 1.7.2.
// 1.1.6 - Updated for SpaceCore 0.6.0.
// 1.1.5 - Added a "hostile" skill, attacking any hostile mobs in sight. Allowed pigs to eat golden carrots. Made jumping (when mounted) take a good bit of hunger. Fixed pets jumping insanely high when mounted (>10 blocks -> 3 blocks). Hopefully fixed pets duplicating when leaving the nether.
// 1.1.4 - Cleaned up some stuff with the jumping code.
// 1.1.3 - Fixed compatibility with Larger Inventory/Hotbar.
// 1.1.2 - Fixed minor issue with loading tracking data for pets with addon textures.
// 1.1.1 - Fixed dependency issues.
// 1.1 - Added pet tracking, for pets you own. Added mount-related skills. Added health upgrade skills. Added message for if someone other than the owner tries to use a pet. Made sitting not consume hunger. Made riding pets take more hunger. Added config option for dungeon loot (default true). Added config options for craftable pet claws, saddles, and armor (default false), with expensive versions (default false). Fixed pets always sitting on world reload. Fixed bug with pets with no health still attacking targets, and still being mountable. Fixed pets showing as cats by default for a second or so after loading. Fixed pet saddles not showing until inventory is viewed. Fixed missing pig saddle graphics. Fixed bug where other players could make your pet sit/stand.
// 1.0 - Add option for what texture to use for your pet. Added pig as a pet option. Fixed theoretical bug where you sometimes would not feed your pet.
// 0.1.6 - Added fancy stat rendering.
// 0.1.5 - Fixed skill desync from client to server, caused crashing and other weird things.
// 0.1.4 - Added some in-game help. Made starting with pet wand configurable. Fixed idle hunger not using saturation. Fixed items being consumed in creative. Fixed requiring materials for leveling in creative. Possibly fixed bug with pets duplicating client-side. Now requires SpaceCore
// 0.1.3 had a version of 0.1.4 in @Mod. Oops :P

@Mod( modid = "SC0_UsefulPets", useMetadata = true, dependencies="required-after:SC0_SpaceCore" )
public class UsefulPets extends BaseMod
{
	public UsefulPets()
	{
		super( "usefulpets" );
	}

	@Instance( "SC0_UsefulPets" )
	public static UsefulPets instance;
	
	@SidedProxy( serverSide = "com.spacechase0.minecraft.usefulpets.CommonProxy",
			     clientSide = "com.spacechase0.minecraft.usefulpets.client.ClientProxy" )
	public static CommonProxy proxy;

	@Override
	@EventHandler
	public void init( FMLInitializationEvent event )
	{
		super.init( event );
		
		petData = new PetTrackingDataHandler();
		registerRecipes();
		registerEntities();
		registerChestStuff();
		proxy.init();
		
		// Make sure it shows up in the config file
		config.get( "general", "fancyStatRender", true ).getBoolean( true );
		
		network = new PacketCodec();
		NetworkRegistry.INSTANCE.registerGuiHandler( this, new GuiHandler() );

		MinecraftForge.EVENT_BUS.register( avoidanceHandler = new AvoidanceEventHandler() );
		PacketInterceptor.addMonitor( new JumpPacketMonitor() );
		
		if ( config.get( "general", "kindnessToSpacechase0", true ).getBoolean( true ) )
		{
			MinecraftForge.EVENT_BUS.register( new KindnessToSpacechase0() );
		}
	}

	@Override
	@EventHandler
	public void postInit( FMLPostInitializationEvent event )
	{
		Skill.configure( config );
		Level.configure( config );
		
		super.postInit( event );
	}
	
	@EventHandler
	public void serverStarting( FMLServerStartingEvent event )
	{
		MinecraftServer server = event.getServer();
		ISaveHandler save = server.worldServerForDimension( 0 ).getSaveHandler();
		
		petData.load( save.getMapFileFromName( "MEOW" ).getParentFile() );
	}
	
	@EventHandler
	public void serverStopping( FMLServerStoppingEvent event )
	{
		petData.save();
	}
	
	private void registerRecipes()
	{
		GameRegistry.addRecipe( new ItemStack( items.wand ),
				                "*",
				                "|",
				                '*', gold_nugget,
				                '|', bone );

		GameRegistry.addRecipe( new ItemStack( items.wand ),
				                " *",
				                "| ",
				                '*', gold_nugget,
				                '|', bone );
		
		GameRegistry.addRecipe( new ItemStack( items.domesticEgg ),
		                        "*u*",
		                        "uOu",
		                        "*u*",
		                        '*', gold_nugget,
		                        'u', new ItemStack( dye, 1, 4 ),
		                        'O', egg );
		
		if ( config.get( "general", "craftableClaws", false ).getBoolean( false ) )
		{
			ItemStack iron = new ItemStack( iron_ingot );
			ItemStack gold = new ItemStack( gold_ingot );
			ItemStack diamondGem = new ItemStack( diamond );
			
			if ( config.get( "general", "craftableClawsAreExpensive", false ).getBoolean( false ) )
			{
				iron = new ItemStack( iron_block );
				gold = new ItemStack( gold_block );
				diamondGem = new ItemStack( diamond );
			}
			
			GameRegistry.addRecipe( new ItemStack( items.ironClaws ),
					                " **",
					                "* *",
					                '*', iron );

			GameRegistry.addRecipe( new ItemStack( items.goldClaws ),
					                " **",
					                "* *",
					                '*', gold );

			GameRegistry.addRecipe( new ItemStack( items.diamondClaws ),
					                " **",
					                "* *",
					                '*', diamondGem );
		}

		if ( config.get( "general", "craftableSaddle", false ).getBoolean( false ) )
		{
			if ( config.get( "general", "craftableSaddleIsExpensive", false ).getBoolean( false ) )
			{
				GameRegistry.addRecipe( new ItemStack( saddle ),
						                "HHH",
						                "H#H",
						                "# #",
						                '#', iron_block,
						                'H', leather );
			}
			else
			{
				GameRegistry.addRecipe( new ItemStack( saddle ),
		                                "HHH",
		                                "H#H",
		                                "# #",
		                                '#', iron_ingot,
		                                'H', leather );
			}
		}
		
		if ( config.get( "general", "craftablePetArmor", false ).getBoolean( false ) )
		{
			ItemStack iron = new ItemStack( iron_ingot );
			ItemStack gold = new ItemStack( gold_ingot );
			ItemStack diamondGem = new ItemStack( diamond );
			
			if ( config.get( "general", "craftablePetArmorIsExpensive", false ).getBoolean( false ) )
			{
				iron = new ItemStack( iron_block );
				gold = new ItemStack( gold_block );
				diamondGem = new ItemStack( diamond_block );
			}
			
			GameRegistry.addRecipe( new ItemStack( iron_horse_armor ),
					                "***",
					                " * ",
					                '*', iron );

			GameRegistry.addRecipe( new ItemStack( golden_horse_armor ),
	                                "***",
	                                " * ",
					                '*', gold );

			GameRegistry.addRecipe( new ItemStack( diamond_horse_armor ),
	                                "***",
	                                " * ",
					                '*', diamondGem );
		}
	}
	
	private void registerEntities()
	{
		petEntityId = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID( PetEntity.class, "Pet", petEntityId );
	}
	
	private void registerChestStuff()
	{
		if ( config.get( "general", "bonusChestAdditions", true ).getBoolean( true ) )
		{
			ChestGenHooks bonusChest = ChestGenHooks.getInfo( ChestGenHooks.BONUS_CHEST );
			bonusChest.addItem( new WeightedRandomChestContent( items.domesticEgg.getPetEgg( "dog" ), 1, 1, 3 ) ); // Wolf
			bonusChest.addItem( new WeightedRandomChestContent( items.domesticEgg.getPetEgg( "cat" ), 1, 1, 3 ) ); // Ocelot
		}
		
		if ( config.get( "general", "dungeonChestAdditions", true ).getBoolean( true ) )
		{
			ChestGenHooks dungeonChest = ChestGenHooks.getInfo( ChestGenHooks.DUNGEON_CHEST );
			dungeonChest.addItem( new WeightedRandomChestContent( new ItemStack( items.goldClaws ), 1, 1, 6 ) );
			dungeonChest.addItem( new WeightedRandomChestContent( new ItemStack( items.ironClaws ), 1, 1, 4 ) );
			dungeonChest.addItem( new WeightedRandomChestContent( new ItemStack( items.diamondClaws ), 1, 1, 2 ) );
		}
		
		if ( config.get( "general", "startWithWand", true ).getBoolean( true ) )
		{
			StarterItemEventHandler.addStarterItem( "PetWand", new ItemStack( items.wand ) );
		}
	}
	
	public static Configuration config;
	public static Items items;
	public PetTrackingDataHandler petData;
	
	public int petEntityId;
	
	public static PacketCodec network;
	public AvoidanceEventHandler avoidanceHandler;
	
	public static final int PET_INVENTORY_GUI_ID = 0;
	public static final int PET_SKILLS_GUI_ID = 1;
	public static final int PET_TRACKING_GUI_ID = 2;
}
