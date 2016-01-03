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


import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.internals.NamedUUID;
import com.kellerkindt.scs.internals.SimpleChangeable;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;

/**
 * @param <T> The type of the child class
 */
public abstract class Shop<T extends Shop<?>> extends SimpleChangeable<T> implements ConfigurationSerializable {
    
    // --- for serialization and deserialization ---
    public static final String KEY_VERSION      = "version";
    
    public static final String KEY_ID           = "id";
    public static final String KEY_AMOUNT       = "amount";
    public static final String KEY_PRICE        = "price";
    public static final String KEY_UNLIMITED    = "unlimited";
    public static final String KEY_ITEMSTACK    = "itemstack";
    public static final String KEY_MEMBERS      = "members";

    public static final String KEY_OWNER        = "owner";
    public static final String KEY_WORLD        = "world";
    
    public static final String KEY_LOCATION     = "location";
    // ---------------------------------------------


    protected final ShowCaseStandalone scs;

    protected UUID        id          = null;
    protected Location    location    = null;
    protected ItemStack   itemStack   = null;
    protected boolean     visible     = false;
    protected int         amount      = 0;
    protected double      price       = 0;
    protected boolean     unlimited   = false;

    protected NamedUUID         world   = null;
    protected NamedUUID         owner   = null;
    protected List<NamedUUID>   members = new ArrayList<NamedUUID>();


    protected Shop () {
        // for deserialization
        // TODO find a  more elegant solution
        this.scs = ShowCaseStandalone.get();
    }
    
    /**
     * Main attributes, but there are more that have to be set!
     */
    public Shop (ShowCaseStandalone scs, UUID id, NamedUUID owner, Location location, ItemStack itemStack) {
        this.scs        = scs;
        this.id         = id;
        this.owner      = owner;
        this.location   = location;
        this.itemStack  = itemStack;

        this.world      = new NamedUUID();

        if (location != null && location.getWorld() != null) {
            world.setName(location.getWorld().getName());
            world.setId  (location.getWorld().getUID());
        }
    }


    /**
     * @return The {@link UUID} of this {@link Shop}
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Sets the {@link UUID} for this {@link Shop}
     * @param id {@link UUID} to set
     * @return itself
     */
    public T setId(final UUID id) {
        return setChanged(
                !Objects.equals(this.id, id),
                new Runnable() {
                    @Override
                    public void run() {
                        Shop.this.id = id;
                    }
                }
        );
    }

    /**
     * @deprecated Use {@link #getId()} instead
     * @return The {@link UUID} of this {@link Shop}
     */
    public UUID getUUID () {
        return getId();
    }

    /**
     * @deprecated Use {@link #setId(UUID)} instead
     * Sets the {@link UUID} for this {@link Shop}
     * @param id {@link UUID} to set
     */
    public void setUUID (UUID id) {
        setId(id);
    }


    
    /**
     * @param location the {@link Location} of this {@link Shop}
     * @return itself
     */
    public Shop setLocation (final Location location) {
        // can be null on creation
        return setChanged(
                !Objects.equals(this.location, location),
                new Runnable() {
                    @Override
                    public void run() {
                        Shop.this.location = location;
                        Shop.this.setWorld(location.getWorld());
                    }
                }
        );
    }
    
    /**
     * @deprecated Isn't that out of the {@link Shop}s scope?
     * @return The {@link Location} of where to spawn the {@link ItemStack}
     */
    public Location getSpawnLocation () {
        return getLocation().clone().add(0.5, 1.2, 0.5);    // TODO: spawn issue?
    }
    
    /**
     * @return The {@link Block} of the current {@link Location}
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
     * @deprecated Use {@link #getWorldId()} or {@link #getWorldName()} instead
     * @return the world where the shop is placed
     */
    public World getWorld () {
        return getLocation().getWorld();
    }

    /**
     * @return The {@link UUID} of the {@link World} this {@link Shop} is in
     */
    public UUID getWorldId() {
        return world.getId();
    }

    /**
     * @return The last known name of thie {@link World} this {@link Shop} is in
     */
    public String getWorldName() {
        return world.getName();
    }
    
    /**
     * @param world The new {@link World} where this {@link Shop} is in
     */
    public T setWorld (final World world) {
        // update the location instance
        getLocation().setWorld(world);

        // save the new world
        return setChanged(
                !(Objects.equals(world.getUID(), this.world.getId()) && Objects.equals(world.getName(), this.world.getName())),
                new Runnable() {
                    @Override
                    public void run() {
                        Shop.this.world.update(
                                world.getUID(),
                                world.getName()
                        );
                    }
                }
        );
    }
    
