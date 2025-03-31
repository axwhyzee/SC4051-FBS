import argparse
from pathlib import Path

from .compilers import COMPILERS
from .compilers.typings import Language


def main():
    parser = argparse.ArgumentParser(
        description=(
            "Compile an .idl file into header file of specified language"
        )
    )
    parser.add_argument(
        "--infile",
        type=str,
        required=True,
        help="Path of .idl file to compile into header file",
    )
    parser.add_argument(
        "--outdir",
        type=str,
        required=True,
        help="Path of directory to output RPC middleware package into",
    )
    parser.add_argument(
        "-l",
        "--lang",
        type=str,
        required=True,
        help="Programming language to compile into",
    )
    args = parser.parse_args()

    root_dir = Path(args.outdir)
    protos_dir = root_dir / "middleware/protos"
    network_dir = root_dir / "middleware/network"

    for dir_ in (protos_dir, network_dir):
        if not dir_.exists():
            dir_.mkdir(parents = True, exist_ok=True)

    lang = Language(args.lang)
    COMPILERS[lang].compile(
        Path(args.infile), protos_dir, root_dir
    )

    if lang == Language.JAVA:
        package = str(network_dir.relative_to(root_dir)).replace("/", ".")
        # copy network template files to out_dir
        for file in (Path("../network") / lang).iterdir():
            print(f"Copying {file} to {network_dir / file.name}")
            with open(network_dir / file.name, "w") as f:
                text = file.read_text()
                # set package path relative to root dir
                if package:
                    text = f"package {package};\n\n" + text
                f.write(text)
