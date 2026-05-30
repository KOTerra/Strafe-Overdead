# Articy Draft 3: Recreating the Strafe-Overdead Logic Project

This guide provides step-by-step instructions to recreate the `Strafe-Overdead-test` project natively within the **Articy Draft 3** UI. By following these steps, you will ensure your project structure, global variables, and flow logic perfectly match the production JSON export.

---

## 1. Project & Template Setup

In Articy, "Entities" (NPCs, Items, Players) should be structured using **Templates** to ensure data consistency across the project.

1.  Open your project and navigate to the **Template Design** tab (press `F9`).
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

1.  Navigate to the **Global Variables** tab (press `F7`).
2.  Right-click in the main area and select **New Variable Set**.
3.  Name the set `NPCState`.
4.  Within the `NPCState` set, create the following **Boolean** variables:
    *   `NPC_Goblin_Visible` (Default: `True`)
    *   `Camera_IsRotated` (Default: `False`)
    *   `First_Goblin_Spawned` (Default: `False`)

---

## 3. Custom Script Methods Setup

To use custom game logic (like spawning NPCs) inside Articy without getting syntax errors, you must define the method signatures in the project settings.

1.  Click the **Articy Button** (top-left) and select **Project Settings**.
2.  Go to the **Expresso Scripts** section and find the **Script Methods** tab.
3.  Add the following method signatures exactly:
    *   `void print(string text)`
    *   `void spawnNPC(string id, float x, float y)`
    *   `void cameraRotate(float degrees, float duration)`
4.  Click **Apply** or **OK**. This allows Articy’s compiler to validate your logic.

---

## 4. Building the Flow (Triggers & Dialogue)

The Flow view represents the "World Logic" or high-level triggers.

1.  Navigate to the **Flow** view (press `F2`).
2.  **Create Spawn Trigger:**
    *   Right-click and create a **Flow Fragment**. Name it `Zone_Spawn_NPC`.
    *   Select the node and locate the **Output Pin**.
    *   In the **Instruction** box of the Output Pin, enter:
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
4.  **Create Dialogue Node:**
    *   Create a **Dialogue** node named `Dialogue_Test`.
    *   Draw a connection from the `Zone_Trigger_Camera` **Output Pin** to the `Dialogue_Test` **Input Pin**.

---

## 5. Building the Inner Dialogue

1.  Double-click the `Dialogue_Test` node to step **inside** it.
2.  **Initial Greeting:**
    *   Create a **Dialogue Fragment**. 
    *   Set the **Speaker** property to the **Player** entity created in Step 1.
    *   Set the **Text** to: `"I need to clear this area."`
3.  **Branching Choices:**
    *   Create two new **Dialogue Fragments** connected to the Greeting.
    *   **Choice A:**
        *   **Menu Text:** `"Rotate Camera Left"`
        *   **Stage Text:** `"Rotating camera to the left."`
        *   **Instruction (Output Pin):** `cameraRotate(-90.0, 1.0);`
    *   **Choice B:**
        *   **Menu Text:** `"Reset & Spawn"`
        *   **Stage Text:** `"Resetting camera and calling reinforcements."`
        *   **Instruction (Output Pin):** 
            ```csharp
            cameraRotate(0.0, 1.0); 
            spawnNPC("NPC_Goblin_Elite", 10.0, 5.0);
            ```

---

## 6. Exporting

To ensure the JSON matches the game engine's expected payload:

1.  Go to the **Export** tab (press `CTRL+E`).
2.  Select the **Generic JSON Export**.
3.  Click **Export Options** (or manage Rulesets) and ensure:
    *   **Export complete project** is selected.
    *   **Include Expresso Scripts** is checked.
    *   **Transformation** is set to "None" or "Default" to keep Technical Names intact.
4.  Choose your export directory (e.g., `assets/articy/export/`) and click **Export**.
