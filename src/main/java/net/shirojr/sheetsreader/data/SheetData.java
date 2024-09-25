package net.shirojr.sheetsreader.data;

import java.util.Map;
import java.util.Optional;

public record SheetData(Map<Integer, RowData> sheet) {

    public Optional<RowData> fromRowIndex(int index) {
        for (var entry : sheet().entrySet()) {
            if (entry.getKey() == index) return Optional.of(entry.getValue());
        }
        return Optional.empty();
    }
}
