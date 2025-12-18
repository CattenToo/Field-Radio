package arnett.fieldRadio.Commands.CommandTree;

import arnett.fieldRadio.Commands.BranchCommand;
import arnett.fieldRadio.Commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class MonitorBranch extends BranchCommand {

    public MonitorBranch(HashMap<SubCommand, String> map) {
        super(map);
    }

    @Override
    public String getName() {
        return "monitor";
    }

    @Override
    public String getDescription() {
        return "All commands related to monitoring";
    }

    @Override
    public String getSyntax() {
        return "/radio monitor <sub command>";
    }

    @Override
    public boolean canUse(Player player) {
        return player.hasPermission("fieldradio.monitor");
    }
}
