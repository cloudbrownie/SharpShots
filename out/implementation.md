# COS126 Final Project: Implementation

Please complete the following questions and upload this `implementation.md`
file to the TigerFile assignment for the "Final Project Implementation".

**Do not alter the formatting**
(e.g. write your answer after the asterisks ** FIELD NAME ** or on the next
line after the header ### HEADER NAME). We have filled in a dummy response
for the first question in the first two sections as an example.

## Basic Information

You may copy and paste your answers from questions 1-8 in the status update.

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


9. **CodePost link for status update:**
https://codepost.io/code/581996

10. **Link to project video:**
https://youtu.be/IhE3J_o_T64

11. **Number of hours to complete implementation:**
Around ~72 hours

## Required Questions

### A. Describe your implemented project in a few sentences below.

My final project is a (sort of) revamped version of the classic game Asteroids.
The player is a ship in space that can move around and destroy asteroids for
points. The player tries to gain as many points as possible while fending off
enemies that get progressively stronger.

Implementation got a little messy, but it works.

### B. Describe your three features.
*Be specific in your description of each feature.
In particular, specify **where** they are implemented
(e.g. .java file name, starting and ending line numbers, method names, etc.).*

  1. The player object is the game element controlled by the player. The player
  object can move around the screen with the user's input, and the can interact
  with the environment by shooting at other entities, such as asteroids and
  enemies in the game. I did not use the mouse for player input as a decision to
  keep the controls somewhat consistent with the original Asteroids game that
  inspired this game.

  Implemented in Player.java

  2. The enemy object is a game element that is randomly created to come and
  fight the player. The enemy can move and shoot towards the player. They
  gradually get stronger as time passes, but are able to drop buffs
  corresponding to their increase attributes upon death.

  Implemented in Enemy.java

  3. The asteroid object is a game element that simply exists and moves around.
  The player can interact with these asteroids by shooting at them and
  destroying them.

  Implemented in Asteroid.java


### C. Describe in detail how to compile and run your program.
*Include a few example run commands and the expected results of running your program.
For non-textual outputs (e.g. graphical or auditory), feel free to describe in
words what the output should be or reference output files (e.g. images, audio files)
of the expected output.*

To run the java file, run the following command:
java-introcs SharpShots

To run the jar file, run the following command:
java-introcs -jar project.jar

You can also run the .jar as you would a .exe app (or any standard PC app).

When run, a StdDraw window should appear along with a start page welcoming you
to the game, along with instructions and basic images of game elements.

### D. Describe how your program accepts user input
*Mention the line number(s) at which your program accepts user input.*

(I am only mentioning lines that are used when running SharpShots, and not other
misc. main testing methods in the other classes)
The program accepts input from the keyboard, prominently in the Player.java
class on lines 184, 189, 192, 197, and 204.

### E. Describe how your program produces output based on user input
*Mention the line number(s) at which your program produces output.*

The program draws to the screen in a StdDraw window. The user interacts with the
program using the keyboard which controls the player on the screen, through
thrusting the ship forward, turning left or right, and shooting at anything the
player desires.

### F. Describe the data structure your program uses
*Also describe how it supports your program's functionality.
Include the variable name and the line number(s) at which it is declared
and initialized).*

The most prominent and integral data structure used was the Java ArrayList.
Several of them were used, most notability in my EventHandler class:
 - line 8:  ArrayList<Entity> entities = new ArrayList<>();
 - line 13: ArrayList<Asteroid> asteroids = new ArrayList<>();
 - line 16: ArrayList<Enemy> enemies = new ArrayList<>();
 - line 19: Arraylist<Buff> buffs = new ArrayList<>();
 - line 22: ArrayList<Bullet> projectiles = new ArrayList<>();

The reason why ArrayLists were used were because they can be variable in length
and are easy to work with. While I could have made a LinkedList class with nodes
for each of my entities, I preferred the out-of-the-box functionality of the
ArrayList and decided it best to not reinvent the wheel. The functionality of an
ArrayList simply suits the variable number of entities in the game at any given
point and worring about implementing a LinkedList structure for my game such
that it is compatible with my different objects would have been a project in
itself.

