<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
</head>
<body>
<h1></h1>
<h3></h3>
<br>

<form name="form1" method="GET" action="HelloServlet" id="form1">
<table>
<tr>
<td>Number</td><td><input type="text" name="phone" id="phone"/></td>
</tr>
<tr>
<td>Message</td><td><textarea rows="8" cols="60" name="message" id="message"></textarea></td>
</tr>
<tr>
<td></td><td><input type="submit" value="Send"/></td>
</tr>
<!-- <tr>
<td>Result</td><td><input type="text" style="height:500px" size="100" value="" id="result" disabled/></td>
</tr> -->
</table>
</form>

<p><p>



<form name="form2" method="GET" action="HelloServlet" id="form2">
<table>


<tr>
<td>Incoming<br>Message</td><td><textarea rows="40" cols="100" name="incoming" id="incoming"></textarea></td>
</tr>


</table>
</form>

</body>
</html>


<script type="text/javascript" src="jq.js"></script>

<script type="text/javascript">
 
 


var form = $('#form1');

var form2 = $('#form2');


refreshSentMessage();
setInterval( refreshSentMessage, 5000 );

function refreshSentMessage() {
	
		
	$.ajax({
		type: form2.attr('method'),
		url: form2.attr('action'),
		data: form2.serialize(),
		
		success: function (data) {
			$('#incoming').attr("value",data);
		}
	});
 
	return false;
	
}


form.submit(function () {
	
	$.ajax({
		type: form.attr('method'),
		url: form.attr('action'),
		data: form.serialize(),
		
		success: function (data) {
			//$('#result').attr("value",data);
	 		var res = data;
			switch ( res ) {
				case "sent" :
					alert("Message SENT!");
					break;
				case "nonum" :
					alert("No number input!");
					break;
			}
		}
	});
 
	return false;
});


</script>