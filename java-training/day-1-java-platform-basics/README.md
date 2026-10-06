# Day 1: Java Platform Basics + Agile/Scrum Basics

## 🎯 Project Objective
The goal of Day 1 is to master the foundational mechanics of the Java Execution Environment—specifically understanding the JDK, JRE, JVM, JVM architecture, compilation/execution pipeline, bytecode inspection via `javap`, class loading via `-verbose:class`, and establishing professional Agile/Scrum product management practices.

---

## 📚 1. Java Platform Core Concepts

### JDK vs JRE vs JVM

```
+-----------------------------------------------------------------------+
| Java Development Kit (JDK)                                            |
|  +-----------------------------------------------------------------+  |
|  | Development Tools (javac, javap, jar, javadoc, jdb)            |  |
|  +-----------------------------------------------------------------+  |
|  | Java Runtime Environment (JRE)                                  |  |
|  |  +-----------------------------------------------------------+  |  |
|  |  | Core Libraries & Class Files (rt.jar, java.base, etc.)   |  |  |
|  |  +-----------------------------------------------------------+  |  |
|  |  | Java Virtual Machine (JVM)                                |  |  |
|  |  |  [ ClassLoader | Memory Areas | Execution Engine (JIT) ]  |  |  |
|  |  +-----------------------------------------------------------+  |  |
|  +-----------------------------------------------------------------+  |
+-----------------------------------------------------------------------+
```

| Component | Full Name | Primary Purpose | Audience | Contains |
| :--- | :--- | :--- | :--- | :--- |
| **JDK** | Java Development Kit | Complete software development kit to write, compile, debug, and run Java code. | Developers | Compiler (`javac`), Disassembler (`javap`), Debugger, JRE, Core APIs. |
| **JRE** | Java Runtime Environment | Software package providing environment to execute already compiled Java bytecode. | End Users | JVM, Core Class Libraries, Supporting Runtime files. |
| **JVM** | Java Virtual Machine | Abstract virtual computing machine that executes Java bytecodes (`.class` files). | OS / Runtime | Class Loader, Runtime Data Areas, Execution Engine (Interpreter + JIT + GC). |

---

## 🏗️ 2. Detailed JVM Architecture

```
                       +-------------------------+
                       |    Java Source (.java)  |
                       +-------------------------+
                                    |
                               ( javac )
                                    |
                       +-------------------------+
                       |   Bytecode (.class)     |
                       +-------------------------+
                                    |
+-----------------------------------v-----------------------------------+
|                     JAVA VIRTUAL MACHINE (JVM)                        |
|                                                                       |
|  +-----------------------------------------------------------------+  |
|  |                      CLASS LOADER SUBSYSTEM                     |  |
|  |  Loading (Bootstrap, Extension/Platform, Application)           |  |
|  |  Linking (Verify, Prepare, Resolve)  |  Initialization            |  |
|  +-----------------------------------------------------------------+  |
|                                   |                                   |
|  +--------------------------------v--------------------------------+  |
|  |                     RUNTIME DATA AREAS (MEMORY)                 |  |
|  |  +-------------------+ +-------------------+ +---------------+  |  |
|  |  |    Method Area    | |     Heap Area     | |  Java Stacks  |  |  |
|  |  +-------------------+ +-------------------+ +---------------+  |  |
|  |  +-------------------+ +-------------------------------------+  |  |
|  |  |   PC Registers    | |        Native Method Stacks       |  |  |
|  |  +-------------------+ +-------------------------------------+  |  |
|  +-----------------------------------------------------------------+  |
|                                   |                                   |
|  +--------------------------------v--------------------------------+  |
|  |                        EXECUTION ENGINE                         |  |
|  |  +------------------+  +-------------------+  +---------------+  |  |
|  |  |   Interpreter    |  | JIT Compiler (C2) |  | Garbage Coll. |  |  |
|  |  +------------------+  +-------------------+  +---------------+  |  |
|  +-----------------------------------------------------------------+  |
|                                   |                                   |
|  +--------------------------------v--------------------------------+  |
|  |               Java Native Interface (JNI) & Native Libraries    |  |
|  +-----------------------------------------------------------------+  |
+-----------------------------------------------------------------------+
```

