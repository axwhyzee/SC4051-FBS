from functools import partial
from pathlib import Path
from typing import Dict

from .base import BaseCompiler
from .common import translate_attr
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


def _get_nested_type(attr_type: str):
    return attr_type.rstrip(">").split("<", 1)[1]

def _is_variable_length(attr_type: str):
    return attr_type == DType.STRING or attr_type.startswith(DType.SEQUENCE.value)


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
                map(lambda key: f"\t{_translate_attr(key)}", model.attrs)
            )
            code += ";\n};\n\n"
            with open(out_dir / TYPES_FILE, "a") as f:
                f.write(code)

        def create_unmarshalling():
            code = f"{model.name} unmarshall_{model.name}(char* message, int& i) {{\n"
            code += f"\t{model.name} {model.name}_struct;\n"

            for attr in model.attrs:
                attr_type = attr.type

                if attr_type.startswith(DType.SEQUENCE.value):
                    # iteratively marshall sequence items
                    nested_type = _get_nested_type(attr_type)
                    code += f"\tint {attr.name}_len = unmarshall_int(message, i);\n"
                    code += f"\tstd::vector<{nested_type}> temp_{attr.name} = std::vector<{nested_type}>();\n"
                    code += f"\tfor (int j=0; j<{attr.name}_len; j++)\n"
                    code += f"\t\ttemp_{attr.name}.push_back(unmarshall_{nested_type}(message, i));\n"
                    code += f"\t{model.name}_struct.{attr.name} = temp_{attr.name};\n"
                else:
                    # structs and fixed-length primitives
                    code += f"\t{model.name}_struct.{attr.name} = unmarshall_{attr_type}(message, i);\n"         
            
            code += f"\treturn {model.name}_struct;\n"
            code += "}\n\n"
            with open(out_dir / UNMARSHALLING_FILE, "a") as f:
                f.write(code)

        def create_marshalling():
            code = f"{model.name} marshall_{model.name}(char* message, int& i, {model.name} val) {{\n"
            for attr in model.attrs:                
                if attr.type.startswith(DType.SEQUENCE.value):
                    # sequences
                    nested_type = _get_nested_type(attr.type)
                    code += f"\t_marshall_len_header(message, i, val.{attr.name}.size());\n"
                    code += f"\tfor (int j=0; j<val.{attr.name}.size(); j++)\n"
                    code += f"\t\tmarshall_{nested_type}(message, i, val.{attr.name}[j]);\n"
                else:
                    # non-sequences
                    if attr.type == DType.STRING:
                        code += f"\t_marshall_len_header(message, i, val.{attr.name}.length());\n"
                    code += f"\tmarshall_{attr.type}(message, i, val.{attr.name});\n"
            code += "}\n\n"
            with open(out_dir / MARSHALLING_FILE, "a") as f:
                f.write(code)

        create_type()
        create_marshalling()
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
            RED=1,
            BLUE=2,
            GREEN=3
        };
        ```
        """

        def create_type():
            code = f"enum {model.name} {{\n"
            for i, key in enumerate(model.keys, start=1):
                code += f"{key}={i},\n"
            code += "\n};\n\n"
            with open(out_dir / TYPES_FILE, "a") as f:
                f.write(code)

        def create_unmarshalling():
            code = f"{model.name} unmarshall_{model.name}(char* message, int& i) {{\n"
            code += f"\tchar enum_id = message[i];\n"
            code += f"\tswitch (enum_id) {{\n"
            
            for i, key in enumerate(model.keys, start=1):
                code += f"\t\tcase {i}:\n"
                code += f"\t\t\treturn ({model.name}) {key};\n"
            code += f"\t}}\n"
            code += "}\n\n"
            with open(out_dir / UNMARSHALLING_FILE, "a") as f:
                f.write(code)


        def create_marshalling():
            code = f"void marshall_{model.name}(char* message, int& i, {model.name} val) {{\n"
            code += "\tmarshall_int(message, i, (int)val);\n"
            code += "}\n\n"
            with open(out_dir / MARSHALLING_FILE, "a") as f:
                f.write(code)

        create_type()
        create_unmarshalling()
        create_marshalling()


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
            code += f"\tvirtual ~{model.name}() {{}};\n"

            for method in model.methods:
                code += f"\tvirtual {method.ret_type} "
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
            code += f"\t{model.name}Stub();\n"
            code += f"\t~{model.name}Stub() {{}};\n"

            for method in model.methods:
                code += f"\t{method.ret_type} "
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
