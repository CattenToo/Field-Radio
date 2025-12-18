package arnett.fieldRadio.Commands.CommandTree.Manage.Config;

import arnett.fieldRadio.Commands.SubCommand;
import arnett.fieldRadio.FieldRadio;
import org.apache.logging.log4j.spi.ObjectThreadContextMap;
import org.bukkit.entity.Player;

import java.util.List;

public class SetConfigValueCommand implements SubCommand {
    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "/radio manage config set <setting> <value>";
    }

    @Override
    public String getSyntax() {
        return "Sets config values, REQUIRES RELOAD";
    }

    @Override
    public boolean execute(Player player, String[] args, int level) {

        SubCommand.super.execute(player, args, level);

        Object previous = FieldRadio.singleton.getConfig().get(args[level]);
        FieldRadio.singleton.getConfig().set(args[level], args[level+1]);
        player.sendMessage(args[level-2] + " set from " + previous + " to " + args[level-1]);
        return true;
    }

    @Override
    public boolean canUse(Player player) {
        return player.hasPermission("fieldradio.manage");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args, int level) {
        if(args.length != level)
            return List.of();
        return FieldRadio.config.getKeys(true).stream().toList();
    }
}
