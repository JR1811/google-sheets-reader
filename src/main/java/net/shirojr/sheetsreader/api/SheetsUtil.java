package net.shirojr.sheetsreader.api;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.sheetsreader.SheetsReader;

import java.util.Optional;

@SuppressWarnings("unused")
public class SheetsUtil {
    /**
     * Get valid Item Identifier if the input exists in the registry
     *
     * @param id String of an item id tag (e.g. <code>minecraft:stick</code>)
     * @return valid Identifier of an Item or Null
     */
    public static Optional<Item> getValidItemFromString(String id) {
        if (!id.contains(":")) {
            SheetsReader.LOGGER.error("Couldn't find namespace of given Item ID");
            return Optional.empty();
        }
        Identifier identifier = new Identifier(id);
        if (!Registry.ITEM.containsId(identifier)) return Optional.empty();
        return Optional.of(Registry.ITEM.get(identifier));
    }
}
