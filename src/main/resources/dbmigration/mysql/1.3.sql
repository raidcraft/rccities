-- apply changes
alter table rccities_residents add column deposit_amount double default 0 not null;
alter table rccities_residents add column withdraw_amount double default 0 not null;

