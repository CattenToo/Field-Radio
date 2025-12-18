package arnett.fieldRadio.Items.Radio;

import arnett.fieldRadio.FieldRadio;
import arnett.fieldRadio.FieldRadioVoiceChat;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.checkerframework.common.initializedfields.qual.InitializedFields;

public class RadioVoiceChatListener implements Listener {

    @EventHandler
    public void onInventoryChange(PlayerInventorySlotChangeEvent e)
    {
        if(Radio.isRadio(e.getNewItemStack()))
        {
            FieldRadio.logger.info("Picked Up Radio <" + Radio.getFrequency(e.getNewItemStack()) + "> by " + e.getPlayer().getName());

            //player added radio to inventory
            //set player to listen to frequency
            FieldRadioVoiceChat.addToFrequency(Radio.getFrequency(e.getNewItemStack()), e.getPlayer().getUniqueId());
        }
        if(Radio.isRadio(e.getOldItemStack()))
        {
            FieldRadio.logger.info("Removed Radio <" + Radio.getFrequency(e.getOldItemStack()) + "> by " + e.getPlayer().getName());

            //player removed radio from inventory
            //remove player from listen to frequency
            FieldRadioVoiceChat.removeFromFrequency(Radio.getFrequency(e.getOldItemStack()), e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e)
    {
        FieldRadioVoiceChat.removeFromGrace(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        FieldRadioVoiceChat.removeFromFrequency(e.getPlayer().getUniqueId());
        FieldRadio.logger.info("Refresh Player");
    }
}
