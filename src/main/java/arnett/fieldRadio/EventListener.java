package arnett.fieldRadio;

import arnett.fieldRadio.Items.Radio;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.N;
import org.eclipse.sisu.Priority;

public class EventListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerJumpEvent e)
    {
        if(e.isCancelled())
            return;

        FieldRadio.logger.info(e.getFrom() + ", " + e.getTo());
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMessageSent(AsyncChatEvent e)
    {
        //todo config for disabling global chat

        //check for radio being held, if so, send through channel
        if(Radio.isHoldingRadio(e.getPlayer()))
        {
            //don't send to those without a radio
            e.viewers().removeIf(audience -> audience instanceof Player player && !Radio.hasRadio(player));

            //build a new message
            e.renderer((source, sourceDisplayName, message, viewer) -> {

                String end = "";

                if (viewer instanceof Player player)
                    end = player.getName();

                return Component.text("<Channel #> ").color(TextColor.color(255, 220, 140))
                        .append(sourceDisplayName)
                        .append(Component.text(": "))
                        .append(message)
                        .append(Component.text(" -> " + end));
            });
        }
    }

}
