Drop TABLE IF EXISTS device;

CREATE TABLE device (
state BOOLEAN DEFAULT NULL,
uuid UUID PRIMARY KEY NOT NULL,
deviceType VARCHAR(64) NOT NULL,
url VARCHAR(128) NOT NULL,
lastEvent TIMESTAMP WITHOUT TIME ZONE
);

Drop TABLE IF EXISTS deviceutilization;

CREATE TABLE DeviceUtilzation(
opened TIMESTAMP WITHOUT TIME ZONE NOT NULL,
closed TIMESTAMP WITHOUT TIME ZONE,
utilization INTEGER,
uuid UUID,
deviceUUID UUID REFERENCES Device ON DELETE CASCADE
);CREATE TABLE DeviceUtilzation(
opened TIMESTAMP WITHOUT TIME ZONE NOT NULL,
closed TIMESTAMP WITHOUT TIME ZONE,
utilization INTEGER,
uuid UUID,
deviceUUID UUID REFERENCES Device ON DELETE CASCADE
);