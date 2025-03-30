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

JAVA_DTYPES: Dict[str, str] = {
    DType.STRING.value: "String",
    DType.INT.value: "int",
    DType.BOOL.value: "boolean",
    DType.FLOAT.value: "float",
    DType.SEQUENCE.value: "{type}[]",
}

MARSHALLER_FILE = "Marshaller.java"
UNMARSHALLER_FILE = "Unmarshaller.java"


_translate_attr = partial(translate_attr, dtypes=JAVA_DTYPES)
_translate_attr_type = partial(translate_attr_type, dtypes=JAVA_DTYPES)


class JavaCompiler(BaseCompiler):

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
        // shapes/Cube.java
        package shapes;

        public record Cube(
            int height,
            int width
        ) {}
        ```
        """

        def create_record():
            package = out_dir.relative_to(root_dir)
            code = ""
            if out_dir != root_dir:
                code += f"package {package};\n\n"
            code += f"public record {model.name}(\n"
            code += ",\n".join(
                map(lambda attr: f"\t{_translate_attr(attr)}", model.attrs)
            )
            code += "\n) {};"
            (out_dir / f"{model.name}.java").write_text(code)

        def create_marshaller():
            code = f"\tpublic static void marshall_{model.name}(byte[] message, int[] i, {model.name} val) {{\n"
            for attr in model.attrs:
                if is_sequence(attr.type):
                    nested_type = get_nested_type(attr.type)
                    code += f"\t\t_marshall_len_header(message, i, val.{attr.name}().length);\n"
                    code += f"\t\tfor ({nested_type} {attr.name}__item : val.{attr.name}())\n"
                    code += f"\t\t\tmarshall_{nested_type}(message, i, {attr.name}__item);\n"
                else:
                    code += f"\t\tmarshall_{attr.type}(message, i, val.{attr.name}());\n"
            code += "\t}\n\n"
            with open(out_dir / MARSHALLER_FILE, "a") as f:
                f.write(code)

        def create_unmarshaller():
            code = f"\tpublic static {model.name} unmarshall_{model.name}(byte[] message, int[] i) {{\n"
            arg_names = []
            for attr in model.attrs:
                arg_names.append(arg_name := f"{attr.name}__arg")
                if is_sequence(attr.type):
                    nested_type = get_nested_type(attr.type)
                    code += f"\t\tint {attr.name}__len = unmarshall_int(message, i);\n"
                    code += f"\t\t{_translate_attr_type(attr.type)} {arg_name} = new {nested_type}[{attr.name}__len];\n"
                    code += f"\t\tfor (int j=0; j<{attr.name}__len; j++)\n"
                    code += f"\t\t\t{arg_name}[j] = unmarshall_{nested_type}(message, i);\n"
                else:
                    code += f"\t\t{_translate_attr_type(attr.type)} {arg_name} = unmarshall_{attr.type}(message, i);\n"

            code += f'\t\treturn new {model.name}({", ".join(arg_names)});\n'
            code += "\t}\n\n"
            with open(out_dir / UNMARSHALLER_FILE, "a") as f:
                f.write(code)

        create_record()
        create_marshaller()
        create_unmarshaller()

    @classmethod
    def _handle_enum(
        cls, model: EnumModel, out_dir: Path, root_dir: Path
    ) -> None:
        def create_type():
            """
            Example:

            ```
            enum = EnumModel(name="Color", keys=[RED, BLUE, GREEN])
            ```

            Translates into ...

            ```
            // Color.java
            public enum Color {
                RED,
                BLUE,
                GREEN;
            }
            ```
            """
            package = out_dir.relative_to(root_dir)
            code = ""
            if out_dir != root_dir:
                code += f"package {package};\n\n"
            code += f"public enum {model.name} {{\n"
            code += f",\n".join(map(lambda key: f"\t{key}", model.keys))
            code += ";\n}"
            (out_dir / f"{model.name}.java").write_text(code)

        def create_marshaller():
            code = f"\tpublic static void marshall_{model.name}(byte[] message, int[] i, {model.name} val) {{\n"
            code += "\t\tmarshall_int(message, i, val.ordinal() + 1);\n"
            code += "\t}\n\n"
            with open(out_dir / MARSHALLER_FILE, "a") as f:
                f.write(code)

        def create_unmarshaller():
            code = f"\tpublic static {model.name} unmarshall_{model.name}(byte[] message, int[] i) throws EnumConstantNotPresentException {{\n"
            code += "\t\tint enum_id = unmarshall_int(message, i);\n"
            code += "\t\tswitch (enum_id) {\n"

            for i, key in enumerate(model.keys, start=1):
                code += f"\t\t\tcase {i}:\n"
                code += f"\t\t\t\treturn {model.name}.{key};\n"
            code += "\t\t\tdefault:\n"
            code += f'\t\t\t\tthrow new EnumConstantNotPresentException({model.name}.class, "Invalid ordinal value: " + enum_id);'
            code += "\t\t}\n"
            code += "\t}\n\n"

            with open(out_dir / UNMARSHALLER_FILE, "a") as f:
                f.write(code)

        create_type()
        create_marshaller()
        create_unmarshaller()

    @classmethod
    def _handle_interface(
        cls, model: InterfaceModel, out_dir: Path, root_dir: Path
    ) -> None:
        def create_service_stub():
            """
            Service stub is implemented by server to handle
            incoming RPCs
            """

            code = ""
            if out_dir != root_dir:
                code += f"package {package};\n\n"
            code += f"public interface {model.name} {{\n"

            for method in model.methods:
                code += f"\t{method.ret_type} "
                code += f"{method.name}("
                code += ", ".join(
                    [_translate_attr(attr) for attr in method.args]
                )
                code += ");\n"
            code += "}"
            (out_dir / f"{model.name}.java").write_text(code)

        def create_client_stub():
            """
            Stub will be called by client to make RPCs
            """

            code = ""
            if out_dir != root_dir:
                code += f"package {package};\n\n"
            code += f"public class {model.name}Stub {{\n"

            for method in model.methods:
                code += f"\t{method.ret_type} "
                code += f"{method.name}("
                code += ", ".join(
                    [_translate_attr(attr) for attr in method.args]
                )
                code += (
                    ") {/* TODO: marshall and send to server via UDP */};\n"
                )
            code += "}"
            (out_dir / f"{model.name}Stub.java").write_text(code)

        package = out_dir.relative_to(root_dir)
        create_service_stub()
        create_client_stub()

    @classmethod
    def compile(cls, in_file: Path, out_dir: Path, root_dir: Path) -> None:
        # copy templates
        for file in Path("templates/java").iterdir():
            if file.suffix == ".java" and not file.stem.startswith("_"):
                (out_dir / file.name).write_text(file.read_text())
        super().compile(in_file, out_dir, root_dir)

        # set package and close off class body
        for file in (MARSHALLER_FILE, UNMARSHALLER_FILE):
            text = (out_dir / file).read_text().replace(
                "{__PACKAGE__}", str(out_dir.relative_to(root_dir))
            ) + "}"
            (out_dir / file).write_text(text)
