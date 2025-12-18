package arnett.fieldRadio.Items.Radio;

import arnett.fieldRadio.FieldRadio;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.DyeColor;
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

public class RadioListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onMessageSent(AsyncChatEvent e)
    {
        //todo config for disabling global chat

        Optional<ItemStack> sendingRadio = Radio.getHeldRadio(e.getPlayer());

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
            ItemStack[] inventoryRadios = Radio.getRadiosFromPlayer(player);

            for (ItemStack receivingRadio : inventoryRadios)
            {
                //frequency check
                if(Radio.matchingFrequencies(sendingRadio.get(), receivingRadio))
                    return false;
            }

            //no match found
            return true;

        });


        String frequency = Radio.getFrequency(sendingRadio.get());
        String mainFq = frequency.substring(0, frequency.indexOf('/'));
        String subFq = frequency.substring(frequency.indexOf('/') + 1);

        TextColor mainFqColor = TextColor.color(Radio.getFrequencyColor(mainFq));
        TextColor subFqColor = TextColor.color(Radio.getFrequencyColor(subFq));

        FieldRadio.logger.info("Message Sent on Frequency <" + mainFq + "/" + subFq + "> by " + e.getPlayer().getName() + ": " + PlainTextComponentSerializer.plainText().serialize(e.message()));

        //build a new message
        e.renderer((source, sourceDisplayName, message, viewer) -> {

            return Component.text("<" + mainFq).color(mainFqColor)
                    .append(Component.text("/").color(mainFqColor))
                    .append(Component.text(subFq + "> ").color(subFqColor))
                    .append(sourceDisplayName.color(mainFqColor))
                    .append(Component.text(": ").color(mainFqColor))
                    .append(message.color(subFqColor));
        });

    }

    @EventHandler
    public void onRadioCraftered(CrafterCraftEvent e)
    {
        if (Radio.isRadio(e.getResult()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onRadioPrepared(PrepareItemCraftEvent e)
    {
        if(e.getRecipe() == null)
            //invalid recipe so skip
            return;

        if (!Radio.isRadio(e.getRecipe().getResult()))
            //not radio recipe so skip
            return;

        ItemStack result = e.getInventory().getResult();

        //returns what is put in the crafting interface
        ItemStack[] mtx = e.getInventory().getMatrix();

        ItemStack dye1 = mtx[1];
        ItemStack dye2 = mtx[2];

        String frequency = dye1.getType().name().replace("_DYE", "");
        String subfrequency = dye2.getType().name().replace("_DYE", "");

        result.editPersistentDataContainer(pdc -> {
            pdc.set(Radio.radioFrequencyKey, PersistentDataType.STRING, frequency + "/" + subfrequency);
        });

        result.lore(List.of(Component.text(frequency + "/" + subfrequency)));

        FieldRadio.logger.info("Radio Prepared: " + frequency + "/" + subfrequency);

        //update result (tbh not sure if this is necessary)
        e.getInventory().setResult(result);
    }
}
