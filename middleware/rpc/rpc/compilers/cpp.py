from functools import partial
from pathlib import Path
from typing import Dict

from .base import BaseCompiler
from .common import (
    get_nested_type,
    is_sequence,
    translate_attr,
    translate_attr_type,
)
from .model import EnumModel, InterfaceModel, StructModel
from .typings import DType

TEMPLATE_DIR = Path("rpc/templates/cpp")
TYPES_FILE = "proto_types.h"

# files to write to
STUBS_HEADER_FILE = "stubs.h"
STUBS_CPP_FILE = "stubs.cpp"
MARSHALLING_HEADER_FILE = "marshalling.h"
MARSHALLING_CPP_FILE = "marshalling.cpp"
UNMARSHALLING_HEADER_FILE = "unmarshalling.h"
UNMARSHALLING_CPP_FILE = "unmarshalling.cpp"

# templates files to read from
TEMPLATE_SERVICER_CPP_FILE = "_servicer.cpp"
TEMPLATE_SERVICER_HEADER_FILE = "_servicer.h"



CPP_DTYPES: Dict[str, str] = {
    DType.STRING.value: "std::string",
    DType.INT.value: "int",
    DType.BOOL.value: "bool",
    DType.FLOAT.value: "float",
    DType.SEQUENCE.value: "std::vector<{type}>",
}

_translate_attr = partial(translate_attr, dtypes=CPP_DTYPES)
_translate_attr_type = partial(translate_attr_type, dtypes=CPP_DTYPES)


