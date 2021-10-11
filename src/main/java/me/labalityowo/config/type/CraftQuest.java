package me.labalityowo.config.type;

import cn.nukkit.item.Item;
import me.labalityowo.config.QuestConfig;

import java.util.ArrayList;

public class CraftQuest extends QuestConfig {

    private Item item;

    public CraftQuest(String name, String description, int minProgress, int maxProgress, ArrayList<Item> itemsReward, ArrayList<String> commandsReward, Item item) {
        super(name, description, minProgress, maxProgress, itemsReward, commandsReward);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
