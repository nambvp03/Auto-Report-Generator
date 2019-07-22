-- Table: public.upper_div_transcript_report

-- DROP TABLE public.upper_div_transcript_report;

CREATE TABLE public.upper_div_transcript_report
(
    sr integer NOT NULL,
    bronco_id integer NOT NULL,
    date_start date NOT NULL,
    final_date date,
    status character varying(30) COLLATE pg_catalog."default" NOT NULL,
    processor character varying(30) COLLATE pg_catalog."default" NOT NULL,
    wam character varying(30) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT upper_div_transcript_report_pkey PRIMARY KEY (sr)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.upper_div_transcript_report
    OWNER to postgres;

	


-- SEQUENCE: public.sr_seq

-- DROP SEQUENCE public.sr_seq;

CREATE SEQUENCE public.sr_seq
    INCREMENT 1
    START 442
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public.sr_seq
    OWNER TO postgres;
	
	
	
	
	
	
	
	
	
	
CREATE TABLE public.first_time_freshmen_report
(
    sr integer NOT NULL,
    bronco_id integer NOT NULL,
    final_date date,
    status character varying(30) COLLATE pg_catalog."default" NOT NULL,
    processor character varying(30) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT upper_div_transcript_report_pkey PRIMARY KEY (sr)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.first_time_freshmen_report
    OWNER to postgres;
