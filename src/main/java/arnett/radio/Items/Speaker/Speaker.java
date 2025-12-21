package arnett.radio.Items.Speaker;

import arnett.radio.Config;
import arnett.radio.FrequencyManager;
import arnett.radio.Radio;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@SuppressWarnings("UnstableApiUsage")
public class Speaker {
    //todo use Heads to monitor the block placement

    public static final NamespacedKey speakerIdentifierKey = new NamespacedKey(Radio.singleton, "speaker");
    public static final NamespacedKey speakerModelKey = new NamespacedKey("radio", "speaker");

    public static ItemStack getSpeaker(String frequency)
    {
        ItemStack speaker = getSpeaker();

        //set frequency
        speaker.editPersistentDataContainer(pdc -> {
            pdc.set(FrequencyManager.radioFrequencyKey, PersistentDataType.STRING, frequency);
        });

        return speaker;
    }

    public static ItemStack getSpeaker()
    {
        if(Config.speaker_useEntity)
            return getSpeakerEntityItem();
        else
            return getspeakerBlockItem();
    }

    static ItemStack getspeakerBlockItem()
    {
        ItemStack speaker;

        try {
            speaker = new ItemStack(Material.matchMaterial(Config.speaker_block_headType));
        }
        catch (Exception e)
        {
            Radio.logger.info("INVALID CONFIG TYPE FOR SPEAKER HEAD: " + Config.speaker_block_headType);
            return ItemStack.of(Material.AIR);
        }

        //sets Item visuals
        speaker.setData(DataComponentTypes.ITEM_NAME, Component.text("Speaker"));
        speaker.setData(DataComponentTypes.ITEM_MODEL, speakerModelKey);

        //Adds Identifier tag
        speaker.editPersistentDataContainer(pdc -> {
            pdc.set(speakerIdentifierKey, PersistentDataType.STRING, "speaker");
        });

        return speaker;
    }

    static ItemStack getSpeakerEntityItem()
    {
        ItemStack speaker = new ItemStack(Material.AIR);

        //todo entity version of the speaker

        return speaker;
    }
}
