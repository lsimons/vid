<%-- Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" trimDirectiveWhitespaces="true" %>
<%@ page import="io.virga.convert.HTML" %>
<%@ page import="io.virga.vid.servlet.PageServlet" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="io.virga.model.LinkSection" %>
<%
    PrintWriter pw = new PrintWriter(out);
    HTML.header(pw, request.getParameter("title"));
    LinkSection section = PageServlet.menu(request);
    HTML.linkSection(pw, section.getTitle(), section.getLinks());
    
%>
