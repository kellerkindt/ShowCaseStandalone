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
package com.kellerkindt.scs.internals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.shops.BuyShop;
import com.kellerkindt.scs.shops.ExchangeShop;
import com.kellerkindt.scs.shops.SellShop;
import com.kellerkindt.scs.shops.Shop;

/**
* @author Sorklin <sorklin at gmail.com>
* Rewritten by kellerkindt
*/
@SerializableAs(ShowCaseStandalone.ALIAS_TRANSACTION)
public class Transaction implements ConfigurationSerializable {
	
	@SerializableAs(ShowCaseStandalone.ALIAS_TRANSACTION_SHOPTYPE)
	public enum ShopType implements ConfigurationSerializable {
		SELL("sell"),
		BUY("buy"),
		EXCHANGE("exchange");

		public static final String KEY	= "name";
		private String name;
		
		private ShopType (String name) {
			this.name	= name;
		}
		
		/**
		 * @see org.bukkit.configuration.serialization.ConfigurationSerializable#serialize()
		 */
		@Override
		public Map<String, Object> serialize() {
			// create the map
			Map<String, Object> map = new HashMap<String, Object>();
			
			// save
			map.put(KEY, name);
			
			// return it
			return map;
		}
		
		/**
		 * @see ConfigurationSerializable
		 */
		public static ShopType deserialize (Map<String, Object> map) {
			// get the name
			String name = (String)map.get(KEY);
			
			// get the type
			for (ShopType type : ShopType.values()) {
				if (type.name.equals(name)) {
					return type;
				}
			}
			
			// nothing found
			return null;
		}
	}
    
	// --- for serialization and deserialization ---
	public static final String KEY_PLAYER_NAME	= "player.name";
	public static final String KEY_PLAYER_UUID	= "player.uuid";
	public static final String KEY_SHOP_UUID	= "shop.uuid";
	public static final String KEY_SHOP_TYPE	= "shop.type";
	public static final String KEY_PRICE		= "shop.price";
	public static final String KEY_QUANTITY		= "quantity";
	public static final String KEY_TIME_CREATED	= "time.created";
	public static final String KEY_TIME_UNDONE	= "time.undone";
	// ---------------------------------------------
	
	private String		playerName; // for display purpose only (in file)
	private UUID		playerId;
	private UUID		shopId;
	private ShopType	type;
	private int			quantity;
	private double		pricePerItem;
	private long		time;
	private long		timeUndone	= -1;
	
	private Transaction () {}
	
	public Transaction(Player player, Shop shop, int quantity) {
		this(player.getUniqueId(), player.getName(), shop.getUUID(), quantity, shop.getPrice(),
				shop instanceof SellShop 		? ShopType.SELL 	:
				shop instanceof BuyShop	 		? ShopType.BUY		:
				shop instanceof ExchangeShop	? ShopType.EXCHANGE : null);
	}
	
	public Transaction(UUID playerId, String playerName, UUID shopId, int quantity, double pricePerItem, ShopType type) {
		this.playerId		= playerId;
		this.playerName		= playerName;
		this.shopId			= shopId;
		this.quantity		= quantity;
		this.pricePerItem	= pricePerItem;
		this.type			= type;
	}
	
	public void undo (ShowCaseStandalone scs) {
		// TODO
		/*
		 * some kind of the opposite event...
		 * But the ShowCasePlayerBuy/SellEvent need the specific shop...
		 */
	}
	
	// --------------- Getter begin  ---------------
	public String getPlayerName() {
		return playerName;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public UUID getShopId() {
		return shopId;
	}
	
	public ShopType getShopType() {
		return type;
	}

	public int getQuantity() {
		return quantity;
	}

	public double getPricePerItem() {
		return pricePerItem;
	}

	public long getTimeCreated() {
		return time;
	}

	public long getTimeUndone() {
		return timeUndone;
	}
	// --------------- Getter end ------------------
	

	/**
	 * @see org.bukkit.configuration.serialization.ConfigurationSerializable#serialize()
	 */
	@Override
	public Map<String, Object> serialize() {
		// map to serialize to
		Map<String, Object> map = new HashMap<String, Object>();
		
		// store
		map.put(KEY_PLAYER_NAME,	playerName);
		map.put(KEY_PLAYER_UUID,	playerId.toString());
		map.put(KEY_SHOP_UUID, 		shopId);
		map.put(KEY_SHOP_TYPE,		type);
		map.put(KEY_PRICE,			pricePerItem);
		map.put(KEY_QUANTITY,		quantity);
		map.put(KEY_TIME_CREATED,	time);
		map.put(KEY_TIME_UNDONE,	timeUndone);
		
		// return the map
		return map;
	}
	

	/**
	 * @see ConfigurationSerializable
	 */
	public static Transaction deserialize (Map<String, Object> map) {
		// create a new Transaction
		Transaction transaction	= new Transaction();
		
		// load
		transaction.playerName	= 					(String)	map.get(KEY_PLAYER_NAME);
		transaction.playerId	= UUID.fromString(	(String)	map.get(KEY_PLAYER_UUID));
		transaction.shopId		= UUID.fromString(	(String)	map.get(KEY_SHOP_UUID));
		transaction.type		=					(ShopType)	map.get(KEY_SHOP_TYPE);
		transaction.pricePerItem= 					(Double)	map.get(KEY_PRICE);
		transaction.quantity	= 					(Integer)	map.get(KEY_QUANTITY);
		transaction.time		=					(Long)		map.get(KEY_TIME_CREATED);
		transaction.timeUndone	= 					(Long)		map.get(KEY_TIME_UNDONE);
		
		
		// return it
		return transaction;
	}   
}
