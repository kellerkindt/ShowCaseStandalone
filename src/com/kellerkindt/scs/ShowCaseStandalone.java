/**
* ShowCaseStandalone
* Copyright (C) 2012 Kellerkindt <copyright at kellerkindt.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.kellerkindt.scs;

import com.kellerkindt.scs.Properties.EconomySystem;
import com.kellerkindt.scs.balance.*;
import com.kellerkindt.scs.events.ShowCaseEvent;
import com.kellerkindt.scs.interfaces.*;
import com.kellerkindt.scs.internals.*;
import com.kellerkindt.scs.listeners.*;
import com.kellerkindt.scs.shops.*;
import com.kellerkindt.scs.storage.YamlPlayerSessionStorage;
import com.kellerkindt.scs.storage.YamlPriceStorage;
import com.kellerkindt.scs.storage.YamlShopStorage;
import com.kellerkindt.scs.utilities.Messaging;
import com.kellerkindt.scs.utilities.Term;
import com.kellerkindt.scs.utilities.TermLoader;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitWorker;
import org.mcstats.Metrics;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ShowCaseStandalone extends JavaPlugin {

    
    private static ShowCaseStandalone       scs;
    private static Date                     startup     = null;
    private static HashMap<Date, String>    warnings    = new HashMap<Date, String>();
    
    private Permission  permission         = null;
    private Balance     balance            = null;

    private Metrics                 metrics         = null;
    private MetricsHandler          metricsHandler  = null;
    private ShopHandler             shopHandler     = null;
    private PlayerSessionHandler    sessionHandler  = null;
    private PriceRangeHandler       priceHandler    = null;
    
    private StorageHandler<ShopHandler, Shop>                   shopStorage     = null;
    private StorageHandler<PlayerSessionHandler, PlayerSession> sessionStorage  = null;
    private StorageHandler<PriceRangeHandler, PriceRange>       priceStorage    = null;
    
    
    private SCSConfiguration    config            = null;
    private Logger              logger;
    
    private Map<Class<? extends Shop>, String>    createPerms     = new HashMap<Class<? extends Shop>, String>();
    private Map<Class<? extends Shop>, Double>    createCosts        = new HashMap<Class<? extends Shop>, Double>();

    @Override
    public void onLoad() {
        getDataFolder().mkdirs();
    }
    
    @Override
    public void onDisable() {
        try {
            // stop changes from being happening
            logger.info("Stopping shop update task");
            shopHandler.stop();

            // save as soon as possible changes to disk
            logger.info("Saving any remaining shop changes");
            shopStorage.saveAll(shopHandler);
            shopStorage.flush();
            shopStorage.stop(true);
            
            logger.info("Saving PlayerSessions");
            sessionStorage.saveAll(sessionHandler);
            sessionStorage.flush();
            
            logger.info("Saving PriceRanges");
            priceStorage.saveAll(priceHandler);
            priceStorage.flush();


            // general cleanup, critical phase is over
            logger.info("Removing displayed items");
            shopHandler.hideAll();


            logger.info("Disable request complete!");
            
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Error while processing disable request", t);
        }
    }

    @Override
    public void onEnable() {
        logger = getLogger();
        logger.info("Starting b"+getDescription().getVersion()+", created by "+Properties.BUILD_AUTHOR +" with great contribution by "+Properties.BUILD_CONTRIBUTOR);
        
        // load the configuration
        logger.info("Loading configuration.");
        loadSCSConfig(this.getConfig());
        
        try {
            // Initialize localization
            logger.info("Loaded localization: " + getConfiguration().getLocalizationFile()); 
            TermLoader.loadTerms(new File( getDataFolder(), getConfiguration().getLocalizationFile() ));
            
        } catch (IOException ioe) {
            // prevent further loading
            throw new RuntimeException("Couldn't load localizations", ioe);
        }

        // static setups
        scs         = this;
        startup     = new Date();
        warnings    = new HashMap<Date, String>();

        
        CommandExecutorListener listener    = new CommandExecutorListener(this);
        PluginCommand           command     = getCommand("scs");
        
        command.setExecutor     (listener);
        command.setTabCompleter (listener);
        
        
        
        
        try {
            logger.info("Initialising handlers");
            shopHandler     = new SimpleShopHandler (this);
            sessionHandler  = new SimplePlayerSessionHandler(getConfiguration());
            priceHandler    = new SimplePriceRangeHandler();
            
            logger.info("Initialising storage handlers");
            shopStorage     = new YamlShopStorage           (this, new File(getDataFolder(), Properties.PATH_STORAGE));
            sessionStorage  = new YamlPlayerSessionStorage  (this, new File(getDataFolder(), Properties.PATH_SESSIONS));
            priceStorage    = new YamlPriceStorage          (      new File(getDataFolder(), Properties.PATH_PRICERANGE));

            shopStorage     .start();
            sessionStorage  .start();
            priceStorage    .start();
            
            logger.info("Loading data");
            shopStorage     .loadAll(shopHandler);
            sessionStorage  .loadAll(sessionHandler);
            priceStorage    .loadAll(priceHandler);
            
            logger.info("Loaded Shops: "+shopHandler.size()+", PlayerSessions: "+sessionHandler.size()+", PriceRanges: "+priceHandler.size());
            
        } catch (IOException ioe) {
            // prevent further loading
            throw new RuntimeException("Couldn't load storage(s)", ioe);
        }
        
        
        // Searching for other plugins
        logger.info("Searching for other Plugins...");
        
        // try to manually hook into plugins
        for (Plugin p : this.getServer().getPluginManager().getPlugins()) {
            hookInto(p);
        }
        
        // no economy system found?
        if (this.balance == null) {
            logger.warning("No economy system found, using dummy economy system!");
            logger.warning("Please get a plugin, either iMonies, EssentialsEco, or BOSEconomy!");
            this.balance = new DummyBalance(this);
        }
        
        // create and prepare all the listeners
        logger.info("Register event listeners");
        registerEvents(new PlayerListener   (this));
        registerEvents(new BlockListener    (this));
        registerEvents(new WorldListener    (this));
        registerEvents(new EntityListener   (this));
        registerEvents(new HopperListener   (this));
        
        registerEvents(new ShowCaseStandalonePluginListener());
        
        // main listener that handle the players requests for SCS
        registerEvents(new ShowCaseExecutingListener(this));
        registerEvents(new ShowCaseVerifyingListener(this));
        
        // depending on the configuration...
        if (getConfiguration().isDisplayShopUsingSigns()) {
            registerEvents(new SignListener(this));
        }
        
        if (getConfiguration().hasAccessThroughInventory()) {
            registerEvents(new InventoryListener(this));
        }
        
        
        // set the prices
        setCreatePrice(SellShop     .class, getConfiguration().getCreatePriceSellShop());
        setCreatePrice(BuyShop      .class, getConfiguration().getCreatePriceBuyShop());
        setCreatePrice(DisplayShop  .class, getConfiguration().getCreatePriceDisplay());
        setCreatePrice(ExchangeShop .class, getConfiguration().getCreatePriceExchange());
        
        // set the permissions
        setCreatePermission(SellShop    .class, Properties.PERMISSION_CREATE_SELL);
        setCreatePermission(BuyShop     .class, Properties.PERMISSION_CREATE_BUY);
        setCreatePermission(DisplayShop .class, Properties.PERMISSION_CREATE_DISPLAY);
        setCreatePermission(ExchangeShop.class, Properties.PERMISSION_CREATE_EXCHANGE);
        

        

        try {
            logger.info("Initilazing Metrics");
            // init metrics
            this.metrics    = new Metrics(this);
            
            // does the server owner want to use this?
            if (!metrics.isOptOut()) {
                
                // init handler
                this.metricsHandler = new MetricsHandler(shopHandler, metrics);
                
                // add listener
                registerEvents(metricsHandler);
                
                // start metrics
                this.metrics.start();
                
                logger.info("Metrics successfully initialized");
            } else {
                logger.info("Metrics is deactivated, ShowCaseStandalone respects your decision and won't use Metrics");
            }
        
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Couldn't activate metrics. This won't affect the functionality of "+getDescription().getName(), ioe);
        }
        


        if (Properties.BUILD_ISDEV) {
            // just to be sure, give a warning that this isn't a stable build
            Messaging.send(getServer().getConsoleSender(), Term.WARNING_DEV_VERSION.get());
        }
        
        // done
        logger.info("Enabled");
    }
    
    /**
     * @param listener  {@link Listener} to register
     */
    private void registerEvents(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
    
    /**
     * @param shopClass Class to bind the permission to
     * @param perm      Permission needed to create a shop of the given class
     */
    public void setCreatePermission (Class<? extends Shop> shopClass, String perm) {
        createPerms.put(shopClass, perm);
    }
    
    /**
     * @param shopClass The class to check the permissions for
     * @return The needed permission to create a shop of the given class or the admin permission
     */
    public String getCreatePermission (Class<? extends Shop> shopClass) {
        
        // get the permission
        String perm = createPerms.get(shopClass);
        
        // check whether the permission is valid
        if (perm == null) {
            // unknown, so only the admin is allowed to
            return Properties.PERMISSION_ADMIN;
            
        } else {
            return perm;
        }
    }
    
    /**
     * @param name Name of the {@link Player}
     * @return The {@link UUID} of the (Offline-){@link Player} on the current {@link Server} with the given name or null
     */
    public UUID getPlayerUUID (String name) {
        return getPlayerUUID(name, getServer());
    }
    
    /**
     * @param name      Name of the {@link Player}
     * @param server    {@link Server} to search for the {@link Player}
     * @return The {@link UUID} of the {@link Player} on this (Offline-){@link Server} with the given name or null
     */
    @SuppressWarnings("deprecation")
    public static UUID getPlayerUUID (String name, Server server) {
        // try to get the online player with the given UUID
        Player          playerOnline     = server.getPlayer(name);
        OfflinePlayer   playerOffline    = null;
        
        // if player is online, return its name
        if (playerOnline != null) {
            return playerOnline.getUniqueId();
        }
        
        // get the offline instance
        playerOffline = server.getOfflinePlayer(name);
        
        // return the name if available
        if (playerOffline != null) {
            return playerOffline.getUniqueId();
        }
        
        return null;
    }
    
    /**
     * @param uuid {@link UUID} of the {@link Player}
     * @return The name of the {@link Player} on the current {@link Server} with the given {@link UUID} or the given {@link UUID}
     */
    public String getPlayerName (UUID uuid) {
        return getPlayerName(uuid, getServer());
    }

    /**
     * @param id {@link UUID} to get the name of the player for
     * @return The name of the player for the given {@link UUID} or null
     */
    public String getPlayerNameOrNull(UUID id) {
        try {
            return getPlayerName(id);
        } catch (Throwable t) {
            logger.log(Level.WARNING, "Failed to fetch player name for id="+id, t);
        }
        return null;
    }
    
    /**
     * @param uuid        {@link UUID} of the {@link Player}, if null, null will be returned
     * @param server    {@link Server} to search for the {@link Player}
     * @return The name of the player of the given {@link UUID} on the given {@link Server} or the given {@link UUID}
     */
    public static String getPlayerName (UUID uuid, Server server) {
        if (uuid == null) {
            return null;
        }

        // try to get the online player with the given UUID
        Player          playerOnline     = server.getPlayer(uuid);
        OfflinePlayer   playerOffline    = null;
        
        // if player is online, return its name
        if (playerOnline != null) {
            return playerOnline.getName();
        }
        
        // get the offline instance
        playerOffline = server.getOfflinePlayer(uuid);
        
        // return the name if available
        if (playerOffline != null) {
            return playerOffline.getName();
        }
        
        throw new RuntimeException("Couldn't get name of player for UUID="+uuid);
    }
    
    /**
     * @param shopClass    Class to set the price for
     * @param price        Price to set for the given class
     */
    public void setCreatePrice (Class<? extends Shop> shopClass, double price) {
        createCosts.put(shopClass, price);
    }
    
    /**
     * @param shopClass Class to get the price for
     * @return The price to create a Shop of the given Class
     */
    public double getCreatePrice (Class<? extends Shop> shopClass) {
        // get the cost
        Double cost = createCosts.get(shopClass);
        
        if (cost == null) {
            logger.warning("No price entry for "+shopClass);
            return 0;
            
        } else {
            return cost;
        }
    }
    
    
    /**
     * Calls the given {@link ShowCaseEvent} and contacts
     * the {@link CommandSender} whether the request was
     * successfully
     * 
     * @param event Event to call
     * @param sender The sender that caused this event
     * @return Whether the event was cancelled
     */
    public boolean callShowCaseEvent (ShowCaseEvent event, CommandSender sender) {
        getServer().getPluginManager().callEvent(event);
        
        if (sender != null) {
            // send the error message
            if (event.isCancelled() && event.getCause() != null) {
                // an error occurred
                scs.sendMessage(sender, event.getCause().getMessage());
    
                
            } else if (!event.isCancelled() && event.getMsgSuccessfully() != null) {
                // successfully
                scs.sendMessage(sender, event.getMsgSuccessfully());
            }
        }
        
        return event.isCancelled();
    }
    
    
    /**
     * @return The time this plugin was enabled
     */
    public static Date getStartupTime () {
        return startup;
    }
    
    /**
     * @return The date of the last warnings since startup
     */
    public static HashMap<Date, String> getWarnings () {
        return warnings;
    }
    
    /**
     * @return The amount of warnings since this plugin was enabled
     */
    public static int getTotalWarnings () {
        return warnings.size();
    }
    
    @Deprecated
    public static ShowCaseStandalone get () {
        return ShowCaseStandalone.scs;
    }
    
    /**
     * The {@link ShopManipulator} is called immediately if the
     * {@link Player} is currently looking at a {@link Shop} or
     * as soon as the player selected one
     * 
     * @param player        {@link Player} to register the given {@link ShopManipulator} for
     * @param manipulator   {@link ShopManipulator} to execute, when the given {@link Player} has selected a {@link Shop}
     * @return Whether it was possible to execute the {@link ShopManipulator} immediately
     */
    @SuppressWarnings("deprecation") // at the moment there is no other way?
    public boolean registerShopManipulator (final Player player, final ShopManipulator manipulator) {
        // update as soon as possible
        Block block = player.getTargetBlock((Set<Material>)null, 50);
        Shop  shop  = getShopHandler().getShop(block);
        
        if (shop != null) {
            // manipulate it immediately
            manipulator.manipulate(shop);
            return true;
            
        } else {
            
            // register for later manipulation
            registerLocationSelector(player, new LocationSelector() {
                @Override
                public void onLocationSelected(Location location) {
                    // get the shop
                    Shop shop = getShopHandler().getShop(location.getBlock());
                    
                    if (shop != null && manipulator.requiresValidShop()) {
                        // manipulate the shop at the selected position
                        manipulator.manipulate(shop);
                        
                    } else {
                        // let the player know
                        sendMessage(player, Term.ERROR_NOT_A_SHOP.get());
                    }
                }
            });
            return false;
        }
    }
    
    /**
     * @param player    {@link Player} to register the {@link LocationSelector} for
     * @param selector    {@link LocationSelector} to register for the given {@link Player}
     */
    public void registerLocationSelector (Player player, LocationSelector selector) {
        player.setMetadata(Properties.METADATA_PLAYER_LOCATIONSELECTOR, new FixedMetadataValue(this, selector));
    }
    
    /**
     * @param player {@link Player} to remove the {@link LocationSelector} for
     * @return The {@link LocationSelector} that has been removed or null if not set
     */
    public LocationSelector removeLocationSelector (Player player) {
        LocationSelector selector = getLocationSelector(player);
        player.removeMetadata(Properties.METADATA_PLAYER_LOCATIONSELECTOR, this);
        return selector;
    }
    
    /**
     * @param player {@link Player} to get the {@link LocationSelector} for
     * @return The {@link LocationSelector} registered for the given {@link Player} or null
     */
    public LocationSelector getLocationSelector (Player player) {
        List<MetadataValue>     selectors     = (List<MetadataValue>)player.getMetadata(Properties.METADATA_PLAYER_LOCATIONSELECTOR);
        LocationSelector        selector    = null;
        
        if (selectors != null && selectors.size() > 0) {
            Iterator<MetadataValue> itr    = selectors.iterator();
            selector = (LocationSelector)itr.next().value();
        }
        
        return selector;
    }
    
    /**
     * @param sender {@link CommandSender} to check
     * @return Whether the given player is a SCS-Admin
     */
    public boolean isAdmin (CommandSender sender) {
        return sender.isOp() || hasPermission(sender, Properties.PERMISSION_ADMIN);
    }
    
    /**
     * @param player    Player to check
     * @param per        Additional permission to check, if the player is not an admin
     * @return Whether the given player is an admin or has the given permission
     */
    public boolean isAdminOrHasPermission (Player player, String per) {
        return hasOnePermission(player, Properties.PERMISSION_ADMIN, per);
    }
    
    /**
     * @param player Player to check
     * @return Whether the given player can use a shop
     */
    public boolean canUse (Player player) {
        return hasOnePermission(player, Properties.PERMISSION_ADMIN, Properties.PERMISSION_USE);
    }
    
    /**
     * Checks whether the given player can manage the given shop
     *
     * <ul>True if:
     *  <li>player is admin ('{@value Properties#PERMISSION_ADMIN}')</li>
     *  <li>checkOwner is set     and the player is   owner  and has the permission '{@value Properties#PERMISSION_MANAGE}'</li>
     *  <li>checkOwner is not set and the player is a member and has the permission '{@value Properties#PERMISSION_MANAGE}'</li>
     * </ul>
     *
     * Alternatively, another permission-nodes can be passed (altPerms) which will be checked
     * if the player doesn't have '{@value Properties#PERMISSION_MANAGE}'
     *
     * @param player        Player to check
     * @param shop            Shop to manage
     * @param checkOwner    Whether the given player has to be the owner, if false, it can also be a member
     * @param altPerms       Alternative permission-nodes that are required, if {@link Properties#PERMISSION_MANAGE} is not given
     * @return whether the given player can manage the given shop
     */
    public boolean canManage (Player player, Shop shop, boolean checkOwner, String ...altPerms) {
        return hasPermission(player, Properties.PERMISSION_ADMIN)
                || ((!checkOwner || shop.isOwner(player.getUniqueId()))
                    &&   (checkOwner || shop.isOwnerOrMember(player.getUniqueId()))
                    && (hasPermission(player, Properties.PERMISSION_MANAGE) || hasAllPermissions(player, altPerms))
        );
    }
    
    /**
     * @param cs    CommandSender to check
     * @param pers    Requested permissions
     * @return Whether the given CommandSender has one of the requested permissions
     */
    public boolean hasOnePermission (CommandSender cs, String ... pers) {
        for (String per : pers) {
            if (hasPermission(cs, per)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * @param cs    CommandSender to check
     * @param pers    Requested permissions
     * @return Whether the given CommandSender has all of the requested permissions
     */
    public boolean hasAllPermissions (CommandSender cs, String ... pers) {
        for (String per : pers) {
            if (!hasPermission(cs, per)) {
                return false;
            }
        }
        
        return true;
    }
    
    
    /**
     * Checks whether the given CommandSenderhas the requested
     * permission, console will always have all permissions
     * @param cs            CommandSender to check
     * @param permission    Requested permission
     * @return Whether the given CommandSender has the requested permission
     */
    public boolean hasPermission (CommandSender cs, String permission) {
        if (cs instanceof Player) {
            return hasPermission((Player)cs, permission);
        } else {
            return (cs instanceof ConsoleCommandSender);
        }
    }
    
    /*
     * Checks if the given player has the given permission
     * First uses Permissions plugin if available, if not
     * it uses bukkit-permission 
     */
    /**
     * Checks whether the given {@link Player} has the
     * given permission. Before asking {@link Bukkit}s
     * permission API, the vaults permissions are being
     * asked
     * 
     * @param player    Player to check
     * @param perm        Requested permission
     * @return Whether the given Player has the given permission or not
     */
    public boolean hasPermission (Player player, String perm) {
        
        boolean hasPer     = false;
        
        if (permission != null) {
            // ask the permission plugin
            hasPer = permission.has(player, perm);
            
        } else {
            // ask bukkit
            hasPer = player.hasPermission(perm);
        }
        
        // only log on debug
        if (getConfiguration().isDebuggingPermissions()) {
            if (hasPer) {
                logger.info(String.format("%s [DisplayName=%s] was granted the permission '%s'", player.getName(), player.getDisplayName(), perm));
            } else {
                logger.info(String.format("%s [DisplayName=%s] was denied the permission '%s'", player.getName(), player.getDisplayName(), perm));
            }
        }
        
        return hasPer;
    }
    
    /*
     * Returns the BalanceHandler
     */
    public Balance getBalanceHandler () {
        return this.balance;
    }
    
    public void setBalanceHandler (Balance bh) {
        this.balance = bh;
    }
    
    /**
     * @return The current ShopHandler
     */
    public ShopHandler getShopHandler () {
        return this.shopHandler;
    }
    
    /**
     * @return The current StorageHandler
     */
    public StorageHandler<ShopHandler, Shop> getShopStorageHandler(){
        return this.shopStorage;
    }
    
    /**
     * @return The {@link PlayerSessionHandler} in use
     */
    public PlayerSessionHandler getPlayerSessionHandler () {
        return sessionHandler;
    }
    
    /**
     * @return The {@link StorageHandler} for the {@link PlayerSessionHandler}
     */
    public StorageHandler<PlayerSessionHandler, PlayerSession> getPlayerSessionStorage () {
        return sessionStorage;
    }
    
    /**
     * @param material    {@link Material} to check for
     * @param price        Price to check
     * @return Whether the given price is in the set range for the given {@link Material}
     */
    public boolean inPriceRange (Material material, double price) {
        PriceRange range = getPriceRangeHandler().getRange(material);
        
        return range == null || (range.getMin() >= price && price <= range.getMax());
    }
    
    /**
     * @return The current {@link PriceRangeHandler}
     */
    public PriceRangeHandler getPriceRangeHandler () {
        return priceHandler;
    }
    
    /**
     * @return The {@link StorageHandler} for the {@link PriceRangeHandler}
     */
    public StorageHandler<PriceRangeHandler, PriceRange> getPriceRangeStorage () {
        return priceStorage;
    }
    
            
    /*
     * Returns formatted money amounts.
     */
    public String formatCurrency(double amount){
        return balance.format(amount);
    }
    
    public SCSConfiguration getConfiguration () {
        return config;
    }
    
    /**
     * Checks if the economy system is allowed
     * @param     className    ClassName of the economy system
     * @return    true if it is allowed, false if it isn't allowed
     */
    public boolean isAllowedEconomySystem (String className) {
        // default
        EconomySystem    system     = EconomySystem.getForName( getConfiguration().getEconomySystem() );
        String            names[]    = system != null ? system.classNames : new String[0];
        
        // invalid EconomySystem set?
        if (system == null) {
            logger.severe("Invalid EconmySystem was set in the configuarion, valid are: ");
            
            for (EconomySystem sys : EconomySystem.values())  {
                logger.severe(" - "+sys.name());
            }
        }
        
        // search for it
        for (String s : names) {
            if (s.equals(className)) {
                return true;
            }
        }
        return false;
    }
    
    /*
     * Configuration file loader
     */
    public void loadSCSConfig(FileConfiguration config){
        // create, load and set it
        this.config = new SCSConfiguration(config);
        
        // check the locale version
        if (Properties.VERSION_LOCALE > getConfiguration().getLocalizationVersion()) {
            logger.warning("Locale file has changed.  Overwriting default locale files with new versions.");
            logger.warning("If you are using a custom locale file, please update with any changes you need.");
            
            for(String defaultName : Properties.DEFAULT_LOCALES){
                this.saveResource(defaultName, true);
            }
            
            this.config.setLocalizationVersion(Properties.VERSION_LOCALE);
            saveConfig();
        }
        

        // save changes
        saveConfig();
    }
    
    /**
     * Sends to all of the registered player
     * the given message 
     * @param shop    Shop to get the players from
     * @param msg    Message to send
     */
    public void sendMessageToAll (Shop<?> shop, String msg) {
        sendMessageToOwner(shop, msg);
        
        for (NamedUUID member : shop.getMembers()) {
            if (member.getId() != null) {
                sendMessage(getServer().getPlayer(member.getId()), msg);
            }

            else if (member.getName() != null) {
                sendMessage(getServer().getPlayerExact(member.getName()), msg);
            }
        }
    }
    
    /**
     * Sends the owner of this shop the given Message
     * @param shop    Shop to get the owner of
     * @param msg    Message to send
     */
    public void sendMessageToOwner (Shop shop, String msg) {
        if (shop.getOwnerId() != null) {
            sendMessage(getServer().getPlayer(shop.getOwnerId()), msg);
        }
    }
    
    /**
     * Sends the given transaction message the owner of
     * the given shop, if the owner wants to receive them
     * @param shop        {@link Shop} of the owner to send the message to
     * @param message    Message to send
     */
    public void sendTransactionMessageToOwner (Shop shop, String message) {
        if (shop.getOwnerId() == null) {
            return; // bank account
        }

        // get the player
        Player player = getServer().getPlayer(shop.getOwnerId());
        
        // nope
        if (player == null) {
            return;
        }
        
        // only if not unlimited or unlimited and shop the message
        if (shop.isUnlimited() && !getConfiguration().getDefaultShowTransactionMessageOnUnlimited()) {
            return;
        }
        
        // does he now want to see them?
        if (!getPlayerSessionHandler().getSession(player).showTransactionMessage()) {
            return;
        }
        
        sendMessage(player, message);
    }
    
    
    /**
     * Sends the given {@link CommandSender} the given message,
     * parses color
     * @param sender    {@link CommandSender} to send the message to
     * @param msg        Message to send
     */
    public void sendMessage (CommandSender sender, String msg) {
        if (sender != null) {
            Messaging.send(sender, msg);
        }
    }
    
    /**
     * @param stack ItemStack to check
     * @return Whether the material of the given ItemStack needs to check the ItemMeta
     */
    public boolean compareItemMeta (ItemStack stack) {
        switch (stack.getType())  {
            case WRITTEN_BOOK:
            case BOOK:
                return false;
                
            default:
                return true;
        }
    }
    
    
    /**
     * Tires to hook into the given Plugin
     * @param plugin
     */
    public void hookInto (Plugin plugin) {
        String className    = plugin.getClass().getName();
        
        // WorldGuard
        if (className.equals("com.sk89q.worldguard.bukkit.WorldGuardPlugin")) {
            registerEvents(new WorldGuardListener(this, plugin));
            logger.info("Hooked into WorldGuard");
        }
        
        // Essentials Economy
        if (className.equals("com.earth2me.essentials.Essentials") && isAllowedEconomySystem(className)) {
            logger.info("Hooked into EssentialsEconomy");
            this.balance    = new EssentialsBalance(this, plugin);
        }
        
        // iConomy 5
        if (className.equals("com.iConomy.iConomy") && isAllowedEconomySystem(className)) {
            logger.info("Hooked into iConomy5");
            this.balance = new iConomy5Balance (this, plugin);
        }
        
        // iConomy 6
        if (className.equals("com.iCo6.iConomy") && isAllowedEconomySystem(className)) {
            logger.info("Hooked into iConomy6");
            this.balance = new iConomy6Balance (this, plugin);
        }
        
        // iConomy
        if (className.equals("com.iCo8.iConomy") && isAllowedEconomySystem(className)) {
            logger.info("Hooked into iConomy8");
            this.balance = new iConomy8Balance (this, plugin);
        }
        
        // BOSEconomy
        if (className.equals("cosine.boseconomy.BOSEconomy") && isAllowedEconomySystem(className)) {
            logger.info("Hooked into BOSEconomy");
            this.balance = new BOSEconomyBalance (this, plugin);
        }
        // Towny
        if (className.equals("com.palmergames.bukkit.towny.Towny")) {
            logger.info("Hooked into Towny");
            registerEvents(new TownyListener(this));
        }
        
        // Residence
        if (className.equals("com.bekvon.bukkit.residence.Residence") && getConfiguration().getResidenceHookInto()) {
            logger.info("Hooked into Residence");
            registerEvents(new ResidenceListener(getConfiguration()));
        }
        
        // Vault
        if (className.equals("net.milkbowl.vault.Vault") && isAllowedEconomySystem(className)) {
            // TODO warp it into VaultBalance?
            RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = scs.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                    logger.info("Hooked into " + economyProvider.getPlugin().getName());
                    balance = new VaultBalance(this, economyProvider.getProvider());
            }
            RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
            if (permissionProvider != null) {
                    logger.info("Hooked into Vault Permissions");
                    permission = permissionProvider.getProvider();
            }
        }
        
        //Attach to DropChest API, if loaded
        if (className.equals("com.narrowtux.dropchest.dropchest")){
            logger.info("Found Old DropChest.  Attempting to hook api.");

            try {
                registerEvents(new DropChestListener(this));
                logger.info("Hooked OLD DropChest listener.");
            } catch (Exception e) {}
        }
        
        //This supports a fork that was done to upgrade DC to 1.1. 
        if (className.equals("com.noheroes.dropchest.dropchest")){
            logger.info("Found New DropChest.  Attempting to hook api.");
            try{ 
                registerEvents(new DropChestListenerV2(this));
                logger.info("Hooked NEW DropChest listener.");
            } catch (Exception e){}
        }
        
        
    }
    
    /**
     * Tries to un-hook from the given plugin
     * @param plugin
     */
    public void unHookPlugin (Plugin plugin) {
        String className    = plugin.getClass().getName();
        
        if (className.equals("com.iConomy.iConomy")) {
            logger.info("Un-hooked iConomy");
            this.balance = new DummyBalance(this);
        }
        
        if (className.equals("com.iCo6.iConomy")) {
            logger.info("Un-hooked iConomy");
            this.balance = new DummyBalance(this);
        }
        
        if (className.equals("com.iCo8.iConomy")) {
            logger.info("Un-hooked iConomy");
            this.balance = new DummyBalance(this);
        }
        
        if (this.permission != null) {
            if (!this.permission.isEnabled()) {
                logger.info("Un-hooked Permissions");
                this.permission = null;
            }
        }

        if (className.equals("cosine.boseconomy.BOSEconomy")) {
            logger.info("Un-hooked BOSEconomy");
            this.balance = new DummyBalance(this);
        }
        
        // Towny
        if (className.equals("com.palmergames.bukkit.towny.Towny")) {
            logger.info("Un-hooked Towny");
            // TODO un-hook
        }
    }
    
    /**
     * Handles hook stuff
     * @author michael <michael at email.com>
     */
    private class ShowCaseStandalonePluginListener implements Listener {
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            // Try to hook into the plugin
            hookInto(event.getPlugin());
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            // Try to hook into the plugin
            unHookPlugin(event.getPlugin());
        }
    }
}
