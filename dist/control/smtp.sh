#! /bin/sh
BIZEMS=/apps/sensems
JAVA_HOME=/usr/lib/jvm/java

CLASSPATH=.
for i in `ls $BIZEMS/lib/*.jar`
do
  CLASSPATH=${CLASSPATH}:${i}
done

SMTP_PID=$BIZEMS/log/smtp.pid

CLASSPATH=$CLASSPATH
export CLASSPATH

start_smtp()
{
	echo -n "Starting SensEms SMTP Server : "
	PID=`cat $SMTP_PID`
        if kill -0 $PID > /dev/null 2>&1; then
                echo "SMTP Server is aleady running"
                exit 1
        else
                rm $SMTP_PID
        fi

	nohup $JAVA_HOME/bin/java -Dsensems.home=$BIZEMS -Ddns.cache.ttl=120 -Djava.net.preferIPv4Stack=true -Xmx512m -Xms256m -XX:MaxMetaspaceSize=128m com.imoxion.sensems.server.nio.ImSensSmtpApplication &

	if [ ! -z "$SMTP_PID" ]; then
		echo $! > $SMTP_PID
	fi

	echo $SMTP_PID
	echo "Smtp Server Start OK"
}

start_dbsend()
{
	echo -n "Starting DBSend Agent Server : "
	PID=`cat $DBSEND_PID`
        if kill -0 $PID > /dev/null 2>&1; then
                echo "DBSend Agent Server is aleady running"
                exit 1
        else
                rm $DBSEND_PID
        fi

	nohup $JAVA_HOME/bin/java -Dsensems.home=$BIZEMS -Djava.net.preferIPv4Stack=true -Xmx256m -Xms128m -XX:MaxMetaspaceSize=64m com.imoxion.sensems.ImDBSendAgent &

	if [ ! -z "$DBSEND_PID" ]; then
		echo $! > $DBSEND_PID
	fi

	echo $DBSEND_PID
	echo "DBSend Agent Server Start OK"
}

start_daemon()
{
	echo -n "Starting SensProxy TaskDaemon Server : "
	PID=`cat $DAEMON_PID`
        if kill -0 $PID > /dev/null 2>&1; then
                echo "TaskDaemon Server is aleady running"
                exit 1
        else
                rm $DAEMON_PID
        fi

	nohup $JAVA_HOME/bin/java -Dsensems.home=$BIZEMS -Djava.net.preferIPv4Stack=true -Xmx32m -Xms32m -XX:MaxMetaspaceSize=64m com.imoxion.sensems.server.daemon.TaskDaemon &

	if [ ! -z "$DAEMON_PID" ]; then
		echo $! > $DAEMON_PID
	fi

	echo $DAEMON_PID
	echo "TaskDaemon Server Start OK"
}

stop_smtp()
{
        echo "STOP SensEMS Smtp Server"
        if [ -f $SMTP_PID ];then
                kill -15 `cat $SMTP_PID`
        fi
}

stop_agent()
{
        echo "STOP Agent Server"
        if [ -f $AGNT_PID ];then
                kill -15 `cat $AGNT_PID`
        fi
}

stop_dbsend()
{
        echo "STOP DBSend Agent Server"
        if [ -f $DBSEND_PID ];then
                kill -15 `cat $DBSEND_PID`
        fi
}

stop_daemon()
{
        echo "STOP TaskDaemon Server"
        if [ -f $DAEMON_PID ];then
                kill -15 `cat $DAEMON_PID`
        fi
}

case "$1" in
    start)
		start_smtp
		start_dbsend
		start_daemon
	;;
    stop)
		stop_dbsend
		stop_smtp
		stop_daemon
	;;
	startsmtp)
		start_smtp
	;;
	startdbsend)
		start_dbsend
	;;
  startdaemon)
		start_daemon
	;;
	stopsmtp)
		stop_smtp
	;;
	stopdbsend)
		stop_dbsend
	;;
  stopdaemon)
		stop_daemon
	;;
esac

exit 0