### G. List the two custom methods written by your project group
*Include method signatures and line numbers.
If your project group wrote more than two custom functions, choose the
two functions that were most extensively tested.*

1. File - BasePolygon, on line 342:
    public Vector collide(BasePolygon b) {}

2. File - CollidablePolygon, on line 61:
    public Vector collide(CollidablePolygon otherPoly) {}

### H. List the line numbers where you test each of your two custom methods twice.
*For each of the four tests (two for each method), explain what was being
tested and the expected result. For non-textual results (e.g. graphical or
auditory), you may describe in your own words what the expected result
should be or reference output files (e.g. images, audio files).*
1. BasePolygon, line 30 - line 71: the defaultTest() method.

In this method, I was testing the collide() method between two BasePolygons to
check if the collision resolution between the two polygonswas accurate enough.
It did so by creating two BasePolygon objects, rotating one of them such that
it would eventually collide with the other polygon, and the expected result
would be for both polygons to be pushed away from each other.

You can test this method by running the following command (assuming files are compiled):
java-introcs BasePolygon

2. BasePolygon, line 74 - line 147: the squareTest() method.

In this method, I was testing the collide() method but with multiple squares to
test a simulation of collision between multiple objects. You are able to control
the red squared using the WASD keys to move around and the Q and E keys to
rotate left or right, respectively. It tests the collisions between polygons the
polygons (just with squares but if it works for squares then it works for all
reasonable N polygons).

You can test this method by running the following command (assuming files are compiled):
java-introcs BasePolygon SQUARES

3. CollidablePolygon, line 121 - line 183: the defaultTest() method.

In this method, I test the collide method between two polygons to check whether
the expensive separating axis theorem collision method is being used, or the
cheap axis aligned bounding box collision method is being used. The program
takes input similar to the squareTest() method in BasePolygon, which allows the
user to interact with the triangle polygon. When moving the triangle towards the
other polygon, the rectangle around the triangle should become red when
colliding with the rectangle of the other polygon. Only when the two polygons
actually touch other should the polygons themselves be blue.

You can test this method by running the following commad (assuming files are compiled):
java-introcs CollidablePolygon

4. CollidablePolygon, line 185 - line 264: the nTest() method.

In the method, I pretty much test the same scenario as the BasePolygon
squareTest() method, except with N number of polygons that the user wishes to
test. The same color effect as seen in the CollidablePolygon defaultTest()
method, except this time each of the polygons are also moved when they collide.

You can test this method by running the following command (assuming files are compiled):
java-introcs CollidablePolygon N

(^ where N is any integer)

## Citing Resources

### A. List below *EVERY* resource your project group looked at
*Bullet lists and links suffice.*

Java Classes:
- Color : https://docs.oracle.com/javase/7/docs/api/java/awt/Color.html
- ArrayList : https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
- Stddraw : https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html
- Stdrandom : https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdRandom.html

Basic Ideas:
- N Tutorial A - Collision Detection and Response : https://www.metanetsoftware.com/technique/tutorialA.html

JAR Tutorial
- JetBrains - JAR in IntelliJ : https://www.jetbrains.com/help/idea/compiling-applications.html#package_into_jar

### B. Did you cite every resource that influenced your code in the code itself?
*Every resource that informed your code should be cited in a comment at/near the
line(s) of code that it informed.*

**Yes or No?**
yes


