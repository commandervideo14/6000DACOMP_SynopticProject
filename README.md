# 6000DACOMP_SynopticProject

This is the repository for a utility Discord bot, which offers a particular set of features that can be invoked in a Discord Server.

No Bot Token is included within these files. In order to run this application, you must supply your own Bot Token as an argument in your Run Configuration.

A SQL Server connection must be established for the application (navigate to BotDb.java to further alter the configuration for this if necessary).

To inititalise the Database and Tables required for the application to write and load from, please reference the BotDB SQL Scripts.txt in the resources folder.

Upon start-up, Slash Commands should be upserted for any Discord Servers it is a Member in, and will also upsert them as it joins any new Discord Servers within a running session.

