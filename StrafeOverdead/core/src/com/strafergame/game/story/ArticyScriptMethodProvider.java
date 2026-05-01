package com.strafergame.game.story;

import com.articy.runtime.logic.IScriptMethodProvider;
import com.articy.runtime.core.ArticyRuntime;
import com.articy.runtime.logic.ArticyVariableManager;
import com.badlogic.gdx.Gdx;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Command Registry for Articy Instructions.
 */
public class ArticyScriptMethodProvider implements IScriptMethodProvider {

    private final Map<String, Consumer<Object[]>> commands = new HashMap<>();
    private ArticyVariableManager currentContext;

    public ArticyScriptMethodProvider() {
        registerDefaultCommands();
    }

    private void registerDefaultCommands() {
        commands.put("print", args -> System.out.println("Articy: " + (args.length > 0 ? args[0] : "")));
        
        // Example game commands
        commands.put("cameraPan", args -> {
            // args: [x, y, duration]
            Gdx.app.postRunnable(() -> {
                System.out.println("Articy Command: Camera Pan to " + args[0] + ", " + args[1]);
            });
        });

        commands.put("cameraRotate", args -> {
            // args: [degrees, duration]
            if (args.length > 0) {
                float degrees = ((Number) args[0]).floatValue();
                Gdx.app.postRunnable(() -> {
                    System.out.println("Articy Command: Setting target camera rotation to " + degrees);
                    if (com.strafergame.Strafer.worldCamera != null) {
                        com.strafergame.Strafer.worldCamera.setTargetRotation(degrees);
                    }
                });
            }
        });
    }

    @Override
    public void setVariableContext(ArticyVariableManager vars) {
        this.currentContext = vars;
    }

    @Override
    public boolean isShadowState() {
        return currentContext != null && currentContext.isInShadowState();
    }

    public void registerCommand(String name, Consumer<Object[]> callback) {
        commands.put(name, callback);
    }

    @Override
    public Object invokeCustomMethod(String name, Object... args) {
        if (isShadowState()) {
            // Skip side-effects during shadow state forecasting
            return null;
        }

        Consumer<Object[]> command = commands.get(name);
        if (command != null) {
            command.accept(args);
        } else {
            Gdx.app.error("Articy", "Unknown script method: " + name);
        }
        return null;
    }
}
