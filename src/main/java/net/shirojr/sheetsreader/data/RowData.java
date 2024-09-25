package net.shirojr.sheetsreader.data;

import net.minecraft.nbt.NbtCompound;
import net.shirojr.sheetsreader.util.NbtKeys;

import java.util.*;

public record RowData(Map<Integer, String> cell) {
    public RowData() {
        this(new HashMap<>());
    }

    public int getRowSize() {
        return cell().size();
    }


    public static Optional<RowData> fromNbt(NbtCompound nbt) {
        if (!nbt.contains(NbtKeys.ROW_DATA)) return Optional.empty();
        NbtCompound contentNbt = nbt.getCompound(NbtKeys.ROW_DATA);

        Map<Integer, String> contentList = new HashMap<>();
        for (String key : contentNbt.getKeys()) {
            contentList.put(Integer.valueOf(key), contentNbt.getString(key));
        }
        return Optional.of(new RowData(contentList));
    }

    public static List<RowData> allRowsFromNbt(NbtCompound nbt) {
        if (!nbt.contains(NbtKeys.ROWS)) return List.of();
        NbtCompound rowsCompound = nbt.getCompound(NbtKeys.ROWS);

        List<RowData> rows = new ArrayList<>();
        for (String key : rowsCompound.getKeys()) {
            Optional<RowData> rowData = fromNbt(rowsCompound.getCompound(key));
            if (rowData.isEmpty()) continue;
            rows.add(rowData.get());
        }
        return rows;
    }

    public static void toNbt(RowData row, NbtCompound nbt) {
        NbtCompound contentNbt = new NbtCompound();
        for (var entry : row.cell().entrySet()) {
            contentNbt.putString(String.valueOf(entry.getKey()), entry.getValue());
        }
        for (int i = 0; i < row.cell().size(); i++) {
            String entry = row.cell().get(i);
            contentNbt.putString(String.valueOf(i), entry);
        }
        nbt.put(NbtKeys.ROW_DATA, contentNbt);
    }

    public static void allRowsToNbt(List<RowData> rows, NbtCompound nbt) {
        NbtCompound contentNbt = new NbtCompound();
        for (RowData row : rows) {
            toNbt(row, contentNbt);
        }
        nbt.put(NbtKeys.ROWS, contentNbt);
    }
}