    /**
     * @param id The {@link UUID} of the new owner of this {@link Shop
     * @return itself
     */
    public T setOwner (final UUID id) {
        return setChanged(
                !Objects.equals(owner.getId(), id),
                new Runnable() {
                    @Override
                    public void run() {
                        owner.setId(id);
                        owner.setName(scs.getPlayerNameOrNull(id));
                    }
                }
        );
    }

    /**
     * @param name The name of the new owner of this {@link Shop}
     * @return itself
     */
    public T setOwner(final String name) {
        return setChanged(
                !Objects.equals(owner.getName(), name),
                new Runnable() {
                    @Override
                    public void run() {
                        owner.setId  (scs.getPlayerUUID(name));
                        owner.setName(name);
                    }
                }
        );
    }
    
    /**
     * @return The {@link UUID} of the owner of this {@link Shop or null
     */
    public UUID getOwnerId() {
        return owner.getId();
    }

    /**
     * @return The last known name of the owner of this {@link Shop or null
     */
    public String getOwnerName() {
        return owner.getName();
    }
    
    /**
     * @param itemStack The new {@link ItemStack} of this {@link Shop}
     * @return itself
     */
    public T setItemStack (final ItemStack itemStack) {
        return setChanged(
                !Objects.equals(this.itemStack, itemStack),
                new Runnable() {
                    @Override
                    public void run() {
                        Shop.this.itemStack = itemStack;
                    }
                }
        );
    }
    
    /**
     * @return The {@link ItemStack} of this shop
     */
    public ItemStack getItemStack () {
        return itemStack; 
    }
    
    /**
     * @param amount The new amount of items in this {@link Shop}
     * @return itself
     */
    public T setAmount (final int amount) {
        return setChanged(
                this.amount != amount,
                new Runnable() {
                    @Override
                    public void run() {
                        Shop.this.amount = amount;
                    }
                }
        );
    }
    
    /**
     * @return The amount of items in this {@link Shop}
     */
    public int getAmount () {
        return amount;
    }
    
    /**
     * @param price The new price of one item in this {@link Shop}
     * @return itself
     */
    public T setPrice (final double price) {
        return setChanged(
                this.price != price,
                new Runnable() {
                    @Override
                    public void run() {
                        Shop.this.price = price;
                    }
                }
        );
    }
    
    /**
     * @return The price for one item in this {@link Shop}
     */
    public double getPrice () {
        return price;
    }
    
    /**
     * @param unlimited Whether this {@link Shop} has an unlimited supply
     * @return itself
     */
    public T setUnlimited (final boolean unlimited) {
        return setChanged(
                this.unlimited != unlimited,
                new Runnable() {
                    @Override
                    public void run() {
                        Shop.this.unlimited = unlimited;
                    }
                }
        );
    }
    
    /**
     * @return Whether this {@link Shop} has an unlimited supply
     */
    public boolean isUnlimited () {
        return unlimited;
    }
    
    /**
     * @param visible Whether this {@link Shop} is currently visible
     */
    public T setVisible (final boolean visible) {
        return setChanged(
                this.visible = visible,
                new Runnable() {
                    @Override
                    public void run() {
                        Shop.this.visible = visible;
                    }
                }
        );
    }
    
    /**
     * @return Whether this {@link Shop} is currently visible
     */
    public boolean isVisible () {
        return visible;
    }
    
    /**
     * @return Whether this {@link Shop} is active i.e. does the sell shop have stuff to sell, does the buy shop have stuff to buy ...
     */
    public abstract boolean isActive ();
    
    /**
     * @return An {@link Iterable} of all members of this {@link Shop} as {@link NamedUUID}
     */
    public Iterable<NamedUUID> getMembers() {
        return members;
    }
    
    /**
     * @param id {@link UUID} of the member to add
     * @return itself
     */
    public T addMember (UUID id) {
        return addMember(id, scs.getPlayerNameOrNull(id));
    }

    /**
     * @param name Name of the member to add
     * @return itself
     */
    public T addMember (String name) {
        return addMember(scs.getPlayerUUID(name), name);
    }

    /**
     * To be able to add a member here, either the id or name
     * mustn't be null
     *
     * @param id {@link UUID} of the member to add or null if unkown
     * @param name Name of the member to add or null if unknown
     * @return itself
     */
    public T addMember (final UUID id, final String name) {
        boolean add = id != null || name != null;

        if (add && id != null) {
            for (NamedUUID member : members) {
                if (id.equals(member.getId())) {
                    add = false;
                    break;
                }
            }
        }

        if (add && id == null && name != null) {
            for (NamedUUID member : members) {
                if (name.equals(member.getName())) {
                    add = false;
                    break;
                }
            }
        }

        return setChanged(
                add,
                new Runnable() {
                    @Override
                    public void run() {
                        members.add(new NamedUUID(id, name));
                    }
                }
        );
    }