class CPPCompiler(BaseCompiler):

    method_counter = 1

    @classmethod
    def _handle_struct(
        cls, model: StructModel, out_dir: Path, root_dir: Path
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

        def create_unmarshalling_header():
            code = f"{model.name} unmarshall_{model.name}(char* message, int& i);\n\n"
            with open(out_dir / UNMARSHALLING_HEADER_FILE, "a") as f:
                f.write(code)


        def create_unmarshalling():
            code = f"{model.name} unmarshall_{model.name}(char* message, int& i) {{\n"
            code += f"\t{model.name} {model.name}__struct;\n"

            for attr in model.attrs:
                attr_type = attr.type
                translated_type = _translate_attr_type(attr.type)

                if is_sequence(attr_type):
                    # iteratively marshall sequence items
                    nested_type = get_nested_type(attr_type)
                    code += f"\tint {attr.name}__len = unmarshall_int(message, i);\n"
                    code += f"\t{translated_type} temp__{attr.name} = {translated_type}();\n"
                    code += f"\tfor (int j=0; j<{attr.name}__len; j++)\n"
                    code += f"\t\ttemp__{attr.name}.push_back(unmarshall_{nested_type}(message, i));\n"
                    code += f"\t{model.name}__struct.{attr.name} = temp__{attr.name};\n"
                else:
                    # structs and fixed-length primitives
                    code += f"\t{model.name}__struct.{attr.name} = unmarshall_{attr_type}(message, i);\n"

            code += f"\treturn {model.name}__struct;\n"
            code += "}\n\n"
            with open(out_dir / UNMARSHALLING_CPP_FILE, "a") as f:
                f.write(code)

        def create_marshalling_header():
            code = f"void marshall_{model.name}(char* message, int& i, {model.name} val);\n\n"
            with open(out_dir / MARSHALLING_HEADER_FILE, "a") as f:
                f.write(code)

        def create_marshalling():
            code = f"void marshall_{model.name}(char* message, int& i, {model.name} val) {{\n"
            for attr in model.attrs:
                if is_sequence(attr.type):
                    # sequences
                    nested_type = get_nested_type(attr.type)
                    code += f"\tmarshall_len_header(message, i, val.{attr.name}.size());\n"
                    code += f"\tfor (int j=0; j<val.{attr.name}.size(); j++)\n"
                    code += f"\t\tmarshall_{nested_type}(message, i, val.{attr.name}[j]);\n"
                else:
                    # non-sequences
                    if attr.type == DType.STRING:
                        code += f"\tmarshall_len_header(message, i, val.{attr.name}.length());\n"
                    code += f"\tmarshall_{attr.type}(message, i, val.{attr.name});\n"
            code += "}\n\n"
            with open(out_dir / MARSHALLING_CPP_FILE, "a") as f:
                f.write(code)

        create_type()
        create_unmarshalling_header()
        create_unmarshalling()
        create_marshalling_header()
        create_marshalling()

    @classmethod
    def _handle_enum(
        cls, model: EnumModel, out_dir: Path, root_dir: Path
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
                code += f"\t{key}={i},\n"
            code += "};\n\n"
            with open(out_dir / TYPES_FILE, "a") as f:
                f.write(code)

        def create_unmarshalling_header():
            code = f"{model.name} unmarshall_{model.name}(char* message, int& i);\n\n"
            with open(out_dir / UNMARSHALLING_HEADER_FILE, "a") as f:
                f.write(code)


        def create_unmarshalling():
            code = f"{model.name} unmarshall_{model.name}(char* message, int& i) {{\n"
            code += f"\tchar enum_id = unmarshall_int(message, i);\n"
            code += f"\tswitch (enum_id) {{\n"

            for i, key in enumerate(model.keys, start=1):
                code += f"\t\tcase {i}:\n"
                code += f"\t\t\treturn ({model.name}) {key};\n"
            code += "\t\tdefault:\n"
            code += '\t\t\tthrow std::runtime_error("Unrecognized enum_id" + std::to_string(enum_id));\n'
            code += f"\t}}\n"
            code += "}\n\n"
            with open(out_dir / UNMARSHALLING_CPP_FILE, "a") as f:
                f.write(code)

        def create_marshalling_header():
            code = f"void marshall_{model.name}(char* message, int& i, {model.name} val);\n\n"
            with open(out_dir / MARSHALLING_HEADER_FILE, "a") as f:
                f.write(code)

        def create_marshalling():
            code = f"void marshall_{model.name}(char* message, int& i, {model.name} val) {{\n"
            code += "\tmarshall_int(message, i, (int)val);\n"
            code += "}\n\n"
            with open(out_dir / MARSHALLING_CPP_FILE, "a") as f:
                f.write(code)

        create_type()
        create_unmarshalling_header()
        create_unmarshalling()
        create_marshalling_header()
        create_marshalling()

    @classmethod
    def _handle_interface(
        cls, model: InterfaceModel, out_dir: Path, root_dir: Path
    ) -> None:
        def create_service_stub_header() -> str:
            """
            Service stub is implemented by server to handle
            incoming RPCs
            """

            code = f"class {model.name} {{\n"
            code += "public:\n"
            code += f"\tvirtual ~{model.name}() {{}};\n"

            for method in model.methods:
                code += f"\tvirtual {_translate_attr_type(method.ret_type)} "
                code += f"{method.name}("
                code += ", ".join(
                    [_translate_attr(attr) for attr in method.args] + ["sockaddr_in client_addr"]
                )
                code += ") = 0;\n"
            code += "};\n\n"
            return code

        def create_client_stub_header() -> str:
            """
            Stub will be called by client to make RPCs
            """

            code = f"class {model.name}Stub {{\n"
            code += "public:\n"
            code += f"\t{model.name}Stub(sockaddr_in server_addr, Protocol& proto) : server_addr(server_addr), proto(proto) {{}};\n"
            code += f"\t~{model.name}Stub() {{}};\n"

            for method in model.methods:
                code += f"\t{_translate_attr_type(method.ret_type)} {method.name}("
                code += ", ".join(
                    [_translate_attr(attr) for attr in method.args]
                )
                code += ");\n"
            
            code += "private:\n"
            code += "\tsockaddr_in server_addr;\n"
            code += "\tProtocol& proto;\n"
            code += "};\n\n"
            return code
        
        def create_client_stub():
            code = ""
            for method in model.methods:
                code += f"{_translate_attr_type(method.ret_type)} {model.name}Stub::{method.name}("
                code += ", ".join(
                    [_translate_attr(attr) for attr in method.args]
                )
                code += ") {\n"
                code += "\tint i = 0;\n"
                code += "\tint buffer_size = proto.get_buffer_size();\n"
                code += "\tchar response_data[buffer_size];\n"
                code += "\tchar request_data[buffer_size];\n"
                code += f"\tmarshall_int(request_data, i, {method.id});\n"

                for arg in method.args:
                    if is_sequence(arg.type):
                        nested_type = get_nested_type(arg.type)
                        code += f"\tmarshall_int(request_data, i, {arg.name}.size());\n"
                        code += f"\tfor ({nested_type} {arg.name}__item : {arg.name})\n"
                        code += f"\t\tmarshall_{nested_type}(request_data, i, {arg.name}__item);\n"
                    else:
                        code += f"\tmarshall_{arg.type}(request_data, i, {arg.name});\n"
                
                code += "\tint response_len = proto.send(server_addr, request_data, i, response_data, buffer_size);\n"
                code += "\ti = 0;\n"
                code += "\tunmarshall_int(response_data, i);  // strip method_id\n"  
                if is_sequence(method.ret_type):
                    nested_type = get_nested_type(method.ret_type)
                    translated_type = _translate_attr_type(method.ret_type)
                    code += f"\t{translated_type} {method.name}__result = {translated_type}();\n"
                    code += f"\tint {method.name}__result__len = unmarshall_int(response_data, i);\n"
                    code += f"\tfor (int j=0; j<{method.name}__result__len; j++)\n"
                    code += f"\t\t{method.name}__result.push_back(unmarshall_{nested_type}(response_data, i));\n"
                    code += f"\treturn {method.name}__result;\n"
                else:
                    code += f"\treturn unmarshall_{method.ret_type}(response_data, i);\n"
                code += "}\n\n"
            return code
        
        def create_servicer_header() -> str:
            return (
                (TEMPLATE_DIR / TEMPLATE_SERVICER_HEADER_FILE)
                .read_text()
                .replace("{__SERVICE_NAME__}", model.name)
            )

        def create_servicer() -> str:
            code = "\n"
            for method in model.methods:
                code += f"\t\tcase {cls.method_counter}: {{\n"
                translated_ret_type = _translate_attr_type(method.ret_type)
                arg_names = []
                for arg in method.args:
                    arg_name = f"{arg.name}__arg"
                    translated_arg_type = _translate_attr_type(arg.type)
                    if is_sequence(arg.type):
                        nested_type = get_nested_type(arg.type)
                        code += f"\t\t\t{translated_arg_type} {arg_name} = {translated_arg_type}();\n"
                        code += f"\t\t\tint {arg_name}__len = unmarshall_int(request_data, i);\n"
                        code += (
                            f"\t\t\tfor (int j=0; j<{arg_name}__len; j++)\n"
                        )
                        code += f"\t\t\t\t{arg_name}.push_back(unmarshall_{nested_type}(request_data, i));\n"
                    else:
                        code += f"\t\t\t{translated_arg_type} {arg_name} = unmarshall_{arg.type}(request_data, i);\n"
                    arg_names.append(arg_name)
                arg_names.append("client_addr")
                code += f'\t\t\t{translated_ret_type} {method.name}__result = service.{method.name}({", ".join(arg_names)});\n'
                code += f"\t\t\tmarshall_int(response_data, j, {cls.method_counter});\n"
                cls.method_counter += 1

                if is_sequence(method.ret_type):
                    nested_type = get_nested_type(method.ret_type)
                    code += f"\t\t\tmarshall_len_header(response_data, j, {method.name}__result.size());\n"
                    code += f"\t\t\tfor ({nested_type} result__seq__item : {method.name}__result)\n"
                    code += f"\t\t\t\tmarshall_{nested_type}(response_data, j, result__seq__item);\n"
                else:
                    code += f"\t\t\tmarshall_{method.ret_type}(response_data, j, {method.name}__result);\n"
                code += "\t\t\treturn j;\n"
                code += "\t\t}\n"

            code += "\t\tdefault:\n"
            code += '\t\t\tthrow std::runtime_error("Invalid method id" + std::to_string(method_id));'

            return (
                (TEMPLATE_DIR / TEMPLATE_SERVICER_CPP_FILE)
                .read_text()
                .replace("{__DISPATCH_CODE__}", code)
                .replace("{__SERVICE_NAME__}", model.name)
            )

        with open(out_dir / STUBS_HEADER_FILE, "a") as f:
            f.write(create_service_stub_header())
            f.write(create_client_stub_header())
            f.write(create_servicer_header())

        with open(out_dir / STUBS_CPP_FILE, "a") as f:
            f.write(create_client_stub())
            f.write(create_servicer())
            

    @classmethod
    def compile(cls, in_file: Path, out_dir: Path, root_dir: Path) -> None:
        # copy templates
        for file in TEMPLATE_DIR.iterdir():
            if file.suffix in (".h", ".cpp") and not file.stem.startswith("_"):
                (out_dir / file.name).write_text(file.read_text())
        super().compile(in_file, out_dir, root_dir)
