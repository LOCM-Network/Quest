package me.labalityowo.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import me.labalityowo.form.QuestForm;

public class QuestCommand extends Command {

    public QuestCommand() {
        super("quest");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        QuestForm.sendMain((Player) commandSender);
        return false;
    }
}
