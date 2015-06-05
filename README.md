An exercise in java web development without frameworks or java build tools. Doesn't do anything.

WARNING
=======
Currently not open source: restrictively licensed according to LICENSE.txt. This application uses Creative Commons
by-nc-nd 4.0 to allow freely and safe viewing of the source code, but allows no other use, for example, no license is
granted to compile or run the code.

The reason for this is that the author is convinced that it would be a very bad idea to try and build anything useful on
top of this codebase. It's an exercise in how to build a minimalist micro service, not a ready to use framework. If you
do want to build something in this style, write your own code line by line.

Hosting
=======
Project hosted at github:

* https://github.com/lsimons/vid

Thoughts
========
Written in pure java as a servlet 3.0 application. 13 factor compatible.

Self-contained dependency free .war that can be installed on any server, including
	linux
	google app engine
	windows

Can use multiple storage backends, including
	google app engine
	mysql
	postgres
	sql server
	local filesystem

Can be fully configured from /etc or through the web interface.

Should be Java 8 but GAE is not Java 8 and GAE was a good exercise target.

Build
=====
*TL;DR*: ./build.sh dist 

Build using [build.sh](build.sh):

	./build.sh

See

    ./build.sh help

for options.

The build requires basic unix tools (as available out of the box on linux, mac, or installed on windows with
[cygwin](http://www.cygwin.com/), including bash, find, and rsync, as well as
[JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or
[OpenJDK 7](http://openjdk.java.net/install/index.html) 
or later, including java, javac and javadoc.

To build or run the fat client GUI you need javafx on your classpath, which requires JDK8 or later, or setting it
manually, for example if you have $JAVA_HOME set to point to your JDK 7,

  export CLASSPATH=$JAVA_HOME/jre/lib/jfxrt.jar

If you think that's stupid, I agree.
If you think GUI clients are stupid, try rm -Rf src/io/virga/vid/client. I won't be offended.
There's not even a build command to run the client anyway, that's an exercise for the reader.

Install
=======
*TL;DR*: build produces a standard java webapp that needs java 7 under out/.

For development/to try things out you don't need to install the application; you can run the application from the
command line using

    ./build.sh run

which will serve it up at

* [http://localhost:8080/](http://localhost:8080/)

To install on a server, install and start your favorite servlet engine, such as [tomcat](http://tomcat.apache.org/) and
then install the application by installing the .war file that the build generates into that servlet engine. If you need
a recommendation for what servlet engine to use, 

For example on mac

    ruby -e "$(curl -fsSL https://raw.github.com/Homebrew/homebrew/go/install)"
    brew install tomcat
    catalina start
    tomcat_home=`/usr/local/bin/catalina | grep CATALINA_BASE | cut -d ':' -f 2`
    ./build.sh war
    cp out/vid-*.war $tomcat_home/webapps/vid.war

Or, as another example on ubuntu linux

    apt-get install bash findutils rsync openjdk-7-jdk
    update-alternatives --set java /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
    update-alternatives --set javac /usr/lib/jvm/java-7-openjdk-amd64/bin/javac
    update-alternatives --set javadoc /usr/lib/jvm/java-7-openjdk-amd64/bin/javadoc
    apt-get install tomcat
    tomcat_home=/var/lib/tomcat7
    ./build.sh war
    cp out/vid-*.war $tomcat_home/webapps/vid.war

This installs the application under tomcat7 under

* http://localhost:8080/vid/

If you want to get rid of the /vid/ in the URL path, that's a standard servlet engine / tomcat configuration change.
[Stackoverflow lists some options](http://stackoverflow.com/questions/715506/tomcat-6-how-to-change-the-root-application/)
for how to do this.

If you want to get rid of the :8080 in the URL path, the recommended approach is to run the application server behind a
dedicated web server such as apache or nginx in a reverse proxy configuration. A good
[guide for running webapps behind apache](https://confluence.atlassian.com/display/DOC/Running+Confluence+behind+Apache)
is provided for atlassian confluence. Make sure to instruct your application server about running behind a proxy, for
example, for tomcat, make sure to
[set proxyName and proxyPort](http://tomcat.apache.org/tomcat-7.0-doc/proxy-howto.html).

If the application will be running on a network where using an outbound HTTP proxy is required, you need to configure
java itself to use that proxy, see
[the java networking and proxy guide](http://docs.oracle.com/javase/7/docs/technotes/guides/net/proxies.html).
If you cannot or do not wish to reconfigure java itself, you can provide the system properties to the tomcat startup
scripts. For example, on ubuntu linux, you can edit the JAVA_OPTS setting in the file

    /etc/default/tomcat7

and configure the proxy settings there.

It is recommended to set up the application to be available only over SSL (HTTPS), running behind a trusted SSL server
implementation such as apache. Proper configuration of SSL is beyond the scope of this documentation; see

* https://github.com/lsimons/sys-open

for a realistic example of a production configuration of apache+tomcat+SSL.

Google App Engine
-----------------
You probably do not want to use GAE SDK > 1.8.8, because 1.8.9 and onward require Java 7 -- and intellij on the mac
still requires Java 6; in general there's various other issues with JDK 7 on mac for GUI apps.

The app runs mostly out of the box on the GAE SDK. You need to edit [web.xml](web/WEB-INF/web.xml). Look for the line

    <url-pattern></url-pattern>

and, change it to

    <url-pattern>/</url-pattern>

Next, to upload to GAE, edit [appengine-web.xml](web/WEB-INF/appengine-web.xml) to set your <application/> ID, then use
a command sequence like

    ./build.sh war
    appcfg.sh update out/artifacts/web_war_exploded

Configure
---------
*TL;DR*: copy [src/vid.properties](src/vid.properties) to
/etc/vid/vid.properties and edit as needed.

Configuration can be defined in:

1. org.virga.Config static properties
2. vid.properties on the classpath (version is written here by build.sh)
3. environment variables (set by build.sh)
4. init-param settings for the ConfigFilter filter in web.xml
5. the file /etc/vid/vid.properties if it exists and is readable
6. a java properties file (.properties or .xml) specified as the init-param vid.properties.file for the ConfigFilter
   filter in web.xml, for example


      <filter>
        <filter-name>ConfigFilter</filter-name>
        <filter-class>io.virga.servlet.ConfigFilter</filter-class>
        <init-param>
          <param-name>vid.properties.file</param-name>
          <param-value>/usr/local/etc/vid/vid.xml</param-value>
        </init-param>
      </filter>
      <filter-mapping>
        <filter-name>ConfigFilter</filter-name>
        <url-pattern>/</url-pattern>
      </filter-mapping>

See [src/vid.properties](src/vid.properties) for all the supported properties. The above list is in order of priority,
that is, /etc/vid/vid.properties overrides any defaults, if it exists, and if you specify a vid.properties.file in the
web.xml that's the most authoritative source.

The recommended approach for configuration is to copy the default
[src/vid.properties](src/vid.properties) to
/etc/vid/vid.properties and then edit it.

The web application does not reload the configuration file(s) once it has been started up. Either restart your
application server or have your application server reload the web application. For most application servers (such as
tomcat), in their default configuration, you can force a reload by simply 'touch'ing the war file, i.e.
 
    touch /var/lib/tomcat7/webapps/vid.war

Configure logging
-----------------
*TL;DR*: logs use slf4j to write to stderr which goes to wherever the application server writes them, i.e. catalina.out
for tomcat. Set java system property org.slf4j.simpleLogger.log.io.virga=DEBUG to enable debug logging.

By default the application bundles [slf4j](http://www.slf4j.org/)-api and slf4j-simple, which writes logs to standard
error, which is redirected to a log file chosen by your application server. For apache tomcat, this is "catalina.out",
for example on ubuntu linux

    $tomcat_home/logs/catalina.out

is linked to

    /var/lib/tomcat7/logs

It's quite possible and perfectly acceptable to use this setup in a production configuration. You will usually get the
highest performance if you do not log to actual files. For example, see

* https://github.com/lsimons/sys-open/tree/master/puppet/modules/common/tomcat

for a setup that sends tomcat logs to syslog. It's recommended that you use a setup like this, so that you can configure
application logging in the same way as all the other logging on the server, rather than use any of the fancy java
logging tools.

If needed, you can somewhat [configure slf4j-simple](http://www.slf4j.org/apidocs/org/slf4j/impl/SimpleLogger.html)
using system properties, though you should not use slf4j-simple to log to files. For example, if you are running the
application under tomcat7 under ubuntu linux, you can enable debug logging using something like

    echo Enable slf4j simple debug logging >> /etc/default/tomcat7
    echo JAVA_ARGS="\$JAVA_ARGS -Dorg.slf4j.simpleLogger.log.io.virga=DEBUG" >> /etc/default/tomcat7
    service tomcat7 restart

On other platforms, you can customize tomcat with a setenv.sh file, for example on mac with homebrew

    tomcat_home=`/usr/local/bin/catalina | grep CATALINA_BASE | cut -d ':' -f 2`
    setenv=$tomcat_home/bin/setenv.sh
    echo Enable slf4j simple debug logging >> $setenv
    echo JAVA_ARGS="\$JAVA_ARGS -Dorg.slf4j.simpleLogger.log.io.virga=DEBUG" >> $setenv

If you wish to configure a different java logging framework, follow the [slf4j manual](http://www.slf4j.org/manual.html)
to do so. Remove the slf4j-impl jar file from [lib/](lib/) and replace it with the slf4j implementation of your choice
(such as [logback-classic](http://logback.qos.ch/)). You can place any configuration files (like logback.xml for
logback) needed by the chosen logging implementation in [src/](src/). Rebuild the war file using

    ./build.sh war

and copy the new war over the existing one.

If you hate slf4j for some reason and don't want to use it, remove the slf4j jars from lib/. The application then falls
back to logging to standard error. If you wish to plug in another logging abstraction, the place to do so is
[io.virga.log.LoggerFactory](src/io/virga/log/LoggerFactory.java). Happy hacking!

Developing using IntelliJ
=========================
This application is mostly developed using IntelliJ Ultimate Edition. Make sure you have a JDK defined named "1.7" and
a tomcat application server named "Tomcat 7". After that, you can open this directory as a project and everything
should work out of the box. The three most useful targets are
* Tomcat 7 (run in debug mode to debug server side and get auto-reloading)
* Tomcat 7 JavaScript (run in debug mode to debug client-side javascript)
* test coverage (run in coverage mode to run all tests and see code coverage)

You can also develop using the free IntelliJ Community Edition. The community edition does not provide built-in support
for building web applications, but you don't really need that support anyway. Instead, run the command

    ./build.sh run-watch

in the background to get most of the benefit of using the Ultimate edition: it will auto (re)compile and (re)package the
web application as you make changes, running it inside an embedded jetty instance, which you can then view at

* http://localhost:8080/

With the community edition, you can use the "remote debug" run target in debug mode to connect the debugger to the jetty
that's being run from the console. You'll need to run with -v -d:

    ./build.sh -v -d run-watch

Developing using VIM or Emacs
=============================
TL;DR: (cat build.sh; cat watcher/build.sh)

Hello dear unix hacker. Yes this project is written in java, but please give it 5 minutes anyway :-). It's comparatively
unix-friendly! For example, I hope you will appreciate the lack of ant, maven, gradle, grails, or various other java
build magic.

I recommend taking a look at how watcher/ is put together so you can replicate something similar for your own setup (or
use it as-is). The main gotcha is that web/min.js and web/min.js.map are kind-of considered source files (IntelliJ
kind-of works best that way) but nevertheless are auto-generated using the
[google closure compiler](https://code.google.com/p/closure-compiler/).

Please do respect the code style:

1. 4 spaces in java, 2 in xml/html/jsp/javascript
2. opening braces go end of line
3. wrap at 120 characters, do not align when wrapping
4. mostly standard java style guide otherwise
5. use java 7 style where applicable (generics with <>, catch multiple exception types in one catch block, etc)
6. see .idea/codeStyleSettings.xml for the rest

(The build is a shell script and not a makefile because javac and javadoc are very good at compiling when you give them
a complete list of source files (split across many directories) in one go; we're not switching to make.)

Developing using Eclipse
========================
See above, under IntelliJ. Eclipse is for masochists.
