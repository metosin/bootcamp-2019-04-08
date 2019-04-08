CREATE TABLE measurements (
  device_id TEXT NOT NULL,
  measured_on TIMESTAMP WITH TIME ZONE NOT NULL,
  payload JSONB NOT NULL
)
--;;

CREATE INDEX ix_measurements_device_id_measured_on
  ON measurements (device_id, measured_on);
--;;

CREATE INDEX ix_measurements_measured_on_device_id
  ON measurements (measured_on, device_id);
--;;
