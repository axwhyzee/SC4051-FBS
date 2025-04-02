import re
from abc import ABC, abstractmethod
from pathlib import Path
from typing import Callable, Dict, List, Type

from .model import (
    Attribute,
    BlockModel,
    EnumModel,
    InterfaceModel,
    Method,
    StructModel,
)
from .typings import Block


def _split_strip(s: str, *args, drop_last: bool = False, **kwargs):
    parts = [part.strip() for part in s.split(*args, **kwargs)]
    if drop_last:
        return parts[:-1]
    return parts


def _parse_attr(s: str) -> Attribute:
    typ, name = _split_strip(s)
    return Attribute(type=typ, name=name)


class AbstractParser(ABC):
    @classmethod
    @abstractmethod
    def parse(cls, name: str, lines: List[str]) -> BlockModel:
        raise NotImplementedError


class EnumParser(AbstractParser):
    @classmethod
    def parse(cls, name: str, lines: List[str]) -> BlockModel:
        return EnumModel(name=name, keys=lines)


class StructParser(AbstractParser):
    @classmethod
    def parse(cls, name: str, lines: List[str]) -> BlockModel:
        return StructModel(
            name=name, attrs=[_parse_attr(attr) for attr in lines]
        )


class InterfaceParser(AbstractParser):
    
    method_counter = 1

    @classmethod
    def parse(cls, name: str, lines: List[str]) -> BlockModel:
        methods = []
        for line in lines:
            if not (match := re.match(r"([\w<>]+)\s+(\w+)\s*\(([^\)]*)\)", line)):
                raise
            ret_type, f_name, args = match.groups()
            methods.append(
                Method(
                    id=cls.method_counter,
                    name=f_name,
                    ret_type=ret_type,
                    args=(
                        [_parse_attr(arg) for arg in _split_strip(args, ",")]
                        if args
                        else []
                    )
                )
            )
            cls.method_counter += 1
        return InterfaceModel(name=name, methods=methods)


PARSERS: Dict[Block, Type[AbstractParser]] = {
    Block.ENUM: EnumParser,
    Block.STRUCT: StructParser,
    Block.INTERFACE: InterfaceParser,
}


def parse_interface_file(
    path: Path, callbacks: Dict[Block, Callable[[BlockModel], None]]
) -> None:
    block_types = "|".join([block.value for block in Block])
    pattern = rf"({block_types})\s+(\w+)\s*{{([^}}]+)}}"
    matches = re.findall(pattern, path.read_text(), re.DOTALL)

    for match in matches:
        block_type_str, block_name, block_body = match
        block_type = Block(block_type_str)
        block_model = PARSERS[block_type].parse(
            block_name, _split_strip(block_body, ";", drop_last=True)
        )
        callbacks[block_type](block_model)
