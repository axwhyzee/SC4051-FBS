from pathlib import Path

from .base import BaseCompiler
from .model import EnumModel, InterfaceModel, StructModel


class CPPCompiler(BaseCompiler):
    @classmethod
    def _handle_struct(
        cls, model: StructModel, out_dir: Path, out_dir_relative_to: Path
    ) -> None:
        raise NotImplementedError

    @classmethod
    def _handle_enum(
        cls, model: EnumModel, out_dir: Path, out_dir_relative_to: Path
    ) -> None:
        raise NotImplementedError

    @classmethod
    def _handle_interface(
        cls, model: InterfaceModel, out_dir: Path, out_dir_relative_to: Path
    ) -> None:
        raise NotImplementedError
