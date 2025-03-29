from typing import Dict, Type

from .base import BaseCompiler
from .cpp import CPPCompiler
from .java import JavaCompiler
from .typings import Language

COMPILERS: Dict[Language, Type[BaseCompiler]] = {
    Language.JAVA: JavaCompiler,
    Language.CPP: CPPCompiler,
}
