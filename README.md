# java-logfile-tail 
Inspired by [Websocket log tailer](https://github.com/davidmoten/websockets-log-tail)

Replace Jetty websockets dependency with standard javax.websockets

This project should run on any "javax.websockets" compatible containers. Tested on :
* Weblogic 12c
* Jetty
* Tomcat

Keyboard Shortcuts:
- Spacebar --> Toggle scrolling
- C --> Clear buffer

Enter filepath in the text field to change tailer target

Bookmark with file options index.html?file=/usr/logs/test.log

Screenshots

Build
---------

    git clone https://github.com/jkr888/java-logfile-tail
    cd java-logfile-tail
    mvn jetty:run
    
Go to [http://localhost:8080/index.html](http://localhost:8080/index.html)



