CREATE TABLE Account (
	id serial NOT NULL,
    fName text NOT NULL,
	lName text NOT NULL,
    email text NOT NULL,
    phone text,
	street text,
	town text NOT NULL,
	county text NOT NULL,
    regDate bigint NOT NULL,
	twoStep text NOT NULL,
	UNIQUE (email),
    PRIMARY KEY (id)
);

CREATE TABLE Password (
    id serial NOT NULL,
    accountId integer NOT NULL,
    password text NOT NULL,
    date bigint NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT "passwordAccountId" FOREIGN KEY (accountId)
        REFERENCES Account (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE Login (
    id serial NOT NULL,
    accountId integer NOT NULL,
    dateTime bigint NOT NULL,
    location text NOT NULL,
    osBrowser text NOT NULL,
    device text NOT NULL,
    cookie text NOT NULL,
    expire bigint NOT NULL,
    type text NOT NULL,
    CONSTRAINT "Login_pkey" PRIMARY KEY (id),
    CONSTRAINT "loginAccountId" FOREIGN KEY (accountId)
        REFERENCES Account (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE Token (
    id serial NOT NULL,
    accountId integer NOT NULL,
    expire bigint NOT NULL,
    code int NOT NULL,
    CONSTRAINT "Token_pkey" PRIMARY KEY (id),
    CONSTRAINT "tokenAccountId" FOREIGN KEY (accountId)
        REFERENCES Account (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE Building (
    id serial NOT NULL,
    accountId integer NOT NULL,
    name text NOT NULL,
    location text NOT NULL,
    CONSTRAINT "Building_pkey" PRIMARY KEY (id),
    CONSTRAINT "buildingAccountId" FOREIGN KEY (accountId)
        REFERENCES Account (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE Room (
    id serial NOT NULL,
    buildingId integer NOT NULL,
    name text NOT NULL,
    bucket text,
    token text,
    floor integer NOT NULL,
    notification integer NOT NULL,
    CONSTRAINT "Room_pkey" PRIMARY KEY (id),
    CONSTRAINT "roomBuildingId" FOREIGN KEY (buildingId)
        REFERENCES Building (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE Recording (
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
        REFERENCES Room (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);