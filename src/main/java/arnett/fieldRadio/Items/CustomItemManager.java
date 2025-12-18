package arnett.fieldRadio.Items;

import arnett.fieldRadio.Config;
import arnett.fieldRadio.FieldRadio;
import arnett.fieldRadio.Items.Radio.Radio;
import arnett.fieldRadio.Items.Radio.RadioListener;
import arnett.fieldRadio.Items.Radio.RadioVoiceChatListener;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;


// tbh this class isn't used that much, but I'm keeping because I want to
// and because it looks better for when there are more items
public class CustomItemManager {

    public static void registerItemEvents(JavaPlugin plugin)
    {
        //radio
        plugin.getServer().getPluginManager().registerEvents(new RadioListener(), plugin);
    }

    public static void registerVoiceChatItemEvents(JavaPlugin plugin)
    {
        //radio
        plugin.getServer().getPluginManager().registerEvents(new RadioVoiceChatListener(), plugin);
    }

    public static void registerRecipies()
    {
        //radio
        if(FieldRadio.config.getBoolean(Config.radio_recipe_basic_enabled.path()))
            for(Recipe r : Radio.getRecipes())
                Bukkit.addRecipe(r);
    }
}
