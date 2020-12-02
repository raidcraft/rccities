-- apply changes
create table rccities_assignments (
  id                            uuid not null,
  plot_id                       uuid,
  resident_id                   uuid,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_assignments primary key (id)
);

create table rccities_cities (
  id                            uuid not null,
  name                          varchar(255),
  creator_id                    uuid,
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
  upgrade_id                    uuid,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_cities primary key (id)
);

create table rccities_city_flags (
  id                            uuid not null,
  city_id                       uuid,
  name                          varchar(255),
  value                         varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_city_flags primary key (id)
);

create table rccities_join_requests (
  id                            uuid not null,
  city_id                       uuid,
  player                        varchar(255),
  player_id                     uuid,
  rejected                      boolean default false not null,
  reject_reason                 varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_join_requests primary key (id)
);

create table rccities_upgrade_level_info (
  id                            uuid not null,
  identifier                    varchar(255),
  name                          varchar(255),
  level_number                  integer not null,
  requirement_description       varchar(255),
  reward_description            varchar(255),
  upgrade_info_id               uuid,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_upgrade_level_info primary key (id)
);

create table rccities_plots (
  id                            uuid not null,
  city_id                       uuid,
  x                             integer not null,
  z                             integer not null,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_plots primary key (id)
);

create table rccities_plot_flags (
  id                            uuid not null,
  plot_id                       uuid,
  name                          varchar(255),
  value                         varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_plot_flags primary key (id)
);

create table rccities_residents (
  id                            uuid not null,
  city_id                       uuid,
  player_id                     uuid,
  profession                    varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_residents primary key (id)
);

create table rccities_upgrades (
  id                            uuid not null,
  name                          varchar(255),
  holder_id                     uuid,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_upgrades primary key (id)
);

create table rccities_upgrade_holders (
  id                            uuid not null,
  name                          varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_upgrade_holders primary key (id)
);

create table rccities_upgrade_info (
  id                            uuid not null,
  holder_id                     varchar(255),
  holder_name                   varchar(255),
  name                          varchar(255),
  description                   varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_upgrade_info primary key (id)
);

create table rccities_upgrade_levels (
  id                            uuid not null,
  identifier                    varchar(255),
  unlocked                      boolean default false not null,
  upgrade_id                    uuid,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_upgrade_levels primary key (id)
);

create table rccities_upgrade_requests (
  id                            uuid not null,
  city_id                       uuid,
  level_identifier              varchar(255),
  info                          varchar(255),
  rejected                      boolean default false not null,
  accepted                      boolean default false not null,
  reject_reason                 varchar(255),
  reject_date                   timestamp,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rccities_upgrade_requests primary key (id)
);

create index ix_rccities_assignments_plot_id on rccities_assignments (plot_id);
alter table rccities_assignments add constraint fk_rccities_assignments_plot_id foreign key (plot_id) references rccities_plots (id) on delete restrict on update restrict;

create index ix_rccities_assignments_resident_id on rccities_assignments (resident_id);
alter table rccities_assignments add constraint fk_rccities_assignments_resident_id foreign key (resident_id) references rccities_residents (id) on delete restrict on update restrict;

create index ix_rccities_city_flags_city_id on rccities_city_flags (city_id);
alter table rccities_city_flags add constraint fk_rccities_city_flags_city_id foreign key (city_id) references rccities_cities (id) on delete restrict on update restrict;

create index ix_rccities_join_requests_city_id on rccities_join_requests (city_id);
alter table rccities_join_requests add constraint fk_rccities_join_requests_city_id foreign key (city_id) references rccities_cities (id) on delete restrict on update restrict;

create index ix_rccities_upgrade_level_info_upgrade_info_id on rccities_upgrade_level_info (upgrade_info_id);
alter table rccities_upgrade_level_info add constraint fk_rccities_upgrade_level_info_upgrade_info_id foreign key (upgrade_info_id) references rccities_upgrade_info (id) on delete restrict on update restrict;

create index ix_rccities_plots_city_id on rccities_plots (city_id);
alter table rccities_plots add constraint fk_rccities_plots_city_id foreign key (city_id) references rccities_cities (id) on delete restrict on update restrict;

create index ix_rccities_plot_flags_plot_id on rccities_plot_flags (plot_id);
alter table rccities_plot_flags add constraint fk_rccities_plot_flags_plot_id foreign key (plot_id) references rccities_plots (id) on delete restrict on update restrict;

create index ix_rccities_residents_city_id on rccities_residents (city_id);
alter table rccities_residents add constraint fk_rccities_residents_city_id foreign key (city_id) references rccities_cities (id) on delete restrict on update restrict;

create index ix_rccities_upgrades_holder_id on rccities_upgrades (holder_id);
alter table rccities_upgrades add constraint fk_rccities_upgrades_holder_id foreign key (holder_id) references rccities_upgrade_holders (id) on delete restrict on update restrict;

create index ix_rccities_upgrade_levels_upgrade_id on rccities_upgrade_levels (upgrade_id);
alter table rccities_upgrade_levels add constraint fk_rccities_upgrade_levels_upgrade_id foreign key (upgrade_id) references rccities_upgrades (id) on delete restrict on update restrict;

create index ix_rccities_upgrade_requests_city_id on rccities_upgrade_requests (city_id);
alter table rccities_upgrade_requests add constraint fk_rccities_upgrade_requests_city_id foreign key (city_id) references rccities_cities (id) on delete restrict on update restrict;

