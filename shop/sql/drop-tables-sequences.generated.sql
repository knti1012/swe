'DROPSEQUENCE'||SEQUENCE_NAME||';'
DROP SEQUENCE HIBERNATE_SEQUENCE;

0 rows affected
'DROPTABLE'||TABLE_NAME||'CASCADECONSTRAINTSPURGE;'
DROP TABLE BESTELLUNG_LIEFERUNG CASCADE CONSTRAINTS PURGE;
DROP TABLE LIEFERUNG CASCADE CONSTRAINTS PURGE;
DROP TABLE BESTELLPOSITION CASCADE CONSTRAINTS PURGE;
DROP TABLE BESTELLUNG CASCADE CONSTRAINTS PURGE;
DROP TABLE ARTIKEL CASCADE CONSTRAINTS PURGE;
DROP TABLE KUNDE CASCADE CONSTRAINTS PURGE;
DROP TABLE ADRESSE CASCADE CONSTRAINTS PURGE;

0 rows affected
