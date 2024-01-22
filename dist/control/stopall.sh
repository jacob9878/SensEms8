#! /bin/sh

BIZEMS_HOME=/apps/sensems

$BIZEMS_HOME/control/emsd.sh stopall
$BIZEMS_HOME/control/smtp.sh stop