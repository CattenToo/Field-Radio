package arnett.fieldRadio;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;

//enum for autocorrect when writing code
// bro tbh this system kinda sucks
public enum Config {
    // Top level
    enabled("enabled"),

    radio_audioFilter_enabled("radio.audio-filter.enabled"),
    radio_audioFilter_LPAlpha("radio.audio-filter.LP-alpha"),
    radio_audioFilter_HPAlpha("radio.audio-filter.HP-alpha"),
    radio_audioFilter_noiseFloor("radio.audio-filter.noise-floor"),
    radio_audioFilter_crackleChance("radio.audio-filter.crackle-chance"),

    // Radio Grace perood
    radio_gracePeriod("radio.grace-period"),

    // Radio Recipe
    radio_recipe_basic_enabled("radio.recipe.basic.enabled"),
    radio_recipe_basic_shape("radio.recipe.basic.shape"),
    radio_recipe_basic_ingredients("radio.recipe.basic.ingredients");

    private final String path;
    Config(String path) {
        this.path = path;
    }

    public String path() {
        return this.path;
    }
}
