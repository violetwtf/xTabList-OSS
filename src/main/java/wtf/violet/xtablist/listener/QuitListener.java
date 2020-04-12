/*
 * Created on 3/12/2020.
 * Copyright (c) 2020 Violet M. <vi@violet.wtf>. All Rights Reserved.
 *
 * Created for the [redacted] Minecraft network. <https://[redacted].com/>
 */

package wtf.violet.xtablist.listener;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import wtf.violet.xtablist.XTabList;

/**
 * QuitListener. This is only enabled if update on join or quit is enabled. It resets the global
 * tab list on disconnects.
 *
 * @author Violet M. vi@violet.wtf
 */
public class QuitListener implements Listener {

  @EventHandler
  public void onQuit(PlayerDisconnectEvent event) {
    XTabList.getInstance().updateTabListGlobally();
  }

}