### C. Did you receive help from classmates, past COS 126 students, or anyone else?
*If so, please list their names. ("A Sunday lab TA" or "Office hours on
Thursday" is ok if you don't know their name.)*

**Yes or No?**
no


### D. Did you encounter any serious problems? If so, please describe.

**Yes or No?**
no


### E. List any other comments here. ###

this was kind of rushed unfortunately, wish i could spend more time refactoring.
got lazy toward the end to properly organize like i was in earlier files.


## Submission Checklist
*Please mark that you’ve done all of the following steps
(fill in square bracket with x, i.e. [x]):*

* [X] Created a project.zip file, unzipped its contents, and checked that our
    compile and run commands work on the unzipped contents. Ensure that the .zip
    file is under 50MB in size.
* [X] Created and uploaded a Loom or YouTube video that...
  * [X] is maximum 2 minutes in length
  * [X] demonstrates live your program's input(s) and output(s)
  * [X] demonstrates live your 3 features
  * [X] does **not** reveal any code
  * [X] includes your project name and the name of each student
  * [X] has its thumbnail and/or starting frame set to an image of your program
        or a title slide
  * [X] is publicly viewable (check in an incognito browser)
  * [X] is linked to in this `implementation.md` file (Q10 under Basic Information)
* [X] Uploaded all .java files to TigerFile. Each .java file should be uploaded
      separately as an additional file.
* [X] Uploaded project.zip file to TigerFile.

*After you’ve submitted the above on TigerFile, **remember to do the following**:*
* [X] Complete and upload this `implementation.md` file to TigerFile.
* [X] Complete and submit this Google Form
    (https://forms.cs50.io/27ca51e0-4d81-4d97-8621-ba1e5d26cd78).


## Partial Credit: Bug Report(s)
*For partial credit for buggy features, you may include a bug report for at
most 4 bugs that your project group was not able to fix before the submission
deadline. For each bug report, copy and paste the following questions and
answer them in full. Your bug report should be detailed enough for the grader
to reproduce the bug.*

***Note:** if your code appears bug-free, you should **not** submit any bug reports.*

### BUG REPORT #1:
**1. Describe in a sentence or two the bug below.**




**2. Describe in detail how to reproduce the bug (e.g. run commands, user input,
etc.).**




**3. Describe the resulting effect of bug and provide evidence
(e.g. copy-and-paste the buggy output, reference screenshot files and/or buggy
output files, include a Loom video of reproducing and showing the effects of
the bug, etc.).**




**4. Describe where in your program code you believe the bug occurs (e.g. line
numbers).**




**5. Please describe what steps you tried to fix the bug.**





## Extra Credit

### A. Going above and beyond the scope of COS126

#### Did your program go above and beyond the scope of COS126?

**Yes or No?**
yes

*If yes, please answer the following question.*

#### Describe in detail how your program went above and beyond the scope of COS126.

I created an algorithm based on the separating axis theorem to simulate somewhat
realistic physic collisions and resolutions. I also went with an OOP approach,
with lots of my program's functionality being dependent on polymorphism. A lot
of classes inherit from others, such as the player, enemies, asteroids, buffs,
and bullets being subclasses of the Entity class.

### B. Runtime Analysis

#### Did you analyze the efficiency of a substantial component of your project?

**Yes or No?**

*If yes, please answer the following questions.*

**1. Specify the scope of the component you are analyzing
(e.g. function name, starting and ending lines of specific .java file).**




**2. What is the estimated runtime (e.g. big-O complexity) of this component?
Provide justification for this runtime (i.e. explain in your own words why
you expect this component to have this runtime performance).**




**3. Provide experimental evidence in the form of timed analysis supporting this
runtime estimate. (Hint: you may find it helpful to use command-line
arguments/flags to run just the specified component being analyzed).**




### C. Packaging project as an executable .jar file

#### Did you package your project as an executable .jar file?

**Yes or No?**
yes

*If yes, please answer the following question.*

#### Describe in detail how to execute your .jar application (e.g. what execution command to use on the terminal).
*Include a few example execution commands and the expected results of running
your program. For non-textual outputs (e.g. graphical or auditory), feel free
to describe in words what the output should be or reference output files
(e.g. images, audio files) of the expected output.*

The following command works perfectly for running the program:
java-introcs -jar project.jar

This will open the stddraw window and start the game.

I've noticed on Windows that double clicking on the .jar file in a file explorer
and running the .jar with an OpenJDK app works perfectly too.
