import os
import re

GRADLE_PROPERTIES = 'gradle.properties'
PLUGIN_XML = 'src/main/resources/META-INF/plugin.xml'
CHANGELOG = 'CHANGELOG.md'

def get_current_version():
    with open(GRADLE_PROPERTIES, 'r') as f:
        content = f.read()
    match = re.search(r'pluginVersion=(.*)', content)
    if match:
        return match.group(1).strip()
    return None

def bump_version(version):
    # Remove -next or other suffixes if present to find base version
    base_version = version.split('-')[0]
    parts = base_version.split('.')
    if len(parts) >= 3:
        parts[-1] = str(int(parts[-1]) + 1)
    elif len(parts) > 0:
        parts[-1] = str(int(parts[-1]) + 1)
    else:
        parts = ['1', '0', '0'] # Default fallback

    new_version = ".".join(parts) + "-next"
    return new_version

def update_gradle_properties(new_version):
    with open(GRADLE_PROPERTIES, 'r') as f:
        lines = f.readlines()

    with open(GRADLE_PROPERTIES, 'w') as f:
        for line in lines:
            if line.startswith('pluginVersion='):
                f.write(f'pluginVersion={new_version}\n')
            else:
                f.write(line)

def update_plugin_xml(new_version):
    if not os.path.exists(PLUGIN_XML):
        print(f"Warning: {PLUGIN_XML} not found.")
        return

    with open(PLUGIN_XML, 'r') as f:
        content = f.read()

    # check if version tag exists
    if '<version>' in content:
        content = re.sub(r'<version>.*?</version>', f'<version>{new_version}</version>', content)
    else:
        # Insert after <id>
        # Assuming <id>...</id> exists
        if '<id>' in content:
            content = re.sub(r'(<id>.*?</id>)', f'\\1\n    <version>{new_version}</version>', content)
        else:
            # Insert inside <idea-plugin> if id is missing (unlikely)
            content = content.replace('<idea-plugin>', f'<idea-plugin>\n    <version>{new_version}</version>')

    with open(PLUGIN_XML, 'w') as f:
        f.write(content)

def clear_changelog():
    with open(CHANGELOG, 'w') as f:
        f.write("- Unreleased\n")

def main():
    if not os.path.exists(GRADLE_PROPERTIES):
        print(f"Error: {GRADLE_PROPERTIES} not found.")
        return

    current_version = get_current_version()
    if not current_version:
        print("Could not find pluginVersion in gradle.properties")
        return

    new_version = bump_version(current_version)
    print(f"Bumping version from {current_version} to {new_version}")

    update_gradle_properties(new_version)
    update_plugin_xml(new_version)
    clear_changelog()
    print("Version bumped and changelog cleared.")

if __name__ == "__main__":
    main()
