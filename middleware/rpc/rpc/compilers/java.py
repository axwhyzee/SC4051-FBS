from functools import partial
from pathlib import Path
from typing import Dict

from .base import BaseCompiler
from .common import TAB, translate_attr
from .model import EnumModel, InterfaceModel, StructModel
from .typings import DType

JAVA_DTYPES: Dict[str, str] = {
    DType.STRING.value: "String",
    DType.INT.value: "int",
    DType.BOOL.value: "boolean",
    DType.FLOAT.value: "float",
    DType.SEQUENCE.value: "{type}[]",
}


_translate_attr = partial(translate_attr, dtypes=JAVA_DTYPES)


class JavaCompiler(BaseCompiler):

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
        // shapes/Cube.java
        package shapes;

        public record Cube(
            int height,
            int width
        ) {}
        ```
        """
        package = out_dir.relative_to(out_dir_relative_to)
        code = ""
        if out_dir != out_dir_relative_to:
            code += f"package {package};\n\n"
        code += f"public record {model.name}(\n"
        code += ",\n".join(
            map(lambda attr: f"{TAB}{_translate_attr(attr)}", model.attrs)
        )
        code += "\n)"
        (out_dir / f"{model.name}.java").write_text(code)

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
        // Color.java
        public enum Color {
            RED,
            BLUE,
            GREEN;
        }
        ```
        """
        package = out_dir.relative_to(out_dir_relative_to)
        code = ""
        if out_dir != out_dir_relative_to:
            code += f"package {package};\n\n"
        code += f"public enum {model.name} {{\n"
        code += f",\n".join(map(lambda key: f"{TAB}{key}", model.keys))
        code += ";\n}"
        (out_dir / f"{model.name}.java").write_text(code)

    @classmethod
    def _handle_interface(
        cls, model: InterfaceModel, out_dir: Path, out_dir_relative_to: Path
    ) -> None:
        def create_service_stub():
            """
            Service stub is implemented by server to handle 
            incoming RPCs
            """

            code = ""
            if out_dir != out_dir_relative_to:
                code += f"package {package};\n\n"
            code += f"public interface {model.name} {{\n"

            for method in model.methods:
                code += f"{TAB}{method.ret_type} "
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
            if out_dir != out_dir_relative_to:
                code += f"package {package};\n\n"
            code += f"public class {model.name}Stub {{\n"

            for method in model.methods:
                code += f"{TAB}{method.ret_type} "
                code += f"{method.name}("
                code += ", ".join(
                    [_translate_attr(attr) for attr in method.args]
                )
                code += (
                    ") {/* TODO: marshall and send to server via UDP */};\n"
                )
            code += "}"
            (out_dir / f"{model.name}Stub.java").write_text(code)

        package = out_dir.relative_to(out_dir_relative_to)
        create_service_stub()
        create_client_stub()
