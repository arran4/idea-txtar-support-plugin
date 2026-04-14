import sys

files = [
    "src/main/kotlin/com/arran4/txtar/CalculateFileSizeAction.kt",
    "src/main/kotlin/com/arran4/txtar/CopyFileToClipboardAction.kt",
    "src/main/kotlin/com/arran4/txtar/CutFileAction.kt",
    "src/main/kotlin/com/arran4/txtar/ExtractFileAction.kt",
    "src/main/kotlin/com/arran4/txtar/RemoveFileAction.kt"
]

for file_path in files:
    with open(file_path, "r") as f:
        content = f.read()

    # The existing update methods in these files check for `project != null && editor != null && psiFile != null`
    # Let's add a check for `psiFile?.fileType is TxtarFileType`
    if "if (project != null && editor != null && psiFile != null) {" in content:
        content = content.replace(
            "if (project != null && editor != null && psiFile != null) {",
            "if (project != null && editor != null && psiFile != null && psiFile.fileType is TxtarFileType) {"
        )
        with open(file_path, "w") as f:
            f.write(content)
