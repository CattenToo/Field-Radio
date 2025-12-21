package arnett.radio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class FrequencyManager {

    //stores which frequency an item is connected to
    public static final NamespacedKey radioFrequencyKey = new NamespacedKey(Radio.singleton, "frequency");

    //stores which dye tab belongs to which dye and vice versa
    //normal is Dye names
    //inverse is custom names
    public static BiMap<String, String> dyeMap = HashBiMap.create();

    public static void reload()
    {
        dyeMap.clear();

        //sets up 2 way map for quick frequency color refrence
        for (String key : Config.frequencyRepresentationDyes.getKeys(false)) {
            Radio.logger.info(key);
            Radio.logger.info(Config.frequencyRepresentationDyes.getString(key));
            dyeMap.put(key, Config.frequencyRepresentationDyes.getString(key));
        }
    }

    public static ItemStack addFrequencyToCraft(ItemStack result, ItemStack[] mtx, List<String> shape)
    {
        //get position of dyes
        StringBuilder frequency = new StringBuilder();
        StringBuilder displayFrequency = new StringBuilder();

        //stores which dye was used for which frequency
        //useful if recipe required two or more dyes for the same sub frequency
        String[] dyesChecker = new String[8];

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
                        frequency.append(item.name());
                        frequency.append(Config.frequencySplitString);
                        displayFrequency.append(Config.frequencyRepresentationDyes.getString(item.name()));
                        displayFrequency.append(Config.frequencySplitString);
                        dyesChecker[digit] = Config.frequencyRepresentationDyes.getString(item.name());
                    }
                    else {
                        //has been added so check if it is the same as the others for this level
                        if(dyesChecker[digit].equals(Config.frequencyRepresentationDyes.getString(item.name())))
                        {
                            frequency.append(item.name());
                            displayFrequency.append(Config.frequencyRepresentationDyes.getString(item.name()));
                        }
                        else
                        {
                            //invalid recipe
                            return ItemStack.of(Material.AIR);
                        }
                    }
                }
            }
        }

        //chop off last bit (the / or .)
        frequency.setLength(frequency.length() - 1);
        displayFrequency.setLength(frequency.length() - 1);

        result.editPersistentDataContainer(pdc -> {
            pdc.set(FrequencyManager.radioFrequencyKey, PersistentDataType.STRING, frequency.toString());
        });

        result.lore(List.of(Component.text(displayFrequency.toString())));

        return result;
    }
}
