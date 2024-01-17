CREATE TABLE users
(
    id                      CHAR(36)                NOT NULL,
    username                CHARACTER VARYING(255)  NOT NULL,
    user_handle             VARBINARY(255)          NOT NULL,
    assertion               MEDIUMTEXT,
    public_key_json         MEDIUMTEXT,
    registration_complete   BIT                     NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE passkey
(
    id              CHAR(36)                NOT NULL,
    key_id          VARBINARY(255)          NOT NULL,
    public_key      VARBINARY(255)          NOT NULL,
    signature_count INTEGER                 NOT NULL,
    transport       CHARACTER VARYING(255)  NOT NULL,
    type            CHARACTER VARYING(255)  NOT NULL,
    user_handle     VARBINARY(255)          NOT NULL,
    PRIMARY KEY (id)
);