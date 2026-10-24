# Publish the Student Grade Calculator online (Render + GitHub)

This project uses Java for the backend and HTML/CSS for the webpage. Deploy it as a **Web Service using Docker**; do not deploy it as a static site because the Java server must run.

## 1. Download and extract

Extract the ZIP. You should see `Dockerfile`, `StudentGradeServer.java`, `index.html`, and this guide in the project folder.

## 2. Upload the project to GitHub

1. Sign in to GitHub, then create a new repository (for example, `student-grade-calculator`).
2. Open the new repository and choose **Add file → Upload files**.
3. Upload the project files from inside the extracted folder. Include `Dockerfile`, `StudentGradeServer.java`, and `index.html`. You can also include the README and deployment guide.
4. Click **Commit changes**. Make sure `Dockerfile` is at the top level of the repository, not inside another nested folder.

If you use GitHub Desktop instead, create/add the extracted project folder as a repository and publish it to GitHub.

## 3. Create the Render service

1. Open https://dashboard.render.com/ and sign in.
2. Choose **New + → Web Service**.
3. Connect GitHub if asked, then select the `student-grade-calculator` repository.
4. In the service settings, choose **Docker** as the runtime/environment. Ensure the Dockerfile path is `./Dockerfile` (or simply `Dockerfile`, depending on the form).
5. Give the service a name, such as `student-grade-calculator`. Choose a region and a plan available in your account.
6. Click **Create Web Service** (or **Deploy Web Service**).
7. Wait until the deployment status shows that it is live. Open the `onrender.com` URL shown on the service page.

## 4. Test the published app

Enter a student name and marks, then click **Calculate Result**. Confirm that total, percentage, grade, and pass/fail status appear.

## Troubleshooting

- **Build fails:** open the Render service's **Logs** and check the first error. Confirm the `Dockerfile`, `StudentGradeServer.java`, and `index.html` are in the repository root.
- **Port or 502 error:** keep the code's `PORT` environment-variable support and the `0.0.0.0` bind in `StudentGradeServer.java`; don't hard-code the cloud port.
- **Page opens but calculation fails:** redeploy the latest commit and check the Render runtime logs.
- **Site feels slow after being idle:** free web services may spin down after inactivity and need a little time to wake up, depending on the current plan limits.

## Updating your site later

Edit the source files, upload/commit the changes to the same GitHub repository, and Render can deploy the new commit automatically when auto-deploy is enabled.

## Data and privacy note

This demo does not store student details or marks in a database. Avoid entering real or sensitive student data in a public demo.
