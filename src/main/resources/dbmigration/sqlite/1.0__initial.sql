-- apply changes
create table rccities_assignments (
  id                            varchar(40) not null,
  plot_id                       varchar(40),
  resident_id                   varchar(40),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_assignments primary key (id),
  foreign key (plot_id) references rccities_plots (id) on delete restrict on update restrict,
  foreign key (resident_id) references rccities_residents (id) on delete restrict on update restrict
);

create table rccities_cities (
  id                            varchar(40) not null,
  name                          varchar(255),
  creator_id                    varchar(40),
  creation_date                 timestamp,
  description                   clob,
  world                         varchar(255),
  x                             integer not null,
  y                             integer not null,
  z                             integer not null,
  pitch                         integer not null,
  yaw                           integer not null,
  plot_credit                   integer not null,
  max_radius                    integer not null,
  exp                           integer not null,
  upgrade_id                    varchar(40),
  money                         double,
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_cities primary key (id)
);

create table rccities_city_flags (
  id                            varchar(40) not null,
  city_id                       varchar(40),
  name                          varchar(255),
  value                         varchar(255),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_city_flags primary key (id),
  foreign key (city_id) references rccities_cities (id) on delete restrict on update restrict
);

create table rccities_join_requests (
  id                            varchar(40) not null,
  city_id                       varchar(40),
  player                        varchar(255),
  player_id                     varchar(40),
  rejected                      int default 0 not null,
  reject_reason                 varchar(255),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_join_requests primary key (id),
  foreign key (city_id) references rccities_cities (id) on delete restrict on update restrict
);

create table rccities_upgrade_level_info (
  id                            varchar(40) not null,
  identifier                    varchar(255),
  name                          varchar(255),
  level_number                  integer not null,
  requirement_description       varchar(255),
  reward_description            varchar(255),
  upgrade_info_id               varchar(40),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_upgrade_level_info primary key (id),
  foreign key (upgrade_info_id) references rccities_upgrade_info (id) on delete restrict on update restrict
);

create table rccities_plots (
  id                            varchar(40) not null,
  city_id                       varchar(40),
  x                             integer not null,
  z                             integer not null,
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_plots primary key (id),
  foreign key (city_id) references rccities_cities (id) on delete restrict on update restrict
);

create table rccities_plot_flags (
  id                            varchar(40) not null,
  plot_id                       varchar(40),
  name                          varchar(255),
  value                         varchar(255),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_plot_flags primary key (id),
  foreign key (plot_id) references rccities_plots (id) on delete restrict on update restrict
);

create table rccities_residents (
  id                            varchar(40) not null,
  city_id                       varchar(40),
  player_id                     varchar(40),
  profession                    varchar(255),
  deposit_amount                double,
  withdraw_amount               double,
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_residents primary key (id),
  foreign key (city_id) references rccities_cities (id) on delete restrict on update restrict
);

create table rccities_upgrades (
  id                            varchar(40) not null,
  name                          varchar(255),
  holder_id                     varchar(40),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_upgrades primary key (id),
  foreign key (holder_id) references rccities_upgrade_holders (id) on delete restrict on update restrict
);

create table rccities_upgrade_holders (
  id                            varchar(40) not null,
  name                          varchar(255),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_upgrade_holders primary key (id)
);

create table rccities_upgrade_info (
  id                            varchar(40) not null,
  holder_id                     varchar(255),
  holder_name                   varchar(255),
  name                          varchar(255),
  description                   varchar(255),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_upgrade_info primary key (id)
);

create table rccities_upgrade_levels (
  id                            varchar(40) not null,
  identifier                    varchar(255),
  unlocked                      int default 0 not null,
  upgrade_id                    varchar(40),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_upgrade_levels primary key (id),
  foreign key (upgrade_id) references rccities_upgrades (id) on delete restrict on update restrict
);

create table rccities_upgrade_requests (
  id                            varchar(40) not null,
  city_id                       varchar(40),
  level_identifier              varchar(255),
  info                          varchar(255),
  rejected                      int default 0 not null,
  accepted                      int default 0 not null,
  reject_reason                 varchar(255),
  reject_date                   timestamp,
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_upgrade_requests primary key (id),
  foreign key (city_id) references rccities_cities (id) on delete restrict on update restrict
);

