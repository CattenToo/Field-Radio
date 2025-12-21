package arnett.radio.Commands.CommandTree.Monitor;

import arnett.radio.Commands.SubCommand;
import arnett.radio.Config;
import arnett.radio.FrequencyManager;
import arnett.radio.Items.Radio.FieldRadio;
import arnett.radio.Items.Radio.FieldRadioVoiceChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.util.*;

// todo THIS COMMAND WILL BE REWORKED LATER

@SuppressWarnings("UnstableApiUsage")
public class FrequencyDisplayCommand implements SubCommand {

    @Override
    public String getName() {
        return "frequency";
    }

    @Override
    public String getDescription() {
        return "Displays the players listening to given frequencies";
    }

    @Override
    public String getSyntax() {
        return "/radio frequency <main> <sub>";
    }

    @Override
    public boolean execute(Player player, String[] args, int level) {

        SubCommand.super.execute(player, args, level);

        Map<String, ArrayList<UUID>> map = FieldRadioVoiceChat.getFrequencys();

        StringBuilder playerList = new StringBuilder();
        map.forEach((frequency, players) -> {

            //check if args match frequency


            for(UUID id : players)
            {
                try {
                    playerList.append(Bukkit.getPlayer(id).getName());
                }
                catch (NullPointerException e)
                {
                    //player not online so Can't get name (this is slower btw)
                    playerList.append(Bukkit.getOfflinePlayer(id).getName());
                }
                playerList.append(", ");
            }

            //split to main and sub
            String main = frequency.substring( 0, frequency.indexOf(Config.frequencySplitString));
            String sub = frequency.substring(frequency.indexOf(Config.frequencySplitString) + 1);

            //display
            player.sendMessage(Component.text("<" + main + Config.frequencySplitString).color(TextColor.color(FieldRadio.getFrequencyColor(main)))
                    .append(Component.text(sub + "> ").color(TextColor.color(FieldRadio.getFrequencyColor(sub))))
                    .append(Component.text(playerList.toString())));

            playerList.setLength(0);
        });

            //no one's got a radio
            if(map.isEmpty())
            {
                player.sendMessage(Component.text("No Active Listeners").decorate(TextDecoration.BOLD));
            }

        return true;
    }

    @Override
    public boolean canUse(Player player)
    {
        return player.hasPermission("radio.monitor");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args, int level) {
        // /DisplayRadioListeners <here> <here> <here> ...
        if (args.length >= level) {
            //returns all dye values
            return FrequencyManager.dyeMap.keySet().stream().toList();
        }

        return List.of();
    }
}
