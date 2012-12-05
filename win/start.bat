@echo off

set p=%~dp0
set lock=%p%db\mongod.lock

IF NOT EXIST %lock%  (
echo "in"
start %p%mongo\mongod.exe -dbpath=%p%\db
)

java.exe -jar %p%textManager.jar
%p%mongo\mongo.exe admin --eval "db.shutdownServer()"

timeout /T 2
del %lock%