<%-- Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" trimDirectiveWhitespaces="true" %>
<%@ page import="io.virga.model.Version" %>
<jsp:include page="/WEB-INF/header.jsp">
    <jsp:param name="title" value="About VID"/>
</jsp:include>
<p>Version <%= Version.getSystemVersion().getValue() %>
</p>
<jsp:include page="/WEB-INF/footer.jsp"/>
