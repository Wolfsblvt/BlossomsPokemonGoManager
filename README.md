[![Travis Build Badge](https://img.shields.io/travis/Wolfsblvt/BlossomsPokemonGoManager.svg)](https://travis-ci.org/Wolfsblvt/BlossomsPokemonGoManager)
[![Codacy](https://img.shields.io/codacy/grade/14b05062286f448fb5b59708c4936e42.svg)](https://www.codacy.com/app/wolfsblvt/BlossomsPokemonGoManager?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Wolfsblvt/BlossomsPokemonGoManager&amp;utm_campaign=Badge_Grade)
[![Codacy Coverage](https://img.shields.io/codacy/coverage/14b05062286f448fb5b59708c4936e42.svg)](https://www.codacy.com/app/wolfsblvt/BlossomsPokemonGoManager?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Wolfsblvt/BlossomsPokemonGoManager&amp;utm_campaign=Badge_Grade)
[![Code Climate](https://img.shields.io/codeclimate/github/Wolfsblvt/BlossomsPokemonGoManager.svg)](https://codeclimate.com/github/Wolfsblvt/BlossomsPokemonGoManager)
[![Code Climate Coverage](https://img.shields.io/codeclimate/coverage/github/Wolfsblvt/BlossomsPokemonGoManager.svg)](https://codeclimate.com/github/Wolfsblvt/BlossomsPokemonGoManager/coverage)
[![Codebeat](https://codebeat.co/badges/6d02b875-b4c1-4ec5-8c59-53715dc40751)](https://codebeat.co/projects/github-com-wolfsblvt-blossomspokemongomanager)
[![License](https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-blue.svg)](https://creativecommons.org/licenses/by-nc-sa/4.0/)
[![Java Version](https://img.shields.io/badge/Java-JRE%208-red.svg)](https://www.java.com/download/)

[![GitHub Latest Release](https://img.shields.io/github/release/Wolfsblvt/BlossomsPokemonGoManager.svg)](https://github.com/Wolfsblvt/BlossomsPokemonGoManager/releases/latest)
[![Github Releases](https://img.shields.io/github/downloads/Wolfsblvt/BlossomsPokemonGoManager/latest/total.svg)](https://github.com/Wolfsblvt/BlossomsPokemonGoManager/releases/latest)
[![Github All Releases](https://img.shields.io/github/downloads/Wolfsblvt/BlossomsPokemonGoManager/total.svg)](https://github.com/Wolfsblvt/BlossomsPokemonGoManager/releases)



# BlossomsPokemonGoManager
BlossomsPokemonGoManager is a tool created for managing your game. It allows you to sort your Pokémon by several values, to rename, transfer, evolve or to power-up one or several of them.  
It was made for easier management of Pokémon, deciding which to keep, and which to throw away.

#### Screenshots
[Tool overview](http://i.imgur.com/SM1Y3Sf.png) **|** [Login](http://i.imgur.com/3UNq3I8.png) **|** [Omnisearch Bar (GIF)](http://i.imgur.com/kW72gxB.gif) **|** [Batch Operations (GIF)](http://i.imgur.com/4H12TJM.gif) **|** [Sorting (GIF)](http://i.imgur.com/1c9rMIi.gif)

## Current Features
- Both Google and PTC authenticated logins
- Localized pokémon names
- Exact IV (*Individual values*) displayed
- Powerful Omnisearch bar, allowing you to search by nickname, species, family, types, moveset, and pokeball used to capture
- Batch operation tools, allowing you to quickly rename, transfer, evolve, and power-up your pokemon.
- Tons of sort options, allowing you to sort your pokemon quickly by nickname, pokedex number, species, IV%, types, moves (with DPS), CP, Max CP, HP, total candies, candies to evolve, stardust to powerup, caught date, and what the pokemon was caught with.

A lot more features are planned! Check [Issue page for enhancements](/../../issues?q=is%3Aissue+is%3Aopen+label%3Aenhancement) for an overview.  
You can even submit your own suggestions.

## Installation / Usage
#### **Latest Stable Release**
1. You need at least **Java 8** installed to run this tool. Verify your Java version [here](https://www.java.com/verify/) or download [the latest Java JRE release](https://www.java.com/download/) and install it.
2. Download [the latest stable version of this tool](/../../releases) from releases.
3. Double-Click the extracted `.jar` file and run it.
4. Log in with your credentials. PTC account directly, for Google follow the instructions and copy the session token.
5. Enjoy the tool!

If the tool is not working under Linux/Ubuntu, try the following steps:

1. Give execution right to the shell script: `chmod +x RunOnUnixLikeSystems.sh`
2. Use Oracle JDK instead of Open JDK. (See issue [#306](/../../issues/306) for more details)

#### **Using the Dev Version**
**WARNING!** This version might be unstable, so use at your own risk.

1. Java SDK for Java 8 has to be installed.
2. Clone the repository on the [develop branch](/../../tree/develop) locally or download the ZIP of this branch.
3. Build the project with either maven compiler, or your IDE of choice (Eclipse, IntelliJ)  .
To use it in an IDE, you have to import the project as a Maven project.
4. Run it as an Application, with `me.corriekay.pokegoutil.BlossomsPoGoManager` as entry file.

*(more detailed instruction will follow)*

## Working on this project
#### Developing
We are open for any help on our tool.  
If you are fond of programming, you can always take one of the open issues, post that you want to help and create a Pull Request. For other features PRs are welcome too, but make sure the feature is really needed and/or not done in another way so that the work isn't wasted.

**IMPORTANT! Please submit Pull Requests only against `develop`! PRs against master will be rejected.**

Thanks for your help.

#### Submitting Issues
That's the best way how you can help us.  
If you found a bug, submit an issue. If you have a suggestion, submit an issue. **Just make sure, BEFORE SUBMITTING, that the issue wasn't already mentioned. Search on the issue tracker helps there.**
Just one thing: **Please try to not group too many bugs/suggestions in one issue** It's better to have ten more issues where it's clear what there is inside than to have one big blob. So don't hesitate to create more than one issue.

If you have **Bugs**, please be as detailed as possible.  
Write down the steps that led to the error. Describe what happened, and what you would've expected. If there is an Error written somehwere, post the full code in your issue. If you want, include a screenshot. And if it's a bug with the development version, specifiy that right at the beginning of your issue.

## Disclaimer
While we are fairly positive that this tool will not get your account into any trouble, please use at your own risk. There are no direct statements from Niantic what third-party tools are allowed and what not, but it might go against their ToS.

## Discord
We use discord to organize our development and answer questions. We also post important announcements there.  
[Our discord can be found here](https://discord.gg/APceUzU)

## FAQ

#### Q: Can I get banned for using this tool?
- **A:** Yes. No. Maybe. It's possible, but no one can really answer that exactly right now. Read the Disclaimer a few lines above this FAQ.

#### Q: The app doesn't start. It tells me `Could not find or load main class ...`?
- **A:** Java 8 is not installed (correctly). Download and install [the latest Java JRE release](https://www.java.com/download/).

#### Q: After login no window shows up. No error is displayed
- **A:** It's a small bug with the current config. Go to your folder where the `.jar` file is and delete the `config.json`.

#### Q: Java 8 is not available for my linux system. What do I do?
- **A:** Read up [Install Oracle Java 8 (JDK8 and JRE8) in Ubuntu or Linux Mint](http://www.webupd8.org/2012/09/install-oracle-java-8-in-ubuntu-via-ppa.html) on how you exactly do it.

#### Q: Is the IV calculated somehow? Is it the correct value or maybe wrong? My IV calculator says something different.
- **A:** The IV isn't calculated, it is directly taken from the API response. It's the exact value like the game has it.

#### Q: What is this alternative IV calculation mode? What does it do?
- **A:** Usual IV rating weighs every three values the same, so 1/3. The alterative mode takes in mind that attack is more important when calculating things like CP of the Pokémon. For a detailed information and a real-life example see the issue about it: [#165](/../../issues/165)

#### Q: Can you add catching of Pokémon and using PokéStops?
- **A:** No. We'll include lucky egg support for evolving, but that's it. We are not just another botting tool. We want to keep the use of our tool as fair as possible and just make managing Pokémon easier.

#### Q: Can you include feature XY?
- **A:** Maybe yes! Submit an issue explaing your suggestion as detailed as possible. We will look over it and see if we will include it and when (:
