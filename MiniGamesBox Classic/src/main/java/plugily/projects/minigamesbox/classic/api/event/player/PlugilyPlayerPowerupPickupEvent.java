

package plugily.projects.minigamesbox.classic.api.event.player;
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

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import plugily.projects.minigamesbox.classic.api.event.PlugilyEvent;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.powerup.BasePowerup;
import plugily.projects.minigamesbox.classic.handlers.powerup.PowerupRegistry;

/**
 * @author Tigerpanzer_02
 * @see PowerupRegistry
 * <p>
 * Called when player pick up a power-up.
 */
public class PlugilyPlayerPowerupPickupEvent extends PlugilyEvent implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private final Player player;
  private final BasePowerup powerup;
  private boolean isCancelled = false;

  public PlugilyPlayerPowerupPickupEvent(PluginArena eventArena, Player player, BasePowerup powerup) {
    super(eventArena);
    this.player = player;
    this.powerup = powerup;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  public Player getPlayer() {
    return player;
  }

  public BasePowerup getPowerup() {
    return powerup;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  @Override
  public boolean isCancelled() {
    return isCancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    isCancelled = cancelled;
  }
}
