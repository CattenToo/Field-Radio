package arnett.fieldRadio.Commands.CommandTree.Manage.Config;

import arnett.fieldRadio.Commands.SubCommand;
import arnett.fieldRadio.FieldRadio;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReloadConfigCommand implements SubCommand {

    @Override
    public boolean execute(Player player, String[] args, int level) {

        SubCommand.super.execute(player, args, level);

        FieldRadio.singleton.reloadConfig();
        return true;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "reloads the config file";
    }

    @Override
    public String getSyntax() {
        return "/radio manage config reload";
    }

    @Override
    public boolean canUse(Player player)
    {
        return player.hasPermission("fieldradio.manage");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args, int level) {
        return List.of();
    }
}