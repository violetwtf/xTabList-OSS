# Released due to unpaid commission
This was commissioned on March 12th, 2020. It's been one month since payment was due, so I'm
releasing it here. This isn't my best work, I was still using instancing back then.

All references to the network have been redacted. Please let me know privately if I missed anything
at vi@violet.wtf.

There is no support provided.

# xTabList
Created for the [redacted] Minecraft network.

## Config
```yaml
# The header of your tab list
header:
- '&6&l{server-name}'
# The footer of your tab list
footer:
- 'Players: &a{player-count} &fPing: &a{ping}'
# Set this to true to update everyone's tab list when people join or quit (this updates the
# player count, so you probably want it to be true.) Set it to false otherwise.
update-on-join-or-quit: true
# Set this to true update everyone's tab list every update-interval seconds. False otherwise.
update-regularly: true
# The interval (in seconds) to update everyone's tab list if update-regularly is true.
update-interval: 5
# Set this to true to spam your console with xTabList debug messages. (Keep it false haha)
debug: false
```
