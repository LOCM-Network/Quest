package me.labalityowo.config.type;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import me.labalityowo.config.QuestConfig;

import java.util.ArrayList;

public class BreakQuest extends QuestConfig {

    private Block block;

    public BreakQuest(String name, String description, int minProgress, int maxProgress, ArrayList<Item> itemsReward, ArrayList<String> commandsReward, Block block) {
        super(name, description, minProgress, maxProgress, itemsReward, commandsReward);
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
