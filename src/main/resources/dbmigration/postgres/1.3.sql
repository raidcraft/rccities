-- apply changes
alter table rccities_residents add column deposit_amount float default 0 not null;
alter table rccities_residents add column withdraw_amount float default 0 not null;

