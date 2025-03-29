from functools import partial
from pathlib import Path
from typing import Dict

from .base import BaseCompiler
from .common import TAB, translate_attr
from .model import EnumModel, InterfaceModel, StructModel
from .typings import DType

TYPES_FILE = "proto_types.h"
STUBS_FILE = "stubs.h"


CPP_DTYPES: Dict[str, str] = {
    DType.STRING.value: "std::string",
    DType.INT.value: "int",
    DType.BOOL.value: "bool",
    DType.FLOAT.value: "float",
    DType.SEQUENCE.value: "std::vector<{type}>",
}

_translate_attr = partial(translate_attr, dtypes=CPP_DTYPES)


class CPPCompiler(BaseCompiler):
    @classmethod
    def _handle_struct(
        cls, model: StructModel, out_dir: Path, out_dir_relative_to: Path
    ) -> None:
        """
        Example:

        ```
        StructModel(
            name="Cube",
            attrs=[("int", "height"), ("int", "width")]
        )
        ```

        Translates into ...

        ```
        // stubs.h

        struct Cube {
            int height;
            int width;
        };
        ```
        """
        code = f"struct {model.name} {{\n"
        code += f";\n".join(
            map(lambda key: f"{TAB}{_translate_attr(key)}", model.attrs)
        )
        code += ";\n};\n\n"
        with open(out_dir / TYPES_FILE, "a") as f:
            f.write(code)

    @classmethod
    def _handle_enum(
        cls, model: EnumModel, out_dir: Path, out_dir_relative_to: Path
    ) -> None:
        """
        Example:

        ```
        enum = EnumModel(name="Color", keys=[RED, BLUE, GREEN])
        ```

        Translates into ...

        ```
        // proto_types.h
        enum Color {
            RED,
            BLUE,
            GREEN
        };
        ```
        """
        code = f"enum {model.name} {{\n"
        code += f",\n".join(map(lambda key: f"{TAB}{key}", model.keys))
        code += "\n};\n\n"
        with open(out_dir / TYPES_FILE, "a") as f:
            f.write(code)

    @classmethod
    def _handle_interface(
        cls, model: InterfaceModel, out_dir: Path, out_dir_relative_to: Path
    ) -> None:
        def create_service_stub() -> str:
            """
            Service stub is implemented by server to handle 
            incoming RPCs
            """

            code = f"class {model.name} {{\n"
            code += "public:\n"
            code += f"{TAB}virtual ~{model.name}() {{}};\n"

            for method in model.methods:
                code += f"{TAB}virtual {method.ret_type} "
                code += f"{method.name}("
                code += ", ".join(
                    [_translate_attr(attr) for attr in method.args]
                )
                code += ") = 0;\n"
            code += "};\n\n"
            return code

        def create_client_stub() -> str:
            """
            Stub will be called by client to make RPCs
            """

            code = ""
            code += f"class {model.name}Stub {{\n"
            code += "public:\n"
            code += f"{TAB}{model.name}Stub();\n"
            code += f"{TAB}~{model.name}Stub() {{}};\n"

            for method in model.methods:
                code += f"{TAB}{method.ret_type} "
                code += f"{method.name}("
                code += ", ".join(
                    [_translate_attr(attr) for attr in method.args]
                )
                code += (
                    ") {/* TODO: marshall and send to server via UDP */};\n"
                )
            code += "};\n\n"
            return code

        with open(out_dir / STUBS_FILE, "a") as f:
            f.write(create_service_stub())
            f.write(create_client_stub())


    @classmethod
    def compile(
        cls, in_file: Path, out_dir: Path, out_dir_relative_to: Path
    ) -> None:
        # create header files
        with open(Path(out_dir / TYPES_FILE), "w") as f:
            f.write("#include <string>\n#include <vector>\n\n")
        with open(Path(out_dir / STUBS_FILE), "w") as f:
            f.write("#include \"proto_types.h\"\n\n")
        super().compile(in_file, out_dir, out_dir_relative_to)
