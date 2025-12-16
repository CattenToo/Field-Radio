package arnett.fieldRadio.Items;

import arnett.fieldRadio.FieldRadio;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class Radio {

    public static ShapedRecipe getRecipe()
    {
        ShapedRecipe recipe = new ShapedRecipe(FieldRadio.key, getRadio());

        //makes shape of recipe
        recipe.shape(
                "O I",
                "IAI",
                "IRI"
        );

        //defines the ingredients (the letters in the shape)
        recipe.setIngredient('O', Material.LIGHTNING_ROD);
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('R', Material.REDSTONE);
        recipe.setIngredient('A', Material.AMETHYST_SHARD);

        return  recipe;
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
            pdc.set(FieldRadio.key, PersistentDataType.STRING, "radio");
        });

        return radio;
    }

    public static boolean isHoldingRadio(Player player)
    {
        return isRadio(player.getInventory().getItemInMainHand()) || isRadio(player.getInventory().getItemInOffHand());
    }

    public static boolean isRadio(ItemStack item)
    {
        return Objects.equals(item.getPersistentDataContainer().get(FieldRadio.key, PersistentDataType.STRING), "radio");
    }

    public static boolean hasRadio(Player player) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .anyMatch(Radio::isRadio);
    }
}
