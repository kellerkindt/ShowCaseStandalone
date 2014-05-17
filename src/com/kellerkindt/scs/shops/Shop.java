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
package com.kellerkindt.scs.shops;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.listeners.Changeable;


public abstract class Shop implements ConfigurationSerializable, Changeable {
	
	// --- for serialization and deserialization ---
	public static final String KEY_VERSION		= "version";
	
	public static final String KEY_UUID			= "uuid";
	public static final String KEY_AMOUNT		= "amount";
	public static final String KEY_PRICE		= "price";
	public static final String KEY_UNLIMITED	= "unlimited";
	public static final String KEY_ITEMSTACK	= "itemstack";
	public static final String KEY_MEMBERS		= "members";

	public static final String KEY_OWNER		= "owner";
	public static final String KEY_WORLD		= "world";
	
	public static final String KEY_LOCATION		= "location";
	// ---------------------------------------------
	
	
	private int							lastHash		= -1;
	private int							version			= Properties.VERSION_SHOP;
	
	private UUID						uuid			= null;
	private Location					location		= null;
	private ItemStack					itemStack		= null;
	private boolean						isVisible		= false;

	private UUID						worldUUID		= null;
	
	private int 						amount			= 0;
	private double 						price 			= 0;
	private boolean 					isUnlimited 	= false;
	
	private UUID						owner			= null;
	private String						ownerName		= null;
	private List<UUID>					members			= new ArrayList<UUID>();
	

	protected Shop () {
		// for deserialization
	}
	
	/**
	 * Main attributes, but there are more that have to be set!
	 */
	public Shop (UUID uuid, UUID owner, Location location, ItemStack itemStack) {
		setUUID		(uuid);
		setOwner	(owner);
		setLocation	(location);
		setItemStack(itemStack);
	}
	
	/**
	 * @return The version this {@link Shop} has been created (or loaded) at
	 */
	public int getVersion () {
		return version;
	}
	
	/**
	 * @return The Unique Shop ID
	 */
	public UUID getUUID () {
		return uuid;
	}
	
