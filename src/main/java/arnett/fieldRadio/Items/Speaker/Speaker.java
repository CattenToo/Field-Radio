package arnett.fieldRadio.Items.Speaker;

import arnett.fieldRadio.Config;
import arnett.fieldRadio.Radio;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Speaker {
    //todo use Heads to monitor the block placement

    public static ItemStack getSpeaker()
    {
        ItemStack speaker;
        try {
             speaker = new ItemStack(Material.matchMaterial(Config.speaker_headType));
        }
        catch (Exception e)
        {
            Radio.logger.info("INVALID CONFIG TYPE FOR SPEAKER HEAD: " + Config.speaker_headType);
            return ItemStack.of(Material.AIR);
        }

        return speaker;
    }
}
