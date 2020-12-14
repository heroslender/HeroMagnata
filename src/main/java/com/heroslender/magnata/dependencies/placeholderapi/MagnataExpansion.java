package com.heroslender.magnata.dependencies.placeholderapi;

import com.heroslender.magnata.Config;
import com.heroslender.magnata.HeroMagnata;
import com.heroslender.magnata.utils.NumberUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class will be registered through the register-method in the
 * plugins onEnable-method.
 */
public class MagnataExpansion extends PlaceholderExpansion {
    private final HeroMagnata plugin;

    public MagnataExpansion(HeroMagnata plugin) {
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    @NotNull
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    @NotNull
    public String getIdentifier() {
        return "heromagnata";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     * <p>
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param player     A {@link org.bukkit.entity.Player Player}.
     * @param identifier A String containing the identifier/value.
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        switch (identifier) {
            case "magnata":
                return plugin.getMagnata();
            case "magnata_money":
                return Double.toString(plugin.getMagnataAccount().join().getMoney());
            case "magnata_money_formatted":
                return NumberUtils.formatShort(plugin.getMagnataAccount().join().getMoney());
            case "magnata_tag":
                if (player != null && player.getName().equals(plugin.getMagnata())) {
                    return Config.TAG_MAGNATA_PAPI;
                }

                return "";
            case "magnata_tag_chat":
                if (player != null && player.getName().equals(plugin.getMagnata())) {
                    return Config.TAG_MAGNATA;
                }

                return "";
            default:
                return null;
        }
    }
}