/*
 * TheBridge - Defend your base and try to wipe out the others
 * Copyright (C)  2021  Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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

package plugily.projects.minigamesbox.classic.utils.version.events;

import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 10.02.2021
 */
public class EventsInitializer {

  public EventsInitializer(JavaPlugin plugin) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_10_R2)) {
      new LegacyEvents(plugin);
    } else {
      new Events(plugin);
    }
  }

}
