#!/bin/bash
clear
echo "Classes generation thru schemas XSD - JAXB"
echo ""
echo "Working dir"
pwd
echo ""

export DEFAULT_XSD_DIR="./schemas/nfe/PL_009_V4_V1.30"
export DEFAULT_PACKAGE_NAME="io.echosystem.ebr.nfe.bind.v4.sv130"
export DEFAULT_DESTINATION_DIR="./classes-geradas/v4130"

read -p "XSD files directory [$DEFAULT_XSD_DIR]: " XSD_DIR
read -p "Package name [$DEFAULT_PACKAGE_NAME]: " PACKAGE_NAME
read -p "Destination dir [$DEFAULT_DESTINATION_DIR]: " DESTINATION_DIR

if [ "$XSD_DIR" == "" ]; then
	export XSD_DIR=$DEFAULT_XSD_DIR
fi

if [ "$PACKAGE_NAME" == "" ]; then
	export PACKAGE_NAME=$DEFAULT_PACKAGE_NAME
fi

if [ "$DESTINATION_DIR" == "" ]; then
	export DESTINATION_DIR=$DEFAULT_DESTINATION_DIR
fi
echo ""
echo "Working arguments:"
echo ""
echo "XSD files directory: $XSD_DIR"
echo "Package name: $PACKAGE_NAME"
echo "Destination dir: $DESTINATION_DIR"
echo ""



if [ -f $DESTINATION_DIR ]; then
	echo "Destination dir already is a regular file"
	exit -1
fi

if [ -d $DESTINATION_DIR ]; then
	export OVERWRITE="?"
	while [[ "$OVERWRITE" != "y" && "$OVERWRITE" != "n" ]]
	do 
		read -p "Destination dir already exists, overwrite?[y/n]: " OVERWRITE
		if [[ "$OVERWRITE" != "y" && "$OVERWRITE" != "Y" ]]; then
			echo "Generation aborted."
			exit -1;
		fi
	done
	rm $DESTINATION_DIR/*
else
	mkdir $DESTINATION_DIR
fi


for XSD_FILE in $XSD_DIR/*; do
	echo $XSD_FILE
	xjc -d $DESTINATION_DIR -p $PACKAGE_NAME -encoding UTF-8 -npa -nv $XSD_FILE	
done

tree $DESTINATION_DIR
