package arnett.fieldRadio.Commands.CommandTree.Manage;

import arnett.fieldRadio.Commands.BranchCommand;
import arnett.fieldRadio.Commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class ConfigBranch extends BranchCommand {


    public ConfigBranch(HashMap<SubCommand, String> map) {
        super(map);
    }

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getDescription() {
        return "Everything related to the config";
    }

    @Override
    public String getSyntax() {
        return "/radio manage config";
    }

    @Override
    public boolean canUse(Player player) {
        return player.hasPermission("fieldradio.manage");
    }
}
