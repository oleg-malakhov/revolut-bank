CREATE TABLE IF NOT EXISTS clients(
  id  INTEGER AUTO_INCREMENT(1, 1),
  passport  VARCHAR(80) UNIQUE,
  name  VARCHAR(80) NOT NULL,
  surname VARCHAR(80) NOT NULL,

  PRIMARY KEY (id),
);

CREATE UNIQUE INDEX IF NOT EXISTS indx_clients_passport ON clients(passport);


CREATE TABLE IF NOT EXISTS accounts(
  id  INTEGER AUTO_INCREMENT(1, 1),
  client_id INTEGER NOT NULL,
  name  VARCHAR(80),
  balance DECIMAL(19, 4)  NOT NULL  DEFAULT 0.0,

  PRIMARY KEY (id),
  FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE INDEX IF NOT EXISTS indx_accounts_client ON accounts(client_id);


CREATE TABLE IF NOT EXISTS transfers(
  account_from  INTEGER NOT NULL,
  account_to  INTEGER NOT NULL,
  timestamp DATETIME  NOT NULL  DEFAULT NOW(),
  money DECIMAL(19, 4)  NOT NULL,
  message VARCHAR(255),

  PRIMARY KEY (account_from, account_to, timestamp),
  FOREIGN KEY (account_from) REFERENCES accounts(id),
  FOREIGN KEY (account_to) REFERENCES accounts(id)
);
