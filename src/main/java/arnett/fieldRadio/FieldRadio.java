package arnett.fieldRadio;

import arnett.fieldRadio.Items.RadioListener;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class FieldRadio extends JavaPlugin {

    public static Logger logger;
    public static JavaPlugin singleton;

    //todo add multiple channels
    //todo add simple voice chat


    @Override
    public void onEnable() {

        singleton = this;

        CustomItemManager.registerRecipies();
        logger = getLogger();
        getServer().getPluginManager().registerEvents(new RadioListener(), this);
    }

    @Override
    public void onDisable() {

    }



}
