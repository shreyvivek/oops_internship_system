# Internship Placement Management System

This is an Object-Oriented Java application I built for managing internship placements between students, company representatives, and career center staff. It uses a clean Boundary-Control-Entity (BCE) architecture to keep things organized, with three main user roles that each have their own workflows—students can browse and apply for internships based on their major and year level, company reps can post opportunities and review applications, and staff members handle approvals and oversee the whole process. The whole thing runs on CSV files for data storage (no databases needed), and I've built both a command-line interface and a Swing GUI so you can use whichever you prefer. The code follows solid OOP principles with proper encapsulation, inheritance where it makes sense, and clear separation of concerns across the boundary, control, and entity layers.

## How to Run

**Compile:**
```bash
cd /Users/shreyv/Desktop/oops_proj
find src/main -name "*.java" > /tmp/files.txt
javac -d out -sourcepath src @/tmp/files.txt
```

**Run:**
- GUI mode (recommended):
```bash
java -cp out main.InternshipPlacementSystem --gui
```
- CLI mode:
```bash
java -cp out main.InternshipPlacementSystem --cli
```
- Mode selection dialog:
```bash
java -cp out main.InternshipPlacementSystem
```
