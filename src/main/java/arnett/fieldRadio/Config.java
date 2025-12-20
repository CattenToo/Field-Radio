package arnett.fieldRadio;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;

//yeah, this system is much better
//basically, all this does is allow for ease of use when coding
public class Config {

    // Top level
    public static boolean enabled;

    public static ConfigurationSection frequencyRepresentationDyes;
    public static String frequencySplitString;
    // Field radio Audio Filter
    public static boolean radio_audioFilter_enabled;
    public static double radio_audioFilter_LPAlpha;
    public static double radio_audioFilter_HPAlpha;
    public static int radio_audioFilter_noiseFloor;
    public static int radio_audioFilter_crackleChance;

    // Radio Grace perood
    public static long radio_gracePeriod;

    // Radio Recipe
    public static boolean radio_recipe_basic_enabled;
    public static List<String> radio_recipe_basic_shape;
    public static ConfigurationSection radio_recipe_basic_ingredients;


    public static void refresh()
    {
         // Top level
         enabled = FieldRadio.config.getBoolean("enabled");

         frequencyRepresentationDyes = FieldRadio.config.getConfigurationSection("frequency-representation.dyes");
         frequencySplitString = FieldRadio.config.getString("frequency-representation.separating-string");
         // Field radio Audio Filter
         radio_audioFilter_enabled = FieldRadio.config.getBoolean("radio.audio-filter.enabled");
         radio_audioFilter_LPAlpha = FieldRadio.config.getDouble("radio.audio-filter.LP-alpha");
         radio_audioFilter_HPAlpha = FieldRadio.config.getDouble("radio.audio-filter.HP-alpha");
         radio_audioFilter_noiseFloor = FieldRadio.config.getInt("radio.audio-filter.noise-floor");
         radio_audioFilter_crackleChance = FieldRadio.config.getInt("radio.audio-filter.crackle-chance");

         // Radio Grace perood
         radio_gracePeriod = FieldRadio.config.getLong("radio.grace-period");

         // Radio Recipe
         radio_recipe_basic_enabled = FieldRadio.config.getBoolean("radio.recipe.basic.enabled");
         radio_recipe_basic_shape = FieldRadio.config.getStringList("radio.recipe.basic.shape");
         radio_recipe_basic_ingredients = FieldRadio.config.getConfigurationSection("radio.recipe.basic.ingredients");
    }
}
