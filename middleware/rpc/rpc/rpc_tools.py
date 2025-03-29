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
        help="Path of directory to output stubs into",
    )
    parser.add_argument(
        "--rootdir",
        type=str,
        required=True,
        help=(
            "Path of directory where entrypoint resides, "
            "so the correct package path can be derived"
        ),
    )
    parser.add_argument(
        "-l",
        "--lang",
        type=str,
        required=True,
        help="Programming language to compile into",
    )
    args = parser.parse_args()

    COMPILERS[Language(args.lang)].compile(
        Path(args.infile), Path(args.outdir), Path(args.rootdir)
    )
