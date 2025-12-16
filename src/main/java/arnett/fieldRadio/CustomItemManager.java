package arnett.fieldRadio;

import arnett.fieldRadio.Items.Radio;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;

public class CustomItemManager {

    public static void registerRecipies()
    {
        //radio
        for(Recipe r : Radio.getRecipes())
            Bukkit.addRecipe(r);
    }
}