### Components of JVM Architecture
1. **Class Loader Subsystem**: Loads, links, and initializes Java `.class` files into runtime memory.
   - **Bootstrap Class Loader**: Loads core Java classes from `java.base` (`java.lang.*`, `java.util.*`).
   - **Platform / Extension Class Loader**: Loads platform-specific extension modules.
   - **Application / System Class Loader**: Loads application classes from the classpath.
2. **Runtime Data Areas**:
   - **Method Area**: Stores class structures, field/method metadata, static variables, and runtime constant pool.
   - **Heap Area**: Stores all instantiated objects and instance variables (managed by Garbage Collector).
   - **Java Thread Stacks**: Stores stack frames per thread for local variables, operand stacks, and partial results.
   - **Program Counter (PC) Registers**: Tracks the memory address of the JVM instruction currently executing.
   - **Native Method Stacks**: Manages stack frames for native C/C++ methods called via JNI.
3. **Execution Engine**:
   - **Interpreter**: Reads bytecode instructions line by line and executes them directly.
   - **JIT (Just-In-Time) Compiler**: Compiles frequently executed bytecode ("hot spots") into native machine code to dramatically improve runtime speed.
   - **Garbage Collector (GC)**: Automatically reclaims memory occupied by unreferenced objects on the heap.

---

## 🔄 3. Compilation & Execution Flow

```
[ Developer ] --(Writes Source Code)--> PlatformInfo.java
                                               |
                                         ( javac Compiler )
                                               |
[ Bytecode File ] <-------------------- PlatformInfo.class
       |
( JVM Launch )
       |
[ Class Loader ] ---> Loads class into Runtime Data Areas (Heap/Method Area)
       |
[ Execution Engine ] -> Interprets/JIT-compiles bytecode into Native Machine Code
       |
[ Hardware CPU ] ----> Executes Machine Instructions (Prints to System Output)
```

---

## 💻 4. Practical Implementation: `PlatformInfo.java`

`PlatformInfo.java` is a native Java diagnostic tool that queries the underlying Java Virtual Machine and Operating System parameters.

### Source Code (`PlatformInfo.java`)
```java
public class PlatformInfo {

    public static void main(String[] args) {

        System.out.println("===== Java Platform Information =====");
        System.out.println("Java Version    : " + System.getProperty("java.version"));
        System.out.println("OS Name         : " + System.getProperty("os.name"));
        System.out.println("Processors      : " + Runtime.getRuntime().availableProcessors());
        System.out.println("Max Heap        : " + Runtime.getRuntime().maxMemory() + " bytes");
        System.out.println("Free Heap       : " + Runtime.getRuntime().freeMemory() + " bytes");
        System.out.println("====================================");
    }
}
```

---

## ⚡ 5. Terminal Execution Instructions

Navigate to the `day-1-java-platform-basics` directory:

```bash
cd day-1-java-platform-basics
```

### Command 1: Verify Installed Java Runtime Version
```bash
java -version
```
*Expected Output:*
```text
java version "21.0.2" 2024-01-16 LTS (or Java 27)
Java(TM) SE Runtime Environment (build 21.0.2+13-LTS-58)
Java HotSpot(TM) 64-Bit Server VM (build 21.0.2+13-LTS-58, mixed mode, sharing)
```

### Command 2: Verify Installed Java Compiler Version
```bash
javac -version
```
*Expected Output:*
```text
javac 21.0.2 (or javac 27)
```

### Command 3: Compile `PlatformInfo.java`
```bash
javac PlatformInfo.java
```
*Result:* Generates `PlatformInfo.class` bytecode file in the current directory.

### Command 4: Execute Compiled Class
```bash
java PlatformInfo
```
*Expected Terminal Output:*
```text
===== Java Platform Information =====
Java Version    : 27
OS Name         : Windows 11
Processors      : 12
Max Heap        : 2061500416 bytes
Free Heap       : 9036920 bytes
====================================
```

---

## 🔍 6. Bytecode Inspection (`javap -c`)

To disassemble `PlatformInfo.class` and inspect the raw Java bytecode:

```bash
javap -c PlatformInfo
```

