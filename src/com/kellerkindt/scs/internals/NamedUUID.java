/*
 * ShowCaseStandalone - A Minecraft-Bukkit-API Shop Plugin
 * Copyright (C) 2016-08-16 22:43 +02 kellerkindt (Michael Watzko) <copyright at kellerkindt.com>
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
 */

package com.kellerkindt.scs.internals;

import com.kellerkindt.scs.Properties;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * TODO
 */
@SerializableAs(Properties.ALIAS_NAMED_UUID)
public class NamedUUID extends SimpleChangeable<NamedUUID> implements ConfigurationSerializable {


    // --- for serialization and deserialization ---
    public static final String KEY_VERSION  = "version";
    public static final String KEY_ID       = "id";
    public static final String KEY_NAME     = "name";
    // ---------------------------------------------

    protected UUID   id;
    protected String name;

    public NamedUUID() {
        this(null, null);
    }

    public NamedUUID(UUID id, String name) {
        this.id   = id;
        this.name = name;
    }

    /**
     * @return The {@link UUID} of this {@link NamedUUID}
     */
    public UUID getId() {
        return id;
    }

    /**
     * @param id New {@link UUID} of this {@link NamedUUID}
     * @return itself
     */
    public NamedUUID setId(final UUID id) {
        return setChanged(
                !Objects.equals(this.id, id),
                new Runnable() {
                    @Override
                    public void run() {
                        NamedUUID.this.id = id;
                    }
                }
        );
    }

    /**
     * @return The name of this {@link NamedUUID}
     */
    public String getName() {
        return name;
    }

    /**
     * @param name New name of this {@link NamedUUID}
     * @return itself
     */
    public NamedUUID setName(final String name) {
        return setChanged(
                !Objects.equals(this.name, name),
                new Runnable() {
                    @Override
                    public void run() {
                        NamedUUID.this.name = name;
                    }
                }
        );
    }

    /**
     * @param id New {@link UUID} of this {@link NamedUUID}
     * @param name New name of this {@link NamedUUID}
     * @return itself
     */
    public NamedUUID update(final UUID id, final String name) {
        this.bulkChanges(new Runnable() {
            @Override
            public void run() {
                NamedUUID.this.setId  (id);
                NamedUUID.this.setName(name);
            }
        });
        return this;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put(KEY_VERSION,    Properties.VERSION_NAMED_UUID);

        if (getId() != null) {
            map.put(KEY_ID,     getId().toString());
        }

        if (getName() != null) {
            map.put(KEY_NAME,   getName());
        }

        return map;
    }

    /**
     * @see ConfigurationSerializable
     */
    public static NamedUUID deserialize (Map<String, Object> map) {
        NamedUUID namedUUID = new NamedUUID();

        try {
            namedUUID.id = map.containsKey(KEY_ID) ? UUID.fromString((String) map.get(KEY_ID)) : null;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        namedUUID.name    = (String)map.get(KEY_NAME);

        switch ((Integer)map.get(KEY_VERSION)) {

            case Properties.VERSION_NAMED_UUID:
            default:
                // nothing to do
                break;
        }

        return namedUUID;
    }

    @Override
    public String toString() {
        return "[id="+id+",name="+name+"]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof NamedUUID) {
            NamedUUID other = (NamedUUID)obj;

            // case 1, UUIDs match
            if (this.id != null && other.id != null && this.id.equals(other.id)) {
                return true;
            }

            // case 2, no UUIDs and names match
            if ((this.id == null || other.id == null) && Objects.equals(this.name, other.name)) {
                return true;
            }
        }

        return false;
    }
}
