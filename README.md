# BattlePass Plugin

A fully featured BattlePass plugin for Minecraft servers (Spigot/Paper 1.20.4+).

This is a complete Java rewrite of the original Skript-based battlepass system, with improved design, better data persistence, and cleaner code architecture.

## Features

- **XP & Leveling System**: Players earn XP through various activities and level up
- **Mining XP**: Earn +2 XP per block mined in survival mode
- **Kill XP**: Earn +3 XP per player kill
- **Special Pickaxe**: The Krampus' Candy Cane netherite pickaxe gives +4 XP per block (3% chance for +8 XP)
- **Double XP Events**: Admins can start timed double XP events
- **Reward System**: Configure text descriptions and item rewards for each level
- **Interactive GUI**: Beautiful inventory-based GUI for viewing progress and claiming rewards
- **Data Persistence**: All player data is saved to YAML files
- **Admin Tools**: Complete set of admin commands for management

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/battlepass` or `/bp [page]` | Open the BattlePass GUI | None |
| `/bptop` | Show top 5 players by level | None |
| `/doublexpevent <time>` | Start a double XP event (e.g., 30m, 1h, 2h30m) | `battlepass.admin` |
| `/stopdoublexp` | Stop the current double XP event | `battlepass.admin` |
| `/doublexpstatus` | Check double XP event status | `battlepass.admin` |
| `/bprewardsetup [page]` | Open reward setup GUI | `battlepass.admin` |
| `/bpreward <level> <text>` | Set reward description for a level | `battlepass.admin` |
| `/bpadmin give <player> <xp/level> <amount>` | Give XP or levels to a player | `battlepass.admin` |
| `/bpadmin reset <player>` | Reset a player's battlepass data | `battlepass.admin` |
| `/lockbp` | Lock the battlepass (prevent XP gain) | `battlepass.admin` |
| `/unlockbp` | Unlock the battlepass | `battlepass.admin` |
| `/bpstatus` | Check battlepass lock/event status | `battlepass.admin` |
| `/bppickaxe [player]` | Give the special BattlePass pickaxe | `battlepass.admin` |
| `/resetbattlepassall` | Initiate full data reset | `battlepass.admin` |
| `/confirmresetall` | Confirm the full data reset | `battlepass.admin` |

## Permissions

- `battlepass.admin` - Access to all admin commands (default: op)

## Building

### Requirements
- Java 17+
- Maven 3.6+

### Build Command
```bash
mvn clean package
```

The compiled JAR will be in the `target` directory.

## Installation

1. Build the plugin or download a release
2. Place the JAR file in your server's `plugins` folder
3. Restart the server
4. Configure rewards using `/bprewardsetup` or `/bpreward` commands

## Configuration

### config.yml
Contains general settings, XP values, and messages.

### playerdata.yml
Automatically generated file storing all player data (level, xp, kills, blocks mined, claimed rewards).

### rewards.yml
Automatically generated file storing reward configurations (text descriptions and items for each level).

## XP Formula

The XP required for each level follows an exponential curve:
```
XP Required = 100 × (1.21 ^ (level - 1))
```

| Level | XP Required |
|-------|-------------|
| 1 | 100 |
| 2 | 121 |
| 3 | 146 |
| 5 | 214 |
| 10 | 556 |
| 20 | 3,788 |
| 50 | 618,793 |

## Project Structure

```
src/main/java/com/battlepass/
├── BattlePassPlugin.java      # Main plugin class
├── commands/                   # All command handlers
│   ├── BattlePassCommand.java
│   ├── BPAdminCommand.java
│   ├── BPPickaxeCommand.java
│   ├── BPStatusCommand.java
│   ├── BPTopCommand.java
│   ├── ConfirmResetCommand.java
│   ├── DoubleXPEventCommand.java
│   ├── DoubleXPStatusCommand.java
│   ├── LockBPCommand.java
│   ├── ResetAllCommand.java
│   ├── RewardSetupCommand.java
│   ├── SetRewardCommand.java
│   ├── StopDoubleXPCommand.java
│   └── UnlockBPCommand.java
├── data/
│   └── PlayerData.java        # Player data model
├── gui/
│   └── GUIManager.java        # GUI creation and management
├── listeners/
│   ├── BlockBreakListener.java
│   ├── GUIListener.java
│   ├── PlayerDeathListener.java
│   └── PlayerJoinListener.java
├── managers/
│   ├── DataManager.java       # Player data persistence
│   ├── DoubleXPManager.java   # Double XP event management
│   └── RewardManager.java     # Reward configuration
└── utils/
    ├── ItemUtils.java         # Item creation utilities
    └── XPUtils.java           # XP calculation utilities
```

## Improvements Over Original Skript

1. **Better Data Storage**: Uses YAML files with proper serialization instead of Skript variables
2. **Cleaner Architecture**: Organized into managers, listeners, and command handlers
3. **Type Safety**: Full Java type checking prevents runtime errors
4. **Performance**: More efficient data structures and async saving
5. **Maintainability**: Well-documented code with clear separation of concerns
6. **Extensibility**: Easy to add new features or modify existing ones
7. **Error Handling**: Proper null checks and exception handling

## License

This project is provided as-is for use on Minecraft servers.
