# Internship Placement Management System (OOP, BCE)

Short summary

An Object-Oriented Internship Placement Management System implemented in Java using a clean Boundary–Control–Entity (BCE) architecture. It supports three roles (Student, Company Representative, Career Center Staff), enforces core business rules (eligibility, approvals, visibility, slots, withdrawals), persists data via CSV files (no DB/JSON/XML), and offers both a CLI and a Swing-based GUI front-end. The design emphasizes encapsulation, clear responsibility boundaries, and maintainable control flows.

OOP design highlights

- Encapsulation: Domain state and invariants live in Entity classes (`main.entity.*`), exposed via clear methods (e.g., `Internship`, `Application`, `User` hierarchy).
- Inheritance/Polymorphism: `User` is a base class extended by `Student`, `CompanyRepresentative`, `CareerCenterStaff`. Role-specific behavior is routed via polymorphism.
- Composition over inheritance in Control: Composed managers (e.g., `ApplicationManager`, `InternshipManager`) orchestrate workflows and rely on repositories for persistence.
- Single Responsibility: Boundary (CLI/GUI) focuses on I/O; Control on use-cases and business rules; Entities on domain state.
- Separation of Concerns (BCE): 
  - Boundary (B): `main.boundary.*` (CLI) and `main.gui.*` (GUI) only handle user interaction.
  - Control (C): `main.control.*` encodes business logic and rules.
  - Entity (E): `main.entity.*` holds domain models and enums.
  - Data (infra): `main.data.*` repositories and file I/O helpers.

Key use-cases supported

- Login, logout, change password (all roles)
- Student: view eligible internships, apply (max 3, by level/major/date/visibility), view status, accept one successful offer (auto-withdraw others), request withdrawal
- Company Rep: register and await approval, create/edit internships (limit 5, before approval), toggle visibility, review/approve/reject applications
- Staff: approve/reject company reps, approve/reject internships, approve/reject withdrawal requests, generate filtered reports

Project layout

- `src/main/boundary`: CLI menus (Boundary)
- `src/main/gui`: Swing GUI (Boundary)
- `src/main/control`: Use-case/application logic (Control)
- `src/main/entity`: Domain models and enums (Entity)
- `src/main/data`: Repositories and CSV I/O (Infrastructure)
- `src/main/util`: Utilities (e.g., input handling)
- `docs/javadoc`: Generated Javadoc (landing page: `index.html`)
- `docs/index.html`: GitHub Pages landing with quick links (this site)

How to run

1) Compile (already compiled to `out/` if you used the provided scripts)

```bash
cd /Users/shreyv/Desktop/oops_proj
find src/main -name "*.java" > /tmp/files.txt
javac -d out -sourcepath src @/tmp/files.txt
```

2) Run

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

Data and persistence

- CSV files under `data/` (example lists provided). The system avoids DB/JSON/XML by requirement.
- Repositories in `main.data.*` load/save entities using `FileHandler`.

Developer docs (Javadoc)

- Open locally: `docs/javadoc/index.html`
- GitHub Pages (after enabling “Pages → Source: docs/”): `https://<your-github-username>.github.io/<your-repo>/` (see buttons below)

GitHub Pages buttons

After you push to GitHub and enable Pages with the `docs/` folder as the source, the site will be served at:

`https://<your-github-username>.github.io/<your-repo>/`

The landing page is `docs/index.html` with one-click buttons for:

- View Code: points to your repository URL
- Javadoc: opens `docs/javadoc/index.html`
- Report: expects a file at `docs/report.pdf` (add your PDF there)
- Demo Video: update the link in `docs/index.html` to your video URL

Maintainers’ notes

- BCE integrity is reflected in packages and Javadoc package overviews (`package-info.java`).
- Business rules are centralized in the Control layer; Boundary only routes and renders.
- Extendability: add new roles or rules by composing new managers or extending entities, without leaking UI or storage concerns.

License

MIT (or your preferred license). Update as necessary.


