package arnett.fieldRadio.Items.Radio;

import arnett.fieldRadio.Config;
import arnett.fieldRadio.Radio;
import arnett.fieldRadio.Items.CustomItemManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.block.Crafter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Optional;

public class FieldRadioListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onMessageSent(AsyncChatEvent e)
    {
        //todo config for disabling global chat

        Optional<ItemStack> sendingRadio = FieldRadio.getHeldRadio(e.getPlayer());

        //check for radio being held, if not, continue normally
        if(sendingRadio.isEmpty())
            return;

        //clears those without a radio (and non players)
        e.viewers().removeIf(audience -> {
            //only check for players
            if(!(audience instanceof Player player))
                //not a player
                return true;

            //check if they have radio
            ItemStack[] inventoryRadios = FieldRadio.getRadiosFromPlayer(player);

            for (ItemStack receivingRadio : inventoryRadios)
            {
                //frequency check
                if(FieldRadio.matchingFrequencies(sendingRadio.get(), receivingRadio))
                    return false;
            }

            //no match found
            return true;

        });

        String frequency = FieldRadio.getFrequency(sendingRadio.get());

        //get main frequency now since it's used multiple times
        String mainFq = frequency.substring(0, frequency.indexOf(Config.frequencySplitString));
        TextColor mainFqTextColor = CustomItemManager.getFrequencyTextColor(mainFq);

        Radio.logger.info("Message Sent on Frequency <" + frequency + "> by " + e.getPlayer().getName() + ": " + PlainTextComponentSerializer.plainText().serialize(e.message()));

        //build a new message
        e.renderer((source, sourceDisplayName, message, viewer) -> {

            TextComponent c = Component.text("<").color(mainFqTextColor);

            String[] split = frequency.split(Config.frequencySplitString);
            for(int i = 0; i < split.length; i++)
            {
                Radio.logger.info(split[i]);
                c = c.append(Component.text(split[i] + (i == split.length - 1 ? "" : Config.frequencySplitString))
                        .color(CustomItemManager.getFrequencyTextColor(split[i]))
                    );

            }

            c = c.append(Component.text( "> ")).color(mainFqTextColor)
            .append(sourceDisplayName.color(TextColor.color(CustomItemManager.getDulledFrequencyColor(split[split.length-1]).asRGB())))
            .append(Component.text(": ")
            .append(message).color(TextColor.color(CustomItemManager.getDulledFrequencyColor(mainFq).asRGB())));

            return c;
        });

    }

    @EventHandler
    public void onRadioCraftered(CrafterCraftEvent e)
    {
        if (!FieldRadio.isRadio(e.getRecipe().getResult()))
            //not radio recipe so skip
            return;

        ItemStack result = e.getResult();

        //returns what is put in the crafting interface
        ItemStack[] mtx = ((Crafter)e.getBlock().getState()).getInventory().getContents();

        //get position of dyes
        StringBuilder frequency = new StringBuilder();

        //stores which dye was used for which frequency
        //useful if recipe required two or more dyes for the same sub frequency
        String[] dyesChecker = new String[8];

        List<String> shape = Config.radio_recipe_basic_shape;

        //check matrix for dyes in correct places
        for(int i = 0; i < shape.size(); i++ )
        {
            for(int j = 0; j < shape.get(i).length(); j++)
            {
                char checked = shape.get(i).charAt(j);

                if(Character.isDigit(checked))
                {

                    Material item = mtx[i*3 + j].getType();
                    int digit = Character.getNumericValue(checked);

                    //dye
                    if(dyesChecker[digit] == null)
                    {
                        //has not yet been added
                        //so just add dye to frequency and to dyes checker
                        frequency.append(Config.frequencyRepresentationDyes.getString(item.name()));
                        frequency.append((digit == 0 ? '/' : '.'));
                        dyesChecker[digit] = Config.frequencyRepresentationDyes.getString(item.name());
                    }
                    else {
                        //has been added so check if it is the same as the others for this level
                        if(dyesChecker[digit].equals(Config.frequencyRepresentationDyes.getString(item.name())))
                        {
                            frequency.append(Config.frequencyRepresentationDyes.getString(item.name()));
                        }
                        else
                        {
                            Radio.logger.info("Failed recipe with " + dyesChecker[digit] + " " + Config.frequencyRepresentationDyes.getString(item.name()));
                            //invalid recipe
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }

        result.editPersistentDataContainer(pdc -> {
            pdc.set(FieldRadio.radioFrequencyKey, PersistentDataType.STRING, frequency.toString());
        });

        result.lore(List.of(Component.text(frequency.toString())));

        Radio.logger.info("Radio Prepared: " + frequency);

        //update result (tbh not sure if this is necessary)
        e.setResult(result);
    }

    @EventHandler
    public void onRadioPrepared(PrepareItemCraftEvent e)
    {
        if(e.getRecipe() == null)
            //invalid recipe so skip
            return;

        if (!FieldRadio.isRadio(e.getRecipe().getResult()))
            //not radio recipe so skip
            return;

        ItemStack result = e.getInventory().getResult();

        //returns what is put in the crafting interface
        ItemStack[] mtx = e.getInventory().getMatrix();

        //get position of dyes
        StringBuilder frequency = new StringBuilder();

        //stores which dye was used for which frequency
        //useful if recipe required two or more dyes for the same sub frequency
        String[] dyesChecker = new String[8];

        List<String> shape = Config.radio_recipe_basic_shape;

        //check matrix for dyes in correct places
        for(int i = 0; i < shape.size(); i++ )
        {
            for(int j = 0; j < shape.get(i).length(); j++)
            {
                char checked = shape.get(i).charAt(j);

                if(Character.isDigit(checked))
                {

                    Material item = mtx[i*3 + j].getType();
                    int digit = Character.getNumericValue(checked);

                    //dye
                    if(dyesChecker[digit] == null)
                    {
                        //has not yet been added
                        //so just add dye to frequency and to dyes checker
                        frequency.append(Config.frequencyRepresentationDyes.getString(item.name()));
                        frequency.append((digit == 0 ? '/' : '.'));
                        dyesChecker[digit] = Config.frequencyRepresentationDyes.getString(item.name());
                    }
                    else {
                        //has been added so check if it is the same as the others for this level
                        if(dyesChecker[digit].equals(Config.frequencyRepresentationDyes.getString(item.name())))
                        {
                            frequency.append(Config.frequencyRepresentationDyes.getString(item.name()));
                        }
                        else
                        {
                            //invalid recipe
                            e.getInventory().setResult(new ItemStack(Material.AIR));
                            return;
                        }
                    }
                }
            }
        }

        //chop off last bit (the / or .)
        frequency.setLength(frequency.length() - 1);

        result.editPersistentDataContainer(pdc -> {
            pdc.set(FieldRadio.radioFrequencyKey, PersistentDataType.STRING, frequency.toString());
        });

        result.lore(List.of(Component.text(frequency.toString())));

        Radio.logger.info("Radio Prepared: " + frequency);

        //update result (tbh not sure if this is necessary)
        e.getInventory().setResult(result);
    }
}