    /**
     * @param id {@link UUID} to remove
     * @return itself
     */
    public T removeMember(UUID id) {
        return removeMember(id, null);
    }

    /**
     * WARNING: This removes the first member that
     * has the same name as given; remember players
     * can have the same names with different {@link UUID}s
     *
     * @param name Name of a member to remove
     * @return itself
     */
    public T removeMember(String name) {
        return removeMember(null, name);
    }

    /**
     * WARNING: Names alone are not unique, there
     * could be multiple players with the same name
     * as given; therefore it alone could result in
     * removing the wrong member
     *
     * Either the {@link UUID} or name has to be not null
     * in order to remove a member
     *
     * @param id {@link UUID} of the member to remove or null if unknown
     * @param name Name of a member to remove or null if unknown
     * @return itself
     */
    public T removeMember(UUID id, String name) {
        NamedUUID toRemove = null;

        if (id != null || name != null) {
            for (NamedUUID member : members) {
                if ((id == null || Objects.equals(member.getId(), id)) && (name == null || Objects.equals(member.getName(), name))) {
                    toRemove = member;
                    break;
                }
            }
        }

        final NamedUUID remove = toRemove;
        return setChanged(
                toRemove != null,
                new Runnable() {
                    @Override
                    public void run() {
                        Shop.this.members.remove(remove);
                    }
                }
        );
    }

