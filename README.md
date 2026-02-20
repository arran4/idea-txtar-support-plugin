# Txtar Support

This project provides support for the [txtar](https://pkg.go.dev/golang.org/x/tools/txtar) file format.

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

To build, run:

```bash
./gradlew buildPlugin
```

The archive will be generated in `build/distributions/`. You can then install it via "Install from Disk...".

## Usage

1. Open a `.txtar` file.
2. Right-click in the editor to access the "Txtar" context menu.
3. Use the available actions to manage the content of the archive.
