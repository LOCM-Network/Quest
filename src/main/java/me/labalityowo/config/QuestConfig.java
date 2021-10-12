package me.labalityowo.config;

import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestConfig {

    private ArrayList<String> commands;
    private HashMap<Item, ArrayList<Integer>> items;
    private ArrayList<ArrayList<Integer>> requirements;

    public QuestConfig(ArrayList<ArrayList<Integer>> requirements, HashMap<Item, ArrayList<Integer>> items, ArrayList<String> commands){
        this.requirements = requirements;
        this.items = items;
        this.commands = commands;
    }

    public ArrayList<String> getCommands() {
        return commands;
    }

    public HashMap<Item, ArrayList<Integer>> getItems() {
        return items;
    }

    public ArrayList<ArrayList<Integer>> getRequirements() {
        return requirements;
    }
}
