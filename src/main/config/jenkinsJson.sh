
#!/bin/sh

APP_HOME=`pwd`
APP_HOME="$APP_HOME"
APP_MAINCLASS=com.mind.xgame.excel2code.JenkinsExcel2Json

CLASSPATH=$APP_HOME 
for i in "$APP_HOME"/lib/*.*; do 
CLASSPATH="$CLASSPATH":"$i" 
done 

JAVA_OPTS="-ms512m -mx512m -Xmn256m -Djava.awt.headless=true -XX:MaxPermSize=128m" 


psid=0 

java $JAVA_OPTS -classpath $CLASSPATH $APP_MAINCLASS