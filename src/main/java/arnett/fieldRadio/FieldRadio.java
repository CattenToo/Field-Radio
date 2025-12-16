package arnett.fieldRadio;

import arnett.fieldRadio.Items.Radio;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class FieldRadio extends JavaPlugin {

    public static Logger logger;
    public static NamespacedKey key;

    //todo add multiple channels
    //todo add simple voice chat


    @Override
    public void onEnable() {

        //Sets Key for ease of use in other classes
        key = new NamespacedKey(this, "field_radio");

        CustomItemManager.registerRecipies();
        logger = getLogger();
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {

    }



}
