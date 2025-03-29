from dataclasses import dataclass
from typing import List


@dataclass
class Attribute:
    type: str
    name: str


@dataclass
class BlockModel:
    name: str


@dataclass
class EnumModel(BlockModel):
    keys: List[str]


@dataclass
class StructModel(BlockModel):
    attrs: List[Attribute]


@dataclass
class Method:
    name: str
    ret_type: str
    args: List[Attribute]


@dataclass
class InterfaceModel(BlockModel):
    methods: List[Method]
