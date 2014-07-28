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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
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
import org.mcstats.Metrics;

import com.kellerkindt.scs.Properties.EconomySystem;
import com.kellerkindt.scs.balance.BOSEconomyBalance;
import com.kellerkindt.scs.balance.DummyBalance;
import com.kellerkindt.scs.balance.EssentialsBalance;
import com.kellerkindt.scs.balance.VaultBalance;
import com.kellerkindt.scs.balance.iConomy5Balance;
import com.kellerkindt.scs.balance.iConomy6Balance;
import com.kellerkindt.scs.balance.iConomy8Balance;
import com.kellerkindt.scs.events.ShowCaseEvent;
import com.kellerkindt.scs.interfaces.Balance;
import com.kellerkindt.scs.interfaces.PlayerSessionHandler;
import com.kellerkindt.scs.interfaces.PriceRangeHandler;
import com.kellerkindt.scs.interfaces.ShopHandler;
import com.kellerkindt.scs.interfaces.StorageHandler;
import com.kellerkindt.scs.internals.MetricsHandler;
import com.kellerkindt.scs.internals.SimplePlayerSessionHandler;
import com.kellerkindt.scs.internals.SimplePriceRangeHandler;
import com.kellerkindt.scs.internals.SimpleShopHandler;
import com.kellerkindt.scs.internals.Transaction;
import com.kellerkindt.scs.listeners.BlockListener;
import com.kellerkindt.scs.listeners.CommandExecutorListener;
import com.kellerkindt.scs.listeners.DropChestListener;
import com.kellerkindt.scs.listeners.DropChestListenerV2;
import com.kellerkindt.scs.listeners.EntityListener;
import com.kellerkindt.scs.listeners.HopperListener;
import com.kellerkindt.scs.listeners.InventoryListener;
import com.kellerkindt.scs.listeners.PlayerListener;
import com.kellerkindt.scs.listeners.ResidenceListener;
import com.kellerkindt.scs.listeners.ShowCaseExecutingListener;
import com.kellerkindt.scs.listeners.ShowCaseVerifyingListener;
import com.kellerkindt.scs.listeners.SignListener;
import com.kellerkindt.scs.listeners.TownyListener;
import com.kellerkindt.scs.listeners.WorldGuardListener;
import com.kellerkindt.scs.listeners.WorldListener;
import com.kellerkindt.scs.shops.BuyShop;
import com.kellerkindt.scs.shops.DisplayShop;
import com.kellerkindt.scs.shops.ExchangeShop;
import com.kellerkindt.scs.shops.SellShop;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.storage.YamlPlayerSessionStorage;
import com.kellerkindt.scs.storage.YamlPriceStorage;
import com.kellerkindt.scs.storage.YamlShopStorage;
import com.kellerkindt.scs.utilities.Messaging;
import com.kellerkindt.scs.utilities.Term;
import com.kellerkindt.scs.utilities.TermLoader;


//TODO: Dropchest listener not working with DC fork in 1.1
//Amounts on sell are wrong (default items
public class ShowCaseStandalone extends JavaPlugin {
	
	public static final String ALIAS_SHOP_BUY				= "scs.buy";
	public static final String ALIAS_SHOP_SELL				= "scs.sell";
	public static final String ALIAS_SHOP_DISPLAY			= "scs.display";
	public static final String ALIAS_SHOP_EXCHANGE			= "scs.exchange";
	public static final String ALIAS_PLAYERSESSION			= "scs.playersession";
	public static final String ALIAS_PRICERANGE				= "scs.pricerange";
	public static final String ALIAS_TRANSACTION			= "scs.transaction";
	public static final String ALIAS_TRANSACTION_SHOPTYPE	= "scs.transaction.shoptype";
	
	public static final String METADATA_PLAYER_LOCATIONSELECTOR	= "scs.locationselector";
	
