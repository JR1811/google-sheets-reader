
# Google Sheets Reader API Usage

This library allows for easy retrieval of un-opinionated data from a Google Sheet.
To retrieve data you need to specify an API Key, the Sheets ID and a range of cells
which should be read in the sheet.

## Retrieving API Credentials

Google Sheets Reader supports API credentials handling from both a config file and datapacks.
Multiple sheets can be loaded at the same time but those two approaches deliver different functionalities.

### API Credentials From A Config File

No extra config library dependencies are needed for config handling.
Sheets from the config are loaded globally. The retrieved sheets data from those credentials are accessible everywhere
but need to be refreshed manually.

To update a config file at runtime, use the `loadFromFile` method of the `SheetsConfigHandler` class.
You can also overwrite the config file with the `saveToFile` method.

### API Credentials From A Datapack

Credentials which are loaded from a datapack (in `data/sheetsreader/sheets_credentials`) behave more like how normal
Datapack values behave. They are refreshable with the in-game command `/reload` and don't need extra implementations.