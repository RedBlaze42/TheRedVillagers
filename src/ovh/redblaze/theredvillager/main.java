package ovh.redblaze.theredvillager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class main extends JavaPlugin implements Listener {
	
	public void onEnable() {
		saveDefaultConfig();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("villagers") && sender.isOp()) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("Vous devez être un joueur");
				return true;
			}
			if(args.length==0) {
				sender.sendMessage("Mauvaise utilisation");
				return true;
			}
			if(args[0].equalsIgnoreCase("display")) {
				command_display(sender, args);
				return true;
			}
				
		}
		
		return false;
	}
	
	boolean command_display(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		if(args.length==2 && args[1].equalsIgnoreCase("remove")) {
			List<Entity> entity_list = player.getWorld().getEntities();
			for (Entity entity : entity_list) {
				if(entity.getScoreboardTags().contains("display_villager") && entity.getLocation().distance(player.getLocation())<100) {
					entity.remove();
				}
			}
			player.sendMessage("Villageois de démo retirés");
			return true;
		}
		player.sendMessage("Création des villageois");
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(this.getDataFolder() + "/display_villagers.txt"));
			String line = reader.readLine();
			while (line != null) {
				Bukkit.dispatchCommand(sender, line);
				line = reader.readLine();
			}
			reader.close();
			player.sendMessage("Villageois créés");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

}
