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

package plugily.projects.minigamesbox.classic.handlers.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 19.12.2021
 */
public class PAPIPlaceholders extends PlaceholderExpansion {

  private final PluginMain plugin;

  public PAPIPlaceholders(PluginMain plugin) {
    this.plugin = plugin;
    register();
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public @NotNull String getIdentifier() {
    return plugin.getPluginNamePrefixLong();
  }

  @Override
  public @NotNull String getAuthor() {
    return "Plugily Projects";
  }

  @Override
  public @NotNull String getVersion() {
    return "2.0.0";
  }

  @Override
  public String onPlaceholderRequest(Player player, @NotNull String id) {
    if(player == null) {
      return null;
    }
    for(Placeholder placeholder : plugin.getPlaceholderManager().getRegisteredPAPIPlaceholders()) {
      if(placeholder.getPlaceholderType() == Placeholder.PlaceholderType.ARENA) {
        continue;
      }
      if(id.toLowerCase().equalsIgnoreCase(placeholder.getId())) {
        return placeholder.getValue(player);
      }
    }
    String[] data = id.split(":", 2);
    if(data.length < 2) {
      return null;
    }
    PluginArena arena = plugin.getArenaRegistry().getArena(data[0]);
    if(arena == null) {
      return null;
    }
    for(Placeholder placeholder : plugin.getPlaceholderManager().getRegisteredPAPIPlaceholders()) {
      if(placeholder.getPlaceholderType() == Placeholder.PlaceholderType.GLOBAL) {
        continue;
      }

      if(data[1].toLowerCase().equalsIgnoreCase(placeholder.getId())) {
        return placeholder.getValue(player, arena);
      }
    }
    return null;
  }
}
