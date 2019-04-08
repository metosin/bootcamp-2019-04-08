-- :name add-measurement! :! :n
-- :doc Adds a new measurement into the system
INSERT INTO measurements (
  device_id,
  measured_on,
  payload
) VALUES (
  :device_id,
  :measured_on,
  :payload
)

-- :name get-measurements :? :*
-- :doc Retrieve the all measurements
SELECT device_id, measured_on, payload
FROM measurements
ORDER BY measured_on DESC, device_id ASC

-- :name get-measurements-for-devices :? :*
SELECT device_id, measured_on, payload
FROM measurements
WHERE device_id IN (:value*:device_ids)
ORDER BY measured_on DESC, device_id ASC

-- :name get-devices :? :*
-- :doc Retrieve ids of all measured devices
SELECT DISTINCT device_id FROM measurements
ORDER BY device_id ASC

-- :name get-device :? :1
-- :doc Retrieve specific device
SELECT DISTINCT device_id FROM measurements
WHERE device_id = :device_id

-- :name has-device? :? :1
-- :doc Check if specific device exists
SELECT EXISTS (
  SELECT 1 FROM measurements WHERE device_id = :device_id
)
