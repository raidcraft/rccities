-- apply changes
alter table rccities_cities alter money drop default;
alter table rccities_cities modify money double;
alter table rccities_residents alter deposit_amount drop default;
alter table rccities_residents modify deposit_amount double;
alter table rccities_residents alter withdraw_amount drop default;
alter table rccities_residents modify withdraw_amount double;
