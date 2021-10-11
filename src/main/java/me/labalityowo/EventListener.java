package me.labalityowo;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import me.labalityowo.config.QuestConfig;
import me.labalityowo.config.type.BreakQuest;
import me.labalityowo.config.type.CraftQuest;
import me.labalityowo.config.type.PlaceQuest;
import me.labalityowo.database.Database;
import me.labalityowo.events.QuestFinishEvent;
import me.labalityowo.quests.QuestData;
import me.labalityowo.quests.QuestSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class EventListener implements Listener {

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date today = new Date();
        if(Quest.getInstance().getSession(uuid) == null){
            Database.execute("SELECT COUNT(*) FROM questClaimedLog WHERE uuid='" + uuid + "'", resultSet -> {
                while (resultSet.next()) {
                    if (resultSet.getInt(1) == 0) {
                        Database.execute("INSERT INTO questClaimedLog(uuid, date) VALUES ('" + uuid + "', '" + format.format(today) + "')");
                        Quest.getInstance().registerQuestSession(player, today);
                    }else{
                        Database.execute("SELECT * FROM questClaimedLog WHERE uuid='" + uuid + "'", resultSet1 -> {
                            while(resultSet1.next()){
                                try {
                                    Date date = format.parse(resultSet1.getString("date"));
                                    if(getDateDiff(date, today, TimeUnit.MINUTES) >= 1){
                                        Quest.getInstance().registerQuestSession(player, today);
                                        System.out.println("Refreshing new quest");
                                        player.sendMessage("Đã làm mới nhiệm vụ!");
                                    }else{
                                        Database.execute("SELECT * FROM questData WHERE uuid='" + uuid + "'", resultSet2 -> {
                                            HashMap<String, QuestData> questData = new HashMap<>();
                                            while(resultSet2.next()){
                                                String questId = resultSet2.getString("questId");
                                                int progress = resultSet2.getInt("progress");
                                                int maxProgress = resultSet2.getInt("maxProgress");
                                                boolean rewardClaimed = resultSet2.getBoolean("rewardClaimed");
                                                questData.put(questId, new QuestData(progress, maxProgress, rewardClaimed));
                                            }
                                            Quest.getInstance().registerQuestSession(player, questData, today);
                                        });
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    @EventHandler
    public void onFinish(QuestFinishEvent event){
        Player player = event.getQuestSession().getPlayer();
        QuestConfig config = Quest.getInstance().getQuestById(event.getQuestId());
        player.sendTitle("DONE!", "Bạn đã làm xong nhiệm vụ " + config.getName());
        if(event.getQuestId().equals("diemdanh") && event.getQuestSession().getQuests().get("online").isComplete()){
            player.sendMessage("Bạn đã mở khóa các nhiệm vụ còn lại!");
        }else if(event.getQuestId().equals("online") && event.getQuestSession().getQuests().get("diemdanh").isComplete()){
            player.sendMessage("Bạn đã mở khóa các nhiệm vụ còn lại!");
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        QuestSession session = Quest.getInstance().getSession(player.getUniqueId().toString());
        session.getQuests().forEach((id, questData) -> {
            if(questData.isComplete()){
               return;
            }
            QuestConfig config = Quest.getInstance().getQuestById(id);
            if(config instanceof BreakQuest){
                if(event.getBlock().getId() == ((BreakQuest) config).getBlock().getId() && event.getBlock().getDamage() == ((BreakQuest) config).getBlock().getDamage()){
                    questData.setProgress(questData.getProgress() + 1, session, id);
                }
            }
        });
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        QuestSession session = Quest.getInstance().getSession(player.getUniqueId().toString());
        session.getQuests().forEach((id, questData) -> {
            if(questData.isComplete()){
                return;
            }
            QuestConfig config = Quest.getInstance().getQuestById(id);
            if(config instanceof PlaceQuest){
                if(event.getBlock().getId() == ((PlaceQuest) config).getBlock().getId() && event.getBlock().getDamage() == ((PlaceQuest) config).getBlock().getDamage()){
                    questData.setProgress(questData.getProgress() + 1, session, id);
                }
            }
        });
    }

    @EventHandler
    public void onCraft(CraftItemEvent event){
        Player player = event.getPlayer();
        QuestSession session = Quest.getInstance().getSession(player.getUniqueId().toString());
        session.getQuests().forEach((id, questData) -> {
            if(questData.isComplete()){
                return;
            }
            QuestConfig config = Quest.getInstance().getQuestById(id);
            if(config instanceof CraftQuest){
                if(event.getTransaction().getPrimaryOutput().getId() == ((CraftQuest) config).getItem().getId() && event.getTransaction().getPrimaryOutput().getDamage() == ((CraftQuest) config).getItem().getDamage()){
                    questData.setProgress(questData.getProgress() + event.getTransaction().getPrimaryOutput().getCount(), session, id);
                }
            }
        });
    }
}
