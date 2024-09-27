package net.shirojr.sheetsreader.data;

import java.util.Map;

public record SheetData(Map<Integer, RowData> rows) {
}
