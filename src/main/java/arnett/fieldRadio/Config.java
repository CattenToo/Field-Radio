package arnett.fieldRadio;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;

//yeah, this system is much better
//basically, all this does is allow for ease of use when coding
public class Config {

    // Top level
    public static boolean enabled = FieldRadio.config.getBoolean("enabled");

    public static boolean radio_audioFilter_enabled = FieldRadio.config.getBoolean("radio.audio-filter.enabled");
    public static double radio_audioFilter_LPAlpha = FieldRadio.config.getDouble("radio.audio-filter.LP-alpha");
    public static double radio_audioFilter_HPAlpha = FieldRadio.config.getDouble("radio.audio-filter.HP-alpha");
    public static int radio_audioFilter_noiseFloor = FieldRadio.config.getInt("radio.audio-filter.noise-floor");
    public static int radio_audioFilter_crackleChance = FieldRadio.config.getInt("radio.audio-filter.crackle-chance");

    // Radio Grace perood
    public static long radio_gracePeriod = FieldRadio.config.getLong("radio.grace-period");

    // Radio Recipe
    public static boolean radio_recipe_basic_enabled = FieldRadio.config.getBoolean("radio.recipe.basic.enabled");
    public static List<String> radio_recipe_basic_shape = FieldRadio.config.getStringList("radio.recipe.basic.shape");
    public static ConfigurationSection radio_recipe_basic_ingredients = FieldRadio.config.getConfigurationSection("radio.recipe.basic.ingredients");
}
