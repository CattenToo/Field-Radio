package arnett.radio.Items.Speaker;

import arnett.radio.Config;
import arnett.radio.Radio;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.checkerframework.checker.units.qual.C;

public class SpeakerListener implements Listener {
    @EventHandler
    public void OnBlockPlaced(BlockPlaceEvent e)
    {
        if(Config.speaker_useEntity)
            return;

        if(!e.getBlock().getType().equals(Config.speaker_block_headType))
            return;

        Radio.logger.info("Placed Speaker");
    }
}
