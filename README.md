# Capacitor File Picker

## Description

Presents the device's native file picking ui and returns the selected file's uri.

## Installation

- `npm i https://github.com/xelits/capacitor-file-picker.git`

## Usage

```ts
import { Plugins } from "@capacitor/core";

const { FilePicker } = Plugins;

FilePicker.showFilePicker({
  fileTypes: ["image/*", "application/pdf"],
}).then(
  (fileResult: FilePickerResult) => {
    const fileUri = fileResult.uri;
    const fileName = fileResult.name;
    const fileMimeType = fileResult.mimeType;
    const fileExtension = fileResult.extension;
    const fileSize = fileResult.size;
  },
  (error) => {
    console.log(error);
  }
);
```

## Methods

| Method                                         | Default | Type                        | Description                                                                       |
| ---------------------------------------------- | ------- | --------------------------- | --------------------------------------------------------------------------------- |
| showFilePicker(options: {fileTypes: string[], uploadType: string)}) // uploadType = "1"(to get base64 string with file path) or "2"- only file path |         | `Promise<FilePickerResult>` | Presents the device's native file picking ui and returns the selected file's uri. |

## Interfaces

FilePickerResult

| Properties | Default | Type     | Description                               |
| ---------- | ------- | -------- | ----------------------------------------- |
| uri        |         | `string` | Uri string pointing to the selected file. |
| name       |         | `string` | The name of the selected file.            |
| mimeType   |         | `string` | The MIME type of the selected file.       |
| extension  |         | `string` | The extension of the selected file.       |
| size       |         | `string` | The size of the selected file.            |


## Android

Register the plugin by adding it to your MainActivity's onCreate:

```java
import com.epicshaggy.filepicker.FilePicker;

public class MainActivity extends BridgeActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Initializes the Bridge
    this.init(savedInstanceState, new ArrayList<Class<? extends Plugin>>() {{
      // Additional plugins you've installed go here
      // Ex: add(TotallyAwesomePlugin.class);
      add(FilePicker.class);
    }});
  }
}
```

## Notes

The file picker only accpets:

- application/pdf
- image/\*

This is because it was developed to meet the need to meet a specific need, but feel free to contribute to the plugin's development. :)
