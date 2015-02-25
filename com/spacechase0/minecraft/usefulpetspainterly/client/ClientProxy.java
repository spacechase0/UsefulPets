package com.spacechase0.minecraft.usefulpetspainterly.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.spacechase0.minecraft.usefulpets.UsefulPetsLog;
import com.spacechase0.minecraft.usefulpets.pet.PetType;
import com.spacechase0.minecraft.usefulpetspainterly.CommonProxy;

public class ClientProxy extends CommonProxy
{
	public void postInit()
	{
		doTextures( PetType.CAT, "cat" );
		doTextures( PetType.DOG, "dog" );
		doTextures( PetType.PIG, "pig" );
		doTextures( PetType.SLIME, "slime" );
	}
	
	private void doTextures( PetType type, String dir )
	{
		String url = "/assets/usefulpetspainterly/textures/entity/" + dir + "/textures.txt";
		
		try
		{
			InputStream stream = getClass().getResourceAsStream( url );
			InputStreamReader reader = new InputStreamReader( stream );
			BufferedReader input = new BufferedReader( reader );
			
			while ( true )
			{
				String line = input.readLine();
				if ( line == null )
				{
					break;
				}
				
				type.textures.add( "usefulpetspainterly:textures/entity/" + dir + "/" + line + ".png" );
			}
		}
		catch ( Exception exception )
		{
			UsefulPetsLog.warning( "[Addon_Painterly] Failed to read textures for " + dir + ":" );
			exception.printStackTrace();
		}
	}
}
