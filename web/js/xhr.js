/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
window.XMLHttpRequest = window.XMLHttpRequest || function() {
  // http://blogs.msdn.com/b/xmlteam/archive/2006/10/23/using-the-right-version-of-msxml-in-internet-explorer.aspx
  /*global ActiveXObject*/
  try {
    return new ActiveXObject("MSXML2.XMLHTTP.6.0");
  } catch (e1) {
  }
  try {
    return new ActiveXObject("MSXML2.XMLHTTP.3.0");
  } catch (e2) {
  }
  throw Error("This browser does not support XMLHttpRequest.");
};
XMLHttpRequest['UNSENT'] = 0;
XMLHttpRequest['OPENED'] = 1;
XMLHttpRequest['HEADERS_RECEIVED'] = 2;
XMLHttpRequest['LOADING'] = 3;
XMLHttpRequest['DONE'] = 4;