	static {
		// register for deserialization
		ConfigurationSerialization.registerClass(BuyShop.class, 				ALIAS_SHOP_BUY);
		ConfigurationSerialization.registerClass(SellShop.class, 				ALIAS_SHOP_SELL);
		ConfigurationSerialization.registerClass(DisplayShop.class, 			ALIAS_SHOP_DISPLAY);
		ConfigurationSerialization.registerClass(ExchangeShop.class, 			ALIAS_SHOP_EXCHANGE);
		ConfigurationSerialization.registerClass(PlayerSession.class,			ALIAS_PLAYERSESSION);
		ConfigurationSerialization.registerClass(PriceRange.class,				ALIAS_PRICERANGE);
		ConfigurationSerialization.registerClass(Transaction.class, 			ALIAS_TRANSACTION);
		ConfigurationSerialization.registerClass(Transaction.ShopType.class,	ALIAS_TRANSACTION_SHOPTYPE);
	}

	
	private static ShowCaseStandalone 		scs;
	private static Date						startup			= null;
	private static HashMap<Date, String>	warnings		= new HashMap<Date, String>();
	
	private Permission 		 	permission 		= null;
	private Balance				balance			= null;
	
	private ShopHandler							shopHandler		= null;
	private PlayerSessionHandler				sessionHandler	= null;
	private PriceRangeHandler					priceHandler	= null;
	
	private StorageHandler<ShopHandler>			shopStorage		= null;
	private StorageHandler<PlayerSessionHandler>sessionStorage	= null;
	private StorageHandler<PriceRangeHandler>	priceStorage	= null;
	
	private MetricsHandler		metricsHandler	= null;
	private Metrics				metrics			= null;
	
	private SCSConfiguration	config			= null;
	private int 				syncTask;
	
	private Map<Class<? extends Shop>, String>	createPerms 	= new HashMap<Class<? extends Shop>, String>();
	private Map<Class<? extends Shop>, Double>	createCosts		= new HashMap<Class<? extends Shop>, Double>();

	@Override
	public void onLoad() {
		getDataFolder().mkdirs();
	}
	
	@Override
	public void onDisable() {
		try {
			log(Level.INFO, "Stopping shop update task.", true);
			shopHandler.stop();
			getServer().getScheduler().cancelTask(syncTask);
			
			log(Level.INFO, "Saving any remaining shop changes.", true);
			shopStorage.save(shopHandler);
			shopStorage.flush();
			
			log(Level.INFO, "Removing display items.", true);
			shopHandler.hideAll();
			
			
			log(Level.INFO, "Saving PlayerSessions", true);
			sessionStorage.save(sessionHandler);
			sessionStorage.flush();
			
			log(Level.INFO, "Saving PriceRanges", true);
			priceStorage.save(priceHandler);
			priceStorage.flush();
			
			
			log(Level.INFO, "Disabled!", false);
			
		} catch (Exception ioe) {
			this.log(Level.WARNING, "Exception on onDisable: " + ioe, false);
		}
	}