### Disassembled Bytecode Analysis
```bytecode
Compiled from "PlatformInfo.java"
public class PlatformInfo {
  public PlatformInfo();
    Code:
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return

  public static void main(java.lang.String[]);
    Code:
         0: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #13                 // String ===== Java Platform Information =====
         5: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
        11: ldc           #21                 // String java.version
        13: invokestatic  #23                 // Method java/lang/System.getProperty:(Ljava/lang/String;)Ljava/lang/String;
        16: invokedynamic #27,  0             // InvokeDynamic #0:makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;
        21: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        24: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
        27: ldc           #30                 // String os.name
        29: invokestatic  #23                 // Method java/lang/System.getProperty:(Ljava/lang/String;)Ljava/lang/String;
        32: invokedynamic #32,  0             // InvokeDynamic #1:makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;
        37: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        40: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
        43: invokestatic  #33                 // Method java/lang/Runtime.getRuntime:()Ljava/lang/Runtime;
        46: invokevirtual #39                 // Method java/lang/Runtime.availableProcessors:()I
        49: invokedynamic #43,  0             // InvokeDynamic #2:makeConcatWithConstants:(I)Ljava/lang/String;
        54: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        57: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
        60: invokestatic  #33                 // Method java/lang/Runtime.getRuntime:()Ljava/lang/Runtime;
        63: invokevirtual #46                 // Method java/lang/Runtime.maxMemory:()J
        66: invokedynamic #50,  0             // InvokeDynamic #3:makeConcatWithConstants:(J)Ljava/lang/String;
        71: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        74: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
        77: invokestatic  #33                 // Method java/lang/Runtime.getRuntime:()Ljava/lang/Runtime;
        80: invokevirtual #53                 // Method java/lang/Runtime.freeMemory:()J
        83: invokedynamic #56,  0             // InvokeDynamic #4:makeConcatWithConstants:(J)Ljava/lang/String;
        88: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        91: return
}
```

### Key Bytecode Instructions Explained:
- `getstatic #7`: Retrieves the static field `System.out` of type `PrintStream`.
- `ldc #13`: Pushes a constant String from the runtime constant pool onto the stack.
- `invokestatic #23`: Invokes static method `System.getProperty()`.
- `invokedynamic`: Efficiently performs string concatenation dynamically (`makeConcatWithConstants`).
- `invokevirtual #15`: Invokes instance method `PrintStream.println()`.
- `return`: Returns `void` from the main method.

---

## 🔬 7. Class Loading Inspection (`java -verbose:class`)

Executing Java with `-verbose:class` reveals the exact order in which the JVM ClassLoader loads `.class` files into memory during runtime:

```bash
java -verbose:class PlatformInfo
```

### Truncated Class Load Log Highlights:
```text
[0.005s][info][class,load] java.lang.Object source: shared objects file
[0.006s][info][class,load] java.lang.String source: shared objects file
[0.007s][info][class,load] java.lang.System source: shared objects file
[0.009s][info][class,load] java.lang.Runtime source: shared objects file
...
[0.068s][info][class,load] PlatformInfo source: file:/C:/Users/bhara/OneDrive/Desktop/Pharmacy%20Store%20Management%20System/java-training/day-1-java-platform-basics/
===== Java Platform Information =====
Java Version    : 27
OS Name         : Windows 11
Processors      : 12
Max Heap        : 2061500416 bytes
Free Heap       : 9036920 bytes
====================================
[0.076s][info][class,load] java.lang.Shutdown source: shared objects file
```

---

## 📋 8. Agile/Scrum Deliverables
Detailed Agile backlog document containing **8 User Stories** and **Definition of Done (DoD)** is located in [AGILE_SCRUM.md](AGILE_SCRUM.md).

---

## ✅ Day 1 Learning Outcomes
1. Mastered JDK, JRE, and JVM concepts and architectural boundaries.
2. Understood internal JVM execution stages: Class Loading, Runtime Memory allocation, Interpreter, JIT, and Garbage Collection.
3. Successfully executed terminal-only compilation (`javac`) and execution (`java`) without reliance on IDE tools.
4. Demonstrated deep bytecode disassembly (`javap -c`) and JVM runtime class loading diagnostics (`-verbose:class`).
5. Established structured Agile product management artifacts including User Stories, Fibonacci story point estimation, and Definition of Done.
