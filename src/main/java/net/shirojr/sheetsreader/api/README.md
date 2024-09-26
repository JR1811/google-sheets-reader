
# Google Sheets Reader API Usage

This library allows for easy retrieval of un-opinionated data from a Google Sheet.
To retrieve data you need to use specific credentials.

- API Key
- Sheets ID
- range of cells

## Retrieving API Credentials

Google Sheets Reader supports API credentials handling from both a config file and datapacks.
Those two approaches deliver different functionalities and use cases but both need the file with the same content.

```json
{
  "apiKey": "1234567890-_ABC",
  "sheetsId": "1234567890-_ABC",
  "range": "'Example table'!C4:G12"
}
```

### API Credentials From A Config File

No extra config library dependencies are needed for config handling.
A Sheet from the config is loaded globally and only one at a time can exist.
The retrieved credentials are accessible everywhere (even when not loaded in a world) but need to be refreshed manually.

Technically it is advised to only use the retrieved data on the logical server side, but it should also be possible
to store and then retrieve the data on the client side too. Just keep in mind that there is no custom S2C or C2S syncing 
in place for that to keep the size of this lib to a minimum.

To update a config file at runtime, use the `loadFromFile` method of the `SheetsConfigHandler` class.
You can also overwrite the config file with the `saveToFile` method.

The API call might block the current thread so if the API call takes a long time the game could potentially freeze.
As a rule of thumb, don't reload the config at run-time if the incoming sheet data is huge.

### API Credentials From A Datapack

Credentials which are loaded from a datapack (in `data/sheetsreader/sheets_credentials`) behave more like how normal
Datapack values behave. They are refreshable with the in-game command `/reload` and don't need extra implementations.
They are only loaded on the logical server side and exist only while a world is loaded.

Using multiple files in the specified datapack directory, you can load multiple sheets at the same time.
The `.json` file's name will be used to refer to the sheet data at runtime, so make sure to use unique names.
If two files with the same name are found, the one which is loaded the latest will define the sheet data since it makes
use of a `HashMap` datatype.

For an example check out the [example.json](../../../../../resources/data/sheetsreader/sheet_credentials/example.json)
file and where it's located at.

The api call is done async, so if you reload datapacks the current thread won't be blocked. Your console will notify
you about a successfully finished API call's data retrieval.

## Accessing `SheetsData`

To get the `SheetsData`, which your credentials retrieved, check out the [DataHolder](./DataHolder.java) class to get 
access to the `SheetData`. `SheetData` consists of multiple `RowData` objects, which contains the cells 
data as `String` values. Empty rows and cells are skipped so make use of the index which are stored together with them.