    /**
     * @param id {@link UUID} of the player to check
     * @return Whether the player with the given {@link UUID} is a member of this {@link Shop}
     */
    public boolean isMember(UUID id) {
        if (id == null) {
            return false;
        }

        for (NamedUUID member : members) {
            if (id.equals(member.getId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * WARNING:  Names are not unique, there could be multiple players
     * with the same name, therefore this might not be reliable
     *
     * @param name Name to check
     * @return Whether there is a player with the given reigstered as member of this {@link Shop}
     */
    public boolean isMember(String name) {
        if (name == null) {
            return false;
        }

        for (NamedUUID member : members) {
            if (name.equals(member.getName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param id {@link UUID} to check for ownership, null for nobody
     * @return Whether the given {@link UUID} has the ownership of this {@link Shop}
     */
    public boolean isOwner(UUID id) {
        return Objects.equals(owner.getId(), id);
    }

    /**
     * WARNING: Names are not unique, there could be multiple players
     * with the same name, therefore this might not be reliable
     *
     * @param name Name to check for ownership, null for nobody
     * @return Whether the given name has the ownership of this {@link Shop}
     */
    public boolean isOwner(String name) {
        return Objects.equals(owner.getName(), name);
    }

    /**
     * @param id {@link UUID} to check ownership and membership for, null returns false
     * @return Whether the player for the given {@link UUID} is either the owner or a member of this {@link Shop}
     */
    public boolean isOwnerOrMember(UUID id) {
        return id != null && isOwner(id) || isMember(id);
    }

    /**
     * WARNING: Names are not unique, there could be multiple players
     * with the same name, therefore this might not be reliable
     *
     * @param name Name to check ownership and membership for, null returns false
     * @return Whther there is a player with the given name that has either the ownership or membership of this {@link Shop}
     */
    public boolean isOwnerOrMember(String name) {
        return name != null && isOwner(name) || isMember(name);
    }


    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put(KEY_VERSION,   Properties.VERSION_SHOP);
        map.put(KEY_ID,        getId().toString());
        map.put(KEY_AMOUNT,    getAmount());
        map.put(KEY_PRICE,     getPrice());
        map.put(KEY_UNLIMITED, isUnlimited());
        map.put(KEY_ITEMSTACK, getItemStack());
        
        List<NamedUUID> members = new ArrayList<NamedUUID>();
        for (NamedUUID member : getMembers()) {
            members.add(member);
        }
        map.put(KEY_MEMBERS,     members);


        map.put(KEY_OWNER,     owner);
        map.put(KEY_WORLD,     world);
        
        // fix for fail import
        map.put(KEY_LOCATION,    new Double[]{
                getLocation().getX(),
                getLocation().getY(),
                getLocation().getZ()
        });

        return map;
    }
    
    
    /**
     * Has to be invoked by a child!
     * Loads the given values into this shop
     * @param map    Map of values
     */
    @SuppressWarnings("unchecked")
    protected void deserialize (Map<String, Object> map, Server server) {
        
        // load the version this Shop has been created
        int     version       = map.containsKey(KEY_VERSION) ? (Integer)map.get(KEY_VERSION) : 0;
        boolean importWorld1  = true;
        boolean importOwner1  = true;
        boolean importMember1 = true;
        
        switch (version) {
            default:
            case 0:
                try {
                    // set the worlds UUID (first entry in the list) as the world itself
                    List<String> world = (List<String>)map.get(KEY_WORLD);
                    map.put(KEY_WORLD, new NamedUUID(
                            world != null && world.size() > 0 ? UUID.fromString(world.get(0)) : null,
                            world != null && world.size() > 1 ? world.get(1) : null
                    ));

                    // already at output state of case1
                    importWorld1 = false;

                } catch (Throwable t) {
                    scs.getLogger().log(Level.SEVERE, "Failed to import world from version="+version, t);
                }

                try {
                    // convert the owners name to its UUID
                    map.put(KEY_OWNER, new NamedUUID(
                            map.containsKey(KEY_OWNER) ? scs.getPlayerUUID((String)map.get(KEY_OWNER)) : null,
                            (String)map.get(KEY_OWNER)
                    ));

                    // already at output state of case1
                    importOwner1 = false;

                } catch (Throwable t) {
                    scs.getLogger().log(Level.SEVERE, "Failed to import owner="+map.get(KEY_OWNER), t);
                }
                

                try {
                    // convert the members from names to UUIDs
                    List<String>    names   = (List<String>)map.get(KEY_MEMBERS);
                    List<NamedUUID> members = new ArrayList<NamedUUID>();

                    for (String name : names) {
                        members.add(new NamedUUID(
                                scs.getPlayerUUID(name),
                                name
                        ));
                    }

                    // save the import
                    map.put(KEY_MEMBERS, members);

                    // already at output state of case1
                    importMember1 = false;
                } catch (Throwable t) {
                    scs.getLogger().log(Level.SEVERE, "Failed to import at least one member", t);
                }

            case 1:
                // key got renamed
                map.put(KEY_ID, map.get("uuid"));

                // import the world
                if (importWorld1) {
                    try {
                        UUID  id    = UUID.fromString((String)map.get(KEY_WORLD));
                        World world = server.getWorld(id);

                        map.put(KEY_WORLD, new NamedUUID(
                                id,
                                world != null ? world.getName() : null
                        ));
                    } catch (Throwable t) {
                        scs.getLogger().log(Level.SEVERE, "Failed to import world="+map.get(KEY_WORLD), t);
                    }
                }

                // import the owner
                if (importOwner1) {
                    try {
                        map.put(KEY_OWNER, new NamedUUID(
                                map.containsKey(KEY_OWNER) ? UUID.fromString((String) map.get(KEY_OWNER)) : null,
                                null
                        ));
                    } catch (Throwable t) {
                        scs.getLogger().log(Level.SEVERE, "Failed to import owner="+map.get(KEY_OWNER), t);
                    }
                }

                // import members
                if (importMember1) {
                    List<NamedUUID> members = new ArrayList<NamedUUID>();
                    for (String id : ((List<String>)map.get(KEY_MEMBERS))) {
                        try {
                            members.add(new NamedUUID(
                                    UUID.fromString(id),
                                    scs.getPlayerNameOrNull(UUID.fromString(id))
                            ));
                        } catch (Throwable t) {
                            scs.getLogger().log(Level.WARNING, "Failed to import member for UUID="+id, t);
                        }
                    }

                    // save the import
                    map.put(KEY_MEMBERS, members);
                }

            case Properties.VERSION_SHOP:
                break;
                
        }

        this.id         = UUID.fromString((String)map.get(KEY_ID));
        this.amount     = (Integer)map.get(KEY_AMOUNT);
        this.price      = (Double) map.get(KEY_PRICE);
        this.unlimited  = (Boolean)map.get(KEY_UNLIMITED);
        this.itemStack  = (ItemStack)map.get(KEY_ITEMSTACK);
        
        // load the members
        this.members.clear();
        this.members.addAll((List<NamedUUID>)map.get(KEY_MEMBERS));

        this.owner  = (NamedUUID)map.get(KEY_OWNER);
        this.world  = (NamedUUID)map.get(KEY_WORLD);


        List<Double> listLocation = (List<Double>)map.get(KEY_LOCATION);
        World        world        = this.world.getId() != null ? server.getWorld(this.world.getId()) : null;

        if (world == null && this.world.getName() != null) {
            world = server.getWorld(this.world.getName());
        }
        // set the location
        this.location = new Location(
                world,
                listLocation.get(0),
                listLocation.get(1),
                listLocation.get(2)
        );
        
        /*
         * if something had to be imported,
         * the shop needs to be rewritten
         */
        if (version != Properties.VERSION_SHOP) {
            this.setChanged(true, new Runnable() {
                @Override
                public void run() {
                    // nothing to do
                }
            });
        }

        else {
            // nothing to do, loading alone is no reason to have the changed state
            this.resetHasChanged();
        }
    }
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getSimpleName()+"[uid="+ id +",owner="+owner+"]@"+hashCode();
    }
}

