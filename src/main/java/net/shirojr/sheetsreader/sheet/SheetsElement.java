package net.shirojr.sheetsreader.sheet;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record SheetsElement(@NotNull Identifier id, @Nullable String name, @Nullable String restriction,
                            @Nullable String reason, @Nullable String magic) {


    public static List<SheetsElement> fromNbt(NbtCompound nbt) {
        List<SheetsElement> elementList = new ArrayList<>();

        NbtList elementsNbt = nbt.getList("elements", NbtElement.COMPOUND_TYPE);
        for (var entry : elementsNbt) {
            NbtCompound entryCompound = (NbtCompound) entry;
            String name = null, restriction = null, reason = null, magic = null;
            Identifier identifier = new Identifier(entryCompound.getString("identifier"));

            if (!entryCompound.contains("identifier")) continue;

            if (entryCompound.contains("name")) name = entryCompound.getString("name");
            if (entryCompound.contains("restriction")) restriction = entryCompound.getString("restriction");
            if (entryCompound.contains("reason")) reason = entryCompound.getString("reason");
            if (entryCompound.contains("magic")) magic = entryCompound.getString("magic");

            elementList.add(new SheetsElement(identifier, name, restriction, reason, magic));
        }

        return elementList;
    }

    public static NbtCompound toNbt(List<SheetsElement> elements, NbtCompound nbt) {
        NbtList list = new NbtList();
        for (SheetsElement element : elements) {
            NbtCompound elementsCompound = new NbtCompound();
            elementsCompound.putString("identifier", element.id.toString());
            if (element.name != null) elementsCompound.putString("name", element.name);
            if (element.restriction != null) elementsCompound.putString("restriction", element.restriction);
            if (element.reason != null) elementsCompound.putString("reason", element.reason);
            if (element.magic != null) elementsCompound.putString("magic", element.magic);
            list.add(elementsCompound);
        }

        nbt.put("elements", list);
        return nbt;
    }
}
