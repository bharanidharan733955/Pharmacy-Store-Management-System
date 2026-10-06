# Day 1 Screenshot Submission Checklist & Instructions

To document training evidence for GitHub / LMS submission, capture screenshots of the following terminal commands:

| Screenshot ID | Command | Purpose | Expected Result |
| :--- | :--- | :--- | :--- |
| `01-java-version.png` | `java -version` | Confirm installed JDK runtime version | Shows JDK 21 / 27 runtime details |
| `02-javac-version.png` | `javac -version` | Confirm installed Java compiler version | Shows `javac` compiler details |
| `03-javac-compile.png` | `javac PlatformInfo.java` | Demonstrate terminal compilation | Generates `PlatformInfo.class` without errors |
| `04-java-run.png` | `java PlatformInfo` | Demonstrate terminal execution | Prints Java Platform Info block |
| `05-javap-bytecode.png` | `javap -c PlatformInfo` | Disassemble class file bytecode | Displays disassembler instructions (`invokevirtual`, `invokestatic`) |
| `06-verbose-class.png` | `java -verbose:class PlatformInfo` | Demonstrate runtime class loading | Shows JVM loading JDK classes and `PlatformInfo` class |

### How to Capture & Save:
1. Open PowerShell or Command Prompt in `day-1-java-platform-basics/`.
2. Execute each command listed above.
3. Take a screenshot (Windows: `Win + Shift + S` or `PrtScn`).
4. Save the image files in this `screenshots/` directory.
