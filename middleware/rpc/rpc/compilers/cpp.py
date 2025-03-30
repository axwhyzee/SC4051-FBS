from functools import partial
from pathlib import Path
from typing import Dict

from .base import BaseCompiler
from .common import TAB, translate_attr
from .model import EnumModel, InterfaceModel, StructModel
from .typings import DType

TYPES_FILE = "proto_types.h"
STUBS_FILE = "stubs.h"
MARSHALLING_FILE = "marshalling.h"
UNMARSHALLING_FILE = "unmarshalling.h"


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
        // proto_types.h
        struct Cube {
            int height;
            int width;
        };

        // unmarshalling.h
        Cube unmarshall_Cube(char* message, int& i, int len) {...}

        // marshalling.h
        void marshall_Cube(char* message, int& i) {...}
        ```
        """
        def create_type():
            code = f"struct {model.name} {{\n"
            code += f";\n".join(
                map(lambda key: f"{TAB}{_translate_attr(key)}", model.attrs)
            )
            code += ";\n};\n\n"
            with open(out_dir / TYPES_FILE, "a") as f:
                f.write(code)

        def create_unmarshalling():
            code = f"{model.name} unmarshall_{model.name}(char* message, int& i, int len) {{\n"
            code += f"{TAB}int attr_len;\n"
            code += f"{TAB}{model.name} {model.name}_struct;\n"

            for attr in model.attrs:
                code += f"{TAB}attr_len = unmarshall_int(message, i, LEN_SIZE);\n"
                attr_type = attr.type
                
                if attr_type.startswith("sequence"):
                    # vector<...>
                    nested_type = attr_type.rstrip(">").split("sequence<", 1)[1]
                    code += f"{TAB}std::vector<{nested_type}> temp_{attr.name} = std::vector<{nested_type}>();\n"
                    code += f"{TAB}for (int j=0; j<attr_len; j++) {{\n"
                    code += f"{TAB*2}temp_{attr.name}.push_back(unmarshall_{nested_type}(message, i, attr_len));\n"
                    code += f"{TAB}}}\n"
                    code += f"{TAB}{model.name}_struct.{attr.name} = temp_{attr.name};\n"
                else:
                    # structs and primitives
                    code += f"{TAB}{model.name}_struct.{attr.name} = unmarshall_{attr_type}(message, i, attr_len);\n"         
            
            code += f"{TAB}return {model.name}_struct;\n"
            code += "}\n\n"
            with open(out_dir / UNMARSHALLING_FILE, "a") as f:
                f.write(code)

        create_type()
        create_unmarshalling()


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

        def create_type():
            code = f"enum {model.name} {{\n"
            code += f",\n".join(map(lambda key: f"{TAB}{key}", model.keys))
            code += "\n};\n\n"
            with open(out_dir / TYPES_FILE, "a") as f:
                f.write(code)

        def create_unmarshalling():
            code = f"{model.name} unmarshall_{model.name}(char* message, int& i, int len) {{\n"
            code += f"{TAB}char enum_id = message[i];\n"
            code += f"{TAB}switch (enum_id) {{\n"
            
            for i, key in enumerate(model.keys, start=1):
                code += f"{TAB*2}case {i}:\n"
                code += f"{TAB*3}return ({model.name}) {key};\n"
            code += f"{TAB}}}\n"
            code += "}\n\n"
            with open(out_dir / UNMARSHALLING_FILE, "a") as f:
                f.write(code)

        create_type()
        create_unmarshalling()


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
        # copy templates
        for file in Path("templates/cpp").iterdir():
            (out_dir / file.name).write_text(file.read_text())        
        super().compile(in_file, out_dir, out_dir_relative_to)
