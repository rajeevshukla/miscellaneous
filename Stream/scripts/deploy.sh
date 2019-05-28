#!/bin/sh

# Set up initial variables
VOIP_JAR=$1
SERVERS=$2
DEPLOY_SERVICE_SCRIPT=$3
SERVICE_SCRIPT=$4
SPRING_PROFILE=$5
ENCRYPTION_KEY=$6


for SERVER in ${SERVERS//,/ }
do
    echo "Starting Deployment on $SERVER"
	
	echo "Creating required directory structure if not exist on $SERVER"
	ssh $SERVER 'mkdir -p ~/voip/scripts; mkdir -p ~/voip/logs'
	
	if [ $? != 0 ]
	then
	   exit 1
	fi
	
	if $DEPLOY_SERVICE_SCRIPT
	then
	  echo "Transferring $SERVICE_SCRIPT to $SERVER"
	  scp $SERVICE_SCRIPT $SERVER:~/voip/scripts/
	  
	  if [ $? != 0 ]
	  then
		exit 1
	  fi
	  
	  echo "Updating permissions for $SERVICE_SCRIPT on $SERVER"
	  ssh $SERVER 'cd ~/voip/scripts; chmod 744 voip-service.sh'
	  
	  if [ $? != 0 ]
	  then
		exit 1
	  fi
	fi
	
	echo "Stopping application on $SERVER"
	ssh $SERVER 'cd ~/voip/scripts; ./voip-service.sh stop'
	
	if [ $? = 2 ]
    then
      exit 1
    fi
	
	echo "Transferring $VOIP_JAR to $SERVER"
	scp $VOIP_JAR $SERVER:~/voip/
	
	if [ $? != 0 ]
    then
      exit 1
    fi
	
	echo "Starting application on $SERVER"
	ssh $SERVER "cd ~/voip/scripts; ./voip-service.sh start $SPRING_PROFILE $ENCRYPTION_KEY"
	
	if [ $? = 1 ]
    then
      exit 1
    fi
	
	echo "Completed Deployment on $SERVER"
done