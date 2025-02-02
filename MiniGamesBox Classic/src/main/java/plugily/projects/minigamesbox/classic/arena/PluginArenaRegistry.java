/*
 * MiniGamesBox - Library box with massive content that could be seen as minigames core.
 * Copyright (C)  2021  Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package plugily.projects.minigamesbox.classic.arena;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class PluginArenaRegistry {

  private final List<PluginArena> arenas = new ArrayList<>();
  private final PluginMain plugin;
  private final List<World> arenaIngameWorlds = new ArrayList<>();
  private final List<World> arenaWorlds = new ArrayList<>();

  private int bungeeArena = -999;

  public PluginArenaRegistry(PluginMain plugin) {
    this.plugin = plugin;
  }

  /**
   * Checks if player is in any arena
   *
   * @param player player to check
   * @return true when player is in arena, false if otherwise
   */
  public boolean isInArena(@NotNull Player player) {
    return getArena(player) != null;
  }

  /**
   * Returns arena where the player is
   *
   * @param player target player
   * @return Arena or null if not playing
   * @see #isInArena(Player) to check if player is playing
   */
  @Nullable
  public PluginArena getArena(Player player) {
    if(player == null) {
      return null;
    }

    java.util.UUID playerId = player.getUniqueId();

    for(PluginArena loopArena : arenas) {
      for(Player arenaPlayer : loopArena.getPlayers()) {
        if(arenaPlayer.getUniqueId().equals(playerId)) {
          return loopArena;
        }
      }
    }

    return null;
  }

  /**
   * Returns arena based by ID
   *
   * @param id name of arena
   * @return Arena or null if not found
   */
  @Nullable
  public PluginArena getArena(String id) {
    for(PluginArena loopArena : arenas) {
      if(loopArena.getId().equalsIgnoreCase(id)) {
        return loopArena;
      }
    }
    return null;
  }

  public int getArenaPlayersOnline() {
    int players = 0;
    for(PluginArena arena : arenas) {
      players += arena.getPlayers().size();
    }
    return players;
  }

  public void registerArena(PluginArena arena) {
    plugin.getDebugger().debug("[{0}] Instance registered", arena.getId());
    arenas.add(arena);
    World startWorld = arena.getStartLocation().getWorld();
    World endWorld = arena.getEndLocation().getWorld();
    World lobbyWorld = arena.getLobbyLocation().getWorld();
    if(startWorld != null) {
      arenaIngameWorlds.add(startWorld);
      arenaWorlds.add(startWorld);
    }
    if(endWorld != null) {
      arenaWorlds.add(endWorld);
    }
    if(lobbyWorld != null) {
      arenaWorlds.add(lobbyWorld);
    }
  }

  public void unregisterArena(PluginArena arena) {
    plugin.getDebugger().debug("[{0}] Instance unregistered", arena.getId());
    arenas.remove(arena);

    World startWorld = arena.getStartLocation().getWorld();
    World endWorld = arena.getEndLocation().getWorld();
    World lobbyWorld = arena.getLobbyLocation().getWorld();
    if(startWorld != null) {
      arenaIngameWorlds.remove(startWorld);
      arenaWorlds.remove(startWorld);
    }
    if(endWorld != null) {
      arenaWorlds.remove(endWorld);
    }
    if(lobbyWorld != null) {
      arenaWorlds.remove(lobbyWorld);
    }
  }

  public PluginArena getNewArena(String id) {
    return new PluginArena(id);
  }

  public void registerArenas() {
    plugin.getDebugger().debug("[ArenaRegistry] Initial arenas registration");
    long start = System.currentTimeMillis();
    if(!arenas.isEmpty()) {
      for(PluginArena arena : new ArrayList<>(arenas)) {
        unregisterArena(arena);
      }
    }
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    ConfigurationSection section = config.getConfigurationSection("instances");
    if(section == null) {
      plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_NO_INSTANCES_CREATED").asKey().build());
      return;
    }
    for(String id : section.getKeys(false)) {
      if(id.equalsIgnoreCase("default")) {
        continue;
      }
      registerArena(id);
    }
    plugin.getDebugger().debug("[ArenaRegistry] Arenas registration completed took {0}ms", System.currentTimeMillis() - start);
  }

  public void registerArena(String key) {
    plugin.getDebugger().debug("[ArenaRegistry] Initial arena registration for " + key);
    long start = System.currentTimeMillis();
    if(!arenas.isEmpty()) {
      List<PluginArena> sameArenas = arenas.stream().filter(pluginArena -> pluginArena.getId().equals(key)).collect(Collectors.toList());
      if(!sameArenas.isEmpty()) {
        for(PluginArena arena : new ArrayList<>(sameArenas)) {
          unregisterArena(arena);
        }
      }
    }

    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    ConfigurationSection section = config.getConfigurationSection("instances");
    if(section == null) {
      plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_NO_INSTANCES_CREATED").asKey().build());
      return;
    }

    PluginArena arena = getNewArena(key);

    if(!validatorChecks(section, arena, key) || !additionalValidatorChecks(section, arena, key)) {
      section.set(key + ".isdone", false);
      ConfigUtils.saveConfig(plugin, config, "arenas");
      registerArena(arena);
    } else {
      arena.setReady(true);
      registerArena(arena);
      arena.start();
      plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INSTANCE_STARTED").asKey().arena(arena).build());
    }

    ConfigUtils.saveConfig(plugin, config, "arenas");
    plugin.getSignManager().loadSigns();

    plugin.getDebugger().debug("[ArenaRegistry] Arena registration for " + key + " completed took {0}ms", System.currentTimeMillis() - start);
  }

  public boolean additionalValidatorChecks(ConfigurationSection section, PluginArena arena, String id) {
    return true;
  }

  private boolean validatorChecks(ConfigurationSection section, PluginArena arena, String id) {

    arena.setMinimumPlayers(section.getInt(id + ".minimumplayers", 3));
    arena.setMaximumPlayers(section.getInt(id + ".maximumplayers", 16));
    arena.setMapName(section.getString(id + ".mapname", id));

    Location lobbyLoc = LocationSerializer.getLocation(section.getString(id + ".lobbylocation", null));
    if(lobbyLoc != null) {
      arena.setLobbyLocation(lobbyLoc);
    }
    Location startLoc = LocationSerializer.getLocation(section.getString(id + ".startlocation", null));
    if(startLoc != null) {
      arena.setStartLocation(startLoc);
    }
    Location endLoc = LocationSerializer.getLocation(section.getString(id + ".endlocation", null));
    if(endLoc != null) {
      arena.setEndLocation(endLoc);
    }
    Location spectatorLoc = LocationSerializer.getLocation(section.getString(id + ".spectatorlocation", null));
    if(spectatorLoc != null) {
      arena.setSpectatorLocation(spectatorLoc);
    }
    if(lobbyLoc == null || startLoc == null || endLoc == null || spectatorLoc == null) {
      plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().value("LOCATIONS ARE INVALID").arena(arena).build());
      return false;
    }

    if(!section.getBoolean(id + ".isdone", false)) {
      plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().value("NOT VALIDATED").arena(arena).build());
      return false;
    }

    return true;
  }

  @NotNull
  public List<PluginArena> getArenas() {
    return arenas;
  }

  public List<World> getArenaIngameWorlds() {
    return arenaIngameWorlds;
  }

  public List<World> getArenaWorlds() {
    return arenaWorlds;
  }

  public void shuffleBungeeArena() {
    if(!arenas.isEmpty()) {
      bungeeArena = ThreadLocalRandom.current().nextInt(arenas.size());
    }
  }

  public int getBungeeArena() {
    if(bungeeArena == -999 && !arenas.isEmpty()) {
      bungeeArena = ThreadLocalRandom.current().nextInt(arenas.size());
    }
    return bungeeArena;
  }
}
