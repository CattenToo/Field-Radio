package arnett.fieldRadio;

import org.bukkit.configuration.ConfigurationSection;

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
         enabled = Radio.config.getBoolean("enabled");

         frequencyRepresentationDyes = Radio.config.getConfigurationSection("frequency-representation.dyes");
         frequencySplitString = Radio.config.getString("frequency-representation.separating-string");
         // Field radio Audio Filter
         radio_audioFilter_enabled = Radio.config.getBoolean("fieldradio.audio-filter.enabled");
         radio_audioFilter_LPAlpha = Radio.config.getDouble("fieldradio.audio-filter.LP-alpha");
         radio_audioFilter_HPAlpha = Radio.config.getDouble("fieldradio.audio-filter.HP-alpha");
         radio_audioFilter_noiseFloor = Radio.config.getInt("fieldradio.audio-filter.noise-floor");
         radio_audioFilter_crackleChance = Radio.config.getInt("fieldradio.audio-filter.crackle-chance");

         // Radio Grace perood
         radio_gracePeriod = Radio.config.getLong("fieldradio.grace-period");

         // Radio Recipe
         radio_recipe_basic_enabled = Radio.config.getBoolean("fieldradio.recipe.basic.enabled");
         radio_recipe_basic_shape = Radio.config.getStringList("fieldradio.recipe.basic.shape");
         radio_recipe_basic_ingredients = Radio.config.getConfigurationSection("fieldradio.recipe.basic.ingredients");
    }
}
