<%@ page info="index" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false" %>

<%
String contextRoot = request.getContextPath();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="expires" content="0" />
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache, must-revalidate" />
    <link rel="icon" href="<%=contextRoot %>/favicon.ico" type="image/x-icon"/> 
    <link rel="shortcut icon" href="<%=contextRoot %>/favicon.ico" type="image/x-icon"/>
    <!-- SimpleTabs -->
	<link rel="stylesheet" media="screen" type="text/css" href="<%=contextRoot %>/simpletabs.css" />
	<script type="text/javascript" src="<%=contextRoot %>/simpletabs_1.3.packed.js"></script>
    <title>PlantUML logs</title>
</head>
<body>
<center><h2>PlantUML site logs</h2>
<p>Back to the <a href=/plantuml>server home page.</a></p>
<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
<!-- pstat1 -->
<ins class="adsbygoogle"
     style="display:inline-block;width:970px;height:90px"
     data-ad-client="ca-pub-5770515425712095"
     data-ad-slot="1822127509"></ins>
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


</center>
<%
if (net.sourceforge.plantuml.pstat.Stats.USE_STATS) {
	net.sourceforge.plantuml.pstat.tick.GraphData gd = net.sourceforge.plantuml.pstat.Stats.getInstance().getGraphData();
%>

<div class="simpleTabs">
    <ul class="simpleTabsNavigation">
        <li><a href="#">Last 10 minutes</a></li>
        <li><a href="#">Last 60 minutes</a></li>
        <li><a href="#">Last 48 hours</a></li>
        <li><a href="#">Last 15 days</a></li>
        <li><a href="#">Last 24 months</a></li>
     </ul>

<div class="simpleTabsContent">
<!-- 10 ------------------------ -->
	<h3>Diagrams per minutes</h3>
	<% gd.hminutes10().getHistogramBuilder().getHistogram(200, 30, 12).printSvg(out); %>

	<table border=0 cellspacing=0 cellpadding=6>
	<tr>
	<td>
	<h3>Image Server Generation</h3>
	<% gd.hminutes10().getChartImageGeneration(150).printSvg(out); %>
	</td>
	<td>
	<h3>Diagrams generated</h3>
	<% gd.minutes10().getChartType(150).printSvg(out); %>
	</td>
	</tr>
	<tr>
	<td>
	<h3>Page Load</h3>
	<% gd.bminutes10().getChartPageLoad(150).printSvg(out); %>
	</td>
	<td>
	<h3>Image Load</h3>
	<% gd.bminutes10().getChartImageLoad(150).printSvg(out); %>
	</td>
	</tr>
	<tr valign=top>
	<td valign=top>
	<h3>Browser</h3>
	<% gd.minutes10().getChartBrowser(150).printSvg(out); %>
	</td>
	<td valign=top>
	<h3>Operation System</h3>
	<% gd.minutes10().getChartOperationSystem(150).printSvg(out); %>
	</td>
	</tr>
	<tr valign=top>
	<td valign=top>
	<h3>Site using image generation</h3>
	<% gd.minutes10().getChartIncomming(150).printSvg(out); %>
	</td>
	</tr>
	</table>

	<h3>Country & Language</h3>
	<table border=0 cellspacing=0 cellpadding=6 valign=top>
	<tr valign=top>
	<td valign=top>
	<% gd.minutes10().getHistoListCountry(400, 20, 12).printSvg(out); %>
	</td>
	<td valign=top>
	<% gd.minutes10().getChartLanguage(150).printSvg(out); %>
	</td>
	</tr>
	</table>
<!-- 10 ------------------------ -->
</div>

<div class="simpleTabsContent">
<!-- 60 ------------------------ -->
	<h3>Diagrams per minutes</h3>
	<% gd.hminutes60().getHistogramBuilder().getHistogram(200, 18, 12).printSvg(out); %>

	<table border=0 cellspacing=0 cellpadding=6>
	<tr>
	<td>
	<h3>Image Server Generation</h3>
	<% gd.hminutes60().getChartImageGeneration(150).printSvg(out); %>
	</td>
	<td>
	<h3>Diagrams generated</h3>
	<% gd.minutes60().getChartType(150).printSvg(out); %>
	</td>
	</tr>
	<tr>
	<td>
	<h3>Page Load</h3>
	<% gd.bminutes60().getChartPageLoad(150).printSvg(out); %>
	</td>
	<td>
	<h3>Image Load</h3>
	<% gd.bminutes60().getChartImageLoad(150).printSvg(out); %>
	</td>
	</tr>
	<tr valign=top>
	<td valign=top>
	<h3>Browser</h3>
	<% gd.minutes60().getChartBrowser(150).printSvg(out); %>
	</td>
	<td valign=top>
	<h3>Operation System</h3>
	<% gd.minutes60().getChartOperationSystem(150).printSvg(out); %>
	</td>
	</tr>
	<tr valign=top>
	<td valign=top>
	<h3>Site using image generation</h3>
	<% gd.minutes60().getChartIncomming(150).printSvg(out); %>
	</td>
	</tr>
	</table>

	<h3>Country & Language</h3>
	<table border=0 cellspacing=0 cellpadding=6 valign=top>
	<tr valign=top>
	<td valign=top>
	<% gd.minutes60().getHistoListCountry(400, 20, 12).printSvg(out); %>
	</td>
	<td valign=top>
	<% gd.minutes60().getChartLanguage(150).printSvg(out); %>
	</td>
	</tr>
	</table>
<!-- 60 ------------------------ -->
</div>
    
<div class="simpleTabsContent">
<!-- 48 ------------------------ -->
	<h3>Diagrams per hour</h3>
	<% gd.hhours48().getHistogramBuilder().getHistogram(200, 22, 12).printSvg(out); %>

	<table border=0 cellspacing=0 cellpadding=6>
	<tr>
	<td>
	<h3>Image Server Generation</h3>
	<% gd.hhours48().getChartImageGeneration(150).printSvg(out); %>
	</td>
	<td>
	<h3>Diagrams generated</h3>
	<% gd.hours48().getChartType(150).printSvg(out); %>
	</td>
	</tr>
	<tr>
	<td>
	<h3>Page Load</h3>
	<% gd.bhours48().getChartPageLoad(150).printSvg(out); %>
	</td>
	<td>
	<h3>Image Load</h3>
	<% gd.bhours48().getChartImageLoad(150).printSvg(out); %>
	</td>
	</tr>
	<tr valign=top>
	<td valign=top>
	<h3>Browser</h3>
	<% gd.hours48().getChartBrowser(150).printSvg(out); %>
	</td>
	<td valign=top>
	<h3>Operation System</h3>
	<% gd.hours48().getChartOperationSystem(150).printSvg(out); %>
	</td>
	</tr>
	<tr valign=top>
	<td valign=top>
	<h3>Site using image generation</h3>
	<% gd.hours48().getChartIncomming(150).printSvg(out); %>
	</td>
	</tr>
	</table>

	<h3>Country & Language</h3>
	<table border=0 cellspacing=0 cellpadding=6 valign=top>
	<tr valign=top>
	<td valign=top>
	<% gd.hours48().getHistoListCountry(400, 20, 12).printSvg(out); %>
	</td>
	<td valign=top>
	<% gd.hours48().getChartLanguage(150).printSvg(out); %>
	</td>
	</tr>
	</table>
<!-- 48 ------------------------ -->
</div>

<div class="simpleTabsContent">
<!-- 15 ------------------------ -->
	<h3>Diagrams per day</h3>
	<% gd.hdays15().getHistogramBuilder().getHistogram(200, 50, 12).printSvg(out); %>

	<table border=0 cellspacing=0 cellpadding=6>
	<tr>
	<td>
	<h3>Image Server Generation</h3>
	<% gd.hdays15().getChartImageGeneration(150).printSvg(out); %>
	</td>
	<td>
	<h3>Diagrams generated</h3>
	<% gd.days15().getChartType(150).printSvg(out); %>
	</td>
	</tr>
	<tr>
	<td>
	<h3>Page Load</h3>
	<% gd.bdays15().getChartPageLoad(150).printSvg(out); %>
	</td>
	<td>
	<h3>Image Load</h3>
	<% gd.bdays15().getChartImageLoad(150).printSvg(out); %>
	</td>
	</tr>
	<tr valign=top>
	<td valign=top>
	<h3>Browser</h3>
	<% gd.days15().getChartBrowser(150).printSvg(out); %>
	</td>
	<td valign=top>
	<h3>Operation System</h3>
	<% gd.days15().getChartOperationSystem(150).printSvg(out); %>
	</td>
	</tr>
	<tr valign=top>
	<td valign=top>
	<h3>Site using image generation</h3>
	<% gd.days15().getChartIncomming(150).printSvg(out); %>
	</td>
	</tr>
	</table>

	<h3>Country & Language</h3>
	<table border=0 cellspacing=0 cellpadding=6 valign=top>
	<tr valign=top>
	<td valign=top>
	<% gd.days15().getHistoListCountry(400, 20, 12).printSvg(out); %>
	</td>
	<td valign=top>
	<% gd.days15().getChartLanguage(150).printSvg(out); %>
	</td>
	</tr>
	</table>
<!-- 15 ------------------------ -->
</div>
    
<div class="simpleTabsContent">
<!-- 31 ------------------------ -->
	<h3>Diagrams per month</h3>
	<%
		net.sourceforge.plantuml.pstat.tick.GraphDataLongTerm gdl = net.sourceforge.plantuml.pstat.Stats.getInstance().getGraphDataLongTerm();
	 gdl.getHistogramBuilder().getHistogram(400, 40, 12).printSvg(out);
	%>
<!-- 31 ------------------------ -->
</div>
</div>

<%
}
%>


</body>
</html>
