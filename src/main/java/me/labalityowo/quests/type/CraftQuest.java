package me.labalityowo.quests.type;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import me.labalityowo.quests.QuestData;

public class CraftQuest extends QuestData {

    private Item item;

    public CraftQuest(int progress, int maxProgress, boolean rewardCollected, Item item) {
        super(progress, maxProgress, rewardCollected);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
