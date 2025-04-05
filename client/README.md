# Client of FPS

## Prerequisites

Make sure you have the following installed on your machine:
- Java Development Kit (JDK). 
- Java compiler (`javac`), which comes with JDK.
- (Optional) A code editor like IntelliJ IDEA, Eclipse, or VS Code.
- **WSL (Windows Subsystem for Linux)** or a **Linux environment**.
  - If using **WSL**, make sure you have a Linux distribution installed (e.g., Ubuntu).
  - If using **Linux**, ensure it’s a properly set up and configured.

## Compile Client

1. Open a terminal/command prompt.
2. Navigate to `client` folder. 
3. Run the following command to compile the Java files:

    ```
    javac boundary/*.java controller/*.java middleware/network/*.java middleware/protos/*.java service/*.java Main.java
    ```

## Run Client

After compilation, run the program by using the java command:

1. Default **ipAddress:** `127.0.0.1` and **port:** `8888`

    ```
    java
    ```
2. Custom ipaddress and port

    ```
    java Main <ipAddress> <port>
    ```
