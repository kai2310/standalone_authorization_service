DROP TABLE IF EXISTS configuration;
DROP TABLE IF EXISTS role_assignments;
DROP TABLE IF EXISTS role_operations;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS account_feature_roles;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS feature_role_operations;
DROP TABLE IF EXISTS feature_roles;


CREATE TABLE feature_roles (
  feature_role_id bigint(20) unsigned NOT NULL auto_increment,
  label varchar(64) character set utf8 NOT NULL,
  realm varchar (64) character set utf8 NOT NULL,
  description longtext character set utf8,
  created datetime NOT NULL,
  created_by varchar(256) character set latin1 collate latin1_general_ci NOT NULL,
  modified timestamp NOT NULL,
  modified_by varchar(256) character set latin1 collate latin1_general_ci NOT NULL,

  PRIMARY KEY  (feature_role_id),
  KEY feature_role_realm (realm)
) ENGINE=InnoDB CHARSET=utf8;


CREATE TABLE feature_role_operations (
  feature_role_id bigint(20) unsigned NOT NULL,
  idx smallint(6) NOT NULL,
  service varchar(256) character set latin1 collate latin1_general_ci NOT NULL,
  resource varchar(256) character set latin1 collate latin1_general_ci NOT NULL,
  action_name varchar(256) character set latin1 collate latin1_general_ci NOT NULL,
  deny enum('yes','no') NOT NULL,
  properties text character set latin1 collate latin1_general_ci,

  PRIMARY KEY  (feature_role_id,idx),
  CONSTRAINT FOREIGN KEY (feature_role_id) REFERENCES feature_roles (feature_role_id)
) ENGINE=InnoDB CHARSET=utf8;


CREATE TABLE accounts
(
  account_id bigint(20) unsigned NOT NULL auto_increment,
  imp_account_type varchar(64) character set latin1 collate latin1_general_ci NOT NULL,
  imp_account_id varchar(64) character set latin1 collate latin1_general_ci NOT NULL,
  account_name varchar(255) character set utf8 NOT NULL,
  status varchar(20) character set utf8 NOT NULL,
  source varchar(255) character set latin1 collate latin1_general_ci NOT NULL,
  created datetime NOT NULL,
  modified timestamp NOT NULL,

  PRIMARY KEY  (account_id),
  UNIQUE KEY account_type_account (imp_account_type,imp_account_id),
  KEY account_name (account_name)
) ENGINE=InnoDB CHARSET=utf8;


CREATE TABLE account_feature_roles (
  account_id bigint(20) unsigned NOT NULL,
  feature_role_id bigint(20) unsigned NOT NULL,

  PRIMARY KEY  (account_id,feature_role_id),
  CONSTRAINT FOREIGN KEY (account_id) REFERENCES accounts (account_id),
  CONSTRAINT FOREIGN KEY (feature_role_id) REFERENCES feature_roles (feature_role_id)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE roles (
  role_id bigint(20) unsigned NOT NULL auto_increment,
  label varchar(64) character set utf8 NOT NULL,
  realm varchar (64) character set utf8 NOT NULL,
  description longtext character set utf8,
  owner_account_type varchar(64) character set latin1 collate latin1_general_ci NOT NULL,
  owner_account_id varchar(64) character set latin1 collate latin1_general_ci NOT NULL,
  created datetime NOT NULL,
  created_by varchar(256) character set latin1 collate latin1_general_ci NOT NULL,
  modified timestamp NOT NULL,
  modified_by varchar(256) character set latin1 collate latin1_general_ci NOT NULL,

  PRIMARY KEY  (role_id),
  KEY role_realm (realm)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE role_operations (
  role_id bigint(20) unsigned NOT NULL,
  idx smallint(6) NOT NULL,
  service varchar(256) character set latin1 collate latin1_general_ci NOT NULL,
  resource varchar(256) character set latin1 collate latin1_general_ci NOT NULL,
  action_name varchar(256) character set latin1 collate latin1_general_ci NOT NULL,
  deny enum('yes','no') NOT NULL,
  properties text character set latin1 collate latin1_general_ci,

  PRIMARY KEY  (role_id,idx),
  CONSTRAINT FOREIGN KEY (role_id) REFERENCES roles (role_id)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE role_assignments (
  role_assignment_id bigint(20) unsigned NOT NULL auto_increment,
  owner_account_type varchar(64) character set latin1 collate latin1_general_ci NOT NULL,
  owner_account_id varchar(64) character set latin1 collate latin1_general_ci NOT NULL,
  subject_type varchar(64) character set latin1 collate latin1_general_ci NOT NULL,
  subject_id varchar(64) character set latin1 collate latin1_general_ci NOT NULL,
  context_type varchar(64) character set latin1 collate latin1_general_ci NOT NULL,
  context_id varchar(64) character set latin1 collate latin1_general_ci NOT NULL,
  realm varchar (64) character set utf8 NOT NULL,
  role_id bigint(20) unsigned NOT NULL,
  scope text character set latin1 collate latin1_general_ci,
  created datetime NOT NULL,
  created_by varchar(256) character set latin1 collate latin1_general_ci NOT NULL,
  modified timestamp NOT NULL,
  modified_by varchar(256) character set latin1 collate latin1_general_ci NOT NULL,

  PRIMARY KEY  (role_assignment_id),
  UNIQUE INDEX st_subject_ct_context_role (subject_type,subject_id,context_type,context_id,role_id),
  CONSTRAINT FOREIGN KEY (role_id) REFERENCES roles (role_id),
  KEY role_assignment_realm (realm)
) ENGINE=InnoDB CHARSET=utf8;


CREATE TABLE configuration (
  config_key varchar(256) character set latin1 collate latin1_general_ci NOT NULL primary key,
  config_value varchar (256) character set utf8 NOT NULL,
  modified timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
)ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE entity_history
(
  entity_history_id bigint unsigned PRIMARY KEY NOT NULL auto_increment,
  entity_type varchar(64) character set latin1 collate latin1_general_ci NOT NULL,
  entity_id  integer unsigned NOT NULL,
  history_action varchar(20) character set latin1 collate latin1_general_ci NOT NULL,
  api_version SMALLINT unsigned not null,
  user varchar(255) character set latin1 collate latin1_general_ci NOT NULL,
  timestamp timestamp NOT NULL,
  serialized_entry longtext not null,

  index history_entry_type_id(entity_type,entity_id)
) ENGINE=InnoDB CHARSET=utf8;

