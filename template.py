from glob import glob
from pathlib import Path
from pathlib import PurePath
from configparser import ConfigParser

IGNORE = [
    ".git",
    ".idea",
    ".gradle",
    "gradle",
    "gradlew",
    "*.bat",
    "template.py"
]

ROOT = Path(__file__).parent
CONFIG = ConfigParser()

TEMPLATE = {
    "mod_id"    : "",
    "mod_name"  : "",
    "mod_class" : "",
    "mod_group" : "",
}

def main():
    read_gitignore()
    parse_properties()

    for file in glob("**/*", recursive=True, root_dir=ROOT):
        if is_ignored(file):
            continue

        path = Path(file)

        if path.is_file():
            apply_file(path)

    apply_path()

def apply(string):
    result = string

    for k in TEMPLATE:
        result = result.replace("{{" + k + "}}", TEMPLATE[k])

    return result

def apply_file(path: Path):
    abspath = ROOT.joinpath(path).resolve()
    contents = abspath.read_text(encoding="utf-8")
    abspath.write_text(apply(contents), encoding="utf-8")

def apply_path():
    abs = lambda path: ROOT.joinpath(path).resolve()

    mod_id    = TEMPLATE["mod_id"]
    mod_class = TEMPLATE["mod_class"]
    mod_group = TEMPLATE["mod_group"]

    Path("src/main/resources/{{mod_id}}.mixins.json").rename(
        f"src/main/resources/{mod_id}.mixins.json")

    Path("src/main/java/github/jodevnull/{{mod_group}}/{{mod_class}}.java").rename(
         "src/main/java/github/jodevnull/{{mod_group}}/" + mod_class + ".java")

    Path("src/main/java/github/jodevnull/{{mod_group}}").rename(
        f"src/main/java/github/jodevnull/{mod_group}")

def is_ignored(path):
    pure = PurePath(path)

    for ignored in IGNORE:
        if path.startswith(ignored) or pure.full_match(ignored):
            return True

    return False

def read_gitignore():
    if Path(".gitignore").exists():
        for line in Path(".gitignore").read_text().splitlines():
            if line.strip() != "" and not line.startswith("#"):
                IGNORE.append(line.strip())

def parse_properties():
    CONFIG.read_string("[template]\n" + Path("gradle.properties").read_text())
    TEMPLATE["mod_id"] = CONFIG.get("template", "mod_id")
    TEMPLATE["mod_name"] = CONFIG.get("template", "mod_name")
    TEMPLATE["mod_class"] = CONFIG.get("template", "mod_class")
    TEMPLATE["mod_group"] = TEMPLATE["mod_class"].lower()

if __name__ == "__main__":
    main()
