# CPSC245-Final-HW


Summary
--------------
This application is a game about throwing knives at a target to score the most 
points possible. Hit locations are marked with a green square; drop, distance 
from the target, and power is taken into account for each throw. The power of
each throw is based on the time the mouse button is held down.


Layout
--------------
Default:
______________________________________________________
|File|Game|Scores|Scale|Mode| 				   		 |
|----------------------------------------------------|
|         	 		Challenge Mode					 |
|											   		 |
|											   		 |
|											   		 |
|				   ________________			   	 	 |
|				   | /----------\ |			  		 |
|				   |/ /--------\ \|			  		 |
|				   || |        | ||			  		 |
|				   || |	 	   | ||		   	 	 	 |
|				   |\ \--____--/ /|		   		 	 |
|				   | \__________/ |		   		 	 |
|				   ----------------		         	 |
|											   		 |
|											   		 |
|											   		 |
|											   		 |
|											   	 	 |
| Last Score: # Running Score: # Remaining Throws: # |
------------------------------------------------------

Practice Mode:
______________________________________________________
|File|Game|Scores|Scale|Mode| 				   		 |
|----------------------------------------------------|
|         	 		Practice Mode					 |
|											   		 |
|											   		 |
|											   		 |
|				   ________________			   	 	 |
|				   | /----------\ |			  		 |
|				   |/ /--------\ \|			  		 |
|				   || |        | ||			  		 |
|				   || |	 	   | ||		   	 	 	 |
|				   |\ \--____--/ /|		   		 	 |
|				   | \__________/ |		   		 	 |
|				   ----------------		         	 |
|											   		 |
|											   		 |
|											   		 |
|											   		 |
|											   	 	 |
|			Last Score: # Running Score: # 			 |
------------------------------------------------------

Error:
______________________________________________________
|File|Game|Scores|Scale|Mode| 				   		 |
|----------------------------------------------------|
|	   			  Warning Message |OK|			 	 |
|											   		 |
|											   		 |
|											   		 |
|				   ________________			   	 	 |
|				   | /----------\ |			  		 |
|				   |/ /--------\ \|			  		 |
|				   || |        | ||			  		 |
|				   || |	 	   | ||		   	 	 	 |
|				   |\ \--____--/ /|		   		 	 |
|				   | \__________/ |		   		 	 |
|				   ----------------		         	 |
|											   		 |
|											   		 |
|											   		 |
|											   		 |
|											   	 	 |
| Last Score: # Running Score: # Remaining Throws: # |
------------------------------------------------------


Menu Breakdown
--------------
File gives users the option to exit. 

Game holds options for starting a new game, saving the current game to be loaded later, and loading a saved game to continue playing.

Scores holds options for adding their score to the high-score list, checking the
top score in the list, exporting the high-score list to share with friends, and
importing a high-score list to compete against.

Scale contains options for target distance, Large (1 meter away) is the closest,
Small (3 meters away) is the smallest, and Medium (2 meters away) is the default.
Scale options do not currently affect scoring.

Mode allows users to switch between competing for scores (challenge mode) and
practicing their throws with unlimited throwing knives (practice mode). Adding 
scores to the score list is disabled in practice mode. Switching from practice
to challenge mode will start a new game with 3 attempts. Switching from challenge
to practice mode will start a new game with infinite attempts.


Game Mechanics
--------------
The Target:
	Scoring works based upon where the knife "lands" on the target, marked by a 
	green rectangle. The location of the green rectangle differs from where you
	clicked, as it takes the power of the throw, the distance to the target, and
	the drop due to gravity into account. The center circle is 10 points and the 
	last ring is worth 1 point. Each ring out from the center is worth one less 
	than the ring it encloses. If you hit any space surrounding the target, other
	than the target, it is worth 0 points.

Throwing The Knife:
	Press and release any mouse button to throw the knife, press and hold before
	releasing for a more powerful throw. On close targets, powerful throws will
	go over the target. On far targets, weak throws will go under the target.
	Trying to throw a knife after the remaining throws counter reaches 0 results
	in an error message displayed in the information area (see Layout). The 
	highest possible score is currently 30 points. 

Challenge Mode:
	In this mode, the user has only 3 attempts (or knives) to score points with,
	once they are finished they can submit their score to the high-score list.
	Scale can be changed during challenge mode without any negative effects.
	Previous challenge-mode games can be loaded in challenge mode, but all games
	will be lost when switching to practice mode. Loading a practice game 
	in challenge mode will load only the last 3 throws from that save. Challenge 
	mode makes use of all three counters at the bottom of the screen. This mode
	can be reset with Game -> New Game.

Practice Mode:
	In this mode, the user has unlimited attempts (or knives) to score points
	with, games can be saved to return to later or exported to challenge mode.
	Practice mode uses only Last Score and Running Score counters to inform the
	user of their game. This is meant mainly to give the user a feel for the drop
	of the knife without a restriction on the number of throws. Scale can be 
	changed at any point in this mode with no negative effects. Users cannot 
	export a practice score to the high-score list in practice mode. This mode
	can also be reset with Game -> New Game.

Saving and Loading Games:
	To save a game regardless of mode, goto Game -> Save Game (.txt), enter a 
	filename (including the .txt extension), and click Save. All marked hits
	on the target will be saved as well as the last score, running score, 
	remaining throws (if applicable), and current mode.
	To load a game regardless of mode, goto Game -> Load Game (.txt), select
	a file (including the .txt extension), and click Open. All marked hits
	will be loaded along with the last score, running score, target scale and 
	remaining throws (if applicable). The loaded mode will change some things 
	based on the mode it is loaded from.
	Loading a practice save into challenge mode will only remember the last 
	three throws from that save and target scale, which the score counters will 
	reflect. A message stating this will also be displayed. 
	Loading a challenge save into practice mode will load the hits, last score, target scale, and running score, which the score counters will reflect.

Saving and Loading Scores:
	To save a high-score list, make sure there is at least one score in the 
	score list, goto Scores -> Save Scores List (.bin), enter a filename
	(including the .bin extension), and click Save. 
	To load a high-score list, goto Scores -> Load Scores List (.bin), select
	a file (including the .bin extension), and click Open. You can then see the
	top score by going to Scale -> Show Top Score.