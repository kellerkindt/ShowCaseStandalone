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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;
import org.mcstats.Metrics.Plotter;

import com.kellerkindt.scs.events.ShowCaseShopHandlerChangedEvent;
import com.kellerkindt.scs.interfaces.ShopHandler;
import com.kellerkindt.scs.shops.Shop;

public class MetricsHandler implements Listener {
    
    private static final String GRAPH_NAME_SHOP_MATERIAL            = "shop_material";
    private static final String GRAPH_NAME_SHOP_TYPE                = "shop_type";
    private static final String GRAPH_NAME_SHOP_KIND                = "shop_kind";
    
    private static final String PLOTTER_NAME_SHOP_UNLIMITED            = "shop_unlimited";
    private static final String PLOTTER_NAME_SHOP_LIMITED            = "shop_limited";
    
    private Map<Class<? extends Shop>, MyPlotter>    kinds         = new HashMap<Class<? extends Shop>, MyPlotter>();
    private Set<MyPlotter>                            plotters    = new HashSet<MyPlotter>();
    
    private Graph graphKind     = null;
    private Graph graphMaterial    = null;
    private Graph graphType        = null;
    
    public MetricsHandler (ShopHandler handler, Metrics metrics) {
        
        // init the plotters
        this.initPlotters(metrics, handler);
    }
    

    @EventHandler
    public void onShopHandlerChangedValues(ShowCaseShopHandlerChangedEvent event) {
        // update plotters
        updatePlotters(event.getShopHandler());
    }

    /**
     * Forces the plotters to update
     * @param handler    ShopHandler which is used to gather the data
     */
    private void updatePlotters (ShopHandler handler) {
        
        // force update
        for (Shop shop : handler) {
            
            // add a new plotter?
            if (kinds.get(shop.getClass()) == null) {
                addKindPlotter(shop.getClass());
            }
            
            for (MyPlotter plotter : plotters) {                
                plotter.update(shop);
            }
        }
    }
    
    /**
     * Adds a plotter for the specific Shop and its class
     * @param shop Shop to ad the plotter for
     */
    public void addKindPlotter (final Class<? extends Shop> clazz) {
        
        // Is there already a plotter for the given class?
        if (kinds.get(clazz) != null) {
            return;
        }
        
        MyPlotter plotter = new MyPlotter(clazz.getSimpleName()) {
            
            private int count = 0; 
            
            @Override
            public void update(Shop p) {
                if (p.getClass().equals(clazz)) {
                    count++;
                }
            }
            
            @Override
            public void reset() {
                count = 0;
            }
            
            @Override
            public int getValue() {
                return count;
            }
        };
        
        graphKind    .addPlotter(plotter);
        plotters    .add(plotter);
        kinds        .put(clazz, plotter);
    }
    
    /**
     * Creates all the needed plotters
     * @param metrics    Metrics to add the Plotters to
     * @param handler    ShopHandler which is used to gather the data
     */
    private void initPlotters (Metrics metrics, ShopHandler handler) {
//        Graph        graph    = null;
        MyPlotter    plotter    = null;
        
        
        // create Plotters for the the activities - dynamically filled
        graphKind    = metrics.createGraph(GRAPH_NAME_SHOP_KIND);
        
        // create Plotters for used Material
        graphMaterial    = metrics.createGraph(GRAPH_NAME_SHOP_MATERIAL);
        for (final Material material : Material.values()) {
            plotter = new MyPlotter(material.toString()) {
                
                private int count = 0;

                @Override
                public void update(Shop p) {
                    // count
                    if (p.getItemStack().getType() == material)
                        count++;
                }
                
                @Override
                public int getValue() {
                    return count;
                }

                @Override
                public void reset() {
                    count = 0;
                }
            };
            
            // add plotter
            plotters.add(plotter);
            graphMaterial.addPlotter(plotter);
        }
        
        // create Plotter for unlimited shops
        graphType    = metrics.createGraph(GRAPH_NAME_SHOP_TYPE);
        plotter = new MyPlotter(PLOTTER_NAME_SHOP_UNLIMITED) {
            
            private int count = 0;
            
            @Override
            public void update(Shop p) {
                if (p.isUnlimited())
                    count ++;
            }
            
            @Override
            public void reset() {
                count = 0;
            }
            
            @Override
            public int getValue() {
                return count;
            }
        };
        // add plotter
        plotters.add(plotter);
        graphType.addPlotter(plotter);
        
        
        
        // create Plotter for limited shops
        plotter = new MyPlotter(PLOTTER_NAME_SHOP_LIMITED) {
            
            private int count = 0;
            
            @Override
            public void update(Shop p) {
                if (!p.isUnlimited())
                    count++;
            }
            
            @Override
            public void reset() {
                count = 0;
            }
            
            @Override
            public int getValue() {
                return count;
            }
        };
        
        // add plotter
        plotters.add(plotter);
        graphType.addPlotter(plotter);
        
        
        // fill data
        updatePlotters(handler);
    }

    
    
    /**
     * Modified class for the Plotters
     * @author kellerkindt
     */
    private abstract class MyPlotter extends Plotter {
        
        public MyPlotter (String name) {
            super(name);
        }
        
        
        @Override
        public abstract void reset ();
        
        
        /**
         * Loads the data from the given shop
         * @param p    Shop to load data from
         */
        public abstract void update(Shop p);

        @Override
        public abstract int getValue();
        
    }
}
