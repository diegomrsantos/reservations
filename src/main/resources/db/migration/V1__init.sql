CREATE TABLE "public"."reservation" (
    "id" uuid NOT NULL,
    "arrival" timestamp,
    "canceled" boolean,
    "departure" timestamp,
    "email" character varying(255),
    "firstname" character varying(255),
    "lastname" character varying(255),
    "version" bigint,
    CONSTRAINT "reservation_pkey" PRIMARY KEY ("id"),
    EXCLUDE USING gist (tsrange("arrival", "departure", '[]') WITH &&)
    WHERE (canceled != true)
) WITH (oids = false);

CREATE INDEX "idx_arrival_departure" ON "public"."reservation" USING btree ("arrival", "departure");
