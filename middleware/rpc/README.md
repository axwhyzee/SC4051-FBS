# RPC 

Language-agnostic command line tool to compile interface file into language specific header files to faciliate socket programm via generated stubs. Stubs also ensure reliable network protocol built on top of UDP.

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
source env/bin/activate
rpc_tools --infile=proto.idl --outdir=../server/protos --rootdir=../server --lang=cpp
```

#### Java

```
source env/bin/activate
rpc_tools --infile=proto.idl --outdir=../client/protos --rootdir=../client --lang=java
```
