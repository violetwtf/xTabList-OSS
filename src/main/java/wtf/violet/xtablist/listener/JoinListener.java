/*
 * Created on 3/12/2020.
 * Copyright (c) 2020 Violet M. <vi@violet.wtf>. All Rights Reserved.
 *
 * Created for the [redacted] Minecraft network. <https://[redacted].com/>
 */

package wtf.violet.xtablist.listener;

import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import wtf.violet.xtablist.XTabList;

public class JoinListener implements Listener {

  // Moved from PostLoginEvent to ServerConnectedEvent, as there was (obviously) no server during
  // a PostLoginEvent.
  @EventHandler
  public void onPostLogin(ServerConnectedEvent event) {
    XTabList instance = XTabList.getInstance();

    if (instance.isUpdateOnJoinQuit()) {
      instance.updateTabListGlobally();
    }

    instance.setPlayerList(event.getPlayer(), event.getServer());
  }

}
