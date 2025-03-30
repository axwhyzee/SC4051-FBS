from typing import Dict

from .model import Attribute
from .typings import DType


def translate_attr_type(typ: str, dtypes: Dict[str, str]) -> str:
    seqs = typ.count(DType.SEQUENCE)
    typ = typ.rstrip(">").split("<")[-1]
    typ = dtypes.get(typ, typ)
    for _ in range(seqs):
        typ = dtypes[DType.SEQUENCE.value].format(type=typ)
    return typ


def translate_attr(attr: Attribute, dtypes: Dict[str, str]) -> str:
    """
    Translate logical model of an attribute
    (or arg) into Java representation.

    Example:

    ```
    >>> attr = Attribute(type="sequence<sequence<int>>", name="matrix")
    >>> _translate_attr(attr)  # "int[][] matrix"
    ```
    """
    return f"{translate_attr_type(attr.type, dtypes)} {attr.name}"
