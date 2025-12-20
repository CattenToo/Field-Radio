package arnett.fieldRadio.Commands.CommandTree.Monitor;

import arnett.fieldRadio.Commands.SubCommand;
import arnett.fieldRadio.Items.Radio.FieldRadio;
import arnett.fieldRadio.Items.Radio.FieldRadioVoiceChat;
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

        if(args.length == level + 1) {

            StringBuilder playerList = new StringBuilder();

            //sent with main frequency
            String mainf = args[level];

            // todo REWORK THIS

            //fill player list
            map.forEach((frequency, idList) ->
            {
                if(frequency.substring(0, frequency.indexOf('/')).equals(mainf))
                {
                    //main frequency entry
                    idList.forEach((id) ->
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
                    });

                    //split to main and sub
                    String main = args[level];
                    String sub = frequency.substring(frequency.indexOf('/') + 1);

                    //display
                    player.sendMessage(Component.text("<" + main + "/").color(TextColor.color(FieldRadio.getFrequencyColor(main)))
                            .append(Component.text(sub + "> ").color(TextColor.color(FieldRadio.getFrequencyColor(sub))))
                            .append(Component.text(playerList.toString())));

                    //clear list for next use
                    playerList.setLength(0);
                }
            });

            boolean presentListener = false;

            //this part is just to display a message if nothing else was shown
            //no one's got a radio of main frequency
            for(String s : map.keySet())
            {
                if(s.contains(mainf))
                {
                    presentListener = true;
                    break;
                }
            }

            if(!presentListener)
                player.sendMessage(Component.text("No Active Listeners").decorate(TextDecoration.BOLD));
        }

        else if (args.length == level + 2){
            StringBuilder playerList = new StringBuilder();

            //sent with main frequency
            String main = args[level];
            // sent with sub frequency
            String sub = args[level+1];

            //fill player list
            List<UUID> listeners = map.get(main + "/" + sub);

            if(listeners != null)
                listeners.forEach((id) ->
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
                });

            if(!playerList.isEmpty())
                //display
                player.sendMessage(Component.text("<" + main + "/").color(TextColor.color(FieldRadio.getFrequencyColor(main)))
                        .append(Component.text(sub + "> ").color(TextColor.color(FieldRadio.getFrequencyColor(sub))))
                        .append(Component.text(playerList.toString())));
            else
                player.sendMessage(Component.text("No Active Listeners").decorate(TextDecoration.BOLD));
        }

        //default assumes no arguments
        else  {
            StringBuilder playerList = new StringBuilder();
            map.forEach((frequency, players) -> {

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
                String main = frequency.substring( 0, frequency.indexOf('/'));
                String sub = frequency.substring(frequency.indexOf('/') + 1);

                //display
                player.sendMessage(Component.text("<" + main + "/").color(TextColor.color(FieldRadio.getFrequencyColor(main)))
                        .append(Component.text(sub + "> ").color(TextColor.color(FieldRadio.getFrequencyColor(sub))))
                        .append(Component.text(playerList.toString())));

                playerList.setLength(0);
            });

            //no one's got a radio
            if(map.isEmpty())
            {
                player.sendMessage(Component.text("No Active Listeners").decorate(TextDecoration.BOLD));
            }
        }

        return true;
    }

    @Override
    public boolean canUse(Player player)
    {
        return player.hasPermission("fieldradio.monitor");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args, int level) {
        // /DisplayRadioListeners <here> <here>
        if (args.length == level || args.length == level + 1) {
            //returns all dye values with some facny code to make it a list
            return Arrays.stream(DyeColor.values()).map(Enum::name).toList();
        }

        return List.of();
    }
}
