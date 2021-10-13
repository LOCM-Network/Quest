package me.labalityowo.config;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import me.labalityowo.Quest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
            ConfigSection section = (ConfigSection) data;
            ArrayList<ArrayList<Integer>> requirements = new ArrayList<ArrayList<Integer>>();

            section.getStringList("chitieu").forEach(requirementRaw -> {
                System.out.println(requirementRaw);
                requirements.add(Quest.getIntegerArray(new ArrayList<String>(Arrays.asList(requirementRaw.split(":")))));
            });


            HashMap<Item, ArrayList<Integer>> items = new HashMap<Item, ArrayList<Integer>>();
            section.getStringList("items").forEach(itemRaw -> {
                System.out.println(itemRaw);
                ArrayList<Integer> itemInfo = Quest.getIntegerArray(new ArrayList<String>(Arrays.asList(itemRaw.split(":"))));
                ArrayList<Integer> minMax = new ArrayList<Integer>();
                minMax.add(itemInfo.get(2));
                minMax.add(itemInfo.get(3));
                items.put(Item.get(itemInfo.get(0), itemInfo.get(1)), minMax);
            });

            ArrayList<String> commands = new ArrayList<String>();
            section.getStringList("commands").forEach(command -> {
                System.out.println(command);
                commands.add(command);
            });
            Quest.getInstance().putConfig(id, new QuestConfig(requirements, items, commands));
            System.out.println("ADded id " + id);
        });
    }
}
