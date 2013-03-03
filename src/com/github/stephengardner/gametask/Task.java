package com.github.stephengardner.gametask;

public class Task {

	private int taskID;
	private String world;
	private String command;
	private long executeTime;

	public Task(String world, String command, long executeTime) {
		this.world = world;
		this.command = command;
		this.executeTime = executeTime;
	}

	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public long getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(long executeTime) {
		this.executeTime = executeTime;
	}

}
