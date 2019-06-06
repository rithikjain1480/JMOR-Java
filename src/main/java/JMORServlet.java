import java.io.*;
import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFrame;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.*;

public class JMORServlet extends HttpServlet {

	private boolean isMultipart;
	private String filePath;
	private int maxFileSize = 50 * 1024;
	private int maxMemSize = 4 * 1024;
	private File file;
	private String sourceText = "", extension;
	private Scanner scanner;

	public void init(){
		// Get the file location where it should be stored.
		filePath = getServletContext().getInitParameter("file-upload"); 
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {

		// Ensure request is a file upload
		isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("text/html");
		java.io.PrintWriter out = response.getWriter();

		if(!isMultipart) {
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet upload</title>");  
			out.println("</head>");
			out.println("<body>");
			out.println("<p>No file uploaded</p>"); 
			out.println("</body>");
			out.println("</html>");
			return;
		}

		DiskFileItemFactory factory = new DiskFileItemFactory();

		factory.setSizeThreshold(maxMemSize);

		factory.setRepository(new File("Macintosh HD/temp"));

		ServletFileUpload upload = new ServletFileUpload(factory);
		
		upload.setSizeMax(maxFileSize);

		try { 
			// Parse the request to get file items.
			List fileItems = upload.parseRequest(request);

			// Process the uploaded file items
			Iterator i = fileItems.iterator();

			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet upload</title>");  
			out.println("</head>");
			out.println("<body>");
			
			while (i.hasNext()) {
				FileItem fi = (FileItem)i.next();
				if (!fi.isFormField ()) {
					// Get the uploaded file parameters
					String fieldName = fi.getFieldName();
					String fileName = fi.getName();
					String contentType = fi.getContentType();
					boolean isInMemory = fi.isInMemory();
					long sizeInBytes = fi.getSize();
					
					// Write the file
					if(fileName.lastIndexOf("\\") >= 0) {
						file = new File(filePath + fileName.substring(fileName.lastIndexOf("\\")));
					} else {
						file = new File(filePath + fileName.substring(fileName.lastIndexOf("\\")+1));
					}
					fi.write(file);
					out.println("<p>Uploaded Filename: " + fileName + "</p><br>");
					out.println("<p>Location: " + file.getAbsolutePath() + "</p>");
				}
			}
			
			extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);
			
			if(extension.toLowerCase().equals("txt")) {
				scanner = new Scanner(file);
				String line;
				while(scanner.hasNextLine()) {
					line = scanner.nextLine();
					sourceText += line;
				}
				
				out.println("<p>" + sourceText + "</p>");
				
				out.println("</body>");
				out.println("</html>");
				
				Window window = new Window(sourceText);
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
			else {
				out.println("<p>Please upload a .txt file</p>");
				out.println("</body>");
				out.println("</html>");
			}
			
			
		} catch(Exception ex) {
			System.out.println(ex);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {

		throw new ServletException("GET method used with " + getClass().getName()+": POST method required.");
	}
}
