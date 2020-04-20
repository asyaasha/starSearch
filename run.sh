#!/bin/bash

sudo apt install maven
mvn clean install
mvn exec:java