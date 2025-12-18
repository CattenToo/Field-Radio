package arnett.fieldRadio.Items.Radio;

import arnett.fieldRadio.Config;
import arnett.fieldRadio.FieldRadio;
import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.UseCooldown;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.persistence.PersistentDataType;
import org.w3c.dom.css.RGBColor;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class Radio {

    public static final NamespacedKey radioIdentifierKey = new NamespacedKey(FieldRadio.singleton, "radio");
    public static final NamespacedKey radioFrequencyKey= new NamespacedKey(FieldRadio.singleton, "frequency");
    public static final Material RadioMaterial = Material.MUSIC_DISC_13;

    public static ArrayList<Recipe> getRecipes()
    {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();

        // Plain Radio
        {
            ShapedRecipe recipe = new ShapedRecipe(radioIdentifierKey, getRadio());

            //get shape of recipe from config
            recipe.shape(FieldRadio.config.getStringList(Config.radio_recipe_basic_shape.path()).toArray(String[]::new));

            //allows for all dye types to be used in a slot
            RecipeChoice.MaterialChoice dyes = new RecipeChoice.MaterialChoice(MaterialTags.DYES);

            ConfigurationSection ingredients = FieldRadio.config.getConfigurationSection(Config.radio_recipe_basic_ingredients.path());

            //defines the ingredients (the letters in the shape)
            if (ingredients != null) {
                for (String key : ingredients.getKeys(false)) {
                    //in case material is dye option
                    if((ingredients.get(key) instanceof String str) && str.equalsIgnoreCase("dyes"))
                    {
                        FieldRadio.logger.info("Set " + key.charAt(0) + " to DYES");
                        recipe.setIngredient(key.charAt(0), dyes);
                    }
                    //just a basic material
                    else {
                        Material mat;
                        mat = Material.matchMaterial(ingredients.getString(key));
                        if (mat != null) {
                            recipe.setIngredient(key.charAt(0), mat);
                        }
                    }
                }
            }

            recipes.add(recipe);
        }

        return  recipes;
    }

    public static ItemStack getRadio()
    {
        //creates Item (off of music disk because of minimal use cases)
        final ItemStack radio = ItemStack.of(RadioMaterial);

        //sets Item visuals
        radio.setData(DataComponentTypes.ITEM_NAME, Component.text("Handheld Radio"));
        radio.setData(DataComponentTypes.ITEM_MODEL, Key.key("arnett", "radio"));

        //sets Item Component Data
        radio.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                        .consumeSeconds(Float.MAX_VALUE)
                        .animation(ItemUseAnimation.TOOT_HORN)
                        .hasConsumeParticles(false)
                        .build());

        radio.editMeta(meta -> {
            meta.getUseCooldown().setCooldownSeconds(5f);
        });

        //removes jukebox functionality
        radio.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);

        //Adds Identifier tag
        radio.editPersistentDataContainer(pdc -> {
            pdc.set(radioIdentifierKey, PersistentDataType.STRING, "radio");
        });

        return radio;
    }

    public static boolean isHoldingRadio(Player player)
    {
        return isRadio(player.getInventory().getItemInMainHand()) || isRadio(player.getInventory().getItemInOffHand());
    }

    public static Optional<ItemStack> getHeldRadio(Player player)
    {
        if (isRadio(player.getInventory().getItemInMainHand()))
            return Optional.of(player.getInventory().getItemInMainHand());

        else if (isRadio(player.getInventory().getItemInOffHand()))
            return  Optional.of(player.getInventory().getItemInOffHand());

        return Optional.empty();
    }

    public static boolean isRadio(ItemStack item)
    {
        //exit this IMEADITEALY if not related to radio through the fastest possible method
        if(item.getType() != RadioMaterial)
            return false;

        return Objects.equals(item.getPersistentDataContainer().get(radioIdentifierKey, PersistentDataType.STRING), "radio");
    }

    public static boolean hasRadio(Player player) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .anyMatch(Radio::isRadio);
    }

    public static ItemStack[] getRadiosFromPlayer(Player player) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(Radio::isRadio)
                .toArray(ItemStack[]::new);
    }

    public static ItemStack[] getRadiosFromPlayer(Player player, String frequency) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(Radio::isRadio)
                .toArray(ItemStack[]::new);
    }

    public static String getFrequency(ItemStack radio)
    {
        return radio.getPersistentDataContainer().getOrDefault(radioFrequencyKey, PersistentDataType.STRING, "N/A");
    }

    public static Boolean matchingFrequencies(ItemStack radio1, ItemStack radio2)
    {
        return getFrequency(radio1).equals(getFrequency(radio2));
    }

    public static int getFrequencyColor(String frequency)
    {
        return  DyeColor.valueOf(frequency).getColor().asRGB();
    }
}
