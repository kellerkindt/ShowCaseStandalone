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
package com.kellerkindt.scs.utilities;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.exceptions.MissingOrIncorrectArgumentException;
import com.kellerkindt.scs.shops.Shop;


public class Utilities {
    
    
    private Utilities () {}
    
    
    /*
     * Quelle: http://www.anyexample.com/programming/java/java_simple_class_to_compute_sha_1_hash.xml
     */
    public static String convertToHex(byte[] data) { 
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    } 
 
    public static String sha1(String text)  throws IOException  { 
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            byte[] sha1hash = new byte[40];
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (Exception e) {
            throw new IOException (e.toString());
        }
    }
    
    public static String getRandomSha1 () {
        byte    bytes[]    = new byte[64];
        new Random().nextBytes(bytes);
        
        return getRandomSha1(new String(bytes));
    }
    
    public static String getRandomSha1 (String s) {
        String sha1    = new Random().nextDouble() + s + System.nanoTime();
        
        try {
            sha1 =  sha1 (sha1);
        } catch (IOException ioe) { }
        
        return sha1;
    }
    
    
    /**
     * Returns MaterialData for log:2,Wool:4 etc
     */
    public static ItemStack getItemStackFromString (String material) throws IOException {
        String args[] = new String[2];
        if (material.contains(":"))
            args = material.split(":");
        else {
            args[0] = material;
            args[1] = "0";
        }
        
        try {
            Material m        = Material.getMaterial(args[0].toUpperCase());
            if (m == null)
                m               = Material.getMaterial(Integer.parseInt(args[0]));
            int     data     = Integer.parseInt(args[1]);
            int        amount    = 0;
            
            if (Properties.DEFAULT_STACK_TO_MAX) {
                amount = m.getMaxStackSize();
            } else {
                amount = Properties.DEFAULT_STACK_AMOUNT;
            }
            
            // for the books (getHandle().tag...)
            return new ItemStack(m, amount, (short)data);
//            return new ItemStack(m, 1, (short)data);
            //return new MaterialData (m, (byte)data);
        } catch (Exception e) {
            throw new IOException (e);
        }
    }
    
    public static MaterialData getMaterialsFromString (String material) throws IOException {
        String args[] = new String[3];
        
        args[0]    = material;
        args[1] = "0";
        
        // load
        if (material.contains(":"))
            args = material.split(":");
        
        try {
            Material m    = Material.getMaterial(args[0].toUpperCase());
            
            if (m == null)
                m = Material.getMaterial(Integer.parseInt(args[0]));
            
            int data = Integer.parseInt(args[1]);
            
            return new MaterialData (m, (byte)data);
        } catch (Exception e) {
            throw new IOException (e);
        }
    }
    
    public static String getStringFromMaterial (MaterialData material) {
        byte    data    = material.getData();
        String     name    = material.toString();
        return name+":"+((int)data);
    }
    
    public static ItemStack getItemStack(Player player, String arg) throws MissingOrIncorrectArgumentException {
        try {
            if(arg.equalsIgnoreCase("this")){
                // get rid of the CraftBukkit version ... causes errors later
                return new ItemStack( player.getItemInHand() );
            } else {
                return Utilities.getItemStackFromString(arg.toUpperCase());
            }
        } catch (Exception ex) {
            throw new MissingOrIncorrectArgumentException();
        }
    }
    
//    public static Activity getActivity (String string) {
//        for (Activity a : Activity.values())
//            if (a.toString().equalsIgnoreCase(string))
//                return a;
//        return null;
//    }
    
    @Deprecated
    public static Enchantment getEnchantmentFromString(String e){
        
        Enchantment ench = null;
        
        String args[] = new String[2];
        if (e.contains(":"))
                args = e.split(":");
        else {
        args[0] = e;
        args[1] = "1";
        }
        
        try{
            ench = Enchantment.getById(Integer.parseInt(args[0]));
        } catch (NumberFormatException nfe) {
        } catch (Exception ex){
            ex.printStackTrace();
        }
        
        return ench;
    }
    
