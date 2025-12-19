package arnett.fieldRadio.Commands.CommandTree.Give;

import arnett.fieldRadio.Commands.SubCommand;
import arnett.fieldRadio.Items.Radio.Radio;
import arnett.fieldRadio.Items.Radio.RadioVoiceChat;
import org.bukkit.entity.Player;

import java.util.List;

public class GiveFieldRadioCommang implements SubCommand {

    @Override
    public boolean execute(Player player, String[] args, int level) {
        if(args.length <= level)
        {
            //no frequency provided
            player.give(Radio.getRadio("defualt/default"));
            return true;
        }
        player.give(Radio.getRadio(args[level]));
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
        return List.of(RadioVoiceChat.getFrequencys().keySet().toArray(new String[0]));
    }
}
