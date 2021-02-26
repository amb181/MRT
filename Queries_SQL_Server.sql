USE metrics_db;

CREATE TABLE IF NOT EXISTS users (
	Signum varchar(7) NOT NULL PRIMARY KEY,
    Last_Name varchar(40) NOT NULL,
    Name varchar(40) NOT NULL,
    Customer_Unit varchar(30),
    Team varchar(20) NOT NULL,
    Organization varchar(50) NOT NULL,
    Line_Manager varchar(7) NOT NULL,
    Access int NOT NULL,
    Supporting_CU varchar(30)
);

CREATE TABLE IF NOT EXISTS tasks_sourcing (
	Task_ID varchar(20) NOT NULL PRIMARY KEY,
    Task varchar(100) NOT NULL,
    Mailbox_Associated varchar(25) NOT NULL,
    Team varchar(20) NOT NULL,
    Network int NOT NULL,
    SAP_Billable varchar(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS networks (
	PD varchar(20) NOT NULL PRIMARY KEY,
    Network INT NOT NULL,
    Activity_code INT,
    Region varchar(30),
    Market varchar(100),
    Customer varchar(100),
    Responsible varchar(100) NOT NULL,
    Subnetwork INT NOT NULL
);

CREATE TABLE IF NOT EXISTS metrics_sourcing (
	id int AUTO_INCREMENT NOT NULL PRIMARY KEY,
    Region varchar(10) NOT NULL,
    Organization varchar(50) NOT NULL,
    Signum varchar(7) NOT NULL,
    Name varchar (60) NOT NULL,
    Requestor varchar(50),
    Task_ID varchar(50) NOT NULL,
    Task varchar(100) NOT NULL,
    Network INT NOT NULL,
    Subnetwork INT NOT NULL,
    Activity_code INT,
    SAP_Billing varchar(12) NOT NULL,
    Work_date date NOT NULL,
    Logged_Time decimal(6,2) NOT NULL,
    Week int NOT NULL,
    FTR varchar(3) NOT NULL,
    On_Time varchar(3) NOT NULL,
    Failed_FTR_Category varchar(30) NOT NULL,
    Failed_On_Time varchar(3) NOT NULL,
    Num_requests int,
    Comments varchar(120),
    FOREIGN KEY(Signum) REFERENCES users(Signum)
);


SELECT id, Organization, Signum, Name, Requestor, Task_ID, Task, Network, Subnetwork, SAP_Billing, 
date_format(Work_date,'%d/%m/%Y') as Work_date, Logged_Time, Week, FTR, On_Time, Failed_FTR_Category, 
Failed_On_Time, Num_requests, Comments 
FROM metrics_sourcing 
WHERE Signum='ealloem' AND Work_date='2020-08-24' AND Task LIKE '%%';

delete from metrics_sourcing where id IN (2283, 2284, 2285, 2286);



DELIMITER //
CREATE PROCEDURE register_metrics(
    _Region varchar(10),
    _Organization varchar(50),
    _Signum varchar(7),
    _Name varchar (60),
    _Requestor varchar(50),
    _Task_ID varchar(50),
    _Task varchar(100),
    _Network INT,
    _Subnetwork INT,
    _Activity_code INT,
    _SAP_Billing varchar(12),
    _Work_date date,
    _Logged_Time decimal(6,2),
    _Week int,
    _FTR varchar(3),
    _On_Time varchar(3),
    _Failed_FTR_Category varchar(30),
    _Failed_On_Time varchar(3),
    _Num_requests int,
    _Comments varchar(120),
    OUT Result INT
	) BEGIN
		DECLARE	TOTAL_TIME DECIMAL(6,2) DEFAULT 0;
        START TRANSACTION;
        INSERT INTO metrics_sourcing VALUES (null, _Region, _Organization, _Signum, _Name, _Requestor, _Task_ID, 
			_Task, _Network, _Subnetwork, _Activity_code, _SAP_Billing, _Work_date, _Logged_time, _Week, _FTR, _On_Time, 
			_Failed_FTR_Category, _Failed_On_Time, _Num_requests, _Comments);
        SELECT SUM(Logged_time) INTO TOTAL_TIME FROM metrics_sourcing WHERE Work_date = _Work_date AND Signum=_Signum;
        IF(TOTAL_TIME > 24.00) THEN
            SET Result = 0;
			ROLLBACK;
		ELSE 
			SET Result = 1;
			COMMIT;
		END IF;
END; //
-- DROP PROCEDURE register_metrics;

SELECT SUM(Logged_Time) FROM metrics_sourcing WHERE Work_date = '2020-08-25';

DELETE FROM metrics_sourcing WHERE Work_date = '2020-08-25';