# COS126 Final Project: Status Update

*Please complete the following questions and upload this file to TigerFile.*

***Do not alter the formatting**
(e.g. write your answer after the asterisks ** FIELD NAME ** or on the next
line after the header ### HEADER NAME). We have filled in a dummy response
for the first question in the first two sections as an example.*

## Basic Information

1. **Name 1:**
Andy Nguyen

2. **NetID 1:**
an4978

3. **Name 2 (include if pair project):**


4. **NetID 2 (include if pair project):**


5. **Project preceptor name:**
Dr. Ruth Fong

6. **Project title:**
Sharp Shots

7. **CodePost link for proposal:**
https://codepost.io/code/566850

8. **CodePost link for revised proposal (include if submitted):**


## Required Questions
*Short answers for the below questions suffice. If you want to alter your plan for
your project, be sure to **email** your project preceptor directly!*

###A. Summarize the MVP version of your project in 1-3 sentences below.

The MVP version of my project has a player that can move around and shoot at
enemies and asteroids for points, and points will be for a highscore type of
game. It will implement a GUI to display the game to the user and take keyboard
inputs from the player to interact with the game.

###B. Summarize your proposed three MVP features (bullets suffice)

1. A player object that handles movement inputs and shooting. Can get strronger
by picking up power ups.

2. Enemy objects that move based on a simple random movement algorithm. Can
shoot at the player and get stronger.

3. Asteroid objects that move around the map. Can be shot at to be destroyed or
moved.


###C. Of those three features, what have you done for your project so far?

The player can move around with the arrow keys and has an indicator for where
and when it can shoot.

The enemy base class exists with a general outline for the movement and attack
pattern methods.

The asteroids can move around and rotate across the map and can collide with
other entities in the game.

All collisions in the game are between convex shapes and I have successfully
implemented SAT collisions with minimum translation vectors for collision
resolutions.


###D. Of those three features, what have you not done for your project yet?

The player can't shoot yet since the bullet classes are still being implemented.

The enemies aren't exactly functionally enemies, more so just entities that
exist on the map.


###E. What problems, if any, have you encountered?

Not quite sure how to properly adjust the angular velocity of asteroids when
they collide with one another since those should be affected by collisions.


###F. What do you plan to do in the week remaining before Dean's Date for an expected version of your project?

I plan on implementing projectiles into the game (normal bullets and homing
bullets) and I plan on having a system for handling the physics simulation.


###G. What do you hope to be able to do before Dean's Date for an ambitious version of your project?

Adding visual effects for game interactions, such as sparks and other particle
effects.

## Optional Questions
*The following questions is optional but highly recommended to answer, as they are
helpful exercises if you have not made much progress yet. They also are a natural
way for your project preceptor to provide additional feedback.*

### Outline your planned API for your project
*(e.g. method signatures and short comments explaining each method; bullets suffice)*




### Do you have any questions for your project preceptor?



