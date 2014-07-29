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
package com.kellerkindt.scs.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.commands.Abort;
import com.kellerkindt.scs.commands.About;
import com.kellerkindt.scs.commands.Add;
import com.kellerkindt.scs.commands.Amount;
import com.kellerkindt.scs.commands.Buy;
import com.kellerkindt.scs.commands.Clear;
import com.kellerkindt.scs.commands.Destroy;
import com.kellerkindt.scs.commands.Disable;
import com.kellerkindt.scs.commands.Display;
import com.kellerkindt.scs.commands.Exchange;
import com.kellerkindt.scs.commands.Get;
import com.kellerkindt.scs.commands.Help;
import com.kellerkindt.scs.commands.Last;
import com.kellerkindt.scs.commands.Member;
import com.kellerkindt.scs.commands.Message;
import com.kellerkindt.scs.commands.Owner;
import com.kellerkindt.scs.commands.Price;
import com.kellerkindt.scs.commands.Prune;
import com.kellerkindt.scs.commands.Purge;
import com.kellerkindt.scs.commands.Range;
import com.kellerkindt.scs.commands.Reload;
import com.kellerkindt.scs.commands.Remove;
import com.kellerkindt.scs.commands.Repair;
import com.kellerkindt.scs.commands.Report;
import com.kellerkindt.scs.commands.Sell;
import com.kellerkindt.scs.commands.Undo;
import com.kellerkindt.scs.commands.Unit;
import com.kellerkindt.scs.commands.Version;
import com.kellerkindt.scs.exceptions.MissingOrIncorrectArgumentException;
import com.kellerkindt.scs.utilities.Term;

public class CommandExecutorListener implements CommandExecutor, TabCompleter {
	
	private ShowCaseStandalone 	scs;
	
	private List<com.kellerkindt.scs.commands.Command> commands = new ArrayList<com.kellerkindt.scs.commands.Command>();
	
