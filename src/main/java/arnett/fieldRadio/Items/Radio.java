package arnett.fieldRadio.Items;

import arnett.fieldRadio.FieldRadio;
import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.N;

import javax.swing.text.html.HTML;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public class Radio {

    public static NamespacedKey radioIdentifierKey = new NamespacedKey(FieldRadio.singleton, "radio");
    public static NamespacedKey radioFrequencyKey= new NamespacedKey(FieldRadio.singleton, "frequency");

    public static ArrayList<Recipe> getRecipes()
    {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();

        // Plain Radio
        {
            ShapedRecipe recipe = new ShapedRecipe(radioIdentifierKey, getRadio());

            //makes shape of recipe
            recipe.shape(
                    "O12",
                    "IAI",
                    "IRI"
            );

            //allows for all dye types to be used in a slot
            RecipeChoice.MaterialChoice dyes = new RecipeChoice.MaterialChoice(MaterialTags.DYES);

            //defines the ingredients (the letters in the shape)
            recipe.setIngredient('O', Material.LIGHTNING_ROD);
            recipe.setIngredient('I', Material.IRON_INGOT);
            recipe.setIngredient('R', Material.REDSTONE);
            recipe.setIngredient('A', Material.AMETHYST_SHARD);
            recipe.setIngredient('1', dyes);
            recipe.setIngredient('2', dyes);

            recipes.add(recipe);
        }

        return  recipes;
    }

    public static ItemStack getRadio()
    {
        //creates Item (off of music disk because of minimal use cases)
        final ItemStack radio = ItemStack.of(Material.MUSIC_DISC_13);

        //sets Item visuals
        radio.setData(DataComponentTypes.ITEM_NAME, Component.text("Handheld Radio"));
        radio.setData(DataComponentTypes.ITEM_MODEL, Key.key("arnett", "radio"));

        //sets Item Component Data
        radio.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                        .consumeSeconds(Float.MAX_VALUE)
                        .animation(ItemUseAnimation.TOOT_HORN)
                        .hasConsumeParticles(false)
                        .build());

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
}
