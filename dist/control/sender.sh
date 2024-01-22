#! /bin/sh
BIZEMS=/apps/sensems

CLASSPATH=.
for i in `ls $BIZEMS/lib/*.jar`
do
  CLASSPATH=${CLASSPATH}:${i}
done

SENDER_PID=$BIZEMS/log/sender.pid

CLASSPATH=$CLASSPATH
export CLASSPATH

#export LANG=ko_KR.eucKR

start()
{
	PID=`cat $SENDER_PID`
	if kill -0 $PID > /dev/null 2>&1; then
		echo "Sender Server is aleady running"
		exit 1
	else
		rm $SENDER_PID
	fi

	nohup java -Xmx128m -Xms64m -Dsensems.home=$BIZEMS com.imoxion.sensems.server.sender.ImSenderServer &

	if [ ! -z "$SENDER_PID" ]; then
		echo $! > $SENDER_PID
	fi
}

stop()
{
	if [ -f $SENDER_PID ];then
		kill -9 `cat $SENDER_PID`
		rm $SENDER_PID
	fi
}

case "$1" in
    start)
	echo -n "Starting Sender server : "
	start
	;;
    stop)
	echo -n "Stop Sender Server"
	stop
	;;
esac

exit 0

