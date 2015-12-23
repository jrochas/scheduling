#!/bin/bash

if [[ $(id -u) -ne 0 ]] ; then echo "Please run the installation script as root" ; exit 1 ; fi

SCRIPT_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

# installation of the ProActive distribution
 
read -e -p "Directory where to install the scheduler: " -i "/opt/proactive" PA_ROOT

PA_ROOT=$(echo $PA_ROOT | xargs)

mkdir -p $PA_ROOT
rm -f $PA_ROOT/default

CURRENT_PADIR="$(dirname "$SCRIPT_DIR")"
PA_FOLDER_NAME="$(basename "$CURRENT_PADIR")"

confirm() {
    # call with a prompt string or use a default
    read -r -p "${1:-Are you sure? [Y/n]} " yn
    yn=${yn,,}    # tolower
    yn=$(echo $yn | xargs)   # trim
    if [[ $yn =~ ^(yes|y|)$ ]]; then
            true
    else
            false
    fi
}

if [ -d "$PA_ROOT/$PA_FOLDER_NAME" ]; then
    if confirm "A folder $PA_ROOT/$PA_FOLDER_NAME already exists, delete its content and replace by a fresh install? [Y/n] " ; then
        rm -rf $PA_ROOT/$PA_FOLDER_NAME
        cp -f -R $CURRENT_PADIR $PA_ROOT
    fi
else
    cp -f -R $CURRENT_PADIR $PA_ROOT
fi



ln -s -f $PA_ROOT/$PA_FOLDER_NAME "$PA_ROOT/default"


# creation of the proactive user

read -e -p "Name of the user starting the ProActive service: " -i "proactive" USER

USER=$(echo $USER | xargs)

read -e -p "Group of the user starting the ProActive service: " -i "proactive" GROUP

GROUP=$(echo $GROUP | xargs)

if confirm "Do you want to create this user? [Y/n] "; then
    if [ "$GROUP" == "$USER" ]; then
        useradd $USER -d $PA_ROOT
    else
        useradd $USER -d $PA_ROOT -g $GROUP
    fi
    passwd $USER;
fi


chown -R $USER:$GROUP $PA_ROOT/$CURRENT_PA_DIR

# installation of the service script

cp $SCRIPT_DIR/proactive-scheduler /etc/init.d/

read -e -p "Protocol used by the proactive server: [http/https] " -i "http" PROTOCOL

PROTOCOL=$(echo $PROTOCOL | xargs)

read -e -p "Port used by the proactive server: " -i "8080" PORT

PORT=$(echo $PORT | xargs)

read -e -p "Number of ProActive nodes to start on the server machine: " -i "4" NB_NODES

NB_NODES=$(echo $NB_NODES | xargs)

if confirm "Setup cron task for cleaning old logs? [Y/n] " ; then
     read -e -p "Cleaning logs older than (in days) [50]: " -i "50" LOGS_CLEANUP_DAYS

     LOGS_CLEANUP_DAYS=$(echo $LOGS_CLEANUP_DAYS | xargs)

     echo "find $PA_ROOT/default/logs -mtime +$LOGS_CLEANUP_DAYS -name '*.log' -exec rm {} \;" >> /etc/crontab
fi



# Escape functions for sed 
escape_rhs_sed ()
{
        echo $(printf '%s\n' "$1" | sed 's:[\/&]:\\&:g;$!s/$/\\/')

}

sed "s/USER=.*/USER=$USER/g"  -i "/etc/init.d/proactive-scheduler"
sed "s/PROTOCOL=.*/PROTOCOL=$PROTOCOL/g"  -i "/etc/init.d/proactive-scheduler"
sed "s/PORT=.*/PORT=$PORT/g"  -i "/etc/init.d/proactive-scheduler"
sed "s/NB_NODES=.*/NB_NODES=$NB_NODES/g"  -i "/etc/init.d/proactive-scheduler"
sed "s/PA_ROOT=.*/PA_ROOT=$(escape_rhs_sed "$PA_ROOT")/g"  -i "/etc/init.d/proactive-scheduler"

chmod 700 /etc/init.d/proactive-scheduler

mkdir -p /var/log/proactive
touch /var/log/proactive/scheduler

chown -R $USER:$USER /var/log/proactive

if confirm "Start the proactive-scheduler service at startup? [Y/n] " ; then
    chkconfig proactive-scheduler on
fi



