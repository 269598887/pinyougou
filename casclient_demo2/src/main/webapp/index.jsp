<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>cas client demo2</title>
</head>
<body>
欢迎来到二品优购<%=request.getRemoteUser()%>
<a href="http://localhost:9100/cas/logout?service=http://www.baidu.com">退出登录</a>
</body>
</html>
