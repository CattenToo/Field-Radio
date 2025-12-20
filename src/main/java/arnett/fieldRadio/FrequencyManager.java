package arnett.fieldRadio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class FrequencyManager {

    //stores which dye tab belongs to which dye and vice versa
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
}
