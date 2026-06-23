# Quick Shulker Multi

[English] | [[简体中文]](docs/README_zh_cn.md)

#### Quickly open shulker boxes(and more) without placing them down and refactor using preprocessor

Support Minecraft from 1.20.6 to latest  
Welcome to add localization to this project!  

Use the multi-version build tool preprocessor to build mods for multiple Minecraft versions quickly.

This project's upstream [MoRanpcy/quickshulker](https://github.com/MoRanpcy/quickshulker) updates [kyrptonaught](https://github.com/kyrptonaught)'s [QuickShulker](https://github.com/kyrptonaught/quickshulker.git) mod to higher Minecraft versions and fixes something.

The mod's i18n and l10n efforts need your help! We would be incredibly grateful if you could assist!

### Mod Naming Convention

| build               | naming                                                                                                     |
|---------------------|------------------------------------------------------------------------------------------------------------|
| release             | quickshulker-v{mod_version}-mc{mc_version}-{commit_count}-{commit_hash}-release.jar                        |
| pull_request        | quickshulker-v{mod_version}-mc{mc_version}-{build_branch}-{build_branch_commit_count}-{commit_hash}-pr.jar |
| ci                  | quickshulker-v{mod_version}-mc{mc_version}-{build_branch}-{build_branch_commit_count}-{commit_hash}-ci.jar |
| local               | quickshulker-v{mod_version}-mc{mc_version}-{build_branch}-{build_timestampmillis}-development.jar          |
| maven               | {mc_version}-{mod_version}.jar                                                                             |
| **EXAMPLE BUILDS:** | **EXAMPLE NAMING:**                                                                                        |
| release             | `quickshulker-v3.2.6-mc1.21.11-187-6068667-release.jar`                                                    |
| pull_request        | `quickshulker-v3.2.6-mc1.21.11-dev-187-eba238a-pr.jar`                                                     |
| ci                  | `quickshulker-v3.2.7-mc1.21.11-dev-192-6fb9034-ci.jar`                                                     |
| local               | `quickshulker-v3.2.7-mc1.21.11-dev-1782234626080-development.jar`                                          |
| maven               | `1.21.11-3.2.7.jar`                                                                                        |

### Quick Download

Click on the link below to download.  
Please choose the appropriate mod for your Minecraft version.

* [The Latest RELEASE (GitHub Release)](https://github.com/EnderPhantomWing/quickshulker-multi/releases/latest)

* [The Newest SNAPSHOT (GitHub Actions)](https://github.com/EnderPhantomWing/quickshulker-multi/actions)

### Thanks

- [kyrptonaught/quickshulker](https://github.com/kyrptonaught/quickshulker) Mod author
- [Grayer0113/quickshulker](https://github.com/Grayer0113/quickshulker/tree/1.20) Support MC 1.20.4
- [Haocen2004/quickshulker](https://github.com/Haocen2004/quickshulker) Support MC 1.20.6 & 1.21(.1)
- [MoRanpcy/quickshulker](https://github.com/MoRanpcy/quickshulker) Support MC 1.21+ and fix some bugs
- And everyone who supports development, including you!

### License and third-party sources

This project follows the [MIT License](https://mit-license.org/)  
[kyrptonaught/quickshulker](https://github.com/kyrptonaught/quickshulker) is the initial version that provides module ideas.  
[MoRanpcy/quickshulker](https://github.com/MoRanpcy/quickshulker) has been ported to version 1.21+.    
[LICENSE](LICENSE) | [THIRD PARTY NOTICES](THIRD_PARTY_NOTICES.md)