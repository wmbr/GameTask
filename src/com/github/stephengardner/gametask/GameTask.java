package com.github.stephengardner.gametask;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class GameTask extends JavaPlugin {

	private ArrayList<Task> tasks;
	private HashMap<World, Long> ticks;

	@Override
	public void onEnable() {
		saveDefaultConfig();

		ticks = new HashMap<World, Long>();

		getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {

			@Override
			public void run() {
				tickMonitor();
			}

		}, 0L, 100L);

		tasks = new ArrayList<Task>();
		loadTasks();
	}

	@Override
	public void onDisable() {
		tasks.clear();
		getServer().getScheduler().cancelTasks(this);
	}

	private void tickMonitor() {
		for (World world : getServer().getWorlds()) {
			if (!ticks.containsKey(world)) {
				ticks.put(world, world.getTime());
				continue;
			}

			long oldTime = ticks.get(world);
			long currentTime = world.getTime();

			Boolean changed = false;

			if (currentTime - oldTime > 120L) {
				changed = true;
			} else if (currentTime < oldTime) {
				if (currentTime > 120L || oldTime < 15880L) {
					changed = true;
				}
			}

			if (changed) {
				ArrayList<Task> tasks = new ArrayList<Task>();
				tasks.addAll(this.tasks);

				for (Task task : tasks) {
					if (task.getWorld().equals(world.getName())) {
						getServer().getScheduler().cancelTask(task.getTaskID());
						this.tasks.remove(task);
						scheduleTask(task);
					}
				}

			}

			ticks.put(world, world.getTime());
		}
	}

	private void loadTasks() {
		ConfigurationSection worlds = getConfig().getConfigurationSection("GameTask");

		for (String world : worlds.getKeys(false)) {
			ConfigurationSection tasks = getConfig().getConfigurationSection("GameTask." + world);

			for (String time : tasks.getKeys(false)) {
				scheduleTask(new Task(world, getConfig().getString("GameTask." + world + "." + time), Long.parseLong(time)));
			}
		}
	}

	private void scheduleTask(Task task) {
		final String command = task.getCommand();
		long worldTime = getServer().getWorld(task.getWorld()).getTime();
		long executeTime = task.getExecuteTime();

		if (executeTime <= worldTime) {
			executeTime = 24000L - worldTime + executeTime;
		} else {
			executeTime = executeTime - worldTime;
		}

		BukkitTask id = getServer().getScheduler().runTaskTimer(this, new Runnable() {

			@Override
			public void run() {
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
			}

		}, executeTime, 24000L);

		task.setTaskID(id.getTaskId());
		tasks.add(task);
	}

}
