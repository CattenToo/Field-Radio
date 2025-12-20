package arnett.fieldRadio.Items.Radio;

import arnett.fieldRadio.Radio;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FieldRadioVoiceChatListener implements Listener {

    @EventHandler
    public void onInventoryChange(PlayerInventorySlotChangeEvent e)
    {
        // so fun fact, when opening a GUI minecraft refreshes the inventory which means
        // this is getting set off, but it's not with the same new and old item because
        // when it is refreshed the old item is AIR and the new item is the actual item
        // which causes an issue because WHY! (syncing that's why)

        // only when a radio is involved and the same radio isn't changed by itself.
        if(FieldRadio.isRadio(e.getNewItemStack()))
        {
            if(e.getOldItemStack().getType().equals(Material.AIR))
            {
                // possible inventory refresh scenario (like opening a chest)
                // or item added to empty slot
                // how to tell the difference? NOT SCIENTIFICALLY POSSIBLE
                // so screw it, one tick later we'll just refresh the player
                // ONLY IF they already are connected to the frequency

                String frequency = FieldRadio.getFrequency(e.getNewItemStack());

                if(FieldRadioVoiceChat.isOnFrequency(frequency, e.getPlayer()))
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Radio.singleton, () -> {
                        FieldRadioVoiceChat.refresh(frequency, e.getPlayer());
                    }, 1);
            }

            Radio.logger.info("Picked Up Radio <" + FieldRadio.getFrequency(e.getNewItemStack()) + "> by " + e.getPlayer().getName());

            //radio added to inventory
            //set player to listen to frequency
            FieldRadioVoiceChat.addToFrequency(FieldRadio.getFrequency(e.getNewItemStack()), e.getPlayer().getUniqueId());
        }
        if(FieldRadio.isRadio(e.getOldItemStack()))
        {

            Radio.logger.info("Removed Radio <" + FieldRadio.getFrequency(e.getOldItemStack()) + "> by " + e.getPlayer().getName());

            //radio removed from inventory
            //remove player from listen to frequency
            FieldRadioVoiceChat.removeFromFrequency(FieldRadio.getFrequency(e.getOldItemStack()), e.getPlayer().getUniqueId());
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
        // so when a player joins their inventory is refreshed by the server which means
        // the inventory slot change event will be called, so we just have to remove
        // all their existing entries if any (which would be the case if there is a serer stop)
        FieldRadioVoiceChat.removeFromFrequency(e.getPlayer().getUniqueId());
    }
}
