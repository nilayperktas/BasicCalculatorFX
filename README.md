# BasicCalculatorFX

This project is a basic calculator application developed using Java and JavaFX[cite: 2].

The calculator allows users to perform standard arithmetic operations such as addition, subtraction, multiplication, and division[cite: 3]. In addition to its core functionality, the application includes a user authentication system (registration and login), and a calculation history feature that records operations into a text file on the local drive[cite: 4]. The interface is built using JavaFX layout controls such as GridPane, VBox, Label, and Button elements[cite: 5].

## Key Features

* **Basic Functionality:**
    * Calculator supports arithmetic operations (+, −, ×, ÷)
    * GUI-based user interaction with buttons and a display label
* **Authentication:**
    * Users can register with a username and password 
    * Secure login/logout system
    * Passwords are hashed before saving
* **File Processing:**
    * User-specific calculation history is saved to a `.txt` file
    * History file updates automatically after each calculation
    * Users can view their past calculations in a display area or separate tab
* **Additional Feature: History Tracking:**
    * Real-time display of user's calculation history
    * Automatically clears or resets on logout 

## Technologies Used

* **Programming Language:** Java
* **GUI Framework:** JavaFX
* **File I/O:** `BufferedWriter`, `FileReader` for handling `.txt` files
* **Project Management:** Maven (`pom.xml` included)

## Installation & References

1.  GitHub project link: (Link not provided in source. Please insert your GitHub repository link here.)
2.  `UserHistory.java` Class contributions by:
    * [Mnour3593](https://linktr.ee/Mnour3593) 
    * [Claude.ai](https://claude.ai/new) 
3.  `Main Application.java` Class contributions by: [Gemini](https://gemini.google.com/app?hl=tr)
4.  `CalculatorController.java` Class contributions by: Me and [Claude.ai](https://claude.ai/new) 
5.  `Calculator-view.fxml` code contributions by: [YouTube (https://youtu.be/n43ksPiJ0mg?si=sxY1Xi_np5ZzSbk6)](https://youtu.be/n43ksPiJ0mg?si=sxY1Xi_np5ZzSbk6) and Scene Builder
6.  `login-view.fxml` code contributions by: Scene Builder 

## Screenshots

* Login screen
    ![Login Screen](Login_Screen.png) 
* Main view
    ![Calculator Main View](Calculator_Main_View.png)
