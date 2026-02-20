# Txtar Support Plugin

<p align="center">
    <img src="pluginIcon.svg" alt="Txtar Support Logo" width="128" height="128">
</p>

This is an IntelliJ IDEA plugin that provides support for the [txtar](https://pkg.go.dev/golang.org/x/tools/txtar) file format.

## Status

The project is currently in active development. Basic support for the `txtar` format has been implemented and the project builds successfully.

## Features

- **File Type Recognition**: Automatically recognizes `.txtar` files.
- **Syntax Highlighting**: Basic syntax highlighting for `txtar` archives.
- **Folding**: Code folding support for file entries in the archive.
- **Editor Actions**:
  - **Append New File**: Quickly add a new file entry to the archive.
  - **Append File...**: Append the content of an external file to the archive.
  - **Remove File**: Remove the file entry at the current caret position.
  - **Append New File from Clipboard**: Paste the clipboard content as a new file entry.

## Build and Install

<div id="marketplace-install-widget"></div>
<script src="https://plugins.jetbrains.com/assets/scripts/mp-widget.js"></script>
<script>
  MarketplaceWidget.setupMarketplaceWidget('install', 30286, "#marketplace-install-widget");
</script>

To build the plugin, run:

```bash
./gradlew buildPlugin
```

The plugin archive will be generated in `build/distributions/`. You can then install it in IntelliJ IDEA via "Install Plugin from Disk...".

## Usage

1. Open a `.txtar` file in IntelliJ IDEA.
2. Right-click in the editor to access the "Txtar" context menu.
3. Use the available actions to manage the content of the archive.

## License

This project is licensed under the BSD 3-Clause License - see the [LICENSE](LICENSE) file for details.
