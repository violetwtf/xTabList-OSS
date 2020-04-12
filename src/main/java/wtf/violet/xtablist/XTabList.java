/*
 * Created on 3/12/2020.
 * Copyright (c) 2020 Violet M. <vi@violet.wtf>. All Rights Reserved.
 *
 * Created for the [redacted] Minecraft network. <https://[redacted].com/>
 */

package wtf.violet.xtablist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import wtf.violet.xtablist.listener.JoinListener;
import wtf.violet.xtablist.listener.QuitListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * xTabList: Add a header and footer to the tab list.
 * @author Violet M. vi@violet.wtf
 */
public final class XTabList extends Plugin {

  private static final String HEADER_KEY = "header";
  private static final String FOOTER_KEY = "footer";
  private static final String UPDATE_ON_JOIN_QUIT_KEY = "update-on-join-or-quit";
  private static final String UPDATE_REGULARLY_KEY = "update-regularly";
  private static final String UPDATE_INTERVAL_KEY = "update-interval";
  private static final String DEBUG_KEY = "debug";
  private static final String HEADER_DEFAULT = "&6&l{server-name}";
  private static final String FOOTER_DEFAULT = "Players: &a{player-count} &fPing: &a{ping}";
  private static final boolean UPDATE_ON_JOIN_QUIT_DEFAULT = true;
  private static final boolean UPDATE_REGULARLY_DEFAULT = true;
  private static final boolean DEBUG_DEFAULT = false;
  private static final int UPDATE_INTERVAL_DEFAULT = 5;

  private static XTabList instance;

  private String header;
  private String footer;
  private boolean updateOnJoinQuit;
  private boolean debug;
  private Configuration config;

  /** Called on plugin enable. */
  @Override
  public void onEnable() {
    instance = this;

    ProxyServer proxy = getProxy();
    PluginManager manager = proxy.getPluginManager();

    manager.registerListener(this, new JoinListener());

    Logger logger = getLogger();

    ConfigurationProvider configProvider = ConfigurationProvider
        .getProvider(YamlConfiguration.class);

    File dataFolder = getDataFolder();
    File configFile = new File(dataFolder, "config.yml");

    try {
      config = configProvider.load(configFile);
    } catch (IOException exception) {
      logger.warning("Could not load config.yml, using defaults!");

      config = new Configuration();

      config.set(HEADER_KEY, singleton(HEADER_DEFAULT));
      config.set(FOOTER_KEY, singleton(FOOTER_DEFAULT));
      config.set(UPDATE_ON_JOIN_QUIT_KEY, UPDATE_ON_JOIN_QUIT_DEFAULT);
      config.set(DEBUG_KEY, DEBUG_DEFAULT);
      config.set(UPDATE_REGULARLY_KEY, UPDATE_REGULARLY_DEFAULT);
      config.set(UPDATE_INTERVAL_KEY, UPDATE_INTERVAL_DEFAULT);

      try {
        if (!dataFolder.exists()) {
          dataFolder.mkdirs();
        }

        configFile.createNewFile();
        configProvider.save(config, configFile);
      } catch (IOException ex) {
        logger.warning("Could not save config file!");
        ex.printStackTrace();
      }
    }

    // Config values (and their defaults)
    header = getConfigEntry(HEADER_KEY, HEADER_DEFAULT);
    footer = getConfigEntry(FOOTER_KEY, FOOTER_DEFAULT);
    updateOnJoinQuit = getBooleanConfigEntry(UPDATE_ON_JOIN_QUIT_KEY, UPDATE_ON_JOIN_QUIT_DEFAULT);
    debug = getBooleanConfigEntry(DEBUG_KEY, DEBUG_DEFAULT);
    boolean updateRegularly = getBooleanConfigEntry(UPDATE_REGULARLY_KEY, UPDATE_REGULARLY_DEFAULT);

    int updateInterval;

    try {
      updateInterval = (int) config.get(UPDATE_INTERVAL_KEY);
    } catch (Throwable rock) {
      logger.warning(
          "Invalid value supplied for update interval, must be int. Using default of " +
              UPDATE_INTERVAL_DEFAULT
      );
      updateInterval = UPDATE_INTERVAL_DEFAULT;
    }

    if (updateRegularly) {
      proxy
          .getScheduler()
          .schedule(this, this::updateTabListGlobally, 0, updateInterval, TimeUnit.SECONDS);
    }

    if (updateOnJoinQuit) {
      manager.registerListener(this, new QuitListener());
    }

    // Just in case something breaks, or it's reloaded
    updateTabListGlobally();

    debug("Debug logging is enabled");
  }

  /**
   * Set a player's tab list.
   * @param player The proxied player to set
   */
  public void setPlayerList(ProxiedPlayer player, Server server) {
    debug("Refreshing player list for " + player.getName());
    player.setTabHeader(
        formatTextField(header, player, server),
        formatTextField(footer, player, server)
    );
  }

  public void setPlayerList(ProxiedPlayer player) {
    Server server = player.getServer();
    setPlayerList(player, server);
  }

  public void updateTabListGlobally() {
    debug("Updating global tab list");
    for (ProxiedPlayer player : getProxy().getPlayers()) {
      setPlayerList(player);
    }
  }

  public static XTabList getInstance() {
    return instance;
  }

  public boolean isUpdateOnJoinQuit() {
    return updateOnJoinQuit;
  }

  /** @return The specified config key (a String[]) as a String with newlines. */
  private String getConfigEntry(String key, String fallback) {
    try {
      // It's checked with our handy rock
      @SuppressWarnings("unchecked")
      List<String> entry = (List<String>) config.get(key);

      if (entry != null) {
        return String.join("\n", entry);
      }
    } catch (Throwable rock) {
      badType(key, "List<String>");
    }

    return fallback;
  }

  private static List<String> singleton(String entry) {
    List<String> list = new ArrayList<>();
    list.add(entry);
    return list;
  }

  /** @return Boolean config entry */
  private boolean getBooleanConfigEntry(String key, boolean fallback) {
    try {
      return (boolean) config.get(key);
    } catch (Throwable rock) {
      badType(key, "boolean");
      return fallback;
    }
  }

  private void badType(String key, String type) {
    getLogger().warning("Bad type for entry " + key + ", should be " + type);
  }

  /** @return a String like "&bHello", converted to a BaseComponent[] */
  private static BaseComponent[] format(String colorCodedString) {
    return TextComponent.fromLegacyText(
        ChatColor.translateAlternateColorCodes('&', colorCodedString)
    );
  }

  private static String replaceKey(String key, String value, String full) {
    return full.replaceAll("\\{" + key + "}", value);
  }

  private BaseComponent[] formatTextField(String text, ProxiedPlayer player, Server server) {
    // Sometimes, like if update regularly is called during a login, we won't have name yet
    String serverName = "Loading...";

    if (server != null) {
      serverName = server.getInfo().getName();
    }

    text = replaceKey("server-name", serverName, text);
    text = replaceKey("player-count", String.valueOf(getProxy().getOnlineCount()), text);
    return format(replaceKey("ping", String.valueOf(player.getPing()), text));
  }

  private void debug(String message) {
    if (debug) {
      getLogger().info(message);
    }
  }

}
