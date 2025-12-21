package arnett.radio.Items.Radio;

import arnett.radio.Config;
import arnett.radio.FrequencyManager;
import arnett.radio.Radio;
import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class FieldRadio {

    // shouldn't ever check the value of the key, only that the radio has it
    public static final NamespacedKey radioIdentifierKey = new NamespacedKey(Radio.singleton, "field_radio");
    //namspace key in resource pack for custom model
    public static final NamespacedKey radioModelKey = new NamespacedKey("radio", "field_radio");

    public static final Material RadioMaterial = Material.MUSIC_DISC_13;

    public static ArrayList<Recipe> getRecipes()
    {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();

        // Plain Radio
        {
            ShapedRecipe recipe = new ShapedRecipe(radioIdentifierKey, getRadio());

            //get shape of recipe from config
            recipe.shape(Config.fieldRadio_recipe_basic_shape.toArray(String[]::new));

            //allows for all dye types to be used in a slot
            RecipeChoice.MaterialChoice dyes = new RecipeChoice.MaterialChoice(MaterialTags.DYES);

            ConfigurationSection ingredients = Config.fieldRadio_recipe_basic_ingredients;

            //defines the ingredients (the letters in the shape)
            if (ingredients != null) {
                for (String key : ingredients.getKeys(false)) {

                    //just a basic material
                    Material mat;
                    try{
                        mat = Material.matchMaterial(ingredients.getString(key));
                    }
                    catch (Exception e)
                    {
                        //material not found or something went wrong
                        Radio.logger.info("Incorrectly registered Material For Radio basic recipe");
                        mat = Material.AIR;
                    }
                    if (mat != null) {
                        recipe.setIngredient(key.charAt(0), mat);
                    }
                }
            }

            //add dyes
            for(int i = 0; i < 8; i++)
            {
                try
                {
                    recipe.setIngredient((char)( i + '0'), dyes);
                    Radio.logger.info("Added Dye for " + i);
                }
                catch (Exception e)
                {
                    Radio.logger.info("stopped at " + i);
                    //frequency not in recipe so exit
                    break;
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
        radio.setData(DataComponentTypes.ITEM_NAME, Component.text("Field Radio"));
        radio.setData(DataComponentTypes.ITEM_MODEL, radioModelKey);

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
            pdc.set(radioIdentifierKey, PersistentDataType.STRING, "field_radio");
        });

        return radio;
    }

    public static ItemStack getRadio(String frequency)
    {
        ItemStack radio = FieldRadio.getRadio();

        radio.editPersistentDataContainer(pdc -> {
            pdc.set(FrequencyManager.radioFrequencyKey, PersistentDataType.STRING, frequency);
        });

        radio.lore(List.of(Component.text(FrequencyManager.convertToDisplayFrequency(frequency))));

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

        return item.getPersistentDataContainer().has(radioIdentifierKey, PersistentDataType.STRING);
    }

    public static boolean hasRadio(Player player) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .anyMatch(FieldRadio::isRadio);
    }

    public static ItemStack[] getRadiosFromPlayer(Player player) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(FieldRadio::isRadio)
                .toArray(ItemStack[]::new);
    }

    public static ItemStack[] getRadiosFromPlayer(Player player, String frequency) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(FieldRadio::isRadio)
                .toArray(ItemStack[]::new);
    }

    public static String getFrequency(ItemStack radio)
    {
        return radio.getPersistentDataContainer().getOrDefault(FrequencyManager.radioFrequencyKey, PersistentDataType.STRING, "none");
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
