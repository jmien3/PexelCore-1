package me.dobrakmato.plugins.pexel.PexelCore;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * All data of plugin is stored in this class.
 * 
 * @author Mato Kormuth
 * 
 */
public class StorageEngine
{
	private static final Map<UUID, PlayerProfile>	profiles	= new HashMap<UUID, PlayerProfile>();
	private static final Map<String, Minigame>		minigames	= new HashMap<String, Minigame>();
	private static final Map<String, ProtectedArea>	areas		= new HashMap<String, ProtectedArea>();
	private static final Map<String, MinigameArena>	arenas		= new HashMap<String, MinigameArena>();
	@SuppressWarnings("rawtypes")
	private static final Map<String, Class>			aliases		= new HashMap<String, Class>();
	private static final Map<String, Lobby>			lobbies		= new HashMap<String, Lobby>();
	private static final Map<String, TeleportGate>	gates		= new HashMap<String, TeleportGate>();
	private static boolean							initialized	= false;
	
	public static void initialize(final PexelCore core)
	{
		if (!StorageEngine.initialized)
			StorageEngine.initialized = true;
	}
	
	public static List<UUID> getFriends(final Player player)
	{
		return StorageEngine.profiles.get(player.getUniqueId()).getFriends();
	}
	
	public static List<UUID> getFoes(final Player player)
	{
		return StorageEngine.profiles.get(player.getUniqueId()).getFoes();
	}
	
	protected static Map<String, ProtectedArea> getAreas()
	{
		return StorageEngine.areas;
	}
	
	/**
	 * Returns profile of specified player.
	 * 
	 * @param player
	 * @return
	 */
	public static PlayerProfile getProfile(final UUID player)
	{
		return profiles.get(player);
	}
	
	public static Minigame getMinigame(final String name)
	{
		return StorageEngine.minigames.get(name);
	}
	
	public static void addMinigame(final Minigame minigame)
	{
		StorageEngine.minigames.put(minigame.getName(), minigame);
	}
	
	public static void addArena(final MinigameArena arena)
	{
		StorageEngine.arenas.put(arena.getName(), arena);
		StorageEngine.areas.put(arena.getName(), arena);
	}
	
	public static int getMinigameArenasCount()
	{
		return StorageEngine.arenas.size();
	}
	
	public static int getMinigamesCount()
	{
		return StorageEngine.minigames.size();
	}
	
	protected static Map<String, Minigame> getMinigames()
	{
		return StorageEngine.minigames;
	}
	
	protected static Map<String, MinigameArena> getArenas()
	{
		return StorageEngine.arenas;
	}
	
	public static MinigameArena getArena(final String arenaName)
	{
		return StorageEngine.arenas.get(arenaName);
	}
	
	public static void addGate(final String name, final TeleportGate gate)
	{
		StorageEngine.gates.put(name, gate);
	}
	
	public static TeleportGate getGate(final String name)
	{
		return StorageEngine.gates.get(name);
	}
	
	public static void removeGate(final String name)
	{
		StorageEngine.gates.remove(name);
	}
	
	@SuppressWarnings("rawtypes")
	public static void registerArenaAlias(final Class arenaClass,
			final String alias)
	{
		StorageEngine.aliases.put(alias, arenaClass);
	}
	
	@SuppressWarnings("rawtypes")
	public static Class getByAlias(final String alias)
	{
		return StorageEngine.aliases.get(alias);
	}
	
	@SuppressWarnings("rawtypes")
	protected static Map<String, Class> getAliases()
	{
		return StorageEngine.aliases;
	}
	
	public static void addLobby(final Lobby lobby)
	{
		StorageEngine.lobbies.put(lobby.getName(), lobby);
	}
	
	public static Lobby getLobby(final String lobbyName)
	{
		return StorageEngine.lobbies.get(lobbyName);
	}
	
	/**
	 * Saves player's profile to file.
	 * 
	 * @param uniqueId
	 */
	public static void saveProfile(final UUID uniqueId)
	{
		Log.info("Saving profile for " + uniqueId.toString() + " to disk...");
		StorageEngine.profiles.get(uniqueId).save(Paths.playerProfile(uniqueId));
	}
	
	/**
	 * Loads player profile from disk or creates an empty one.
	 * 
	 * @param uniqueId
	 */
	public static void loadProfile(final UUID uniqueId)
	{
		File f = new File(Paths.playerProfile(uniqueId));
		if (f.exists())
		{
			Log.info("Load profile for " + uniqueId + "...");
			StorageEngine.profiles.put(uniqueId,
					PlayerProfile.load(Paths.playerProfile(uniqueId)));
		}
		else
		{
			Log.info("Creating new profile for " + uniqueId.toString());
			StorageEngine.profiles.put(uniqueId, new PlayerProfile(uniqueId));
		}
	}
	
	public static void saveData()
	{
		//Save lobbies.
		YamlConfiguration yaml_lobbies = new YamlConfiguration();
		int i_lobbies = 0;
		for (Lobby l : StorageEngine.lobbies.values())
		{
			yaml_lobbies.set("lobbies.lobby" + i_lobbies + ".name", l.getName());
			yaml_lobbies.set("lobbies.lobby" + i_lobbies + ".checkinterval",
					l.getCheckInterval());
			yaml_lobbies.set("lobbies.lobby" + i_lobbies + ".thresholdY",
					l.getThresholdY());
			l.getRegion().serialize(yaml_lobbies,
					"lobbies.lobby" + i_lobbies + ".region");
			i_lobbies++;
		}
		try
		{
			yaml_lobbies.save(new File(Paths.lobbiesPath()));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		//Save arenas
		YamlConfiguration yaml_arenas = new YamlConfiguration();
		int i_arenas = 0;
		for (MinigameArena a : StorageEngine.arenas.values())
		{
			yaml_arenas.set("arenas.arena" + i_arenas + ".name", a.getName());
			yaml_arenas.set("arenas.arena" + i_arenas + ".type",
					a.getClass().getSimpleName());
			yaml_arenas.set("arenas.arena" + i_arenas + ".minigame",
					a.getMinigame().getName());
			yaml_arenas.set("arenas.arena" + i_arenas + ".slots",
					a.getMaximumSlots());
			yaml_arenas.set("arenas.arena" + i_arenas + ".owner", a.getOwner());
			a.getRegion().serialize(yaml_arenas,
					"arenas.arena" + i_arenas + ".region");
			i_arenas++;
		}
		try
		{
			yaml_arenas.save(new File(Paths.arenasPath()));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		//Save gates
		YamlConfiguration yaml_gates = new YamlConfiguration();
		int i_gates = 0;
		for (String key : StorageEngine.gates.keySet())
		{
			TeleportGate tg = StorageEngine.gates.get(key);
			yaml_gates.set("gates.gate" + i_gates + ".name", key);
			yaml_gates.set("gates.gate" + i_gates + ".type", tg.getType());
			yaml_gates.set("gates.gate" + i_gates + ".content", tg.getContent());
			tg.getRegion().serialize(yaml_gates,
					"gates.gate" + i_gates + ".region");
			i_gates++;
		}
		try
		{
			yaml_gates.save(new File(Paths.gatesPath()));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void gateEnter(final Player player, final Location location)
	{
		//Find the right gate
		for (TeleportGate gate : StorageEngine.gates.values())
			if (gate.getRegion().intersects(location))
				gate.execute(player);
	}
}
