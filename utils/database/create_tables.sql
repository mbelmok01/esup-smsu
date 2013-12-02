create table account (ACC_ID integer not null auto_increment, ACC_LABEL varchar(32) not null unique, primary key (ACC_ID)) ENGINE=InnoDB;
create table basic_group (BGR_ID integer not null auto_increment, BGR_LABEL varchar(255) not null unique, primary key (BGR_ID)) ENGINE=InnoDB;
create table customized_group (CGR_ID integer not null auto_increment, ROL_ID integer not null, ACC_ID integer not null, CGR_LABEL varchar(32) not null unique, CGR_QUOTA_SMS bigint not null, CGR_QUOTA_ORDER bigint not null, CGR_CONSUMED_SMS bigint not null, primary key (CGR_ID)) ENGINE=InnoDB;
create table fonction (FCT_ID integer not null auto_increment, FCT_NAME varchar(32) not null unique, primary key (FCT_ID)) ENGINE=InnoDB;
create table mail (MAIL_ID integer not null auto_increment, MAIL_CONTENT varchar(300) not null, MAIL_STATE varchar(16) not null, MAIL_SUBJECT varchar(300), TPL_ID integer, primary key (MAIL_ID)) ENGINE=InnoDB;
create table mail_recipient (MRC_ID integer not null auto_increment, MRC_ADDRESS varchar(100) not null unique, MRC_LOGIN varchar(32), primary key (MRC_ID)) ENGINE=InnoDB;
create table message (MSG_ID integer not null auto_increment, MSG_DATE datetime, MSG_CONTENT varchar(255) not null, MSG_STATE varchar(16) not null, ACC_ID integer not null, TPL_ID integer, PER_ID integer not null, SVC_ID integer, MAIL_ID integer unique, BGR_SENDER_ID integer not null, BGR_RECIPIENT_ID integer, primary key (MSG_ID)) ENGINE=InnoDB;
create table pending_member (MBR_LOGIN varchar(255) not null, MBR_VALIDATION_CODE varchar(8) not null, MBR_DATE_SUBSCRIPTION datetime, primary key (MBR_LOGIN)) ENGINE=InnoDB;
create table person (PER_ID integer not null auto_increment, PER_LOGIN varchar(32) not null unique, primary key (PER_ID)) ENGINE=InnoDB;
create table recipient (RCP_ID integer not null auto_increment, RCP_PHONE varchar(255) not null unique, RCP_LOGIN varchar(32), primary key (RCP_ID)) ENGINE=InnoDB;
create table role (ROL_ID integer not null auto_increment, ROL_NAME varchar(32) not null unique, primary key (ROL_ID)) ENGINE=InnoDB;
create table role_composition (ROL_ID integer not null, FCT_ID integer not null, primary key (ROL_ID, FCT_ID)) ENGINE=InnoDB;
create table service (SVC_ID integer not null auto_increment, SVC_NAME varchar(32) not null unique, SVC_KEY varchar(16) not null unique, primary key (SVC_ID)) ENGINE=InnoDB;
create table supervisor (CGR_ID integer not null, PER_ID integer not null, primary key (PER_ID, CGR_ID)) ENGINE=InnoDB;
create table supervisor_sender (MSG_ID integer not null, PER_ID integer not null, primary key (PER_ID, MSG_ID)) ENGINE=InnoDB;
create table template (TPL_ID integer not null auto_increment, TPL_LABEL varchar(32) not null unique, TPL_HEADING varchar(50), TPL_BODY varchar(160), TPL_SIGNATURE varchar(50), primary key (TPL_ID)) ENGINE=InnoDB;
create table to_mail_recipient (MRC_ID integer not null, MAIL_ID integer not null, primary key (MRC_ID, MAIL_ID)) ENGINE=InnoDB;
create table to_recipient (RCP_ID integer not null, MSG_ID integer not null, primary key (RCP_ID, MSG_ID)) ENGINE=InnoDB;
alter table customized_group add index FKBA973141C7B62A7C (ROL_ID), add constraint FKBA973141C7B62A7C foreign key (ROL_ID) references role (ROL_ID);
alter table customized_group add index FKBA973141B076996B (ACC_ID), add constraint FKBA973141B076996B foreign key (ACC_ID) references account (ACC_ID);
alter table mail add index FK3305B7A17C89BF (TPL_ID), add constraint FK3305B7A17C89BF foreign key (TPL_ID) references template (TPL_ID);
alter table message add index FK38EB00078CA9CFB4 (SVC_ID), add constraint FK38EB00078CA9CFB4 foreign key (SVC_ID) references service (SVC_ID);
alter table message add index FK38EB000791DC8E5F (BGR_RECIPIENT_ID), add constraint FK38EB000791DC8E5F foreign key (BGR_RECIPIENT_ID) references basic_group (BGR_ID);
alter table message add index FK38EB0007A17C89BF (TPL_ID), add constraint FK38EB0007A17C89BF foreign key (TPL_ID) references template (TPL_ID);
alter table message add index FK38EB000783F4308D (PER_ID), add constraint FK38EB000783F4308D foreign key (PER_ID) references person (PER_ID);
alter table message add index FK38EB00073063FAD5 (MAIL_ID), add constraint FK38EB00073063FAD5 foreign key (MAIL_ID) references mail (MAIL_ID);
alter table message add index FK38EB0007BA2599F (BGR_SENDER_ID), add constraint FK38EB0007BA2599F foreign key (BGR_SENDER_ID) references basic_group (BGR_ID);
alter table message add index FK38EB0007B076996B (ACC_ID), add constraint FK38EB0007B076996B foreign key (ACC_ID) references account (ACC_ID);
alter table role_composition add index FK7545E761C7B62A7C (ROL_ID), add constraint FK7545E761C7B62A7C foreign key (ROL_ID) references role (ROL_ID);
alter table role_composition add index FK7545E761EC946650 (FCT_ID), add constraint FK7545E761EC946650 foreign key (FCT_ID) references fonction (FCT_ID);
alter table supervisor add index FK9AD6536883F4308D (PER_ID), add constraint FK9AD6536883F4308D foreign key (PER_ID) references person (PER_ID);
alter table supervisor add index FK9AD6536821602D8F (CGR_ID), add constraint FK9AD6536821602D8F foreign key (CGR_ID) references customized_group (CGR_ID);
alter table supervisor_sender add index FK48E09EEC83F4308D (PER_ID), add constraint FK48E09EEC83F4308D foreign key (PER_ID) references person (PER_ID);
alter table supervisor_sender add index FK48E09EEC44EB8045 (MSG_ID), add constraint FK48E09EEC44EB8045 foreign key (MSG_ID) references message (MSG_ID);
alter table to_mail_recipient add index FKF3333D53063FAD5 (MAIL_ID), add constraint FKF3333D53063FAD5 foreign key (MAIL_ID) references mail (MAIL_ID);
alter table to_mail_recipient add index FKF3333D53D7211A3 (MRC_ID), add constraint FKF3333D53D7211A3 foreign key (MRC_ID) references mail_recipient (MRC_ID);
alter table to_recipient add index FKE3CADD5544EB8045 (MSG_ID), add constraint FKE3CADD5544EB8045 foreign key (MSG_ID) references message (MSG_ID);
alter table to_recipient add index FKE3CADD555376F779 (RCP_ID), add constraint FKE3CADD555376F779 foreign key (RCP_ID) references recipient (RCP_ID);
