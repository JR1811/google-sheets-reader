package net.shirojr.sheetsreader.api;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.sheetsreader.SheetsReader;
import net.shirojr.sheetsreader.data.SheetData;
import net.shirojr.sheetsreader.data.datapack.SheetsDatapackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
public class SheetsUtil {
    /**
     * Get valid Item Identifier if the input exists in the registry
     *
     * @param id String of an item id tag (e.g. <code>minecraft:stick</code>)
     * @return valid Identifier of an Item or Null
     */
    public static Optional<Identifier> getValidItemFromString(@Nullable String id) {
        if (id == null || !id.contains(":")) {
            SheetsReader.LOGGER.error("Couldn't find namespace of given Item ID");
            return Optional.empty();
        }
        Identifier identifier = new Identifier(id);
        if (!Registry.ITEM.containsId(identifier)) return Optional.empty();
        return Optional.of(identifier);
    }

    public static Optional<SheetData> getSheet(Identifier identifier) {
        return Optional.ofNullable(getAllSheets().get(identifier));
    }

    public static Map<Identifier, SheetData> getAllSheets() {
        Map<Identifier, SheetData> map = new HashMap<>();
        map.put(new Identifier(SheetsReader.MODID, "config.json"), DataHolder.CONFIG_SHEET);
        map.putAll(DataHolder.DATAPACK_SHEETS);
        return map;
    }

    public static Identifier getSheetIdentifier(String name) {
        return new Identifier(SheetsReader.MODID, "%s/%s".formatted(SheetsDatapackHandler.directory, name));
    }
}