	@Override
	public void onEnable() {
		log(Level.INFO, "Starting build "+Properties.buildNumber +", made on "+Properties.buildDate +" by "+Properties.BUILD_AUTHOR +" with help by "+Properties.BUILD_CONTRIBUTOR, false);
		
		// setup startup
		startup		= new Date();
		warnings	= new HashMap<Date, String>();

		// load the configuration
		log(Level.INFO, "Loading configuration.", false);
		loadSCSConfig(this.getConfig());
		
		
		
		// Initialize localization
		log(Level.INFO, "Loaded localization: " + getConfiguration().getLocalizationFile(), true);
		try { 
			TermLoader.loadTerms(new File( getDataFolder(), getConfiguration().getLocalizationFile() ));
		} catch (IOException ioe){
			log(Level.WARNING, "IOError: could not find/connect to localization file.", false);
			ioe.printStackTrace();
			log(Level.WARNING, "Disabling SCS.", false);
			getPluginLoader().disablePlugin(this);
		}
		
		// register our commands
		ShowCaseStandalone.scs 	= this;
		
		CommandExecutorListener listener 	= new CommandExecutorListener(this);
		PluginCommand			command		= getCommand("scs");
		
		command.setExecutor		(listener);
		command.setTabCompleter	(listener);
		
		
		
		
		try {
			log(Level.INFO, "Initialising handlers", true);
			shopHandler		= new SimpleShopHandler (this);
			sessionHandler	= new SimplePlayerSessionHandler(getConfiguration());
			priceHandler	= new SimplePriceRangeHandler();
			
			log(Level.INFO, "Initialising storage handlers", true);
			shopStorage		= new YamlShopStorage			(this, new File(getDataFolder(), "yaml-storage"));
			sessionStorage	= new YamlPlayerSessionStorage	(this, new File(getDataFolder(), "sessions.yml"));
			priceStorage	= new YamlPriceStorage			(new File(getDataFolder(), "pricerange.yml"));
			
			log(Level.INFO, "Loading data", true);
			shopStorage		.load(shopHandler);
			sessionStorage	.load(sessionHandler);
			priceStorage	.load(priceHandler);
			
			log(Level.INFO, "Loaded Shops: "+shopHandler.size()+", PlayerSessions: "+sessionHandler.size(), false);
			
		} catch (Exception e) {
			log(Level.WARNING, "Exception while loading shops: " + e, false);
			e.printStackTrace();
		}
		
		
		// Searching for other plugins
		log(Level.INFO, "Searching for other Plugins...", true);
		
		// try to manually hook into plugins
		for (Plugin p : this.getServer().getPluginManager().getPlugins())
			hookInto(p);
		
		// Not found any economy system?
		if (this.balance == null) {
			log(Level.WARNING, "No economy system found, using dummy economy system!", false);
			log(Level.WARNING, "Please get a plugin, either iMonies, EssentialsEco, or BOSEconomy!", false);
			this.balance = new DummyBalance(this);
		}
		
		//Instantiate and Register the listeners.  Do this last to avoid NPEs for chunk load events.
		log(Level.INFO, "Register event listeners.", true);
		registerEvents(new PlayerListener	    	(this));
		registerEvents(new BlockListener      		(this));
		registerEvents(new WorldListener 	    	(this));
		registerEvents(new InventoryListener    	(this));
		registerEvents(new EntityListener	    	(this));
		registerEvents(new HopperListener			(this));
		
		registerEvents(new ShowCaseStandalonePluginListener(this));
		
		// main listener
		registerEvents(new ShowCaseExecutingListener(this));
		registerEvents(new ShowCaseVerifyingListener(this));
		
		
		// set the prices
		setCreatePrice(SellShop		.class, getConfiguration().getCreatePriceSellShop());
		setCreatePrice(BuyShop		.class, getConfiguration().getCreatePriceBuyShop());
		setCreatePrice(DisplayShop	.class,	getConfiguration().getCreatePriceDisplay());
		setCreatePrice(ExchangeShop	.class,	getConfiguration().getCreatePriceExchange());
		
		// set the permissions
		setCreatePermission(SellShop	.class, Properties.PERMISSION_CREATE_SELL);
		setCreatePermission(BuyShop		.class, Properties.PERMISSION_CREATE_BUY);
		setCreatePermission(DisplayShop	.class, Properties.PERMISSION_CREATE_DISPLAY);
		setCreatePermission(ExchangeShop.class, Properties.PERMISSION_CREATE_EXCHANGE);
		
		
		// init listeners
		if (getConfiguration().isDisplayShopUsingSigns()) {
			registerEvents(new SignListener(this));
		}
		
		try {
			log(Level.INFO, "Initilazing Metrics", true);
			// init metrics
			this.metrics		= new Metrics(this);
			
			// does the server owner want to use this?
			if (!metrics.isOptOut()) {
				
				// init handler
				this.metricsHandler	= new MetricsHandler(shopHandler, metrics);
				
				// add listener
				registerEvents(metricsHandler);
				
				// start metrics
				this.metrics.start();
				
				log(Level.INFO, "Metrics successfully initialized", true);
			} else {
				log(Level.INFO, "Metrics is deactivated, ShowCaseStandalone respects your decision and won't use Metrics", true);
			}
		
		} catch (IOException ioe) {
			log(Level.WARNING, "Couldn't initialize Metrics", false);
		}
		
	
		
		// set up scheduler
		syncTask = scs.getServer().getScheduler().scheduleSyncRepeatingTask(scs, new Runnable() {
			
			@Override
			public void run() {
				// let the ShopHandler to its stuff
				ShowCaseStandalone.this.shopHandler.tick();
				
				try {
					// save changes
					ShowCaseStandalone.this.shopStorage.save(shopHandler);
				} catch (IOException ioe) {
					log(Level.WARNING, "Couldn't save shop changes", false);
					ioe.printStackTrace();
				}
			}
		}, 5L, getConfiguration().getSaveInterval());
		

		// Warning if this is a dev-build
		if (Properties.buildIsDev) {
			Messaging.send(getServer().getConsoleSender(), Term.WARNING_DEV_VERSION.get());
		}
		
		
		log(Level.INFO, "Enabled!", false);
	}
	
