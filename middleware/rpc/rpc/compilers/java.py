from pathlib import Path

from .base import BaseCompiler
from .model import Attribute, EnumModel, InterfaceModel, StructModel
from .typings import JAVA_DTYPES, DType

TAB = " " * 4


def _translate_attr(attr: Attribute) -> str:
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
    base_type = typ.rstrip(">").split("<")[-1]
    return (
        f"{JAVA_DTYPES.get(base_type, base_type)}"
        f'{seqs * "[]"} '
        f"{attr.name}"
    )


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
            int width,
            int depth
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
        package = out_dir.relative_to(out_dir_relative_to)
        code = ""
        if out_dir != out_dir_relative_to:
            code += f"package {package};\n\n"
        code += f"public interface {model.name}ServiceStub {{\n"

        for method in model.methods:
            code += f"{TAB}{method.ret_type} "
            code += f"{method.name}("
            code += ", ".join([_translate_attr(attr) for attr in method.args])
            code += ");\n"
        code += "}"
        (out_dir / f"{model.name}.java").write_text(code)
