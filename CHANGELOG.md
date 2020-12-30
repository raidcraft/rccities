## [1.11.3](https://github.com/raidcraft/rccities/compare/v1.11.2...v1.11.3) (2020-12-30)


### Bug Fixes

* Added workaround to allow pistons to move across world guard regions which are used for plot protection. Fixes [#31](https://github.com/raidcraft/rccities/issues/31) ([0c63f11](https://github.com/raidcraft/rccities/commit/0c63f11cc89151ba64b778500a87cfaed0be55fe))
* Fix exception type if command execution fails ([6c00862](https://github.com/raidcraft/rccities/commit/6c00862c1a2f2923ee8a3acbd16f476cf9c3b679))
* Make usage of external economy plugin configurable to still support servers without RCEconomy ([ad2c4c1](https://github.com/raidcraft/rccities/commit/ad2c4c11bb69cc6843c6d6417424cad36b9fc7c4))

## [1.11.2](https://github.com/raidcraft/rccities/compare/v1.11.1...v1.11.2) (2020-12-30)


### Bug Fixes

* Fixes region update after promote resident to a new role with build permissions. Fixes [#30](https://github.com/raidcraft/rccities/issues/30) ([c243db3](https://github.com/raidcraft/rccities/commit/c243db3eaac6b5fd04a39c6bc8270d34de95c99b))
* Minor improvement to prevent errors during migration of city bank accounts ([2d7c3d6](https://github.com/raidcraft/rccities/commit/2d7c3d62c3bfa35654cfe646865a9dc1bd91c1d5))

## [1.11.1](https://github.com/raidcraft/rccities/compare/v1.11.0...v1.11.1) (2020-12-28)


### Bug Fixes

* Improve stability when converting city bank accounts into economy system ([9ecc640](https://github.com/raidcraft/rccities/commit/9ecc6409bd228643628556e83cd182be0005c2fe))

# [1.11.0](https://github.com/raidcraft/rccities/compare/v1.10.0...v1.11.0) (2020-12-28)


### Bug Fixes

* Fix minor checks for city bank transfers ([78fba42](https://github.com/raidcraft/rccities/commit/78fba4278ca2889f5d2537a93a29fda58a818d89))


### Features

* Migrate city bank accounts into Economy wrapper. Closes [#13](https://github.com/raidcraft/rccities/issues/13) ([98ec2e2](https://github.com/raidcraft/rccities/commit/98ec2e2af088b076f1eec4e42fbac4a3c9f08ee9))

# [1.10.0](https://github.com/raidcraft/rccities/compare/v1.9.0...v1.10.0) (2020-12-27)


### Bug Fixes

* Add RCEconomy as dependency ([8117a39](https://github.com/raidcraft/rccities/commit/8117a39b1827e50d487d86d7af35cc7d03fc5969))


### Features

* Use RCEconomy instead of plain Vault API and add description for deposit and withdraw transfers (City accounts still maintained by RCCities) ([44f9ac5](https://github.com/raidcraft/rccities/commit/44f9ac5b9fd1c5549f9886fceadb9cb9a0f9a23b))

# [1.9.0](https://github.com/raidcraft/rccities/compare/v1.8.9...v1.9.0) (2020-12-27)


### Features

* Players can now teleport once a day to a foreign city. Closes [#20](https://github.com/raidcraft/rccities/issues/20) ([c83357d](https://github.com/raidcraft/rccities/commit/c83357d245c58edc52708777f6b24e5a11829ee1))

## [1.8.9](https://github.com/raidcraft/rccities/compare/v1.8.8...v1.8.9) (2020-12-15)


### Bug Fixes

* Disable custom mob spawn listener. Fixes [#16](https://github.com/raidcraft/rccities/issues/16) ([7fb2a0d](https://github.com/raidcraft/rccities/commit/7fb2a0dfb8049e2911bef348e64732d779a541d0))

## [1.8.8](https://github.com/raidcraft/rccities/compare/v1.8.7...v1.8.8) (2020-12-14)


### Bug Fixes

* change default config to use h2 database to enable unit testing ([285728d](https://github.com/raidcraft/rccities/commit/285728d671f665f3cc7390298e6c312f9b31e08f))
* Fix city size and plot cost calculation. Fixes [#15](https://github.com/raidcraft/rccities/issues/15) ([8eaa9cb](https://github.com/raidcraft/rccities/commit/8eaa9cb9006ce5be5590c114c9417dcbc7267575))
* Fix command completion order ([5984299](https://github.com/raidcraft/rccities/commit/598429950590d4c80ac4786ffba6fa501bb6efc2))

## [1.8.7](https://github.com/raidcraft/rccities/compare/v1.8.6...v1.8.7) (2020-12-14)


### Bug Fixes

* Change order of city command context to use resident city first before looking for location cities. Fixes [#14](https://github.com/raidcraft/rccities/issues/14) ([4a30f87](https://github.com/raidcraft/rccities/commit/4a30f87316136f790f1d03927a8f7073a66e42de))

## [1.8.6](https://github.com/raidcraft/rccities/compare/v1.8.5...v1.8.6) (2020-12-14)


### Bug Fixes

* Don't set resident role via setter when resident is load by database. Close [#12](https://github.com/raidcraft/rccities/issues/12) ([09e54db](https://github.com/raidcraft/rccities/commit/09e54dbb7abf152fbcb5fbce7931413de2344308))

## [1.8.5](https://github.com/raidcraft/rccities/compare/v1.8.4...v1.8.5) (2020-12-12)


### Bug Fixes

* Prevent usage of MARK_FREE flag via command. Close [#10](https://github.com/raidcraft/rccities/issues/10) ([bf8167f](https://github.com/raidcraft/rccities/commit/bf8167f0063592d927c7885ad00e0fce00d9d0b8))

## [1.8.4](https://github.com/raidcraft/rccities/compare/v1.8.3...v1.8.4) (2020-12-12)


### Bug Fixes

* Fix typo in resident command [#9](https://github.com/raidcraft/rccities/issues/9) ([3a6b766](https://github.com/raidcraft/rccities/commit/3a6b766e855388f6ab06cfc1055a44c1896e6127))

## [1.8.3](https://github.com/raidcraft/rccities/compare/v1.8.2...v1.8.3) (2020-12-12)


### Bug Fixes

* Fix NPE when trying to recreate dynmap markers ([a64e5b5](https://github.com/raidcraft/rccities/commit/a64e5b5ed0b2373c858e85261a6423a7e5c03dc1))
* Fix recreating plot area markers by first deleting existing marker ([722b13e](https://github.com/raidcraft/rccities/commit/722b13e9ea5e8361c44e62272bb06429702f09cf))
* Fix region update behavior when none residents get promoted first time with build permissions ([8764531](https://github.com/raidcraft/rccities/commit/87645312b195d2789c8d75a84f75e595c9e7e6c4))

## [1.8.2](https://github.com/raidcraft/rccities/compare/v1.8.1...v1.8.2) (2020-12-11)


### Bug Fixes

* fix npe when player is no resident in any town ([9012ec3](https://github.com/raidcraft/rccities/commit/9012ec38899058697f6a93f43097b29ceb662b1f))

## [1.8.1](https://github.com/raidcraft/rccities/compare/v1.8.0...v1.8.1) (2020-12-10)


### Bug Fixes

* **residents:** npe when querying uncached resident ([04d04f2](https://github.com/raidcraft/rccities/commit/04d04f242f61a4695796a46e8d67031066c988ae))

# [1.8.0](https://github.com/raidcraft/rccities/compare/v1.7.1...v1.8.0) (2020-12-10)


### Features

* Change default config values ([2445c9a](https://github.com/raidcraft/rccities/commit/2445c9a3168a0bf1ff6933e58bbc91f8ecd9a5ee))

## [1.7.1](https://github.com/raidcraft/rccities/compare/v1.7.0...v1.7.1) (2020-12-10)


### Bug Fixes

* Improve Dynmap support ([15832c4](https://github.com/raidcraft/rccities/commit/15832c4cfdaf00bc0cf8c6128721edd843256157))

# [1.7.0](https://github.com/raidcraft/rccities/compare/v1.6.0...v1.7.0) (2020-12-09)


### Features

* Add deletion of dynmap markers ([b285c7a](https://github.com/raidcraft/rccities/commit/b285c7a5a99f86e9645625ec197d82819437ac82))

# [1.6.0](https://github.com/raidcraft/rccities/compare/v1.5.0...v1.6.0) (2020-12-09)


### Features

* Add dynmap integration ([9e4d865](https://github.com/raidcraft/rccities/commit/9e4d8651c4aca948304abbbd925c7549d606d9dc))

# [1.5.0](https://github.com/raidcraft/rccities/compare/v1.4.3...v1.5.0) (2020-12-08)


### Bug Fixes

* Silent debug comment, increased migration delay to 500ms to decrease pressure ([fcba10f](https://github.com/raidcraft/rccities/commit/fcba10fd982df27767bdc7a01ed45957b52e457b))


### Features

* Implement plot migration from old RCCities plot regions ([86c4ee4](https://github.com/raidcraft/rccities/commit/86c4ee4de7fc3ece555fec57adaa5d3665043e90))

## [1.4.3](https://github.com/raidcraft/rccities/compare/v1.4.2...v1.4.3) (2020-12-07)


### Bug Fixes

* Fix loading of upgrade holder configuration ([67c837c](https://github.com/raidcraft/rccities/commit/67c837c2ef625d201501a836567487221899e1e8))

## [1.4.2](https://github.com/raidcraft/rccities/compare/v1.4.1...v1.4.2) (2020-12-07)


### Bug Fixes

* Check if resident is already member of another town. Introduce -u flag for /plot claim command to avoid marking new plot with torches. ([ff6b355](https://github.com/raidcraft/rccities/commit/ff6b3550cfb5f09e38db5b70b3257e87eade2f8c))
* Improve order of intelligent city detection ([1d0c837](https://github.com/raidcraft/rccities/commit/1d0c837c747659265b2f1dc821e3424d2e811a59))
* NPE with citiy names containing german umlauts ([b8866cc](https://github.com/raidcraft/rccities/commit/b8866cc079d88a56899f7865dd9cba3b690eddf2))
* Remove warning about missed confirmation if already confirmed ([0048819](https://github.com/raidcraft/rccities/commit/0048819657e11bff921b280629ddff1dba6c8ada))

## [1.4.1](https://github.com/raidcraft/rccities/compare/v1.4.0...v1.4.1) (2020-12-06)


### Bug Fixes

* Fix a few bugs detected during tests ([934db4f](https://github.com/raidcraft/rccities/commit/934db4f814c838dffb79fcf325fd06c43477448f))
* Fix calculation of plot costs ([1ba8efa](https://github.com/raidcraft/rccities/commit/1ba8efa296fb32781440c7828668a663bf060965))
* Fix join costs handling ([8c8028c](https://github.com/raidcraft/rccities/commit/8c8028cfe3c7d79c8843f9487e5df67c67fca657))
* Fix npe ([6842b6f](https://github.com/raidcraft/rccities/commit/6842b6f0bfdcb50c7fc0db7e36318707655c41fe))
* Fix plot costs calculation ([a09bdea](https://github.com/raidcraft/rccities/commit/a09bdea02fefd5d3fe9d5a840fdf5fedb8b99a87))
* Fix processing of join costs ([a0b01bd](https://github.com/raidcraft/rccities/commit/a0b01bdceadd0f973ee708d59b64adc70bd42b87))
* Fix some npes ([c7a6932](https://github.com/raidcraft/rccities/commit/c7a6932145082a3e6d8f3791d531e34a30128e23))
* More bugfixes ([027a135](https://github.com/raidcraft/rccities/commit/027a1354ccd88b990864af75a8814ec226a5aa5b))
* More bugfixes ([b268f78](https://github.com/raidcraft/rccities/commit/b268f78006a70145c2d3f841046624f450ca4128))
* More fixes ([f824bae](https://github.com/raidcraft/rccities/commit/f824bae6cbe845a6f40538ce3ead7322627b50f4))
* Remove usage of raw plot flag names. Use class reference instead ([8b9d7ac](https://github.com/raidcraft/rccities/commit/8b9d7ac34e306350afc461969e6d2e85f64c56ab))

# [1.4.0](https://github.com/raidcraft/rccities/compare/v1.3.0...v1.4.0) (2020-12-05)


### Bug Fixes

* Fix command context ([f5e7391](https://github.com/raidcraft/rccities/commit/f5e73911d3e80b7683a29645aa9bb1109e115ec0))
* Improve command handling ([5c3995f](https://github.com/raidcraft/rccities/commit/5c3995fbdfa81bc67380c798fe2e405c873b400e))


### Features

* Add command to buy new plots ([de06272](https://github.com/raidcraft/rccities/commit/de062725419dab653f6340d911c05e94b5af8e22))
* Add simple commands to set resident role ([6efdc05](https://github.com/raidcraft/rccities/commit/6efdc05f68dea8c7947262d43538a8c82020274a))
* Plots will be automatically marked when claimed ([b51743d](https://github.com/raidcraft/rccities/commit/b51743d2baadb29724d9633407b720f7562731a2))

# [1.3.0](https://github.com/raidcraft/rccities/compare/v1.2.0...v1.3.0) (2020-12-04)


### Bug Fixes

* Fix DB migration files. Fix Plot Mark flag. ([cc72aae](https://github.com/raidcraft/rccities/commit/cc72aae1f211fd04fb14aa73ee02861971feae80))
* Fix Plot Mark command ([587b689](https://github.com/raidcraft/rccities/commit/587b6891afeaf267134b0aeb648e1c4280ff8864))
* Fix some NPEs ([6d98cf1](https://github.com/raidcraft/rccities/commit/6d98cf1ee3a90e4d5ad5b924d6ee18f96500403d))
* More essential fixed ([fd622c2](https://github.com/raidcraft/rccities/commit/fd622c2a2d6275df8accb0f98da99808ef7ec490))


### Features

* Add dedicate commands to mark/unmark plots, fix NPEs ([f1a4f5f](https://github.com/raidcraft/rccities/commit/f1a4f5ffadf47e851959d236a62f699d4b3649ba))
* Add deposit and withdraw resident commands ([a862d15](https://github.com/raidcraft/rccities/commit/a862d151bd95205812fc125a829a925d44d5d539))
* Add money integration ([0ab9b5b](https://github.com/raidcraft/rccities/commit/0ab9b5bb1f882eec6b6888878a74cec4cb1a003d))
* Fix commands ([7170037](https://github.com/raidcraft/rccities/commit/7170037bc1b540fa3f0b1313f16e0195f9fe7e8a))

# [1.2.0](https://github.com/raidcraft/rccities/compare/v1.1.0...v1.2.0) (2020-12-02)


### Features

* Add dbmigration resources, fix npe while startup ([0e3aa57](https://github.com/raidcraft/rccities/commit/0e3aa57858ff673efde5fb98722c9856107c8969))
* Start fixing command implementation ([2a9b87a](https://github.com/raidcraft/rccities/commit/2a9b87a3d81cea0273d396ea4df77e9aeea77969))

# [1.1.0](https://github.com/raidcraft/rccities/compare/v1.0.0...v1.1.0) (2020-12-01)


### Bug Fixes

* Remove local path in grade properties ([921c81f](https://github.com/raidcraft/rccities/commit/921c81ff749b88133ae91ff84084d8cdf17b243f))


### Features

* Adapt worldguard changes ([d783238](https://github.com/raidcraft/rccities/commit/d783238aefef3d1d382b3fbf6dce09d3305ed1a6))
* Add old original implementation and update gradle scripts to contain required dependencies ([c3dbaa1](https://github.com/raidcraft/rccities/commit/c3dbaa18e6d84fde188ff1eece6b3481224b5a05))
* Add SchematicManager, add requirements and rewards (todo) ([92c126b](https://github.com/raidcraft/rccities/commit/92c126bf7cff0c0dac3bb131651f4e21004a1114))
* Add updates configuration ([16503f7](https://github.com/raidcraft/rccities/commit/16503f72bbdd0ae4ae9d12d51be2468082f05a3c))
* Finis command classes ([e4cb4bd](https://github.com/raidcraft/rccities/commit/e4cb4bdf7a2ddec8dfc38a44edd04e0afaee5539))
* Fix AbstractCity and Pluginconfig ([8b748d3](https://github.com/raidcraft/rccities/commit/8b748d3c1d4aaae51de6e8a6288a31e79a7123d9))
* Fix city database entities ([da57f9b](https://github.com/raidcraft/rccities/commit/da57f9b20ed222ff7b040ebc357379b8e9fd7a93))
* Fix city plot implementation ([83636b4](https://github.com/raidcraft/rccities/commit/83636b4e7ee742053ca709a073ee8847680e4ac6))
* Fix flags package ([d467079](https://github.com/raidcraft/rccities/commit/d46707981b75e6205ef60ea08a56b9b6815f13ca))
* Fix managers ([35b6ec7](https://github.com/raidcraft/rccities/commit/35b6ec7bfca951f1c586986e741668444b4a17f8))
* Fix more original classes ([e0673a8](https://github.com/raidcraft/rccities/commit/e0673a8b7b1f2acf5de574bfd213365f629a7927))
* fix remaining problems. Now compilable ([ca35119](https://github.com/raidcraft/rccities/commit/ca35119a6472714b80717ce27211179e6ed4ed7b))
* Fix upgrades database implementation ([f128209](https://github.com/raidcraft/rccities/commit/f128209b19c5e8c090c3fd3fd27dedc45d64be5b))
* Further req implementation ([3a37dd3](https://github.com/raidcraft/rccities/commit/3a37dd3e4fe67f84f750f701dabff1a30c156084))
* Further updates on original code ([b02c8e1](https://github.com/raidcraft/rccities/commit/b02c8e1ed99e1ba2cdad2cfa00238d98d38fe6a6))
* Include schematic manager ([d81c5f4](https://github.com/raidcraft/rccities/commit/d81c5f4224db19f1d64653810ef084296ef384b0))
* Start updating upgrades implementation ([6c49537](https://github.com/raidcraft/rccities/commit/6c49537df58749e0a47997aa18aa3a6060b4df8b))

# 1.0.0 (2020-11-26)


### Features

* Set initial project configuration ([554ab84](https://github.com/raidcraft/rccities/commit/554ab84ae49f5ff492edc024b00842a43fa625bb))
