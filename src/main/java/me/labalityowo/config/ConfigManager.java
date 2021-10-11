package me.labalityowo.config;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import jdk.jfr.internal.tool.Main;
import me.labalityowo.Quest;
import me.labalityowo.config.type.BreakQuest;
import me.labalityowo.config.type.CraftQuest;
import me.labalityowo.config.type.PlaceQuest;
import me.labalityowo.config.type.SellingQuest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    public static void initialize(){
        File configFile = new File(Quest.getInstance().getDataFolder().getAbsolutePath() + File.separator + "quests.yml");
        try {
            configFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Config config = new Config(configFile);
        config.getAll().forEach((id, data) -> {
            ConfigSection configSection = (ConfigSection) data;
            ArrayList<String> commands = new ArrayList<String>();
            ArrayList<Item> items = new ArrayList<Item>();
            configSection.getList("commands").forEach(command -> {
                commands.add((String) command);
            });
            configSection.getList("items").forEach(itemArray -> {
                ArrayList<Object> itemInfo = (ArrayList<Object>) itemArray;
                Item item = Item.get((int)itemInfo.get(0), (int)itemInfo.get(1), (int)itemInfo.get(2));
                item.setCustomName(TextFormat.colorize((String) itemInfo.get(3)));
                item.setLore(TextFormat.colorize((String) itemInfo.get(4)));
                for (ArrayList<Integer> enchant : (ArrayList<ArrayList<Integer>>) itemInfo.get(5)) {
                    item.addEnchantment(Enchantment.get(enchant.get(0)).setLevel(enchant.get(1)));
                    System.out.println(Enchantment.get(enchant.get(0)).setLevel(enchant.get(1)));
                }
                items.add(item);
            });
            Quest.QuestType type;
            switch (configSection.getString("type")){
                case "default":
                    type = Quest.QuestType.DEFAULT;
                    break;
                case "normal":
                    type = Quest.QuestType.NORMAL;
                    break;
                case "vip":
                    type = Quest.QuestType.VIP;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + configSection.getString("type"));
            }
            QuestConfig quest;
            List<Integer> extradata = configSection.getIntegerList("extradata");
            switch(configSection.getString("questtype")){
                case "break":
                    quest = new BreakQuest(
                            configSection.getString("name"),
                            configSection.getString("description"),
                            configSection.getInt("minProgress"),
                            configSection.getInt("maxProgress"),
                            items,
                            commands,
                            Block.get(extradata.get(0)) //support meta
                    );
                break;
                case "place":
                    quest = new PlaceQuest(
                            configSection.getString("name"),
                            configSection.getString("description"),
                            configSection.getInt("minProgress"),
                            configSection.getInt("maxProgress"),
                            items,
                            commands,
                            Block.get(extradata.get(0)) //support meta
                    );
                    break;
                case "craft":
                    quest = new CraftQuest(
                            configSection.getString("name"),
                            configSection.getString("description"),
                            configSection.getInt("minProgress"),
                            configSection.getInt("maxProgress"),
                            items,
                            commands,
                            Item.get(extradata.get(0), extradata.get(1))
                    );
                    break;
                case "sell":
                    quest = new SellingQuest(
                            configSection.getString("name"),
                            configSection.getString("description"),
                            configSection.getInt("minProgress"),
                            configSection.getInt("maxProgress"),
                            items,
                            commands
                    );
                    break;
                default:
                    quest = new QuestConfig(
                            configSection.getString("name"),
                            configSection.getString("description"),
                            configSection.getInt("minProgress"),
                            configSection.getInt("maxProgress"),
                            items,
                            commands
                    );
                    break;
            }
            Quest.getInstance().registerQuestConfig(type, id, quest);
        });
    }
}
