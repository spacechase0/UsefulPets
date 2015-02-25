package com.spacechase0.minecraft.usefulpets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

public class UsefulPetsLog
{
	public static void info( String str )
	{
		logger.info( str );
	}
	
	public static void fine( String str )
	{
		logger.info( str );
	}
	
	public static void warning( String str )
	{
		logger.warn( str );
	}
	
	public static void severe( String str )
	{
		logger.error( str );
	}
	
	private static Logger makeLogger()
	{
		Logger logger = LogManager.getLogger( "SC0_UsefulPets" );
		
		return logger;
	}
	
	private static final Logger logger = makeLogger();
}
