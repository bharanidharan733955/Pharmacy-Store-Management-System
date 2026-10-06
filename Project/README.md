# 💊 Pharmacy Store Management System

A Java-based enterprise management platform built following Agile/Scrum methodologies for managing pharmacy inventory, stock reorders, medicine expiration dates, billing transactions, and multi-branch sales analytics.

---

## 🚀 Key Features
- **Platform Diagnostic Utility**: Native JVM runtime, OS, processor, and heap memory inspector (`PlatformInfo.java`).
- **Monthly & Multi-Branch Usage Analyser**: 1-D and 2-D array calculation engine for revenue and unit distribution (`MonthlyUsageAnalyser.java`).
- **Business Constants Engine**: Centralized configuration management for tax rates, discount slabs, stock reorder thresholds, and currency formatting (`PharmacyConstants.java`).
- **Inventory & Sales Tracking**: Real-time sales calculation, tier grading, type casting, and overflow protection for large sales volumes (`PharmacyInventoryManager.java`).

---

## 📁 Repository Structure
```
Pharmacy Store Management System/
│
├── .gitignore                      # Git exclusion rules
├── README.md                       # Project documentation
├── AGILE_BACKLOG.md                # 8 User Stories, Estimates & Definition of Done
│
├── Daily Task/
│   ├── Day 1/
│   │   └── PlatformInfo.java       # JVM Diagnostics & Bytecode inspection lab
│   └── Day 2/
│       └── MonthlyUsageAnalyser.java # 1-D & 2-D Usage Analyser lab
│
└── Project/
    └── src/
        └── com/
            └── pharmacy/
                ├── PharmacyConstants.java        # Centralized business rules & constants
                └── PharmacyInventoryManager.java # Core inventory analytics engine
```

---

## 🛠️ How to Compile & Run (Terminal / Command Line)

### Day 1: JVM Platform Diagnostic
```bash
# Navigate to Day 1 folder
cd "Daily Task/Day 1"

# Compile code
javac PlatformInfo.java

# Execute program
java PlatformInfo

# Inspect Bytecode
javap -c PlatformInfo

# Inspect Class Loading
java -verbose:class PlatformInfo
```

### Day 2: Monthly & Branch Usage Analyser
```bash
# Navigate to Day 2 folder
cd "Daily Task/Day 2"

# Compile code
javac MonthlyUsageAnalyser.java

# Execute program
java MonthlyUsageAnalyser
```

### Project Core Application
```bash
# Navigate to Project directory
cd Project/src

# Compile all source files
javac com/pharmacy/*.java

# Run main inventory manager application
java com.pharmacy.PharmacyInventoryManager
```

---

## 📊 Agile & Scrum Metadata
- **Methodology**: Scrum Framework with 2-week Sprints
- **Total User Stories**: 8 Stories (40 Story Points)
- **DoD (Definition of Done)**: Documented in [AGILE_BACKLOG.md](file:///c:/Users/bhara/OneDrive/Desktop/Pharmacy%20Store%20Management%20System/AGILE_BACKLOG.md)
