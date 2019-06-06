PROJECT TITLE: JMOR

PURPOSE OF PROJECT: This project allows the user to upload a text file on some topic to an HTML form. Our project parses the text file for keywords and crawls Google for relevant links according to the keywords.

VERSION or DATE: 2019-05-06

HOW TO START THIS PROJECT:
This project is in Eclipse, not BlueJ, because a servlet was necessary to establish communication with the HTML form, and we needed dependencies for some of the libraries we used. Since it connects to the localhost, use Eclipse Java EE IDE for Web Developers instead of the standard Eclipse Java IDE.

All of the dependencies are included in the pom.xml, and jar files necessary for the project should be included within the "Referenced Libraries." If they are not visit the following links for the necessary files:
http://commons.apache.org/proper/commons-fileupload/download_fileupload.cgi
http://commons.apache.org/proper/commons-io/download_io.cgi
Use the download link for the zip file under "Binaries."

To run the localhost through Eclipse, you will need to download an Apache Tomcat server (version 9). The download can be found here: http://tomcat.apache.org/
Once I downloaded it, I changed all the .sh files within the bin folder of Apache Tomcat to executable in Terminal before using it. I do not know if this is necessary, but if the server does not work, this may fix any issues.
After downloading, change the run configurations to run the index.jsp file to the Tomcat server.

Before running, a file path in the code must be changed. In the web.xml file (found under src/main/webapp/WEB-INF), change the param-value within the context-param to wherever you would like the file to be saved on your laptop.

Go to localhost:8080/JMOR-Java to view the form (change port if your Tomcat Server is on a different port). Upload a text file (test and wordsworth are provided as test files) to the form, and a Java applet will appear. Click the generate links button to begin generating links, and click ok on the popup that appears. After a few seconds, the screen will fill with the top relevant links. A complete list of keywords and links can be found in the console.

If you have any questions, or something is not working right, feel free to ask us!


AUTHORS: Rithik Jain and Adishree Ghatare
