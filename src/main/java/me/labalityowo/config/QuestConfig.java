package me.labalityowo.config;

import cn.nukkit.item.Item;

import java.util.ArrayList;

public class QuestConfig {

    private final String description;

    private String name;

    private int minProgress;
    private int maxProgress;

    private ArrayList<Item> itemsReward;

    public ArrayList<Item> getItemsReward() {
        return itemsReward;
    }

    public ArrayList<String> getCommandsReward() {
        return commandsReward;
    }

    private ArrayList<String> commandsReward;

    public QuestConfig(String name, String description, int minProgress, int maxProgress, ArrayList<Item> itemsReward, ArrayList<String> commandsReward){
        this.name = name;
        this.description = description;
        this.minProgress = minProgress;
        this.maxProgress = maxProgress;
        this.itemsReward = itemsReward;
        this.commandsReward = commandsReward;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public int getMinProgress() {
        return minProgress;
    }
}
