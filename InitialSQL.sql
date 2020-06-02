BEGIN TRANSACTION;
PRAGMA foreign_keys=on;

DROP TABLE IF EXISTS Results_values;
DROP TABLE IF EXISTS Tolerance_params_values;
DROP TABLE IF EXISTS Tolerance_params;
DROP TABLE IF EXISTS Results;
DROP TABLE IF EXISTS Verifications;
DROP TABLE IF EXISTS Elements;
DROP TABLE IF EXISTS Devices;

CREATE TABLE Devices (
id INTEGER PRIMARY KEY AUTOINCREMENT, 
name TEXT, 
type TEXT,  
serialNumber TEXT, 
owner TEXT, 
GosNumber TEXT);

CREATE TABLE Elements (
id INTEGER PRIMARY KEY AUTOINCREMENT, 
deviceId INTEGER,
type TEXT, 
serNumber TEXT, 
poleCount INTEGER, 
measUnit TEXT, 
moduleToleranceType TEXT, 
phaseToleranceType TEXT, 
nominalId INTEGER,
FOREIGN KEY (deviceId) REFERENCES Devices(id) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY (nominalId) REFERENCES Results(id) ON UPDATE CASCADE ON DELETE CASCADE);

CREATE TABLE Verifications (
id	INTEGER PRIMARY KEY AUTOINCREMENT,
deviceId INTEGER,
verificationDate TEXT,
pathOfDoc TEXT,
pathOfProtocol TEXT,
typeOfDoc TEXT,
FOREIGN KEY (deviceId) REFERENCES Devices(id) ON UPDATE CASCADE ON DELETE CASCADE);

CREATE TABLE Results (
id INTEGER PRIMARY KEY AUTOINCREMENT,
elementId INTEGER,
measDate TEXT,
FOREIGN KEY (elementId) REFERENCES Elements(id) ON UPDATE CASCADE ON DELETE CASCADE);

CREATE TABLE Results_values (
id INTEGER PRIMARY KEY AUTOINCREMENT,
resultId INTEGER, freq REAL,
MODULE_S11 REAL, ERROR_MODULE_S11 REAL, PHASE_S11 REAL, ERROR_PHASE_S11 REAL,
MODULE_S12 REAL, ERROR_MODULE_S12 REAL, PHASE_S12 REAL, ERROR_PHASE_S12 REAL,
MODULE_S21 REAL, ERROR_MODULE_S21 REAL, PHASE_S21 REAL, ERROR_PHASE_S21 REAL, 
MODULE_S22 REAL, ERROR_MODULE_S22 REAL, PHASE_S22 REAL, ERROR_PHASE_S22 REAL,
FOREIGN KEY (ResultId) REFERENCES Results(id) ON UPDATE CASCADE ON DELETE CASCADE);

CREATE TABLE Tolerance_params (id INTEGER PRIMARY KEY AUTOINCREMENT,
elementId INTEGER,
type TEXT,
FOREIGN KEY (elementId) REFERENCES Elements(id) ON UPDATE CASCADE ON DELETE CASCADE);

CREATE TABLE Tolerance_params_values (id INTEGER PRIMARY KEY AUTOINCREMENT,
toleranceParamsId INTEGER, freq REAL,
DOWN_MODULE_S11 REAL, UP_MODULE_S11 REAL, DOWN_PHASE_S11 REAL, UP_PHASE_S11 REAL,
DOWN_MODULE_S12 REAL, UP_MODULE_S12 REAL, DOWN_PHASE_S12 REAL, UP_PHASE_S12 REAL,
DOWN_MODULE_S21 REAL, UP_MODULE_S21 REAL, DOWN_PHASE_S21 REAL, UP_PHASE_S21 REAL, 
DOWN_MODULE_S22 REAL, UP_MODULE_S22 REAL, DOWN_PHASE_S22 REAL, UP_PHASE_S22 REAL,
FOREIGN KEY (toleranceParamsId) REFERENCES Tolerance_params(id) ON UPDATE CASCADE ON DELETE CASCADE);

/*
  Creating triggers on deleting because cascading deleting is not working 
  when sql query sended from java program using JDBC 
  (it is not right for MySQL, for example)
*/
CREATE TRIGGER onDeviceDeleting BEFORE DELETE ON Devices
BEGIN
	DELETE FROM Elements WHERE deviceId = OLD.id;
	DELETE FROM Verifications WHERE deviceId = OLD.id;
END;

CREATE TRIGGER onElementDeleting BEFORE DELETE ON Elements
BEGIN
	DELETE FROM Results WHERE elementId = OLD.id;
	DELETE FROM Tolerance_params WHERE elementId = OLD.id;
END;

CREATE TRIGGER onResultsDeleting BEFORE DELETE ON Results
BEGIN
	DELETE FROM Results_values WHERE resultId = OLD.id;
END;

CREATE TRIGGER onToleranceParamsDeleting BEFORE DELETE ON Tolerance_params
BEGIN
	DELETE FROM Tolerance_params_values WHERE toleranceParamsId = OLD.id;
END;

/*And also for updating*/
CREATE TRIGGER onDeviceUpdate AFTER UPDATE ON Devices
BEGIN
	UPDATE Elements SET deviceId = NEW.id WHERE Elements.deviceId = OLD.id;
	UPDATE Verifications SET deviceId = NEW.id WHERE Verifications.deviceId = OLD.id;
END;

CREATE TRIGGER onElementUpdate AFTER UPDATE ON Elements
BEGIN
	UPDATE Results SET elementId = NEW.id WHERE Results.elementId = OLD.id;
	UPDATE Tolerance_params SET elementId = NEW.id WHERE Tolerance_params.elementId = OLD.id;
END;

CREATE TRIGGER onResultsUpdate AFTER UPDATE ON Results
BEGIN
	UPDATE Results_values SET resultId = NEW.id WHERE Results_values.resultId = OLD.id;
END;

CREATE TRIGGER onToleranceParamsUpdate AFTER UPDATE ON Tolerance_params
BEGIN
	UPDATE Tolerance_params_values SET toleranceParamsId = NEW.id WHERE Tolerance_params_values.toleranceParamsId = OLD.id;
END;

COMMIT;