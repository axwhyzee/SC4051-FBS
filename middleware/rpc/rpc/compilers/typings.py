from enum import Enum
from typing import Dict


class Language(str, Enum):
    JAVA = "java"
    CPP = "cpp"


class Block(str, Enum):
    STRUCT = "struct"
    INTERFACE = "interface"
    ENUM = "enum"


class DType(str, Enum):
    STRING = "string"
    INT = "int"
    BOOL = "bool"
    FLOAT = "float"
    SEQUENCE = "sequence"
