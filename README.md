# java-logfile-tail 
Inspired by [Websocket log tailer](https://github.com/davidmoten/websockets-log-tail)

Replace Jetty websockets dependency with standard javax.websockets

Screenshot
----------
![Example Weblogic AdminServer log](https://github.com/jkr888/java-logfile-tail/blob/master/picture/screenshot-01.png?raw=true)

Platform Tested
---------------
  * Weblogic 12c
  * Jetty 9.4.x
  * Tomcat 9.x
  * Springboot executable ( upcoming )

Features
--------
  * Scrolling On/OFF
  * Insert marker line
  * Clear buffer

Build
---------
    git clone https://github.com/jkr888/java-logfile-tail
    cd java-logfile-tail
    mvn clean jetty:run
    
Go to [http://localhost:8080/index.html](http://localhost:8080/index.html)



