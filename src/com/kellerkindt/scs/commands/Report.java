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
package com.kellerkindt.scs.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.utilities.Term;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class Report extends SimpleCommand {
    
    public static final SimpleDateFormat    DATE_FORMATTER        = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    public static final String                DATE_SPLITTER        = ":";
    public static final String                DATE_SPLITTER_FILE    = "-";
    public static final String                SUFFIX                = "_report.txt";

    public Report(ShowCaseStandalone scs, String...permissions) {
        super(scs, permissions, false);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        // nothing to do
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        
        try {
            
            String    date         = DATE_FORMATTER.format(new Date());
            String    dateFile    = date.replace(DATE_SPLITTER, DATE_SPLITTER_FILE);    
            File    file         = new File(scs.getDataFolder(), dateFile+SUFFIX);
            
            FileOutputStream    fos    = new FileOutputStream(file);
            PrintStream            ps    = new PrintStream(fos);
            
            ps.println("---- SCS report, created at     " + date + " ----");
            ps.println(" - Current SCS version:          "+scs.getDescription().getVersion());
            ps.println(" - Current storage version:      "+Properties.VERSION_STORAGE_SHOP);
            ps.println(" - Current Bukkit version:       "+scs.getServer().getVersion());
            ps.println(" - Startup:                      "+DATE_FORMATTER.format(ShowCaseStandalone.getStartupTime()));
            ps.println(" - Total warnings since startup: "+ShowCaseStandalone.getTotalWarnings());
            
            for (Date d : ShowCaseStandalone.getWarnings().keySet())
                ps.println("   "+DATE_FORMATTER.format(d) + ": "+ShowCaseStandalone.getWarnings().get(d));
            
            ps.println(" - Plugin list:");
            
            for (Plugin p : scs.getServer().getPluginManager().getPlugins())
                ps.println("   "+p.getName() + " " + p.getDescription().getVersion());
            
            ps.println("---- SCS report, end ----");
            
            ps.flush();
            fos.flush();
            
            ps.close();
            fos.close();
            
            scs.sendMessage(sender, "Saved to: "+file.getAbsolutePath());
        } catch (Exception e) {
            scs.sendMessage(sender, Term.ERROR.get()+e.getMessage());
            e.printStackTrace();
        }
        
        
    }
}
