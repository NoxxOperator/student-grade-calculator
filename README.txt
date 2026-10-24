STUDENT GRADE CALCULATOR - JAVA + HTML
======================================

FILES
- index.html: Web interface (HTML, CSS, and a small amount of browser JavaScript).
- StudentGradeServer.java: Java backend for validating marks and calculating results.
- Dockerfile: Instructions for cloud deployment on Render or Railway.
- run.bat: Run locally on Windows.
- DEPLOY_TO_RENDER.md: Step-by-step public deployment guide.

RUN LOCALLY ON WINDOWS
1. Install Java JDK 11 or newer.
2. Double-click run.bat, or open Command Prompt in this folder and run:
     javac StudentGradeServer.java
     java StudentGradeServer
3. Visit http://localhost:8765 in your browser.
4. Keep the command window open while using the site. Press Ctrl+C to stop it.

DEPLOY ONLINE
Follow DEPLOY_TO_RENDER.md. The app uses a Dockerfile, reads Render's PORT environment variable, and binds to 0.0.0.0 so it can receive public requests.

GRADING RULES
- Five subjects, each between 0 and 100.
- Passing requires at least 35 marks in every subject.
- A+: 90% or above; A: 80-89.99%; B: 70-79.99%; C: 60-69.99%; D: 50-59.99%; F: below 50% or failing a subject.

IMPORTANT
- This demo does not save student marks to a database. Results are calculated for the current submission only.
- Do not open index.html directly by double-clicking it. When running locally, visit http://localhost:8765 while the Java server is running.
