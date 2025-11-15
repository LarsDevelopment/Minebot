# Minebot

A simple Minecraft Discord bot. This was my first Java plugin project ever! (So pls dont hate on the code 😭)

## Key Features & Benefits

*   Connect your Minecraft server to Discord.
*   Relay server information to Discord.
*   Simple and easy to configure.
*   Easly whitelist people with the /whitelist command!

## Prerequisites & Dependencies

*   Java Development Kit (JDK) - Version 8 or higher.
*   Maven (for building the project).
*   A Discord bot token.
*   A Minecraft Server running PaperMC or Spigot.

## Installation & Setup Instructions

1.  **Clone the Repository:**

    ```bash
    git clone https://github.com/LarsDevelopment/Minebot.git
    cd Minebot
    ```

2.  **Build the Project:**

    ```bash
    mvn clean install
    ```

    This will create a `MineBot.jar` file in the `target` directory. If the above fails you may need to update your maven, and JDK version.

3.  **Configure the Bot:**

    *   Locate the `config.yml` file in `src/main/resources/`.
    *   **Discord Bot Token:** Add your Discord bot token to the `config.yml` file.
    *   **Minecraft Server IP:** Add your Minecraft server IP.
    *   **Discord Channel ID:** Add the ID of the Discord channel you want the bot to send messages to.


4.  **Add the `.jar` to your plugins folder.**

    *   Navigate to the `target` folder where you build the project in step 2.
    *   Copy `MineBot.jar` to the `/plugins/` folder of your PaperMC or Spigot server.
    *   Start the server to load the plugin.

## Usage Examples & API Documentation

This bot primarily relays information from your Minecraft server to a Discord channel.  There is no API for external use.

## Configuration Options

The following options can be configured in the `config.yml` file:

| Option              | Description                                       | Example                  |
| ------------------- | ------------------------------------------------- | ------------------------ |
| `token`     | The token for your Discord bot.                   | `"YOUR_DISCORD_BOT_TOKEN"` |
| `channel` | The ID of the Discord channel to send messages to. | `"YOUR_DISCORD_CHANNEL_ID_HERE"`        |
| `guildId` | The ID of your Discord server to register commands to. | `"GUILD_ID_HERE"` |
| `adminrole` | The ID of your Discord Admin role so they can whitelist people using the command! | `"ADMIN_ROLE_ID_HERE"` |


## Contributing Guidelines

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Make your changes.
4.  Submit a pull request.

Please ensure your code follows the project's coding style.

## License Information

License not specified.

## Acknowledgments

*   [PaperMC](https://papermc.io/) - For providing a stable and optimized Minecraft server platform.
*   [JDA (Java Discord API)](https://github.com/DV8FromTheWorld/JDA) - For simplifying Discord bot development.
