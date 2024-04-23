An Explanation of How to Run this Program From the Command Line

1. Open the command prompt on one's computer

2. Change the current working directory to  the directory where your code is located. 
For example, if your code is located in "C:\Users\HP\Desktop\EduClimaProject\src", 
you can use the following command:
cd C:\Users\HP\Desktop\EduClimaProject\src

3. Compile the code using the 'javac' command. Use the '--module-path' option to specify
the path to the JavaFX library, and the '--add-modules' option to add the required 
modules. In our case, 

javac --module-path C:\openjfx-20.0.1_windows-x64_bin-sdk\javafx-sdk-20.0.1\lib 
--add-modules javafx.controls,javafx.fxml MainApp.java
(all on the same line)

4. Run the code using the 'java' command. Again, use '--module-path' and '--add-modules'
option to specify the JavaFX library and modules. In our case:
java --module-path C:\openjfx-20.0.1_windows-x64_bin-sdk\javafx-sdk-20.0.1\lib 
--add-modules javafx.controls,javafx.fxml MainApp
where 'MainApp' is the name of the main class from which our program is run. 