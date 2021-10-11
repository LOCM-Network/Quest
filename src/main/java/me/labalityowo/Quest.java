package me.labalityowo;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import me.labalityowo.commands.QuestCommand;
import me.labalityowo.config.ConfigManager;
import me.labalityowo.config.QuestConfig;
import me.labalityowo.database.Database;
import me.labalityowo.quests.QuestData;
import me.labalityowo.quests.QuestSession;
import me.labalityowo.scheduler.OnlineQuestTask;
import me.labalityowo.scheduler.QuestResetTask;

import java.sql.SQLException;
import java.util.*;

public class Quest extends PluginBase {

    private static Quest instance;

    public enum QuestType{
        DEFAULT,
        NORMAL,
        VIP
    }

    //Quest ID = Quest Config id

    private HashMap<QuestType, HashMap<String, QuestConfig>> quests = new HashMap<QuestType, HashMap<String, QuestConfig>>();
    private HashMap<String, QuestSession> sessions = new HashMap<String, QuestSession>();

    public void onEnable() {
        instance = this;
        quests.put(QuestType.DEFAULT, new HashMap<String, QuestConfig>());
        quests.put(QuestType.NORMAL, new HashMap<String, QuestConfig>());
        quests.put(QuestType.VIP, new HashMap<String, QuestConfig>());
        Database.initialize();
        ConfigManager.initialize();

        Database.execute("CREATE TABLE IF NOT EXISTS questData (uuid TEXT, questId TEXT, progress INTEGER, maxProgress INTEGER, rewardClaimed INTEGER)");
        Database.execute("CREATE TABLE IF NOT EXISTS questClaimedLog(uuid TEXT, date TEXT)");

        getServer().getPluginManager().registerEvents(new EventListener(), this);

        getServer().getScheduler().scheduleRepeatingTask(new QuestResetTask(), 1728000);
        getServer().getScheduler().scheduleRepeatingTask(new OnlineQuestTask(), 1200);
        getServer().getCommandMap().register("quest", new QuestCommand());
    }

    public void registerQuestSession(Player player, HashMap<String, QuestData> questData, Date claimed){
        sessions.put(player.getUniqueId().toString(), new QuestSession(player, questData, claimed));
        System.out.println("added");
    }

    public void registerQuestSession(Player player, Date claimed){
        HashMap<String, QuestData> questDatas = new HashMap<String, QuestData>();
        getQuestsByType(QuestType.DEFAULT).forEach((id, questInfo) -> {
            if(questInfo.getMinProgress() == questInfo.getMaxProgress()){
                questDatas.put(id, new QuestData(0, questInfo.getMaxProgress()));
            }else{
                questDatas.put(id, new QuestData(0, Quest.generateRandom(questInfo.getMinProgress(), questInfo.getMaxProgress())));
            }
        });

        /*
        ArrayList<String> normalKeys = new ArrayList<String>(getQuestsByType(QuestType.NORMAL).keySet());
        for(int i = 0; i < 10; i++){
            String id = normalKeys.get(generateRandom(0, normalKeys.size() - 1));
            QuestConfig questInfo = getQuestsByType(QuestType.NORMAL).get(id);
            if(questInfo.getMinProgress() == questInfo.getMaxProgress()){
                questDatas.put(id, new QuestData(0, questInfo.getMaxProgress()));
            }else{
                questDatas.put(id, new QuestData(0, Quest.generateRandom(questInfo.getMinProgress(), questInfo.getMaxProgress())));
            }
        }

        ArrayList<String> vipKeys = new ArrayList<String>(getQuestsByType(QuestType.VIP).keySet());
        for(int i = 0; i < 2; i++){
            String id = normalKeys.get(generateRandom(0, normalKeys.size() - 1));
            QuestConfig questInfo = getQuestsByType(QuestType.VIP).get(id);
            if(questInfo.getMinProgress() == questInfo.getMaxProgress()){
                questDatas.put(id, new QuestData(0, questInfo.getMaxProgress()));
            }else{
                questDatas.put(id, new QuestData(0, Quest.generateRandom(questInfo.getMinProgress(), questInfo.getMaxProgress())));
            }
        }

         */

        sessions.put(player.getUniqueId().toString(), new QuestSession(player, questDatas, claimed));
        System.out.println("added");
    }

    public void registerQuestConfig(QuestType type, String id, QuestConfig config){
        quests.get(type).put(id, config);
    }

    public QuestSession getSession(String uuid){
        return sessions.get(uuid);
    }

    public HashMap<String, QuestConfig> getQuestsByType(QuestType type){
        return quests.get(type);
    }

    public QuestConfig getQuestsById(QuestType type, String id){
        return getQuestsByType(type).get(id);
    }

    //hehe ulgy as fuck

    public QuestConfig getQuestById(String id){
        if(quests.get(QuestType.DEFAULT).get(id) != null){
            return quests.get(QuestType.DEFAULT).get(id);
        }else if(quests.get(QuestType.NORMAL).get(id) != null){
            return quests.get(QuestType.NORMAL).get(id);
        }else if(quests.get(QuestType.VIP).get(id) != null){
            return quests.get(QuestType.VIP).get(id);
        }
        return null;
    }

    public static Quest getInstance() {
        return instance;
    }

    public static int generateRandom(int min, int max){
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        sessions.forEach((uuid, session) -> {
            Database.execute("DELETE FROM questData WHERE uuid='" + uuid + "'");
            ((QuestSession) session).getQuests().forEach((id, questData) -> {
                Database.execute("INSERT INTO questData(uuid, questId, progress, maxProgress, rewardClaimed) VALUES ('" + uuid + "', '" + id + "', '" + questData.getProgress() + "', '" + questData.getMaxProgress() + "', '" + (questData.isRewardClaimed() ? "1" : "0") + "')");
            });
        });
        try {
            Database.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
