package arnett.fieldRadio.Commands.CommandTree;

import arnett.fieldRadio.Commands.BranchCommand;
import arnett.fieldRadio.Commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class ManageBranch extends BranchCommand {

    public ManageBranch(HashMap<SubCommand, String> map) {
        super(map);
    }

    @Override
    public String getName() {
        return "manage";
    }

    @Override
    public String getDescription() {
        return "All commands related to managing connections";
    }

    @Override
    public String getSyntax() {
        return "/radio manage <sub command>";
    }

    @Override
    public boolean canUse(Player player) {
        return player.hasPermission("fieldradio.manage");
    }
}