	/**
	 * Registers the given Listener
	 * @param listener	Listener to register
	 */
	private void registerEvents(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
//		log(Level.INFO, "Registered "+listener.getClass().getSimpleName(), true);
	}
	
	/**
	 * @param shopClass	Class to bind the permission to
	 * @param perm		Permission needed to create a shop of the given class
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
	 * @return The {@link UUID} of the (Offline-){@link Player} on the current {@link Server} with the given name
	 */
	public UUID getPlayerUUID (String name) {
		return getPlayerUUID(name, getServer());
	}
	
	/**
	 * @param name		Name of the {@link Player}
	 * @param server	{@link Server} to search for the {@link Player}
	 * @return The {@link UUID} of the {@link Player} on this (Offline-){@link Server} with the given name or null
	 */
	@SuppressWarnings("deprecation")
	public static UUID getPlayerUUID (String name, Server server) {
		// try to get the online player with the given UUID
		Player 			playerOnline 	= server.getPlayer(name);
		OfflinePlayer 	playerOffline	= null;
		
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
	 * @param uuid		{@link UUID} of the {@link Player}
	 * @param server	{@link Server} to search for the {@link Player}
	 * @return The name of the player of the given {@link UUID} on the given {@link Server} or the given {@link UUID}
	 */
	public static String getPlayerName (UUID uuid, Server server) {
		// try to get the online player with the given UUID
		Player 			playerOnline 	= server.getPlayer(uuid);
		OfflinePlayer 	playerOffline	= null;
		
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
	 * @param shopClass	Class to set the price for
	 * @param price		Price to set for the given class
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
			log(Level.WARNING, "Price for unknown class requested: "+shopClass, true);
			return 0;
			
		} else {
			return cost;
		}
	}
	
	
	/**
	 * Logging for this module only.
	 * @param l Log Level
	 * @param message 
	 * @param debug Whether to log the message only if debugging is enabled
	 */
	public void log (Level l, String message, boolean debug) {
		// log only for debug reasons?
		if((debug && getConfiguration().isDebuggingLog()) || !debug) {
			// just log it
			getLogger().log(l, message);
		}
	}
	
	/**
	 * Static logger for the minecraft.log
	 * @param l Log Level
	 * @param message 
	 */
	public static void slog(Level l, String message) {
//		if(Properties.showExtraMessages)
//			ShowCaseStandalone.get().getLogger().log(l, message);
//		else
//			ShowCaseStandalone.logr.log(l, "[SCS] " + message);
		
		// TODO
		get().getLogger().log(l, message);
		
		// warning log
		if (Level.WARNING == l || Level.SEVERE == l) {
			warnings.put(new Date(), message);
		}
	}
	
	
	/**
     * Calls the given ShowCaseEvent
     * @param event Event to call
     * @param The sender that caused this event
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
	public static Date getStartup () {
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
	
	/**
	 * Thread Debug logger.  Logs to debug.log when activated.
	 * @param l Log level
	 * @param message 
	 */
	public static void dlog(String message){
		slog(Level.INFO, message);
	}
	
//	/** TODO
//	 * Transaction logger.  Logs to transaction.log when activated.
//	 * If mysql is active, we should be sending this to that module for 
//	 * storage in the transaction table.
//	 * @param customer
//	 * @param owner
//	 * @param action
//	 * @param quantity
//	 * @param price
//	 * @param item
//	 * @param sha1
//	 * @param inventory 
//	 */
//	public static void tlog(String customer, String owner, String action, 
//			int quantity, double price, String item, UUID uuid, int inventory){
//		if(Properties.logTransactions){
//			StringBuilder msg = new StringBuilder();
//			msg.append("Transaction: ");
//			msg.append("c:").append(customer).append(", o:").append(owner);
//			msg.append(", a:").append(action);
//			msg.append(", q:").append(quantity);
//			msg.append(", i:").append(item);
//			msg.append(", pr:").append(price);
//			msg.append(", shp:").append(uuid.toString());
//			msg.append(", inv:").append(inventory);
//			
//			if(logTrans != null)
//				logTrans.log(Level.INFO, msg.toString());
//		}
//	}
	