	/**
	 * Sets the UUID for this Shop
	 * @param uuid UUID to set
	 */
	public void setUUID (UUID uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * @param value the location of this shop
	 */
	public void setLocation (Location value) {
		// can be null on creation
		if (value != null) {
			location = value;
			setWorld(value.getWorld());
		}
	}
	
	/**
	 * @return The spawn point
	 */
	public Location getSpawnLocation () {
		return getLocation().clone().add(0.5, 1.2, 0.5);	// TODO: spawn issue?
	}
	
	/**
	 * @return The block of the current location
	 */
	public Block getBlock () {
		return getLocation().getBlock();
	}
	
	/**
	 * @return the location where the shop is placed
	 */
	public Location getLocation () {
		return location;
	}
	
	/**
	 * @return the chunk where the shop is placed
	 */
	public Chunk getChunk () {
		return getLocation().getChunk();
	}
	
	/**
	 * @param value the world where the plugin is in
	 */
	public void setWorld (World value) {
		getLocation().setWorld(value);
		
		if (value != null) {
			worldUUID = value.getUID();
		}
	}
	
	/**
	 * @return the world where the shop is placed
	 */
	public World getWorld () {
		return getLocation().getWorld();
	}
	
	/**
	 * @return The {@link UUID} of the current world
	 */
	public UUID getWorldUUID () {
		return worldUUID;
	}
	
	/**
	 * @param uuid The {@link UUID} of the new owner
	 */
	public void setOwner (UUID uuid) {
		this.owner = uuid;
	}
	
	/**
	 * @return The {@link UUID} of the owner
	 */
	public UUID getOwner () {
		return owner;
	}
	
	/**
	 * Sets the ItemStack for this shop
	 * @param value	The ItemStack to set
	 */
	public void setItemStack (ItemStack value) {
		this.itemStack	= value;
	}
	
	/**
	 * @return The ItemStack of this shop
	 */
	public ItemStack getItemStack () {
		return itemStack; 
	}
	
	/**
	 * @param value the amount of items
	 */
	public void setAmount (int value) {
		this.amount	= value;
	}
	
	/**
	 * @return the amount of items in this shop
	 */
	public int getAmount () {
		return amount;
	}
	
	/**
	 * @param value the price of this shop
	 */
	public void setPrice (double value) {
		this.price	= value;
	}
	
	/**
	 * @return the price for one item in this shop
	 */
	public double getPrice () {
		return price;
	}
	
	/**
	 * @param value true if the shop is unlimited, false if not
	 */
	public void setUnlimited (boolean value) {
		this.isUnlimited	= value;
	}
	
	/**
	 * @return true if the shop is a unlimited shop, false if not
	 */
	public boolean isUnlimited () {
		return isUnlimited;
	}
	
	/**
	 * Sets whether this shop is visible
	 * @param value if the shop is visible, false if not
	 */
	public void setVisible (boolean value) {
		this.isVisible	= value;
	}
	
	/**
	 * @return true if the shop is visible, false if not
	 */
	public boolean isVisible () {
		return isVisible;
	}
	
	/**
	 * @return true if the shop is active i.e. does the sell shop have stuff to sell, does the buy shop have stuff to buy ... false if not
	 */
	public abstract boolean isActive ();
	
	/**
	 * @return The list of all members
	 */
	public List<UUID> getMembers() {
		return members;
	}
	
	/**
	 * @param members The new list of members to set
	 */
	public void setMembers (List<UUID> members) {
		this.members = members;
	}
	
	/**
	 * Adds a member to this shop, if
	 * it isn't added yet
	 * @param id	{@link UUID} of the member to add
	 */
	public void addMember (UUID id) {
		if (id != null && !getMembers().contains(id)) {
			getMembers().add(id);
		}
	}
	
	/**
	 * Removes a member from this shop
	 * @param name
	 */
	public void removeMember (UUID name) {
		getMembers().remove(name);
	}
	
	/**
	 * @return Whether the given player is a member of this shop  
	 */
	public boolean isMember (UUID player) {
		return getMembers().contains(player);
	}
	
	/**
	 * @return Whether the given player is the owner of this shop, if no one is set, it always return true
	 */
	public boolean isOwner (UUID player) {
		return getOwner() != null ? getOwner().equals(player) : true;
	}


	/**
	 * Checks if the player can manage this shop
	 * That means he has to be the owner or a member
	 * @param sender
	 * @return
	 */
	public boolean isOwnerOrMember (UUID player) {
		if (isOwner(player) || isMember(player))
			return true;
		
		return false;
	}
	

    /**
     * @see org.bukkit.configuration.serialization.ConfigurationSerializable#serialize()
     */
    @Override
    public Map<String, Object> serialize() {
    	Map<String, Object> map = new HashMap<String, Object>();

    	map.put(KEY_VERSION, 	Properties.VERSION_SHOP);
    	map.put(KEY_UUID, 		getUUID().toString());
    	map.put(KEY_AMOUNT, 	getAmount());
    	map.put(KEY_PRICE, 		getPrice());
    	map.put(KEY_UNLIMITED, 	isUnlimited());
    	map.put(KEY_ITEMSTACK, 	getItemStack());
    	
    	List<String> members = new ArrayList<String>();
    	for (UUID uuid : getMembers()) {
    		members.add(uuid.toString());
    	}
    	map.put(KEY_MEMBERS, 	members);

    	map.put(KEY_OWNER, 		getOwner()			.toString());
    	map.put(KEY_WORLD,		getWorld().getUID()	.toString());
    	
    	// fix for fail import
    	map.put(KEY_LOCATION,	new Double[]{
    			getLocation().getX(),
    			getLocation().getY(),
    			getLocation().getZ()});
    	
    	
    	
    	return map;
    }
    
    
    /**
     * Has to be invoked by a child!
     * Loads the given values into this shop
     * @param map	Map of values
     */
    @SuppressWarnings("unchecked")
	protected void deserialize (Map<String, Object> map, Server server) {
    	
    	// load the version this Shop has been created
		version		= map.containsKey(KEY_VERSION) ? (Integer)map.get(KEY_VERSION) : 0;
    	
    	switch (version) {
			default:
    		case 0:
    			
    			// set the worlds UUID (first entry in the list) as the world itself
    			map.put(KEY_WORLD, ((List<String>)map.get(KEY_WORLD)).get(0));
    			
    			// convert the owners name to its UUID
    			map.put(KEY_OWNER, ""+ShowCaseStandalone.getPlayerUUID((String)map.get(KEY_WORLD), server));
    			
    			
    			// convert the members from names to UUIDs
    			List<String> listMemberNames	= (List<String>)map.get(KEY_MEMBERS);
    			List<String> listMemberUUIDs	= new ArrayList<String>();
    			map.put(KEY_MEMBERS, listMemberUUIDs);
    			
    			for (String name : listMemberNames) {
    				listMemberUUIDs.add( ""+ShowCaseStandalone.getPlayerUUID(name, server) );
    			}
    			
    	
    		case Properties.VERSION_SHOP:
    			break;
    			
    	}
    	
    	
    	setUUID		(UUID.fromString((String)map.get(KEY_UUID)));
    	setAmount	((Integer)	map.get(KEY_AMOUNT));
    	setPrice	((Double)	map.get(KEY_PRICE));
    	setUnlimited((Boolean)	map.get(KEY_UNLIMITED));
    	setItemStack((ItemStack)map.get(KEY_ITEMSTACK));
    	
    	// load the members
    	for (String id : ((List<String>)map.get(KEY_MEMBERS))) {
			addMember(UUID.fromString(id));
    	}

    	// load the world
		setOwner(UUID.fromString( (String)map.get(KEY_OWNER) ));
    	
    	
    	
    	// load the location
						worldUUID		= UUID.fromString( (String)map.get(KEY_WORLD) );
		World 			world			= server.getWorld( worldUUID );
		List<Double> 	listLocation	= (List<Double>)map.get(KEY_LOCATION);
    	
		// set the location
    	setLocation(
    			new Location(
    					world,
    					listLocation.get(0),
    					listLocation.get(1),
    					listLocation.get(2)
    					)
    			);
    	
    	/*
    	 *  if there was no upgrade, resetHasChanged;
    	 *  since nothing is different from file
    	 */
    	if (version == Properties.VERSION_SHOP) {
    		resetHasChanged();
    	}
    }
    
    /**
     * @see com.kellerkindt.scs.listeners.Changeable#hasChanged()
     */
    @Override
    public boolean hasChanged() {
    	return lastHash != hashCode();
    }
    
    /**
     * @see com.kellerkindt.scs.listeners.Changeable#resetHasChanged()
     */
    @Override
    public void resetHasChanged() {
    	lastHash = hashCode();
    }
    
    @Override
    public int hashCode() {
    	return   (uuid 			!= null ? uuid.hashCode() 			: 0)
    			+(location 		!= null ? location.hashCode() 		: 0)
    			+(itemStack		!= null ? itemStack.hashCode()		: 0)
    			+(isUnlimited 			? 1 						: 0)
    			+(worldUUID		!= null ? worldUUID.hashCode()		: 0)
    			+(getMembers()	!= null	? getMembers().hashCode()	: 0)
    			+(int)price
    			+amount
    			+(owner			!= null ? owner.hashCode()			: 0)
    			+(ownerName		!= null ? owner.hashCode()			: 0)
    			+members.hashCode();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return getClass().getSimpleName()+"[uid="+uuid+",owner="+owner+"]@"+hashCode();
    }
}

