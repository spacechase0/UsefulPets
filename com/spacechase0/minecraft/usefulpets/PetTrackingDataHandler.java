package com.spacechase0.minecraft.usefulpets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.spacechase0.minecraft.spacecore.util.Vector3i;

public class PetTrackingDataHandler
{
	public static class PetData
	{
		public int dim;
		public Vector3i pos;
		
		public String name;
		public String tex;
		public int level;
	}
	
	public void setPetData( String owner, UUID id, PetData pet )
	{
		if ( !data.containsKey( owner ) )
		{
			data.put( owner, new HashMap< UUID, PetData >() );
		}
		
		data.get( owner ).put( id, pet );
	}
	
	public Map< UUID, PetData > getPetData( String owner )
	{
		if ( !data.containsKey( owner ) )
		{
			return null;
		}
		
		return data.get( owner );
	}
	
	public void removePetData( String owner, UUID pet )
	{
		Map< UUID, PetData > data = getPetData( owner );
		if ( data != null )
		{
			data.remove( pet );
		}
	}

	public void load( File directory )
	{
		lastDir = directory;
		data.clear();
		
		BufferedReader buffer = null;
		try
		{
			File actual = new File( directory, FILENAME );
			if ( !actual.exists() )
			{
				return;
			}
			UsefulPetsLog.fine( "Loading tracking data..." );
			
			InputStream stream = new FileInputStream( actual );
			InputStreamReader reader = new InputStreamReader( stream );
			buffer = new BufferedReader( reader );
			
			while ( true )
			{
				String line = buffer.readLine();
				if ( line == null )
				{
					break;
				}
				
				Matcher matcher = loadPattern.matcher( line );
				if ( !matcher.matches() )
				{
					UsefulPetsLog.warning( "Invalid entry (r1) in " + FILENAME + ", skipping: " + line );
					continue;
				}
				
				try
				{
					String user = matcher.group( 1 );
					UUID entityId = UUID.fromString( matcher.group( 2 ) );
	
					int dim = Integer.valueOf( matcher.group( 3 ) );
					int posX = Integer.valueOf( matcher.group( 4 ) );
					int posY = Integer.valueOf( matcher.group( 5 ) );
					int posZ = Integer.valueOf( matcher.group( 6 ) );
					
					int level = Integer.valueOf( matcher.group( 7 ) );
					String tex = matcher.group( 8 );
					String name = matcher.group( 9 );
					
					
					PetData data = new PetData();
					data.dim = dim;
					data.pos = new Vector3i( posX, posY, posZ );
					
					data.name = name;
					data.tex = tex;
					data.level = level;
					
					
					UsefulPetsLog.fine( "Found pet " + entityId + " of " + user + " at (" + posX + ", " + posY + ", " + posZ + ") in dimension " + dim );
					setPetData( user, entityId, data );
				}
				catch ( NumberFormatException exception )
				{
					UsefulPetsLog.warning( "Invalid entry (r2) in " + FILENAME + ", skipping: " + line );
					continue;
				}
			}
		}
		catch ( Exception exception )
		{
			exception.printStackTrace();
		}
		finally
		{
			if ( buffer != null )
			{
				try
				{
					buffer.close();
				}
				catch ( Exception exception )
				{
				}
			}
		}
	}
	
	public void save()
	{
		BufferedWriter buffer = null;
		try
		{
			File actual = new File( lastDir, FILENAME );
			if ( actual.exists() )
			{
				actual.delete();
			}
			UsefulPetsLog.fine( "Saving tracked pet data..." );
			
			OutputStream stream = new FileOutputStream( actual );
			OutputStreamWriter writer = new OutputStreamWriter( stream );
			buffer = new BufferedWriter( writer );
			
			Iterator< Entry< String, Map< UUID, PetData > > > it = data.entrySet().iterator();
			while ( it.hasNext() )
			{
				Entry< String, Map< UUID, PetData > > entry = it.next();
				
				Iterator< Entry< UUID, PetData > > it2 = entry.getValue().entrySet().iterator();
				while ( it2.hasNext() )
				{
					Entry< UUID, PetData > entry2 = it2.next();
					
					UUID id = entry2.getKey();
					PetData pet = entry2.getValue();
					
					String str = String.format( "%s,%s=%s{%s,%s,%s},%s,%s,%s", entry.getKey(), id, pet.dim, pet.pos.x, pet.pos.y, pet.pos.z, pet.level, pet.tex, pet.name );
					buffer.write( str + "\n" );
				}
			}
		}
		catch ( Exception exception )
		{
			exception.printStackTrace();
		}
		finally
		{
			if ( buffer != null )
			{
				try
				{
					buffer.close();
				}
				catch ( Exception exception )
				{
				}
			}
		}
	}
	
	private static Map< String, Map< UUID, PetData > > data = new HashMap< String, Map< UUID, PetData > >();
	
	private static Pattern loadPattern = Pattern.compile( "([a-zA-Z0-9\\-]+),([0-9A-Za-z\\-]+)\\=(\\-?[0-9]+)\\{(\\-?[0-9]+),(\\-?[0-9]+),(\\-?[0-9]+)\\},([0-9]+),([a-zA-Z0-9./:\\-]+),(.+)" );
	
	private File lastDir;
	private static final String FILENAME = "petTracking.dat";
}
