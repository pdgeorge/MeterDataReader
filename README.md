# MeterDataReader
For reading meter data from AEMO files to a postgresql db using java.
For reference on file structure, please review: https://aemo.com.au/-/media/files/electricity/nem/retail_and_metering/metering-procedures/2017/mdff_specification_nem12_nem13_final_v102.pdf

# How to use
The following steps can be completed in any order:

* Have the sample data that you wish to use, saved to the file nem12samepledata.txt (Example data currently uploaded. If modifying, remember to follow guidelines of AEMO specifications).

* Create a postgresql table using the following statement:
```SQL
create table meter_readings ( 
id uuid default gen_random_uuid() not null,
"nmi" varchar(10) not null,
"timestamp" timestamp not null,
 "consumption" numeric not null,
constraint meter_readings_pk primary key (id),
constraint meter_readings_unique_consumption unique ("nmi", "timestamp")
);
```

* Create a .env file in the root directory with the following fields populated, where DB_USER and DB_PASSWORD is the authorised user and DB_URL and DB_DB is the DB the table has been created in:
  * DB_USER
  * DB_PASSWORD
  * DB_URL
  * DB_DB

## Building and running
To build and run: 
`mvn clean package`
`mvn exec:java`

To run tests:
`mvn clean`
`mvn test`

# Notes
A base level of validation has been added, however due to time constraints, it is not as thorough as would be done in a professional setting.