This project requires a MySQL database and Java 8 only to run. Everything that is needed to run it will be contained in the compiled source code (the .jar file).

To run the application you must first create the MySQL database and user, _exactly as indicated here._ The database schema does NOT need to be created because it will be created when the application first starts up.

## DB creation instructions
1. Login as the root mysql user
2. Run these commands:

mysql> create database project3ag;
Query OK, 1 row affected (0.01 sec)

mysql> GRANT ALL ON project3ag.* TO 'project3ag'@'localhost' IDENTIFIED BY 'cs6310p3ag';
Query OK, 0 rows affected (0.01 sec)

##Environment variables
The Gurobi jar file is included in the application executable, but its environment variables still need to be set.  Make sure these variables are set
in your environment when you run the app as described below (i.e. these are present when running the "env" command):

1. GRB_LICENSE_FILE	(e.g. /home/ubuntu/gurobi.lic)
2. GUROBI_HOME (e.g. /opt/gurobi650/linux64)
3. LD_LIBRARY_PATH	(e.g. /opt/gurobi650/linux64/lib)
4. PATH	(e.g. /opt/gurobi650/linux64/bin)

##Starting up the application
Once the database is created exactly as above, you can run the app.

To run it, navigate to the directory that is provided in this source zip file:

cd dist/

Then issue this command to run the application using the medium-sized static source data:

./medium.sh

This command will execute the main jar file and take the following actions:

1. Create the database schema
2. Populate the database with the initial required data
3. Start up a webserver on port 8080
4. Log to system out in case of errors

Navigate to http://localhost:8080 and follow the instructions to login and use the app.