-- apply changes
alter table rccities_cities alter column money drop default;
alter table rccities_cities alter column money set null;
alter table rccities_residents alter column deposit_amount drop default;
alter table rccities_residents alter column deposit_amount set null;
alter table rccities_residents alter column withdraw_amount drop default;
alter table rccities_residents alter column withdraw_amount set null;
