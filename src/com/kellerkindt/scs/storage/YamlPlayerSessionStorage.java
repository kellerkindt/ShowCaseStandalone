/**
* ShowCaseStandalone
* Copyright (C) 2014 Kellerkindt <copyright at kellerkindt.com>
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
package com.kellerkindt.scs.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.kellerkindt.scs.PlayerSession;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.PlayerSessionHandler;
import com.kellerkindt.scs.interfaces.StorageHandler;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class YamlPlayerSessionStorage implements StorageHandler<PlayerSessionHandler> {

    public static final String PATH_LIST    = "PlayerSessionVariables";
    
    private ShowCaseStandalone  scs;
    private File                file;
    
    public YamlPlayerSessionStorage (ShowCaseStandalone scs, File file) {
        this.scs    = scs;
        this.file   = file;
    }

    @Override
    public void load(PlayerSessionHandler handler) throws IOException {

        // nothing to load if the file does not exist
        if (!file.exists()) {
            return;
        }
        
        // load the file
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
        
        
        List<?> list = conf.getList(PATH_LIST);
        
        for (Object o : list) {
            // well, just to be really sure :)
            if (o instanceof PlayerSession) {
                handler.addSession((PlayerSession)o);
                
            } else {
                scs.getLogger().severe("Unknown value in the PlayerSession file: "+o);
            }
        }
    }

    @Override
    public void save(PlayerSessionHandler handler) throws IOException {
        YamlConfiguration     conf     = new YamlConfiguration();
        List<PlayerSession>    list    = new ArrayList<PlayerSession>();
        
        // add it to the list
        for (PlayerSession session : handler) {
            list.add(session);
        }
        
        // add it to the YAML file
        conf.set(PATH_LIST, list);
        
        // save it to disk
        conf.save(file);
    }

    @Override
    public void flush() throws IOException {
        // nothing to do
    }
}
