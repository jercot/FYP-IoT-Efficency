DROP TABLE IF EXISTS "FYP"."Room";DROP TABLE IF EXISTS "FYP"."Building";
DROP TABLE IF EXISTS "FYP"."Login";
DROP TABLE IF EXISTS "FYP"."Account";

-- Table: "FYP"."Account"
CREATE TABLE "FYP"."Account"
(
    id serial NOT NULL,
    name character varying(255)[] COLLATE pg_catalog."default" NOT NULL,
    email character varying(255)[] COLLATE pg_catalog."default" NOT NULL,
    phone integer,
    "regDate" bigint NOT NULL,
    CONSTRAINT "Account_pkey" PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE "FYP"."Account"
    OWNER to "Jer";
	
-- Table: "FYP"."Login"
CREATE TABLE "FYP"."Login"
(
    id serial NOT NULL,
    "accountId" integer NOT NULL,
    "dateTime" bigint NOT NULL,
    location character varying(255)[] COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "Login_pkey" PRIMARY KEY (id),
    CONSTRAINT "loginAccountId" FOREIGN KEY ("accountId")
        REFERENCES "FYP"."Account" (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE "FYP"."Login"
    OWNER to "Jer";
	
-- Table: "FYP"."Building"
CREATE TABLE "FYP"."Building"
(
    id serial NOT NULL,
    "accountId" integer NOT NULL,
    name character varying(255)[] COLLATE pg_catalog."default" NOT NULL,
    location character varying(255)[] COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "Building_pkey" PRIMARY KEY (id),
    CONSTRAINT "buildingAccountId" FOREIGN KEY ("accountId")
        REFERENCES "FYP"."Account" (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE "FYP"."Building"
    OWNER to "Jer";
	
-- Table: "FYP"."Room"
CREATE TABLE "FYP"."Room"
(
    id serial NOT NULL,
    "buildingId" integer NOT NULL,
    name character varying(255)[] COLLATE pg_catalog."default" NOT NULL,
    bucket character varying(20)[] COLLATE pg_catalog."default" NOT NULL,
    floor integer,
    CONSTRAINT "roomBuildingId" FOREIGN KEY (id)
        REFERENCES "FYP"."Building" (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE "FYP"."Room"
    OWNER to "Jer";