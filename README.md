# Demise 2000
## Top down adventure game
My plan is to use this as the foundation for building several 2D adventure, dungeon crawler RPG, and farming-sim "cozy games." This game has a slanted top down view.  Enemy and NPC actors are categorized as Entity class.  Widgets are interactive objects in the game world, that the player can destroy or trigger with the action button.  Particles include attack swoshes, leaves, blood splatter.   Sprite sheets are generally 200px by 200px with each 50x50 row being for a character direction, and each column being a different animation frame. 

Enemy pathfinding works by generating a pathfind number grid around the player in several steps.  The frequency that this is done in game loop ticks can be configured.     

### Controls
* WASD and Arrow keys = Movement
* E = Activate
* F = Attack
* Shift = Sprint
* I = Inventory
* Q = Toolbar

### Level Editor
* Numpad 0 = change edit mode
* F1 = save edits
* F2 = reload map data from file
* Numpad decimal = latch paint mode
* Numpad + = delete decor and other sprites
* Mouse MC/RC = change asset
* Mouse LC = paint
* ` In-game console

## Building
The project can be built using Eclipse. Unzip the project folder archive, or clone the repo using git.  File >> Open Projects from FileSystem >> select the project folder.  Finish
