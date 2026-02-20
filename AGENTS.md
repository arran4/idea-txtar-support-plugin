# Agent Instructions

This project uses `README.md` and `plugin.xml` to document features.

## Feature Updates

When implementing a new feature:
1.  **Update `README.md`**: Add a description of the new feature to the "Features" section.
2.  **Update `src/main/resources/META-INF/plugin.xml`**: Ensure the `<description>` tag reflects the current capabilities of the plugin.

## Release Process

When preparing a release or completing a significant task:
1.  **Update `plugin.xml`**: Update the `<change-notes>` section in `src/main/resources/META-INF/plugin.xml` with the latest changes.
    *   **Note:** We do not maintain a separate `CHANGELOG.md` file. All change logs are kept directly in `plugin.xml`.
