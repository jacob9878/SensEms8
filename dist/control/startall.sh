#! /bin/sh

BIZEMS_HOME=/apps/sensems

$BIZEMS_HOME/control/emsd.sh startall
$BIZEMS_HOME/control/smtp.sh start