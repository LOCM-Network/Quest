package me.labalityowo.config.type;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import me.labalityowo.config.QuestConfig;

import java.util.ArrayList;

public class SellingQuest extends QuestConfig {

    public SellingQuest(String name, String description, int minProgress, int maxProgress, ArrayList<Item> itemsReward, ArrayList<String> commandsReward) {
        super(name, description, minProgress, maxProgress, itemsReward, commandsReward);
    }
}
