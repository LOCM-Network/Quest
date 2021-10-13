package me.labalityowo;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginBase;
import me.labalityowo.commands.QuestCommand;
import me.labalityowo.config.ConfigManager;
import me.labalityowo.config.QuestConfig;
import me.labalityowo.database.Database;
import me.labalityowo.quests.QuestData;
import me.labalityowo.quests.QuestSession;
import me.labalityowo.quests.type.BlockQuest;
import me.labalityowo.quests.type.BreakQuest;
import me.labalityowo.quests.type.CraftQuest;
import me.labalityowo.quests.type.PlaceQuest;
import me.labalityowo.scheduler.OnlineQuestTask;
import me.labalityowo.scheduler.QuestResetTask;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class Quest extends PluginBase{

    private static Quest instance;

    private HashMap<String, QuestConfig> configsMap = new HashMap<String, QuestConfig>();
    private HashMap<String, QuestSession> sessionsMap = new HashMap<String, QuestSession>();

    @Override
    public void onEnable() {
        instance = this;
        Database.initialize();
        ConfigManager.initialize();
        Database.execute("CREATE TABLE IF NOT EXISTS questData (uuid TEXT, questType TEXT, questExtraInformation TEXT, progress INTEGER, maxProgress INTEGER, rewardsClaimed INTEGER)");
        Database.execute("CREATE TABLE IF NOT EXISTS questClaimedLog(uuid TEXT, date TEXT)");
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getScheduler().scheduleRepeatingTask(new QuestResetTask(), 1728000);
        getServer().getScheduler().scheduleRepeatingTask(new OnlineQuestTask(), 1200);
        getServer().getCommandMap().register("quest", new QuestCommand());
    }


    public void putConfig(String id, QuestConfig config){
        configsMap.put(id, config);
    }

    public QuestSession getSession(UUID uuid){
        return sessionsMap.get(uuid.toString());
    }

    public QuestSession getSession(String uuid){
        return sessionsMap.get(uuid);
    }

    public QuestData rawQuestDataHandling(String type, ArrayList<Integer> extraInformation, int maxProgress, boolean rewardCollected){
        QuestData questData;
        switch(type){
            case "place1":
            case "place":
                Block blockBreak = Block.get(extraInformation.get(0));
                blockBreak.setDamage(extraInformation.get(1));
                questData = new PlaceQuest(0, maxProgress, rewardCollected, blockBreak);
                break;
            case "break1":
            case "break":
                Block blockPlace = Block.get(extraInformation.get(0));
                blockPlace.setDamage(extraInformation.get(1));
                questData = new BreakQuest(0, maxProgress, rewardCollected, blockPlace);
                break;
            case "craft1":
            case "craft":
                Item item = Item.get(extraInformation.get(0), extraInformation.get(1));
                questData = new CraftQuest(0, maxProgress, rewardCollected, item);
                break;
            default:
                questData = new QuestData(0, maxProgress, rewardCollected);
                break;
        }
        return questData;

    }

    public QuestConfig getQuestConfig(String id){
        return configsMap.get(id);
    }

    //refresh
    public void addOrUpdateSession(Player player, Date date){
        String uuid = player.getUniqueId().toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Database.execute("DELETE FROM questData WHERE uuid='" + uuid + "'");
        Database.execute("INSERT INTO questClaimedLog(uuid, date) VALUES('" + uuid + "', '" + format.format(date) +"')");
        HashMap<String, QuestData> dataMap = new HashMap<String, QuestData>();
        configsMap.forEach((id, questConfig) -> {
            QuestData questData;
            Integer selectedMaxProgress = 0;
            ArrayList<ArrayList<Integer>> requirements = questConfig.getRequirements();
            ArrayList<Integer> extraInformation = new ArrayList<Integer>();
            if(!requirements.isEmpty()){
                ArrayList<Integer> selectedRequirement = requirements.get((int)(Math.random() * requirements.size()));
                extraInformation.add(selectedRequirement.get(0));
                extraInformation.add(selectedRequirement.get(1));
                selectedRequirement.remove(0);
                selectedRequirement.remove(1);
                selectedMaxProgress = selectedRequirement.get((int)(Math.random() * selectedRequirement.size()));
                System.out.println("Randomized | Requirements: " + requirements + " | maxProgress: " + selectedMaxProgress);
            }
            if(id.equals("online")){
                selectedMaxProgress = 30;
            }else if(id.equals("diemdanh")){
                selectedMaxProgress = 1;
            }

            dataMap.put(id, rawQuestDataHandling(id, extraInformation, selectedMaxProgress, false));
        });
        sessionsMap.put(player.getUniqueId().toString(), new QuestSession(player, dataMap));
    }

    public void registerSession(Player player, HashMap<String, QuestData> dataMap){
        sessionsMap.put(player.getUniqueId().toString(), new QuestSession(player, dataMap));
    }

    public static Quest getInstance() {
        return instance;
    }

    public static ArrayList<Integer> getIntegerArray(ArrayList<String> stringArray) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(String stringValue : stringArray) {
            try {
                result.add(Integer.parseInt(stringValue));
            } catch(NumberFormatException nfe) {

            }
        }
        return result;
    }

    @Override
    public void onDisable() {
        sessionsMap.forEach((uuid, session) -> {
            Database.execute("DELETE FROM questData WHERE uuid='" + uuid + "'");
            session.getQuestMap().forEach((id, questData) -> {
                String questInformation = "";
                if(questData instanceof BlockQuest){
                    questInformation = ((BlockQuest) questData).getBlock().getId() + ":" + ((BlockQuest) questData).getBlock().getDamage();
                }else if(questData instanceof CraftQuest){
                    questInformation = ((CraftQuest) questData).getItem().getId() + ":" + ((CraftQuest) questData).getItem().getDamage();
                }
                Database.execute("INSERT INTO questData(uuid, questType, questExtraInformation, progress, maxProgress, rewardsClaimed) VALUES ('" + uuid + "', '" + id + "', '" + questInformation +"', '" + questData.getProgress() + "', '" + questData.getMaxProgress() + "', '" + (questData.isRewardCollected() ? "1" : "0") + "')");
            });
        });
    }
}
