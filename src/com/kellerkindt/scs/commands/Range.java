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
package com.kellerkindt.scs.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.exceptions.MissingOrIncorrectArgumentException;
import com.kellerkindt.scs.interfaces.PriceRangeHandler;
import com.kellerkindt.scs.utilities.Term;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class Range extends SimpleCommand {

	public Range(ShowCaseStandalone scs, String...permissions) {
		super(scs, permissions, false, 2);
	}

	@Override
	public List<String> getTabCompletions(CommandSender sender, String[] args) {
		
		List<String> list = new ArrayList<String>();
		
		if (args.length <= 1) {
			for (Material material : Material.values()) {
				if (args.length == 0 || material.name().toLowerCase().startsWith(args[0].toLowerCase())) {
					list.add(material.name());
				}
			}
		}
		
		// nothing to do
		return list;
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws CommandException {
		
		// try to get the material?
		Material material = Material.getMaterial(args[0].toUpperCase());
		
		// failed? because global is requested?
		if (material == null && !"global".equalsIgnoreCase(args[0]) && !"remove".equalsIgnoreCase(args[0])) {
			throw new MissingOrIncorrectArgumentException();
		}
		
		PriceRangeHandler 	handler = scs.getPriceRangeHandler();
		boolean				global	= material == null && "global".equalsIgnoreCase(args[1]);
		boolean				remove	= material == null && "remove".equalsIgnoreCase(args[1]);
		double				min		= 0;
		double				max		= Double.MAX_VALUE;
		

		String message = null;
		
		if (!remove) {
			try {
				// the first one is the min value
				if (args.length > 1) {
					min	= Double.parseDouble(args[1]);
				}
				
				// the (optional) second one is the max value
				if (args.length > 2) {
					max = Double.parseDouble(args[2]);
				}
				
			} catch (Throwable t) {
				throw new MissingOrIncorrectArgumentException();
			}
			
			
			
			
			// update
			if (args.length > 1) {
				if (global) {
					handler.setGlobalMin(min);
					
					if (args.length > 2) {
						handler.setGlobalMax(max);
					} else {
						max = handler.getGlobalMax();
					}
					
					if (max == Double.MAX_VALUE) {
						max = Double.POSITIVE_INFINITY;
					}
					message = Term.MESSAGE_PRICERANGE_UPDATED_GLOBAL.get(Double.toString(min), Double.toString(max));
					
				} else {
					handler.setMin(material, min);
					
					if (args.length > 2) {
						handler.setMax(material, max);
					} else {
						max = handler.getMax(material);
					}
					
					if (max == Double.MAX_VALUE) {
						max = Double.POSITIVE_INFINITY;
					}
					
					message = Term.MESSAGE_PRICERANGE_UPDATED.get(material.toString(), Double.toString(min), Double.toString(max));
				}
				
				
			} else {
				min = handler.getMin(material);
				max = handler.getMax(material);
				
				if (max == Double.MAX_VALUE) {
					max = Double.POSITIVE_INFINITY;
				}
				
				if (global) {
					message = Term.MESSAGE_PRICERANGE_GLOBAL.get(Double.toString(min), Double.toString(max));
				} else {
					message = Term.MESSAGE_PRICERANGE.get(material.toString(), Double.toString(min), Double.toString(max));
				}
				
			}
			
			
			
			
			
		} else {
			// remove
			if (args.length < 2) {
				throw new MissingOrIncorrectArgumentException();
			}
			
			material = Material.getMaterial(args[1].toUpperCase());
			
			if (material == null) {
				throw new MissingOrIncorrectArgumentException();
			}
			
			handler.remove(material);
			message = Term.MESSAGE_PRICERANGE_REMOVED.get(material.toString());
		}
		
		
		// aaaaaand finally, send it to the sender
		// --- ever told you how I fucking hate writing commands?
		scs.sendMessage(sender, message);
		
	}
	
	
}
