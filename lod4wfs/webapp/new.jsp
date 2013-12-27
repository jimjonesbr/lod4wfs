<%@ page import="java.io.*"%>
<%@ page import="de.ifgi.lod4wfs.core.GlobalSettings"%>

<HTML>
<HEAD>
<TITLE>LOD4WFS Administration Interface</TITLE>
</HEAD>

<BODY>

	<H1>New SPARQL-Based WFS Layer </H1>
	<FORM NAME="form1">
		SPARQL Endpoint <INPUT TYPE="TEXT"
			VALUE="<%out.println(GlobalSettings.default_SPARQLEndpoint);%>"
			name="txt1"> <br> Name<input type="TEXT" value="Name" name=txtName><br> 
			Title<input type="TEXT" value="Title" name=txtTitle><br> 
			Abstract<input type="TEXT" value="Abstract" name=txtAbstract><br> 
			Key-words<input type="TEXT" value="Key-words" name=txtKeywords><br>
			Geometry Variable<input type="TEXT" value="?GeometryVariable" name=txtVariable><br> 
			SPARQL Query <TEXTAREA NAME="textarea1" ROWS="5"></TEXTAREA><br>
	<input type="submit" id="btnCreate" name="btnCreate" value="Create"/>
		
		
	</FORM>

	<%if(request.getParameter("btnCreate")!=null) //btnSubmit is the name of your button, not id of that button.
	{
		out.print(request.getParameter("txtName"));
		if(request.getParameter("txtName").equals("Name")){
			out.println("Equal!");
		}
		//<input type="SUBMIT" value="Save" name="saveButton" onclick="document.forms[0].action = 'list.jsp'">
	} %>



</BODY>

</HTML>
