<html>
<head>
    <title>Access Management Service UI</title>
    <link href='//fonts.googleapis.com/css?family=Droid+Sans:400,700' rel='stylesheet' type='text/css'/>
    <link href='css/reset.css' media='screen' rel='stylesheet' type='text/css'/>
    <link href='css/screen.css' media='screen' rel='stylesheet' type='text/css'/>
    <link href='css/rubicon.css' media='screen' rel='stylesheet' type='text/css'/>
    <link href='css/reset.css' media='print' rel='stylesheet' type='text/css'/>
    <link href='css/screen.css' media='print' rel='stylesheet' type='text/css'/>
    <link href='css/rubicon.css' media='print' rel='stylesheet' type='text/css'/>
    <script type="text/javascript" src="lib/shred.bundle.js"></script>
    <script src='lib/jquery-1.8.0.min.js' type='text/javascript'></script>
    <script src='lib/jquery.slideto.min.js' type='text/javascript'></script>
    <script src='lib/jquery.wiggle.min.js' type='text/javascript'></script>
    <script src='lib/jquery.ba-bbq.min.js' type='text/javascript'></script>
    <script src='lib/handlebars-1.0.0.js' type='text/javascript'></script>
    <script src='lib/underscore-min.js' type='text/javascript'></script>
    <script src='lib/backbone-min.js' type='text/javascript'></script>
    <script src='lib/swagger.js' type='text/javascript'></script>
    <script src='swagger-ui.js' type='text/javascript'></script>
    <script src='lib/highlight.7.3.pack.js' type='text/javascript'></script>

    <script type="text/javascript">
        $(function () {
            window.swaggerUi = new SwaggerUi({
                url: document.location.protocol +"//" + document.location.host + "/access/apidocs/",
                apiKeyName: "access_token",
                dom_id:"swagger-ui-container",
                supportHeaderParams: false,
                supportedSubmitMethods: ['get', 'post', 'put', 'delete'],
                onComplete: function(swaggerApi, swaggerUi){
                    for (var i=0; i < swaggerApi.apisArray.length; i++) {
                        //console.log(swaggerApi.apisArray[i].basePath);
                        swaggerApi.apisArray[i].basePath=swaggerApi.apisArray[i].basePath.replace("http://<base>",document.location.protocol +"//" + document.location.host + "/access");
                        swaggerApi.apisArray[i].name=swaggerApi.apisArray[i].name.replace(".json","");
                        //console.log(swaggerApi.apisArray[i].basePath);
                    }

                    log("Loaded SwaggerUI");
                    $('pre code').each(function(i, e) {hljs.highlightBlock(e)});

                },
                onFailure: function(data) {
                    log("Unable to Load SwaggerUI");
                },
                docExpansion: "none"
            });

            $('#input_apiKey').change(function() {
                var key = $('#input_apiKey')[0].value;
                log("key: " + key);
                if(key && key.trim() != "") {
                    log("added key " + key);
                    window.authorizations.add("key", new ApiKeyAuthorization("access_token", key, "query"));
                }
            })

            window.swaggerUi.load();
        });



    </script>
</head>

<body class="swagger-section">
<div id='header'>
    <div class="swagger-ui-wrap">
        <a id="rubilogo" href="http://www.rubiconproject.com">Access Management Service API</a>

        <form id='api_selector'>
            <div class='input'><input id="input_baseUrl" name="baseUrl" type="hidden" value="<%=request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath())%>/access/apidocs/"/></div>
            <div class='input'><input placeholder="access_token" id="input_apiKey" name="apiKey" type="text"/></div>
            <div class='input'><a id="explore" href="#">Update Access Token</a></div>
        </form>
    </div>
</div>

<div id="message-bar" class="swagger-ui-wrap">
    &nbsp;
</div>

<div id="swagger-ui-container" class="swagger-ui-wrap">

</div>

</body>

</html>
