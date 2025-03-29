from typing import Dict

from .model import Attribute
from .typings import DType

TAB = " " * 4


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
    typ = attr.type
    seqs = typ.count(DType.SEQUENCE)
    typ = typ.rstrip(">").split("<")[-1]
    typ = dtypes.get(typ, typ)
    for _ in range(seqs):
        typ = dtypes[DType.SEQUENCE.value].format(type=typ)
    return f"{typ} {attr.name}"