	public CommandExecutorListener (ShowCaseStandalone scs) {
		this.scs = scs;
		
		// TODO
		commands.add(new Abort								(scs, Properties.PERMISSION_USE));
		commands.add(new About								(scs, Properties.PERMISSION_USE));
		commands.add(new Add								(scs, Properties.PERMISSION_USE));
		commands.add(new Amount								(scs, Properties.PERMISSION_MANAGE));
		commands.add(new Buy								(scs, Properties.PERMISSION_CREATE_BUY));
		commands.add(new Clear								(scs, Properties.PERMISSION_ADMIN));
		commands.add(new Destroy							(scs, Properties.PERMISSION_ADMIN));
		commands.add(new Disable							(scs, Properties.PERMISSION_ADMIN));
		commands.add(new Display							(scs, Properties.PERMISSION_CREATE_DISPLAY));
		commands.add(new Exchange							(scs, Properties.PERMISSION_CREATE_EXCHANGE));
		commands.add(new Get								(scs, Properties.PERMISSION_MANAGE));
		commands.add(new Help								(scs, Properties.PERMISSION_USE, Properties.PERMISSION_ADMIN));
		commands.add(new Last								(scs, Properties.PERMISSION_USE));
		commands.add(new com.kellerkindt.scs.commands.List	(scs, Properties.PERMISSION_ADMIN));
		commands.add(new Member								(scs, Properties.PERMISSION_MANAGE));
		commands.add(new Message							(scs, Properties.PERMISSION_USE));
		commands.add(new Owner								(scs, Properties.PERMISSION_ADMIN));
		commands.add(new Price								(scs, Properties.PERMISSION_MANAGE));
		commands.add(new Prune								(scs, Properties.PERMISSION_ADMIN));
		commands.add(new Purge								(scs, Properties.PERMISSION_ADMIN));
		commands.add(new Range								(scs, Properties.PERMISSION_ADMIN));
		commands.add(new Reload								(scs, Properties.PERMISSION_ADMIN));
		commands.add(new Remove								(scs, Properties.PERMISSION_REMOVE));
		commands.add(new Repair								(scs, Properties.PERMISSION_REPAIR));
		commands.add(new Report								(scs, Properties.PERMISSION_ADMIN));
		commands.add(new Sell								(scs, Properties.PERMISSION_CREATE_SELL));
		commands.add(new Undo								(scs, Properties.PERMISSION_USE));
		commands.add(new Unit								(scs, Properties.PERMISSION_USE));
		commands.add(new Version							(scs, Properties.PERMISSION_USE));
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String lable, String[] args) {
		
		List<String> list = null;
		
		// hasn't found a sub-command yet
		if (args.length < 1) {
			list = new ArrayList<String>();
			
			for (com.kellerkindt.scs.commands.Command cmd : commands) {
				list.add(cmd.getName());
			}
			
			
		} else if (args.length == 1) {
			list = new ArrayList<String>();
			
			for (com.kellerkindt.scs.commands.Command cmd : commands) {
				if (cmd.getName().startsWith(args[0]) && cmd.hasPermissions(sender)) {
					list.add(cmd.getName());
				}
			}
			
			
		} else if (args.length > 1) {
			com.kellerkindt.scs.commands.Command cmd = null;
			
			// check whether there is a suitable command
			for (com.kellerkindt.scs.commands.Command c : commands) {
				if (c.getName().equals(args[0]) && c.hasPermissions(sender)) {
					cmd = c;
					break;
				}
			}
			
			// get the commands tab complete if found
			if (cmd != null) {
				list = cmd.getTabCompletions(sender, Arrays.copyOfRange(args, 1, args.length));
			}
		}
		
		return list;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		
		
		try {
			com.kellerkindt.scs.commands.Command cmd = null;
			
			// try to find the command
			for (com.kellerkindt.scs.commands.Command c : commands) {
				if (c.getName().equals(args[0])) {
					cmd = c;
					break;
				}
			}
			
			if (cmd == null) {
				// let the sender know
				scs.sendMessage(sender, Term.ERROR_COMMAND_UNKNOWN.get());
				return true;
			}
			
			if (!cmd.hasPermissions(sender)) {
				// let the sender know
				scs.sendMessage(sender, Term.ERROR_INSUFFICIENT_PERMISSION.get());
				return true;
			}
			
			if (cmd.hasToBeAPlayer() && !(sender instanceof Player)) {
				// let the sender know
				scs.sendMessage(sender, Term.ERROR_EXECUTE_AS_PLAYER.get());
				return true;
			}
			
			if (cmd.getMinArgumentCount() > (args.length-1)) {
				// let the sender know
				scs.sendMessage(sender, Term.ERROR_MISSING_OR_INCORRECT_ARGUMENT.get());
				return true;
			}
			
			
			// execute it
			cmd.execute(sender, Arrays.copyOfRange(args, 1, args.length));
			
		} catch (Throwable t) {
			
			// only print the exception if somehow expected
			if (!(t instanceof MissingOrIncorrectArgumentException)) {
				t.printStackTrace();
				scs.sendMessage(sender, Term.ERROR_MISSING_OR_INCORRECT_ARGUMENT.get());
			}
			
			else {
				scs.log(Level.WARNING, "Incorrect command call by "+sender.getName(), false);
				scs.sendMessage(sender, t.getMessage());
			}
			
		}
		
		return true;
	}
	
	
//	@Override
//    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//		
//            String mainArg;
//            
//            if(args.length < 1)
//                mainArg = "help";
//            else
//                mainArg = args[0];
//            
//            try {
//                
//                //General commands
//                if (mainArg.equalsIgnoreCase("abort"))
//                    this.cmd = new AbortCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("help"))
//                    this.cmd = new HelpCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("about"))
//                	this.cmd = new AboutCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("version"))
//                	this.cmd = new VersionCmd(scs, sender, args);
//                
//                //Customer commands
//                else if (mainArg.equalsIgnoreCase("last"))
//                    this.cmd = new LastCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("undo"))
//                    this.cmd = new UndoCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("unit"))
//                    this.cmd = new UnitCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("message") || mainArg.equalsIgnoreCase("messages"))
//                    this.cmd = new MessageCmd(scs, sender, args);
//                
//                //Creation/deletion commands
//                else if (mainArg.equalsIgnoreCase("buy"))
//                    this.cmd = new BuyCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("display"))
//                    this.cmd = new DisplayCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("remove"))
//                    this.cmd = new RemoveCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("sell"))
//                    this.cmd = new SellCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("exchange"))
//                	this.cmd = new ExchangeCmd(scs, sender, args);
//                
//                //Management commands
//                else if (mainArg.equalsIgnoreCase("add"))
//                    this.cmd = new AddCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("get"))
//                    this.cmd = new GetCmd(scs, sender, args);
//                else if(mainArg.equalsIgnoreCase("owner"))
//                    this.cmd = new OwnerCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("price"))
//                    this.cmd = new PriceCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("amount")) //Was "limit"
//                    this.cmd = new AmountCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("member"))
//                	this.cmd = new MemberCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("list"))
//                	this.cmd = new ListCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("purge"))
//                	this.cmd = new PurgeCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("range"))
//                	this.cmd = new RangeCmd(scs, sender, args);
//                
//                //Admin commands
//                else if (mainArg.equalsIgnoreCase("destroy"))
//                	this.cmd = new DestroyCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("debug"))
//                    this.cmd = new DebugCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("clear"))
//                    this.cmd = new ClearCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("reload"))
//                    this.cmd = new ReloadCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("disable"))
//                    this.cmd = new DisableCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("enable")) //Alias for reload
//                    this.cmd = new ReloadCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("prune") || mainArg.equalsIgnoreCase("cleanup"))
//                    this.cmd = new PruneCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("report"))
//                	this.cmd = new ReportCmd(scs, sender, args);
//                else if (mainArg.equalsIgnoreCase("repair"))
//                	this.cmd = new RepairCmd(scs, sender, args);
//                
//                
//                //Unknown
//                else
//                    throw new MissingOrIncorrectArgumentException(Term.ERROR_COMMAND_UNKNOWN.get());
//                
//                return cmd.execute();
//                
//            } catch (MissingOrIncorrectArgumentException miae) {
//                Messaging.send(sender, "`r" + miae.getMessage());
//                return true;
//            } catch (InsufficientPermissionException nperm) {
//                Messaging.send(sender, "`r" + nperm.getMessage());
//                return true;
//            }
//        }
}