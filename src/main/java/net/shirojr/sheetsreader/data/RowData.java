package net.shirojr.sheetsreader.data;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @param cells index - cell content
 */
public record RowData(Map<Integer, CellData> cells) {
    public RowData() {
        this(new HashMap<>());
    }

    public Map<Integer, Object> getCellContent() {
        return cells().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, test -> test.getValue().getEffectiveValue()));
    }

    public Map<Integer, CellFormat> getCellFormat() {
        return cells().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, test -> test.getValue().getEffectiveFormat()));
    }
}
