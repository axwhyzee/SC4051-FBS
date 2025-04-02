from abc import ABC, abstractmethod
from functools import partial
from pathlib import Path
from typing import Callable, cast

from .model import BlockModel, EnumModel, InterfaceModel, StructModel
from .parse import parse_interface_file
from .typings import Block


class BaseCompiler(ABC):
    """
    Abstract base class for compilers.

    Compiles compile an interface definition file (.idl
    file extension) into language-specific proto files
    defining client and server stubs
    """

    @classmethod
    @abstractmethod
    def _handle_struct(
        cls, model: StructModel, out_dir: Path, root_dir: Path
    ) -> None:
        raise NotImplementedError

    @classmethod
    @abstractmethod
    def _handle_enum(
        cls, model: EnumModel, out_dir: Path, root_dir: Path
    ) -> None:
        raise NotImplementedError

    @classmethod
    @abstractmethod
    def _handle_interface(
        cls, model: InterfaceModel, out_dir: Path, root_dir: Path
    ) -> None:
        raise NotImplementedError

    @classmethod
    def compile(cls, in_file: Path, out_dir: Path, root_dir: Path) -> None:
        callbacks = {
            block: cast(
                Callable[[BlockModel], None],
                partial(
                    handler,
                    out_dir=out_dir,
                    root_dir=root_dir,
                ),
            )
            for block, handler in (
                (Block.ENUM, cls._handle_enum),
                (Block.INTERFACE, cls._handle_interface),
                (Block.STRUCT, cls._handle_struct),
            )
        }
        parse_interface_file(in_file, callbacks)
