# Java Developer Training Repository (JDK 21)

This repository contains hands-on training labs, practical code implementations, terminal-based workflows, and Agile/Scrum product artifacts for **Day 1** and **Day 2**.

---

## 📁 Repository Map

```
java-training/
│
├── day-1-java-platform-basics/
│   ├── PlatformInfo.java          # JVM Diagnostic tool
│   ├── README.md                  # Comprehensive JDK/JRE/JVM Architecture guide
│   ├── AGILE_SCRUM.md             # 8 User Stories, DoD & Sprint Templates
│   ├── .gitignore                 # Exclusion rules for Day 1
│   └── screenshots/               # Screenshot submission guide & checklist
│       └── README.md
│
└── day-2-java-language-fundamentals/
    ├── src/
    │   ├── Constants.java         # Centralized immutable business constants
    │   └── MonthlyUsageAnalyser.java # 1D/2D arrays, casting, overflow lab
    ├── README.md                  # Java Data Types, Operators, Git & SSH guide
    └── .gitignore                 # Exclusion rules for Day 2
```

---

## 🚀 Quick Execution Guide

### Day 1 Tasks:
```bash
cd day-1-java-platform-basics
javac PlatformInfo.java
java PlatformInfo
javap -c PlatformInfo
java -verbose:class PlatformInfo
```

### Day 2 Tasks:
```bash
cd day-2-java-language-fundamentals
javac src/Constants.java src/MonthlyUsageAnalyser.java
java -cp src MonthlyUsageAnalyser
```
