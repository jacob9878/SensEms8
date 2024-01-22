#! /bin/sh
BIZEMS=/apps/sensems
JAVA_BIN=/apps/java/bin

CLASSPATH=.
for i in $(ls $BIZEMS/lib/*.jar); do
  CLASSPATH=${CLASSPATH}:${i}
done

EMSD_PID=$BIZEMS/log/emsd.pid
SENDER_PID=$BIZEMS/log/sender.pid

CLASSPATH=$CLASSPATH
export CLASSPATH

#export LANG=ko_KR.eucKR

start_emsd() {
  PID=$(cat $EMSD_PID)
  if kill -0 $PID >/dev/null 2>&1; then
    echo "EMS Server is aleady running"
    exit 1
  else
    rm $EMSD_PID
  fi

  nohup $JAVA_BIN/java -Xmx128m -Xms64m -Dsensems.home=$BIZEMS com.imoxion.sensems.server.ImEmsServer &

  if [ ! -z "$EMSD_PID" ]; then
    echo $! >$EMSD_PID
  fi
}

start_sender() {
  PID=$(cat $SENDER_PID)
  if kill -0 $PID >/dev/null 2>&1; then
    echo "Sender Server is aleady running"
    exit 1
  else
    rm $SENDER_PID
  fi

  nohup $JAVA_BIN/java -Xmx128m -Xms64m -Dsensems.home=$BIZEMS com.imoxion.sensems.server.sender.ImSenderServer &

  if [ ! -z "$SENDER_PID" ]; then
    echo $! >$SENDER_PID
  fi
}

stop_emsd() {
  if [ -f $EMSD_PID ]; then
    kill -9 $(cat $EMSD_PID)
    rm $EMSD_PID
  fi
}

stop_sender() {
  if [ -f $SENDER_PID ]; then
    kill -9 $(cat $SENDER_PID)
    rm $SENDER_PID
  fi
}

case "$1" in
start)
  echo -n "Starting EMS server : "
  start_emsd
  echo "[" $(cat $EMSD_PID) "]"
  ;;
stop)
  echo -n "Stop EMS Server"
  stop_emsd
  ;;
startall)
  echo -n "Starting EMS & Sender server : "
  start_sender
  sleep 1
  start_emsd
  echo "[" $(cat $EMSD_PID) "]"
  ;;
stopall)
  echo -n "Stop EMS & Sender Server"
  stop_emsd
  stop_sender
  ;;
esac

exit 0
