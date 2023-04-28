<base href="<%= request.getContextPath() %>/" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache, must-revalidate" />
<meta name="color-scheme" content="light dark" />
<link rel="icon" href="favicon.ico" type="image/x-icon"/>
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
<link rel="stylesheet" href="plantuml.css" />
<script src="webjars/monaco-editor/0.36.1/min/vs/loader.js"></script>
<script src="plantumllanguage.js"></script>
<script src="plantuml.js"></script>
<script>
  const VERSION = <%= net.sourceforge.plantuml.version.Version.version() %>;
  const VERSION_STRING = "<%= net.sourceforge.plantuml.version.Version.versionString() %>";
</script>
