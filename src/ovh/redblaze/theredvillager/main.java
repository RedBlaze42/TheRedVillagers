package ovh.redblaze.theredvillager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class main extends JavaPlugin implements Listener {
	
	List<Chunk> not_to_unload_chunks = new ArrayList<>();
	
	public void onEnable() {
		saveDefaultConfig();
		
		ArrayList<String> array_list = new ArrayList<>(Arrays.asList("display_villagers.txt"));
		for (String file_name : array_list) {
			if (!new File(getDataFolder(), file_name).exists()) {
				  saveResource(file_name, false);
			}			
		}
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
			}else if(args[0].equalsIgnoreCase("update")){
				command_update(sender);
				return true;
			}
			
			/*
			World world = getServer().getWorlds().get(0);
			Chunk chun = new Location(world,0,0,0).getChunk();
			chun.load();
			//command
			chun.unload();
			*/
			
		}
		
		return false;
	}
	
	boolean command_update(CommandSender sender) {
		Player player = (Player) sender;
		player.sendMessage("Début de l'update");
		try {
			Runtime.getRuntime().exec("python " + this.getDataFolder() + "/update.py");
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedReader command_file;
		BufferedReader coord_file;
		List<String> commands = new ArrayList<String>();
		List<String> coords = new ArrayList<String>();
		try {
			coord_file = new BufferedReader(new FileReader( this.getDataFolder() + "/update_coordinates.txt" ));
			command_file = new BufferedReader(new FileReader( this.getDataFolder() + "/update_commands.txt" ));
			String line;
			while((line = command_file.readLine()) != null) {
				commands.add(line);
			}
			while((line = coord_file.readLine()) != null) {
				coords.add(line);
			}
			coord_file.close();
			command_file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		World world = player.getWorld();
		List<Entity> entity_list = world.getEntities();
		for (Entity entity : entity_list) {
			if(entity.getScoreboardTags().contains("trade_villager")) {
				entity.remove();
			}
		}
		Location old_loc = player.getLocation();
		player.sendMessage("Villageois retirés");
		for (int i = 0; i < commands.size(); i++) {
//			String[] villager_coords = coords.get(i).split("|");
//			Location villager_loc = new Location(world, Integer.parseInt(villager_coords[0]), Integer.parseInt(villager_coords[1]), Integer.parseInt(villager_coords[2]));
//			Chunk summon_chunk = villager_loc.getChunk();

//			player.sendMessage(String.valueOf(summon_chunk.isLoaded()));
			//player.teleport(villager_loc);
//			while(!summon_chunk.isLoaded()) {
//			};
			Bukkit.dispatchCommand(sender, commands.get(i));
		}
		//player.teleport(old_loc);
		return true;
		
		
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
	
	void remove_custom_villagers(World world) {
		List<Entity> entity_list = world.getEntities();
		for (Entity entity : entity_list) {
			if(entity.getScoreboardTags().contains("trade_villager")) {
				entity.remove();
			}
		}
	}

}
