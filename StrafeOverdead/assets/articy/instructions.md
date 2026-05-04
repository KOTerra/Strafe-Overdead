# Articy Draft 3: Recreating the Strafe-Overdead Logic Project

This guide provides step-by-step instructions to recreate the `Strafe-Overdead-test` project natively within the **Articy Draft 3** UI. By following these steps, you will ensure your project structure, global variables, and flow logic perfectly match the production JSON export.

---

## 1. Project & Template Setup

In Articy, "Entities" (NPCs, Items, Players) should be structured using **Templates** to ensure data consistency across the project.

1.  Open your project and navigate to the **Template Design** tab (press **F9**).
2.  Click **Create Template** and select **Entity** as the base type.
3.  In the **Properties** inspector, set the **Technical Name** to `Tpl_DynamicEntity`. 
    *   *Note: This matches the class-specific registry used in the game engine.*
4.  Switch to the **Entities** tab in the Navigator.
5.  Right-click the **Entities** folder and select **New > Entity**.
    *   Create an entity named **Player**. 
    *   Create an entity named **Goblin**.
6.  Select both entities, go to the **Template** property in the inspector, and assign the `Tpl_DynamicEntity` template you just created.

---

## 2. Global Variables Setup

Global Variables (GV) are namespaced in Articy to prevent naming collisions and to organize logical state.

1.  Navigate to the **Global Variables** tab (press **F7**).
2.  Right-click in the main area and select **New Variable Set**.
3.  Name the set `NPCState`.
4.  Within the `NPCState` set, create the following **Boolean** variables:
    *   `NPC_Goblin_Visible` (Default: **True**)
    *   `Camera_IsRotated` (Default: **False**)
    *   `First_Goblin_Spawned` (Default: **False**)

---

## 3. Building the Flow (Triggers & Dialogue)

The Flow view represents the "World Logic" or high-level triggers. Articy will automatically detect custom methods like `spawnNPC` and `cameraRotate` when you type them into expressions.

1.  Navigate to the **Flow** view (press **F2**).
2.  **Create Spawn Trigger:**
    *   Right-click and create a **Flow Fragment**. Name it `Zone_Spawn_NPC`.
    *   Select the node and locate the **Output Pin**.
    *   In the **Instruction** box of the **Output Pin**, enter:
        ```csharp
        print("Spawning Initial Goblin"); 
        spawnNPC("NPC_Goblin", 22.0, 10.0); 
        NPCState.First_Goblin_Spawned = true;
        ```
3.  **Create Camera Trigger:**
    *   Create a second **Flow Fragment**. Name it `Zone_Trigger_Camera`.
    *   Locate the **Input Pin**. In the **Condition** box, enter:
        ```csharp
        NPCState.First_Goblin_Spawned == true
        ```
    *   In the **Output Pin** of this same node, enter the **Instruction**:
        ```csharp
        print("Entering Camera Zone"); 
        cameraRotate(45.0, 1.0);
        ```
4.  **Connect to Dialogue:**
    *   Create a **Dialogue** node named `Dialogue_Test`.
    *   Draw a connection from the `Zone_Trigger_Camera` **Output Pin** to the `Dialogue_Test` **Input Pin**.

---

## 4. Building the Inner Dialogue

1.  Double-click the `Dialogue_Test` node to step **inside** it.
2.  **Initial Greeting:**
    *   Create a **Dialogue Fragment**. 
    *   Set the **Speaker** property to the **Player** entity.
    *   Set the **Menu Text** to `"Greeting"` and the **Stage Text** to `"NPC is here! What shall we do next?"`.
3.  **Branching Choices:**
    Create four **Dialogue Fragments** and connect the Greeting to each of them. Add the following logic to their **Input Pins**:

    *   **Choice 1:** 
        *   **Menu:** `"Spawn a friend for the Goblin"`
        *   **Stage:** `"Another one joins the party!"`
        *   **Instruction (Input Pin):** `spawnNPC("NPC_Goblin", 24.0, 10.0);`
    *   **Choice 2:** 
        *   **Menu:** `"Extreme Camera Tilt."`
        *   **Stage:** `"Hold on tight!"`
        *   **Instruction (Input Pin):** `cameraRotate(-90.0, 1.0);`
    *   **Choice 3:** 
        *   **Menu:** `"Just reset camera and finish."`
        *   **Stage:** `"Restoring camera tilt."`
        *   **Instruction (Input Pin):** `cameraRotate(0.0, 1.0);`
    *   **Choice 4:** 
        *   **Menu:** `"Restore Camera."`
        *   **Stage:** `"Back to normal."`
        *   **Instruction (Input Pin):** `cameraRotate(0.0, 1.0);`

4.  **Connecting Flow:**
    *   Draw a connection from the **Output Pin** of **Choice 2** to the **Input Pin** of **Choice 4**.

---

## 5. Exporting

To ensure the JSON matches the game engine's expected payload:

1.  Go to the **Export** tab (press **CTRL+E**).
2.  Select the **Generic JSON Export**.
3.  Click **Export Options** and ensure:
    *   **Export complete project** is selected.
    *   **Include Expresso Scripts** is enabled.
4.  Click **Export** and choose your destination folder.
