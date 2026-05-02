  
  Phase 1: Setup Global Variables  
   1. Open the Global Variables tab in Articy.  
   2. Create a new Variable Set named NPCState.  
   3. Add the following variables inside the set:  
       * NPC_Goblin_Visible (Boolean, Default: True)  
       * Camera_IsRotated (Boolean, Default: False)  
       * First_Goblin_Spawned (Boolean, Default: False)  
  
  Phase 2: Create the NPC Entity  
   1. Go to the Entities tab.  
   2. Create a new Entity.  
   3. Critical: In the Property sheet, set the Technical Name to NPC_Goblin.  
       * Note: Our engine uses this name to attach the static goblin sprite and AI.  
  
  Phase 3: Building the World Triggers  
   1. Open the Flow view.  
   2. Trigger 1 (Spawn):  
       * Create a FlowFragment and name it Zone_Spawn_NPC.  
       * Click the Output Pin (the small circle on the right side of the node).  
       * In the Template/Script box for that pin, type:  
          print('Spawning Initial Goblin'); spawnNPC('NPC_Goblin', 22.0, 10.0); NPCState.First_Goblin_Spawned = true;  
   3. Trigger 2 (Camera):  
       * Create another FlowFragment named Zone_Trigger_Camera.  
       * Click the Input Pin (left side). In the Condition box, type:  
          NPCState.First_Goblin_Spawned == true  
       * Click the Output Pin (right side). In the Instruction box, type:  
          print('Entering Camera Zone'); cameraRotate(45.0, 1.0);  
  
  Phase 4: The Branching Dialogue  
   1. Create a Dialogue node named Dialogue_Test.  
   2. Draw a Connection from the Output Pin of Zone_Trigger_Camera to Dialogue_Test.  
   3. Double-click Dialogue_Test to go inside and create the fragments:  
       * Greeting: Set Text to "NPC is here! What shall we do next?".  
       * Option 0: Create a connection from Greeting to a new fragment.  
           * Menu Text: Spawn a friend for the Goblin.  
           * Input Pin Script: spawnNPC('NPC_Goblin', 24.0, 10.0);  
       * Option 1: Create another connection from Greeting.  
           * Menu Text: Extreme Camera Tilt.  
           * Input Pin Script: cameraRotate(-90.0, 1.0);  
           * Sub-Branch: Connect this fragment to a new one named Restore Camera.  
               * Input Pin Script: cameraRotate(0.0, 1.0);  
  Phase 5: Exporting for the Engine  
   1. Click the Articy Button (top left) -> Export.  
   2. Choose Generic JSON Export.  
   3. Ensure "Include Global Variables" and "Include Technical Names" are checked.  
   4. Export the files into StrafeOverdead/assets/articy/export/.  
  
  Designer Pro-Tips for this Integration:  
   * Technical Names: Always fill these out for Entities. The Java ArticyMapperSystem uses them to find the right Ashley entity to hide/show.  
   * Input vs Output Pins: We configured the engine to execute Input Pin scripts the moment a node is reached. This is perfect for immediate feedback (like the camera tilt).  
   * Shadow State: You can safely test complex logic branches; the engine will only "commit" your spawnNPC or print commands when you actually click the choice in the console.  
