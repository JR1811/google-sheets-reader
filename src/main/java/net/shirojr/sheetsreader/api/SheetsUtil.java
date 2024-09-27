package net.shirojr.sheetsreader.api;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class SheetsUtil {
    /**
     * Get valid Item Identifier if the input exists in the registry
     *
     * @param id String of an item id tag (e.g. <code>minecraft:stick</code>)
     * @return valid Identifier of an Item or Null
     */
    public static Optional<Identifier> getValidIdFromString(String id) {
        Identifier identifier = new Identifier(id);
        if (!Registry.ITEM.containsId(identifier)) return Optional.empty();
        return Optional.of(identifier);
    }
}
