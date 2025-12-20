package arnett.fieldRadio.Commands.CommandTree.Give;

import arnett.fieldRadio.Commands.SubCommand;
import arnett.fieldRadio.Config;
import arnett.fieldRadio.FrequencyManager;
import arnett.fieldRadio.Items.Radio.FieldRadio;
import org.bukkit.entity.Player;

import java.util.List;

public class GiveFieldRadioCommang implements SubCommand {

    @Override
    public boolean execute(Player player, String[] args, int level) {
        if(args.length <= level)
        {
            //no frequency provided
            player.give(FieldRadio.getRadio("BRODCAST"));
            return true;
        }

        StringBuilder frequency = new StringBuilder();

        for(int i = level; i < args.length; i++)
        {
            frequency.append(args[i]).append(Config.frequencySplitString);
        }

        frequency.setLength(frequency.length() - 1);

        player.give(FieldRadio.getRadio(frequency.toString()));
        return true;
    }

    @Override
    public String getName() {
        return "fieldradio";
    }

    @Override
    public String getDescription() {
        return "gives field radio of frequency";
    }

    @Override
    public String getSyntax() {
        return "/radio give fieldradio <frequency>";
    }

    @Override
    public boolean canUse(Player player)
    {
        return player.hasPermission("fieldradio.give");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args, int level) {
        return FrequencyManager.dyeMap.inverse().keySet().stream().toList();
    }
}
