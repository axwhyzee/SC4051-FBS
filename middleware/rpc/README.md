# RPC 

Language-agnostic command line tool to compile interface file into language specific header files to faciliate socket programm via generated stubs. Stubs also ensure reliable network protocol built on top of UDP.

## How It Works

A parser generates logical models of interface components from the interface file. Next, language-specific compilers, build these components part by part to generate RPC stubs.

## Quickstart

_Note: Run all commands from the `SC4051-FBS/middleware/rpc` directory_

### Install RPC tools
```
# setup python env
python -m venv env
source env/bin/activate
pip install .  # installs rpc package
```

### Compile Interface File into Stubs

#### C++

```
mkdir ../client
mkdir ../client/protos
source env/bin/activate
rpc_tools --infile=proto.idl --outdir=../../server/protos --rootdir=../../server --lang=cpp
# check outdir for generated stubs
```

#### Java

```
mkdir ../client
mkdir ../client/protos
source env/bin/activate
rpc_tools --infile=proto.idl --outdir=../../client --lang=java
# check outdir for generated stubs
```

rpc_tools --infile=proto.idl --outdir=./client --lang=cpp
