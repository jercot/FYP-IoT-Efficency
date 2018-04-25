DROP TABLE IF EXISTS FYP.Recording;
DROP TABLE IF EXISTS FYP.Room;
DROP TABLE IF EXISTS FYP.Building;
DROP TABLE IF EXISTS FYP.Login;
DROP TABLE IF EXISTS FYP.Password;
DROP TABLE IF EXISTS FYP.Account;
DROP SCHEMA IF EXISTS FYP;

CREATE SCHEMA FYP;

-- Table: FYP.Account
CREATE TABLE FYP.Account
(
    id serial NOT NULL,
    fName text COLLATE pg_catalog."default" NOT NULL,
	lName text COLLATE pg_catalog."default" NOT NULL,
    email text COLLATE pg_catalog."default" NOT NULL,
    phone text COLLATE pg_catalog."default",
	street text COLLATE pg_catalog."default",
	town text COLLATE pg_catalog."default" NOT NULL,
	county text COLLATE pg_catalog."default" NOT NULL,
    regDate bigint NOT NULL,
	twoStep text COLLATE pg_catalog."default" NOT NULL,
	UNIQUE (email),
    CONSTRAINT "Account_pkey" PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE FYP.Account
    OWNER to "Jer";
	
-- Table: FYP.Password
CREATE TABLE FYP.Password
(
    id serial NOT NULL,
    accountId integer NOT NULL,
    password text COLLATE pg_catalog."default" NOT NULL,
    date bigint NOT NULL,
    CONSTRAINT "Password_pkey" PRIMARY KEY (id),
    CONSTRAINT "passwordAccountId" FOREIGN KEY (accountId)
        REFERENCES FYP.Account (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE FYP.Password
    OWNER to "Jer";
	
-- Table: FYP.Login
CREATE TABLE FYP.Login
(
    id serial NOT NULL,
    accountId integer NOT NULL,
    dateTime bigint NOT NULL,
    location text COLLATE pg_catalog."default" NOT NULL,
    osBrowser text COLLATE pg_catalog."default" NOT NULL,
    device text COLLATE pg_catalog."default" NOT NULL,
    cookie text COLLATE pg_catalog."default" NOT NULL,
    expire bigint NOT NULL,
    type text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "Login_pkey" PRIMARY KEY (id),
    CONSTRAINT "loginAccountId" FOREIGN KEY (accountId)
        REFERENCES FYP.Account (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE FYP.Login
    OWNER to "Jer";
	
-- Table: FYP.Building
CREATE TABLE FYP.Building
(
    id serial NOT NULL,
    accountId integer NOT NULL,
    name text COLLATE pg_catalog."default" NOT NULL,
    location text COLLATE pg_catalog."default" NOT NULL,
	UNIQUE(accountId, name),
    CONSTRAINT "Building_pkey" PRIMARY KEY (id),
    CONSTRAINT "buildingAccountId" FOREIGN KEY (accountId)
        REFERENCES FYP.Account (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE FYP.Building
    OWNER to "Jer";
	
-- Table: FYP.Room
CREATE TABLE FYP.Room
(
    id serial NOT NULL,
    buildingId integer NOT NULL,
    name text COLLATE pg_catalog."default" NOT NULL,
    bucket text COLLATE pg_catalog."default",
    token text COLLATE pg_catalog."default",
    floor integer NOT NULL,
    notification integer NOT NULL,
	UNIQUE (buildingId, name),
    CONSTRAINT "Room_pkey" PRIMARY KEY (id),
    CONSTRAINT "roomBuildingId" FOREIGN KEY (buildingId)
        REFERENCES FYP.Building (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE FYP.Room
    OWNER to "Jer";
	
-- Table: FYP.Recording
CREATE TABLE FYP.Recording
(
    id serial NOT NULL,
    roomId integer NOT NULL,
    movement smallint NOT NULL,
    humidAve smallint NOT NULL,
    humidMed smallint NOT NULL,
    humidMin smallint NOT NULL,
    humidMax smallint NOT NULL,
    lightAve smallint NOT NULL,
    lightMed smallint NOT NULL,
    lightMin smallint NOT NULL,
    lightMax smallint NOT NULL,
    tempAve real NOT NULL,
    tempMed real NOT NULL,
    tempMin real NOT NULL,
    tempMax real NOT NULL,
    time bigint NOT NULL,
    CONSTRAINT "Recording_pkey" PRIMARY KEY (id),
    CONSTRAINT "Recording_roomId_fkey" FOREIGN KEY (roomId)
        REFERENCES FYP.Room (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE FYP.Recording
    OWNER to "Jer";