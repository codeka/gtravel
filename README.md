# Global Traveler

A Minecraft mod that adds a "Global Traveler" enchantment, which works mostly
like the Global Traveler trait in PlusTic: you can bind the tool to an
inventory, and then any blocks subsequently mined by that tool will be
instantly teleported into that inventory.

## EnderIO Enchanter

I used the below script to add a recipe to the EnderIO enchanter in my world.
You'll need to choose an ingredient that makes sense for your particular mods
(I'm currently playing Omnifactory, so gregtech is a good source of ingredients)

Put the below into a file called .minecraft/config/enderio/recipes/user/gtravel.xml:

    <?xml version="1.0" encoding="UTF-8"?>
    <enderio:recipes
        xmlns:enderio="http://enderio.com/recipes"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://enderio.com/recipes recipes.xsd ">
      <recipe name="Enchanter: gtravel:global_traveler" required="true" disabled="false">
        <enchanting>
          <input name="gregtech:compressed_9:14" amount="4"/>
          <enchantment name="gtravel:global_traveler" costMultiplier="1"/>
        </enchanting>
      </recipe>
    </enderio:recipes>
