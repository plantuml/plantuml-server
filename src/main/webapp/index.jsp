<%@ page info="index" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<%
	long date0 = System.currentTimeMillis();
String token = net.sourceforge.plantuml.pstat.Stats.getInstance().getNewToken(date0);

String contextRoot = request.getContextPath();
String host = "http://" + request.getServerName() + ":" + request.getServerPort();
String encoded = "";
String umltext = "";
String imgurl = "";
String svgurl = "";
String txturl = "";
String mapurl = "";
Object mapNeeded = request.getAttribute("net.sourceforge.plantuml.servlet.mapneeded");
Object encodedAttribute = request.getAttribute("net.sourceforge.plantuml.servlet.encoded");
if (encodedAttribute != null) {
    encoded = encodedAttribute.toString();
    if (!encoded.isEmpty()) {
	    imgurl = host + contextRoot + "/img/" + encoded;
	    svgurl = host + contextRoot + "/svg/" + encoded;
	    txturl = host + contextRoot + "/txt/" + encoded;
        if (mapNeeded != null) {
            mapurl = host + contextRoot + "/map/" + encoded;
        }
	}
}
Object decodedAttribute = request.getAttribute("net.sourceforge.plantuml.servlet.decoded");
if (decodedAttribute != null) {
	umltext = decodedAttribute.toString();
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="expires" content="0" />
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache, must-revalidate" />
    <link rel="stylesheet" href="<%=contextRoot %>/plantuml002.css" type="text/css"/>
    <link rel="icon" href="<%=contextRoot %>/favicon.ico" type="image/x-icon"/> 
    <link rel="shortcut icon" href="<%=contextRoot %>/favicon.ico" type="image/x-icon"/>
    <title>PlantUMLServer</title>
<!-- page load timer -->
<script type='text/javascript'>
var starttime = new Date().getTime();
function getXMLHttpRequest() 
{
    if (window.XMLHttpRequest) {
        return new window.XMLHttpRequest;
    }
    else {
        try {
            return new ActiveXObject("MSXML2.XMLHTTP.3.0");
        }
        catch(ex) {
            return null;
        }
    }
}

function wndwidth(){
	var w = 0;
	//IE
	if(!window.innerWidth){
	    if(!(document.documentElement.clientWidth == 0)){
	        //strict mode
	        w = document.documentElement.clientWidth;
	    } else{
	        //quirks mode
	        w = document.body.clientWidth;
	    }
	} else {
	    //w3c
	    w = window.innerWidth;
	}
	return w
}

function wndheight(){
	var h = 0;
	//IE
	if(!window.innerHeight){
	    if(!(document.documentElement.clientHeight == 0)){
	        //strict mode
	        h = document.documentElement.clientHeight;
	    } else{
	        //quirks mode
	        h = document.body.clientHeight;
	    }
	} else {
	    //w3c
	    h = window.innerHeight;
	}
	return h;
}

</script>
<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-16629806-2']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
</head>
<body>
<div id="header">
    <%-- PAGE TITLE --%>
    <h1>PlantUML Server</h1>
<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
<!-- pstat2 -->
<ins class="adsbygoogle"
     style="display:inline-block;width:970px;height:90px"
     data-ad-client="ca-pub-5770515425712095"
     data-ad-slot="9727170700"></ins>
<script>
(adsbygoogle = window.adsbygoogle || []).push({});
</script>
<table border=0 cellspacing=0 cellpadding=0>
<tr>
<td width="150">
<div id="sidesoc1">
<script type="text/javascript">
/* <![CDATA[ */
    (function() {
        var s = document.createElement('script'), t = document.getElementsByTagName('script')[0];
        s.type = 'text/javascript';
        s.async = true;
        s.src = 'http://api.flattr.com/js/0.6/load.js?mode=auto';
        t.parentNode.insertBefore(s, t);
    })();
/* ]]> */</script>
<a class="FlattrButton" style="display:none;" rev="flattr;button:compact;" href="http://plantuml.sourceforge.net/"></a>
</div>

</td>
<td width="150">

<div id="sidesoc2">
<script src="//platform.linkedin.com/in.js" type="text/javascript"></script>
<script type="IN/Share" data-url="http://plantuml.sourceforge.net" data-counter="right"></script>
</div>

</td>
<td width="150">

<div id="sidesoc3">
<a href='https://twitter.com/share' class='twitter-share-button' data-lang='en' data-url='http://plantuml.sourceforge.net'
 data-text='Trying PlantUML' data-count='horizontal' data-size='medium'></a>
<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src='https://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document,'script','twitter-wjs');</script>
</div>

</td>
<td width="150">

<div id="sidesoc4">
<div id='fb-root'></div>
<script>(function(d, s, id) {
var js, fjs = d.getElementsByTagName(s)[0];
if (d.getElementById(id)) return;
js = d.createElement(s); js.id = id;
js.src = '//connect.facebook.net/en_US/all.js#xfbml=1';
fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>
<div class='fb-like' data-href='http://plantuml.sourceforge.net' data-send='false' data-layout='button_count' data-width='110' data-show-faces='false'></div>
</div>

</td>
<td width="150">

<div id="sidesoc5">
<div class='g-plusone' data-href='http://plantuml.sourceforge.net' data-size='medium' data-annotation='bubble' ></div>
<script>
(function() {
var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
po.src = 'https://apis.google.com/js/plusone.js';
var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);
})();
</script>
</div>

</td>
</tr>
</table>
    <p>This application provides a servlet which serves images created by <a href="http://plantuml.sourceforge.net">PlantUML</a>.<br>
    (Check <a href=log.jsp>detailed site logs here</a>.)</p>
</div>
<div id="content">
    <%-- CONTENT --%>
    <form method="post" accept-charset="UTF-8"  action="<%=contextRoot %>/form">
        <p>
            <textarea name="text" cols="120" rows="10"><%=umltext %></textarea>
            <br/>
            <input type="submit" />
        </p>
    </form>
    <hr/>
    You can enter here a previously generated URL:
    <form method="post" action="<%=contextRoot %>/form">
        <p>
            <input name="url" type="text" size="150" value="<%=imgurl %>" />
            <br/>
            <input type="submit"/>
        </p>
    </form>
        
    <% if ( !imgurl.isEmpty()) { %>
    <hr/>
    <a href="<%=svgurl%>">View as SVG</a>&nbsp;
    <a href="<%=txturl%>">View as ASCII Art</a>
    <% if (mapNeeded != null) { %>
    <a href="<%=mapurl%>">View Map Data</a>
    <% } //endif %>
    <% if (!encoded.isEmpty()) { %>
    <p align=center>
        <a href="//www.pinterest.com/pin/create/button/?url=http%3A%2F%2Fplantuml.sourceforge.net%2F&media=http%3A%2F%2Fwww.plantuml.com%2Fplantuml%2Fimg%2F<%=encoded %>&description=PlantUML"
 data-pin-do="buttonPin" data-pin-config="none"><img border=0 src="//assets.pinterest.com/images/pidgets/pin_it_button.png" /></a>
 </p>
    <% } //endif %>
<script>
function doneLoading() {
    var loadtime = new Date().getTime();
    var loadduration = loadtime - starttime;
    document.getElementById('loadtime2').innerHTML = 'image loaded in '+ loadduration +' ms';
    var r1 = getXMLHttpRequest();
    if (r1 != null) {
      r1.open("GET", "/plantuml/sx1?t=<%=token %>&s="+starttime+"&i="+loadtime+"&w="+wndwidth()+"&h="+wndheight(), true);
      r1.send();
    }
};    
</script>
    <p id="diagram">
        <% if (mapNeeded != null) { %>
        <img src="<%=imgurl %>" alt="PlantUML diagram" usemap="#umlmap"  onload="doneLoading()" />
        <map name="umlmap">
            <c:import url="<%=mapurl %>" />
        </map>
        <% } else { %>
        <img src="<%=imgurl %>" alt="PlantUML diagram"  onload="doneLoading()" />
        <% } %>
    </p>
    <% } //endif %>
</div>
<div id="side2">
<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
<!-- ServletSkyscraper1 -->
<ins class="adsbygoogle"
     style="display:inline-block;width:160px;height:600px"
     data-ad-client="ca-pub-5770515425712095"
     data-ad-slot="9576384701"></ins>
<script>
(adsbygoogle = window.adsbygoogle || []).push({});
</script>
</div>
<!-- This comment is used by the TestProxy class  
@startuml
Bob -> Alice : hello
@enduml
-->
<div id="loading">(<i id="loadtime0"></i>&nbsp;/&nbsp;<i id="loadtime1"></i>&nbsp;/&nbsp;<i id="loadtime2"></i>)</div>
<%-- FOOTER --%>
<%@ include file="footer.jspf" %> 

<!-- Start of StatCounter Code for Default Guide -->
<script type="text/javascript">
var sc_project=9301480; 
var sc_invisible=1; 
var sc_security="6eff847c"; 
var scJsHost = (("https:" == document.location.protocol) ?
"https://secure." : "http://www.");
document.write("<sc"+"ript type='text/javascript' src='" +
scJsHost+
"statcounter.com/counter/counter.js'></"+"script>");
</script>
<noscript><div class="statcounter"><a title="web analytics"
href="http://statcounter.com/" target="_blank"><img
class="statcounter"
src="http://c.statcounter.com/9301480/0/6eff847c/1/"
alt="web analytics"></a></div></noscript>
<!-- End of StatCounter Code for Default Guide -->

<!-- page load timer start -->
<script type='text/javascript'>
var endtime = new Date().getTime();
var endduration = endtime - starttime;
document.getElementById('loadtime1').innerHTML = 'page loaded in '+ endduration +' ms';
<%long date1 = System.currentTimeMillis()+10; 
long duration = date1 - date0;
net.sourceforge.plantuml.pstat.Stats.getInstance().logHtmlCreate(token, date0, date1, encoded, request);%>
document.getElementById('loadtime0').innerHTML = 'Page generated in <%=duration%> ms';

var r2 = getXMLHttpRequest();
if (r2 != null) {
  r2.open("GET", "/plantuml/sx1?t=<%=token %>&s="+starttime+"&p="+endtime+"&w="+wndwidth()+"&h="+wndheight() , true);
  r2.send();
}
</script>
<!-- page load timer end -->

</body>
</html>
