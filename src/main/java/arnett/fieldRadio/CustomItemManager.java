package arnett.fieldRadio;

import arnett.fieldRadio.Items.Radio;
import org.bukkit.Bukkit;

public class CustomItemManager {

    public static void registerRecipies()
    {
        Bukkit.addRecipe(Radio.getRecipe());
    }
}
