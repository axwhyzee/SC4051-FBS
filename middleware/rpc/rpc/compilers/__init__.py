"""
Compilers take in an interface file and generate language-specific
stubs. 

RPC messages are sent as bytestreams, so the compilers generate 
language-specific marshalling and unmarshalling functions as well
to serialize objects into bytes and vice versa from the bytestream.

The diagram below shows the structure of an RPC message. Numbers in
parentheses represent length of field in bytes.

```
+-----------------+------------------+----------------+
|  METHOD_ID (4)  |  ARG1_LEN (0|4)  |  ARG1_VAL (N)  |
+-----------------+------------------+----------------+
                  |    ... repeat for other args ...  |
                  +------------------+----------------+
```

Details:
* Structs are flattened according to the order by which the attributes
  are defined in the interface file.
* Only variable-length types like strings and sequences have ARG_LEN
  headers.
* Responses use the same METHOD_ID as that in the request.
"""

from typing import Dict, Type

from .base import BaseCompiler
from .cpp import CPPCompiler
from .java import JavaCompiler
from .typings import Language

COMPILERS: Dict[Language, Type[BaseCompiler]] = {
    Language.JAVA: JavaCompiler,
    Language.CPP: CPPCompiler,
}
