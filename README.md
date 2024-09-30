# OPSC7312

Savr - Personal Finance Management App

## SAVR TEAM
Thato Sebelemetja ST10067544

Nakeisha Naidoo ST10054051

Jashil Roopnarain ST10092261

Malik Fahad Mannan ST10091422

## YouTube Video demonstration video
 -
 - https://drive.google.com/drive/folders/1g4WEJwl7cAov-9psYqoA17oE_HvVpXzA

## Collaboration Reposytories
- https://github.com/st10091422/OPSC7312.git
- https://github.com/st10091422/OPSC7312-API.git
- https://github.com/ST10067544-Thato/OPSC7312-Part2.git

# Overview
Savr is a personal finance management app designed to simplify your financial journey. With features like expense tracking, savings management, biometric login, and in-depth user analysis, Savr offers a complete solution to manage your money effectively.

Software Required 

# Development Environment:
## 1.	Android Studio (Latest version recommended)
-	IDE for developing and testing Android applications
-	Includes the Android SDK

## 2.Kotlin
-	Primary programming language for developing the app.
-	Integrated within Android Studio.

# 3. nodejs
- Programming language to develop the api used to manage data through firebase

## Testing & Emulation:
3.	Android Emulator
-	Comes pre-installed with Android Studio.
-	Use it to test the app on different virtual devices.

## Version Control:
4.	Git
-	Version control system for managing code versions.
-	Use GitHub, GitLab, or Bitbucket for remote repositories.

Database:
5.	Firebase firestore Database
-	Used to store and manage data within the app.
Other Tools:
6.	JDK 11 or higher
-	Ensure you have the correct Java Development Kit (JDK) for Android development.


# App Navigation

1. Launch Screen
-	Description: Savr's launch screen displays the app’s logo and provides two primary options for users.
-	Options:
-	Login: Navigate to the login screen for registered users.
-	Sign Up: Navigate to the registration screen for new users.

2. Login Screen
-	Description: Registered users can log in by entering their credentials or using biometric authentication.
-	Features:
-	Email & Password Login: Enter email and password to access your account.
-	Forgot Password?: Navigate to the password recovery screen if you’ve forgotten your credentials.
-	Biometric Login: Log in easily using your fingerprint.

3. Sign Up Screen
-	Description: New users can create an account by filling in their name, email, and password.
-	Features: Once signed up, users are redirected to the dashboard.

4. Forgot Password Screen
-	Description: For users who have forgotten their password, this screen helps reset it.
-	Features: Sends a password recovery link via email or SMS to allow users to set a new password.


5. Reset Password Screen
-	Description: Allows users to set a new password after initiating the recovery process.

6. Dashboard Screen
-	Description: The central hub of Savr, displaying key features and providing access to all financial tools.
-	Features:
-	View Recent Transactions: Overview of your latest financial activities.
-	Manage Savings: Track and update your savings goals.
-	Access User Analysis: Get insights into your financial habits and trends.

7. User Analysis Screen
-	Description: Provides visual insights into your financial activities over different time frames.
-	Features:
-	Monthly View: Analyse financial habits for the current month.
-	Weekly View: Breakdown of activities across weeks.
-	Yearly View: Long-term insights into your yearly financial behaviour.

8. Transactions Screen
-	Description: Displays all recent financial activities and transactions.
-	Features: Add New Transactions: Easily add new expense or income entries by tapping “Add Expense.”

9. Add Expense Screen
-	Description: Input fields to log new financial transactions, including amount, category, and date.

10. Savings Screen
-	Description: Provides a detailed view of your current savings and goals.
-	Features: Manage Savings: Set savings goals and track your progress.

11. Profile Screen
-	Description: Manage your personal information, preferences, and settings.
-	Features:
-	Edit Profile: Update personal details like name, email, and password.
-	View Notifications: Access app-related notifications.

12. Security Settings
-	Description: Manage Savr’s security settings, including biometric login and fingerprint management.
-	Features:
-	Enable Fingerprint: Toggle biometric security options.
-	Add/Delete Fingerprint: Manage stored fingerprints for faster login.

13. General Settings
-	Description: Customize app settings, including language preferences, notification management, and account deletion.
-	Features:
-	Change Password: Update your account password.
-	Delete Account: Permanently delete your account (confirmation required).

15. Confirmation Popups
-	Description: Ensures users don't accidentally delete accounts or log out.
-	Features:
-	Delete Account Confirmation: Prompts users before they can permanently delete their account.
-	Logout Confirmation: Prompts users before logging out of the app.


Key Features
-	Secure Login: Login using email/password or biometrics.
-	Expense Tracking: Track and categorize expenses in real-time.
-	User Analysis: View detailed analysis of your financial habits (weekly, monthly, yearly).
-	Savings Management: Set and monitor your savings goals.
-	Biometric Security: Secure your account with fingerprint login.
-	Notifications: Stay updated with notifications about your finances and account activity.

# API design considerations
- RESTful Design: Follow RESTful principles, such as using appropriate HTTP methods (GET, POST, PUT, DELETE), and ensuring stateless interactions.
- Versioning: Implement versioning in the API and right now it uses /v1, this allows for backward compatibility and smooth transitions as the API evolves for the final Poe.

 ![image](https://github.com/user-attachments/assets/eb67f35b-33cf-4740-8aaf-53cabeec1c94)

- Consistent Error Responses: Design a consistent error response format to make it easy to handle errors.
- Logging: Implemented logging to capture errors and usage patterns, which helps in troubleshooting and improving the API.

# Github usage
- GitHub allowed us to track changes, collaborate with each other, and maintain different versions of the codebase.
- configured workflows to run automated unit tests on each commit or pull request to ensure code quality through the use of github actions.
- Maintain documentation in the repository’s README files.
- Configured Render to automatically deploy your API whenever there are updates to the API repository.
  
![image](https://github.com/user-attachments/assets/cdae79c2-963e-456a-8e78-062c520a5ac8)

![image](https://github.com/user-attachments/assets/02991a6a-278a-4567-a9cb-0b747c1bd62c)


# Github Actions
![image](https://github.com/user-attachments/assets/8b344fdf-3db3-4b41-85ee-141fec9513de)

![image](https://github.com/user-attachments/assets/385a2459-b156-4022-9ff8-3bddcdcc196b)


- GitHub Actions is a was used to automate workflows, it is configured to perform the the tasks specified in the main.yml when there is a push to the repo. this is how it was utilized within the project.

- The GitHub Actions configuration automatically triggers the execution of unit tests. These tests validate all the components of the application, ensuring that each part wors as expected.

= the main.yml configures the repository to ensure that the application is built and tested every time code is pushed to the repository or a pull request is created. This process involves several steps:

GitHub Actions triggers a build process that compiles the application whenever changes are pushed. This ensures that the new code integrates smoothly with the existing codebase without introducing compilation errors.






