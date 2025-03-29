from setuptools import find_packages, setup

setup(
    name="rpc",
    version="1.0.0",
    packages=find_packages(),
    entry_points={
        "console_scripts": [
            "rpc_tools = rpc.rpc_tools:main",
        ],
    },
)