	@Deprecated
	public static ShowCaseStandalone get () {
		return ShowCaseStandalone.scs;
	}
	
	/**
	 * @param player		{@link Player} to register the given {@link ShopManipulator} for
	 * @param manipulator	{@link ShopManipulator} to execute, when the given {@link Player} has selected a {@link Shop}
	 * @return Whether it was possible to execute the {@link ShopManipulator} immediately
	 */
	public boolean registerShopManipulator (final Player player, final ShopManipulator manipulator) {
		// TODO
		Block block = player.getTargetBlock(null, 50);
		Shop  shop	= getShopHandler().getShop(block);
		
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
	 * @param player	{@link Player} to register the {@link LocationSelector} for
	 * @param selector	{@link LocationSelector} to register for the given {@link Player}
	 */
	public void registerLocationSelector (Player player, LocationSelector selector) {
		player.setMetadata(METADATA_PLAYER_LOCATIONSELECTOR, new FixedMetadataValue(this, selector));
	}
	
	/**
	 * @param player {@link Player} to remove the {@link LocationSelector} for
	 * @return The {@link LocationSelector} that has been removed or null if not set
	 */
	public LocationSelector removeLocationSelector (Player player) {
		LocationSelector selector = getLocationSelector(player);
		player.removeMetadata(METADATA_PLAYER_LOCATIONSELECTOR, this);
		return selector;
	}
	
	/**
	 * @param player {@link Player} to get the {@link LocationSelector} for
	 * @return The {@link LocationSelector} registered for the given {@link Player} or null
	 */
	public LocationSelector getLocationSelector (Player player) {
		List<MetadataValue> 	selectors 	= (List<MetadataValue>)player.getMetadata(METADATA_PLAYER_LOCATIONSELECTOR);
		LocationSelector		selector	= null;
		
		if (selectors != null && selectors.size() > 0) {
			Iterator<MetadataValue> itr	= selectors.iterator();
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
	 * @param player	Player to check
	 * @param per		Additional permission to check, if the player is not an admin
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
	 * Checks whether the given player can manage
	 * the given shop
	 * @param player		Player to check
	 * @param shop			Shop to manage
	 * @param checkOwner	Whether the given player has to be the owner, if false, it can also be a member
	 * @return whether the given player can manage the given shop
	 */
	public boolean canManage (Player player, Shop shop, boolean checkOwner) {
		return hasPermission(player, Properties.PERMISSION_ADMIN)
				|| ((!checkOwner || shop.isOwner(player.getUniqueId()))
						&& (checkOwner || shop.isOwnerOrMember(player.getUniqueId()))
						&& hasPermission(player, Properties.PERMISSION_MANAGE));
	}
	
	/**
	 * @param cs	CommandSender to check
	 * @param pers	Requested permissions
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
	 * @param cs	CommandSender to check
	 * @param pers	Requested permissions
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
	 * @param cs			CommandSender to check
	 * @param permission	Requested permission
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
	 * @param player	Player to check
	 * @param perm		Requested permission
	 * @return Whether the given Player has the given permission or not
	 */
	public boolean hasPermission (Player player, String perm) {
		
		boolean hasPer 	= false;
		
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
				log(Level.FINEST, String.format("%s [DisplayName=%s] was granted the permission '%s'", player.getName(), player.getDisplayName(), perm), true);
			} else {
				log(Level.INFO, String.format("%s [DisplayName=%s] was denied the permission '%s'", player.getName(), player.getDisplayName(), perm), true);
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
	public StorageHandler<ShopHandler> getShopStorageHandler(){
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
	public StorageHandler<PlayerSessionHandler> getPlayerSessionStorage () {
		return sessionStorage;
	}
	
	/**
	 * @param material	{@link Material} to check for
	 * @param price		Price to check
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
	public StorageHandler<PriceRangeHandler> getPriceRangeStorage () {
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
	 * @param 	className	ClassName of the economy system
	 * @return	true if it is allowed, false if it isn't allowed
	 */
	public boolean isAllowedEconomySystem (String className) {
		// default
		EconomySystem	system 	= EconomySystem.getForName( getConfiguration().getEconomySystem() );
		String			names[]	= system != null ? system.classNames : new String[0];
		
		// invalid EconomySystem set?
		if (system == null) {
			log(Level.SEVERE, "Invalid EconmySystem was set in the configuarion set, valid are: ", false);
			
			for (EconomySystem sys : EconomySystem.values())  {
				log(Level.SEVERE, " - "+sys.name(), false);
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
		if (Properties.localeVersion > getConfiguration().getLocalizationVersion()) {
			log(Level.INFO, "Locale file has changed.  Overwriting default locale files with new versions.", false);
			log(Level.INFO, "If you are using a custom locale file, please update with any changes you need.", false);
			
			for(String defaultName : Properties.defaultLocaleFiles){
				this.saveResource(defaultName, true);
			}
			
			this.config.setLocalizationVersion(Properties.localeVersion);
			saveConfig();
		}
		

		// save changes
		saveConfig();
	}
	
	/**
	 * Sends to all of the registered player
	 * the given message 
	 * @param shop	Shop to get the players from
	 * @param msg	Message to send
	 */
	public void sendMessageToAll (Shop shop, String msg) {
		sendMessageToOwner(shop, msg);
		
		for (UUID id : shop.getMembers()) {
			sendMessage(getServer().getPlayer(id), msg);
		}
	}
	
	/**
	 * Sends the owner of this shop the given Message
	 * @param shop	Shop to get the owner of
	 * @param msg	Message to send
	 */
	public void sendMessageToOwner (Shop shop, String msg) {
		sendMessage(getServer().getPlayer(shop.getOwner()), msg);
	}
	
	/**
	 * Sends the given transaction message the owner of
	 * the given shop, if the owner wants to receive them
	 * @param shop		{@link Shop} of the owner to send the message to
	 * @param message	Message to send
	 */
	public void sendTransactionMessageToOwner (Shop shop, String message) {
		// get the player
		Player player = getServer().getPlayer(shop.getOwner());
		
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
	 * @param player	{@link CommandSender} to send the message to
	 * @param msg		Message to send
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
		String className	= plugin.getClass().getName();
		
		// WorldGuard
		if (className.equals("com.sk89q.worldguard.bukkit.WorldGuardPlugin")) {
			registerEvents(new WorldGuardListener(this, plugin));
			log(Level.INFO, "Hooked into WorldGuard", true);
		}
		
		// Essentials Economy
		if (className.equals("com.earth2me.essentials.Essentials") && isAllowedEconomySystem(className)) {
			log(Level.INFO, "Hooked into EssentialsEconomy", true);
			this.balance	= new EssentialsBalance(this, plugin);
		}
		
		// iConomy 5
		if (className.equals("com.iConomy.iConomy") && isAllowedEconomySystem(className)) {
			log(Level.INFO, "Hooked into iConomy5", true);
			this.balance = new iConomy5Balance (this, plugin);
		}
		
		// iConomy 6
		if (className.equals("com.iCo6.iConomy") && isAllowedEconomySystem(className)) {
			log(Level.INFO, "Hooked into iConomy6", true);
			this.balance = new iConomy6Balance (this, plugin);
		}
		
		// iConomy
		if (className.equals("com.iCo8.iConomy") && isAllowedEconomySystem(className)) {
			log(Level.INFO, "Hooked into iConomy8", true);
			this.balance = new iConomy8Balance (this, plugin);
		}
		
		// BOSEconomy
		if (className.equals("cosine.boseconomy.BOSEconomy") && isAllowedEconomySystem(className)) {
			log(Level.INFO, "Hooked into BOSEconomy", true);
			this.balance = new BOSEconomyBalance (this, plugin);
		}
		// Towny
		if (className.equals("com.palmergames.bukkit.towny.Towny")) {
			log(Level.INFO, "Hooked into Towny", true);
			registerEvents(new TownyListener(this));
		}
		
		// Residence
		if (className.equals("com.bekvon.bukkit.residence.Residence") && getConfiguration().getResidenceHookInto()) {
			log(Level.INFO, "Hooked into Residence", true);
			registerEvents(new ResidenceListener(getConfiguration()));
		}
		
		// Vault
		if (className.equals("net.milkbowl.vault.Vault") && isAllowedEconomySystem(className)) {
			// TODO warp it into VaultBalance?
			RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = scs.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
					log(Level.INFO, "Hooked into " + economyProvider.getPlugin().getName(), true);
					balance = new VaultBalance(this, economyProvider.getProvider());
			}
			RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
			if (permissionProvider != null) {
					log(Level.INFO, "Hooked into Vault Permissions", true);
					permission = permissionProvider.getProvider();
			}
		}
		
		//Attach to DropChest API, if loaded
		if (className.equals("com.narrowtux.dropchest.dropchest")){
			log(Level.INFO, "Found Old DropChest.  Attempting to hook api.", true);

			try {
				registerEvents(new DropChestListener(this));
				log(Level.INFO, "Hooked OLD DropChest listener.", true);
			} catch (Exception e) {}
		}
		
		//This supports a fork that was done to upgrade DC to 1.1. 
		if (className.equals("com.noheroes.dropchest.dropchest")){
			log(Level.INFO, "Found New DropChest.  Attempting to hook api.", true);
			try{ 
				registerEvents(new DropChestListenerV2(this));
				log(Level.INFO, "Hooked NEW DropChest listener.", true);
			} catch (Exception e){}
		}
	}
	
	/**
	 * Tires to unhook from the given plugin
	 * @param plugin
	 */
	public void unHookPlugin (Plugin plugin) {
		String className	= plugin.getClass().getName();
		
		if (className.equals("com.iConomy.iConomy")) {
			log(Level.INFO, "Un-hooked iConomy", true);
			this.balance = new DummyBalance(this);
		}
		
		if (className.equals("com.iCo6.iConomy")) {
			log(Level.INFO, "Un-hooked iConomy", true);
			this.balance = new DummyBalance(this);
		}
		
		if (className.equals("com.iCo8.iConomy")) {
			log(Level.INFO, "Un-hooked iConomy", true);
			this.balance = new DummyBalance(this);
		}
		
		if (this.permission != null) {
			if (!this.permission.isEnabled()) {
				log(Level.INFO, "Un-hooked Permissions", true);
				this.permission = null;
			}
		}

		if (className.equals("cosine.boseconomy.BOSEconomy")) {
			log(Level.INFO, "Un-hooked BOSEconomy", true);
			this.balance = new DummyBalance(this);
		}
		
		// Towny
		if (className.equals("com.palmergames.bukkit.towny.Towny")) {
			log(Level.INFO, "Un-hooked Towny", true);
			// TODO why !?
//			this.getServer().getPluginManager().registerEvents(townyListener, this);
		}
	}
	
	/* Allows late binding.
	 * Class used to hook in Plugins like:
	 * - iConomy
	 * - BOSEconomy
	 * - Permission
	 */
	private class ShowCaseStandalonePluginListener implements Listener {
		
//		private ShowCaseStandalone scs;

		public ShowCaseStandalonePluginListener(ShowCaseStandalone plugin) {
//			this.scs = plugin;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			// Try to hook into the plugin
			hookInto(event.getPlugin());
		}
		
		/*
		 * Listen for Permissions, iConomy and BOSEconomy
		 */
		//This is causing all sorts of erros in the new system.  Disable change to dummy class.
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			// Try to hook into the plugin
			unHookPlugin(event.getPlugin());
		}
	}
}
