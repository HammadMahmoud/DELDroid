<%-- 
    Document   : index
    Created on : Mar 16, 2015, 12:25:56 PM
    Author     : sdaLab
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html><head>
        
<link rel="stylesheet" type="text/css" href="mystyle.css">
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script>
$(document).ready(function(){
    $("#filepath").change(function() {
    var fileName = $(this).val().split('\\').pop().split('/').pop()
    //alert(fileName);
    $('#app').val(fileName);
            });
});
</script>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Self-Adaptive Android System (AdaptDroid)</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style type="text/css"></style></head>
    
        <body background="bg.jpg">
        <!--TABLE START-->
<table width="50%" border="0" align="center">
	<tr>
		<td width="25%"><img src="uci_logo.png" alt="University of California, Irvine" align=middle> </td>
		<td with="50%"><center><br /><br /><a href="index.jsp"><h1><span style="font-family:'Comic Sans MS';font-size:1.3em;color:#0000FD"><span style="font-size:1.0em;color:#0A1FE1">
                                Self-Adaptive Android System (AdaptDroid)</span></span></h1></a></center></td>
		<td td width="25%"><img src="newSeal.png" alt="Software Engineering Analysis Lab" align=middle></td>
	</tr>
	<tr>
		<td colspan="3" height="30"> <hr size="2" width="100%"></td>
	</tr>
        <tr>
            <td align="center" width="25%"><h3>Generates App architectural design</h3></td>
            <td width="50%" align="left" >
                <div>
                    <h3>This online service allows you to generate an architectural design model for the Android applications. The generated architecture returned as XML or JSON file from a RESTful WS<br />
                For example, to get the architectural model of the Android App <i>seal.uci.edu.consumewebservices</i> as JSON. Make a RESTful WS call to
                <a href="http://localhost:8080/SelfAdaptiveWeb/webresources/as/TestDevice/seal.uci.edu.consumewebservices.xml">http://localhost:8080/SelfAdaptiveWeb/webresources/as/TestDevice/seal.uci.edu.consumewebservices.xml</a> <br />
                <br /> If you need an XML file, change the extention on the previous link to .xml
                </h3>
                    </div>
            </td>
            <td width="25%"></td>
        </tr>
        <tr><td colspan=3""><hr></td></tr>
        <tr>
            <td align="center" width="25%"><h3>Removes a package from a device</h3></td>
            <td colspan="2">
                <h3>To remove the package <i>seal.uci.edu.consumewebservices</i> from a device <i>TestDevice</i> make a RESTful WS call to <br /> 
                <a href="http://localhost:8080/SelfAdaptiveWeb/webresources/as/TestDevice?removedPackage=seal.uci.edu.consumewebservices">
                    http://localhost:8080/SelfAdaptiveWeb/webresources/as/TestDevice?removedPackage=seal.uci.edu.consumewebservices</a> <br />
                    </h3>
            </td>
        </tr>
        <tr><td colspan=3""><hr></td></tr>
        <tr>
            <td align="center" width="25%"><h3>Updates component mode</h3></td>
            <td colspan="2">
                <h3>To change the mode of the component <i>seal.uci.edu.consumewebservices.MainActivity</i> to a <i>running</i> mode from the device <i>TestDevice</i> make a RESTful WS call to <br /> 
                <a href="http://localhost:8080/SelfAdaptiveWeb/webresources/as/TestDevice/updateComponent?pkg=seal.uci.edu.consumewebservices&component=MainActivity&mode=running">
                    http://localhost:8080/SelfAdaptiveWeb/webresources/as/TestDevice/updateComponent?pkg=seal.uci.edu.consumewebservices&component=MainActivity&mode=running</a> <br />
                    </h3>
            </td>
        </tr>
        <tr><td colspan=3""><hr></td></tr>
        <tr>
            <td align="center" width="25%"><h3>Updates battery status</h3></td>
            <td colspan="2">
                <h3>To change the battery status on the device <i>TestDevice</i> to <i>BATTERY_OKAY</i> make a RESTful WS call to <br /> 
                <a href="http://localhost:8080/SelfAdaptiveWeb/webresources/as/000000000000000/updateBattery?batteryStatus=BATTERY_OKAY">
                    http://localhost:8080/SelfAdaptiveWeb/webresources/as/000000000000000/updateBattery?batteryStatus=BATTERY_OKAY</a> <br />
                    </h3>
            </td>
        </tr>
        
        
        
        
<!--	<tr>
		<td colspan="3"><br/><b>RevealDroid</b>: A novel machine learning-based Android malware detection and family identification approach, that provides selectable features that enable obfuscation resiliency, efficiency, and accuracy for detection and family identification.
		</td>
	</tr>
	<tr>
		<td > ROW:3 COL:1 </td>
		<td align="center">  
		<br/>     
		<h3>Please upload apk file:</h3>
        <form id="mainform" action="detection.action" method="POST" enctype="multipart/form-data">
            <input type="file" id="filepath" name="filepath"><br><br>
            <input name="app" id="app" type="text" placeholder="app name" size="50"><br/><br/>
            <input name="search" type="submit" value="Analyze">
        </form>
	</td>
		<td> ROW:3 COL:3 </td>
	</tr>
	<tr>
		<td> ROW:4 COL:1 </td>
		<td align="center"><br/>RevealDroid Approach:<br/><br/>
		<img src="revealdroid_approach.png" alt="RevealDroid approach" height="281" width="637">
		
		</td>
		<td> ROW:4 COL:3 </td>
	</tr>
	<tr>
		<td colspan="3"><h3>Contacts:</h3>
		<a href="mailto:jgarci40@gmu.edu">Josh Garcia (Postdoctoral Associate)</a><br/>
		<a href="mailto:mhammad2@masonlive.gmu.edu">Mahmoud Hammad (PhD Student)</a><br/>
		<a href="mailto:pedrood@gmail.com">Bahman Pedrood (PhD Student)</a><br/>
		<a href="mailto:abagheri@masonlive.gmu.edu">Ali Bagheri Kaligh (PhD Student)</a><br/>
		<a href="mailto:smalek@gmu.edu">Sam Malek (Associate Professor)</a><br/>
		</td>
	</tr>-->
	<tr><td colspan="3"><br/><br/><hr size="2" width="100%"></td></tr>
	<tr>
		<td>
		 </td>
		 <td align="center"><a href="http://seal.ics.uci.edu/" target="_blank">Software Engineering Analysis Lab (SEAL) </a> </td>
		 <td></td>
	</tr>
</table>
<!--TABLE END-->
</body></html>