    @Deprecated
    public static int getEnchantmentLevelFromString(String e){
        
        int strength = 1;
        String args[] = new String[2];
        if (e.contains(":"))
                args = e.split(":");
        else {
        args[0] = e;
        args[1] = "1";
        }
        
        try {
            strength = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            //Can't parse to a number.  Assume a strength of 1.
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return strength;
    }
    
    
    
    /**
     * From https://github.com/davboecki/SignCodePad/blob/master/de/davboecki/signcodepad/event/SignCreate.java
     * but changed by kellerkindt
     * 
     * @param sign     The Sign
     * @return         The block where the sign is placed on
     */
    public static Block getBlockBehind(Sign sign) {
        Location signloc = sign.getBlock().getLocation();
        double x = -1;
        double y = signloc.getY();
        double z = -1;

        switch ((int) sign.getRawData()) {
            //west
            case 2:
                x = signloc.getX();
                z = signloc.getZ() + 1;
    
                break;
    
            //east
            case 3:
                x = signloc.getX();
                z = signloc.getZ() - 1;
    
                break;
    
            //south
            case 4:
                x = signloc.getX() + 1;
                z = signloc.getZ();
    
                break;
    
            //north
            case 5:
                x = signloc.getX() - 1;
                z = signloc.getZ();
    
                break;
            default:
                return null;
                
        }

        return sign.getBlock().getWorld().getBlockAt(new Location(sign.getBlock().getWorld(), x, y, z));
    }
    
    
    /**
     * Checks if the Shop is behind the Sign
     * @param sign
     * @param shop
     * @return
     */
    public static boolean isShopBehind (Sign sign, Shop shop) {
        
        Block    block    = getBlockBehind(sign);
        
        if (block == null)
            return false;
        
        int    x    = block.getX() - shop.getLocation().getBlockX();
        int    y    = block.getY() - shop.getLocation().getBlockY();
        int    z    = block.getZ() - shop.getLocation().getBlockZ();
        
        return x == 0 && y == 0 && z == 0; 
    }
    
//    /**
//     * Converts  the given String into
//     * the NBTBase it was before
//     * @param string String to load from
//     * @return Loaded NBTBase
//     */
//    public static NBTBase getNBTBaseFromString (String string) {
//        
//        // create streams
//        byte[]                    bytes    = new HexBinaryAdapter().unmarshal(string);
//        ByteArrayInputStream    bais    = new ByteArrayInputStream(bytes);
//        DataInputStream            dis        = new DataInputStream(bais);
//        
//        // load
//        return NBTBase.b(dis);
//    }
    
    
    /**
     * Serializes the MetaData of an ItemStack
     * and marshals it then in a String
     * @param itemMeta The MetaData so save
     * @return The marshaled ItemStack as String
     * @throws IOException On any internal Exception
     */
    public static String toHexString (ItemMeta itemMeta) throws IOException {
        
        // create streams
        ByteArrayOutputStream    baos    = new ByteArrayOutputStream();
        ObjectOutputStream        oos        = new ObjectOutputStream(baos);
        
        // write map
        oos.writeObject(itemMeta.serialize());
        oos.flush();
        
        // toHexString
        return new HexBinaryAdapter().marshal(baos.toByteArray());
    }
    
    /**
     * Deserializes the ItemMeta of an ItemStack from a marshaled String
     * @param hex ItemMeta marshaled in a String
     * @return The deserialized ItemMeta
     * @throws IOException On any internal exception
     */
    @SuppressWarnings("unchecked")
    public static ItemMeta toItemMeta (String hex) throws IOException {
        
        // create streams
        byte[]                    bytes    = new HexBinaryAdapter().unmarshal(hex);
        ByteArrayInputStream    bais    = new ByteArrayInputStream(bytes);
        ObjectInputStream        ois        = new ObjectInputStream(bais);
        
        try {
            return (ItemMeta)ConfigurationSerialization.deserializeObject((Map<String, Object>)ois.readObject());
            
        } catch (ClassNotFoundException cnfe) {
            throw new IOException(cnfe);
        }
    }
    
    /**
     * Converts the given ItemStack to a representative String
     * @param itemStack ItemStack to convert to a String
     * @return Representative String for the ItemStack
     */
    public static String toString (ItemStack itemStack) {
        int id     = itemStack.getTypeId();
        int dur    = itemStack.getDurability();
        
        return id + ":" + dur;
    }
    
    /**
     * Creates a new ItemStack from the information given in the given String
     * @param string    ItemStack information such as the id and the durability
     * @param amount    Amount for the new created ItemStack
     * @return A new ItemStack from the given String
     */
    public static ItemStack toItemStack (String string, int amount) {
        Validate.notNull(string);
        
        String     splitted[]     = string.split(":");
        int     id            = Integer.parseInt(splitted[0]);
        
        if (splitted.length > 1) {
            return new ItemStack(id, amount, Short.parseShort(splitted[1]));
        } else {
            return new ItemStack(id, amount);
        }
    }
}
