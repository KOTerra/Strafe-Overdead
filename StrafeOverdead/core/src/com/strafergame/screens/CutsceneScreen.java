package com.strafergame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.strafergame.game.GameStateManager;
import com.strafergame.game.GameStateType;

import java.io.FileNotFoundException;

/**
 * A dedicated screen for playing cutscene videos using gdx-video.
 * <p>
 * Skip behavior (standard game pattern):
 * <ol>
 *   <li>Press any key → a "Press ENTER to skip" prompt appears</li>
 *   <li>Press ENTER while the prompt is visible → cutscene is skipped</li>
 *   <li>If no key is pressed for a few seconds, the prompt fades away</li>
 * </ol>
 * <p>
 * Usage: {@code CutsceneScreen.playCutscene("cutscenes/yee.webm");}
 */
public class CutsceneScreen implements Screen {

    private static CutsceneScreen instance;

    private VideoPlayer videoPlayer;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private String pendingCutscenePath;
    private boolean playing;
    private boolean completed;

    /**
     * Saved input processor to restore when the cutscene ends.
     */
    private com.badlogic.gdx.InputProcessor previousInputProcessor;

    /**
     * The game state to return to when the cutscene ends.
     */
    private GameStateType returnState = GameStateType.PLAY;

    // --- Skip prompt state ---
    /** Whether the "Press ENTER to skip" prompt is currently showing */
    private boolean skipPromptVisible;
    /** Whether the user confirmed the skip by pressing ENTER */
    private boolean skipConfirmed;
    /** Timer tracking how long the prompt has been visible (seconds) */
    private float skipPromptTimer;
    /** How long the prompt stays visible before fading (seconds) */
    private static final float SKIP_PROMPT_DURATION = 3f;

    private final InputAdapter skipInputProcessor = new InputAdapter() {
        @Override
        public boolean keyDown(int keycode) {
            if (!playing) return false;

            if (skipPromptVisible) {
                // Prompt is showing — only ENTER confirms the skip
                if (keycode == Input.Keys.ENTER) {
                    skipConfirmed = true;
                }
                // Any other key just resets the timer to keep the prompt visible
                skipPromptTimer = 0f;
                return true;
            } else {
                // First key press — show the skip prompt
                skipPromptVisible = true;
                skipPromptTimer = 0f;
                return true;
            }
        }

        // Consume mouse/touch events so they don't bleed through, but don't trigger skip
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return playing;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return playing;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false; // don't consume mouse move
        }
    };

    public CutsceneScreen() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        glyphLayout = new GlyphLayout();
    }

    /**
     * Starts playback of the given cutscene file and transitions to the CUTSCENE game state.
     *
     * @param cutscenePath internal path to the .webm file (e.g. "cutscenes/yee.webm")
     */
    public static void playCutscene(String cutscenePath) {
        playCutscene(cutscenePath, GameStateType.PLAY);
    }

    /**
     * Starts playback of the given cutscene file, returning to the specified state when done.
     *
     * @param cutscenePath internal path to the .webm file
     * @param returnTo     the game state to transition to after the cutscene
     */
    public static void playCutscene(String cutscenePath, GameStateType returnTo) {
        CutsceneScreen screen = getInstance();
        screen.pendingCutscenePath = cutscenePath;
        screen.returnState = returnTo;
        GameStateManager.changeState(GameStateType.CUTSCENE);
    }

    @Override
    public void show() {
        playing = false;
        completed = false;
        skipPromptVisible = false;
        skipConfirmed = false;
        skipPromptTimer = 0f;

        // Save current input processor and replace with skip handler
        previousInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(skipInputProcessor);

        // Create and start the video player
        try {
            if (videoPlayer != null) {
                videoPlayer.dispose();
            }
            videoPlayer = VideoPlayerCreator.createVideoPlayer();

            videoPlayer.setOnCompletionListener(new VideoPlayer.CompletionListener() {
                @Override
                public void onCompletionListener(FileHandle file) {
                    // Don't end here — this fires inside update(), so the player
                    // would be disposed while render() is still using it.
                    completed = true;
                }
            });

            if (pendingCutscenePath != null) {
                FileHandle file = Gdx.files.internal(pendingCutscenePath);
                videoPlayer.play(file);
                playing = true;
            }
        } catch (FileNotFoundException e) {
            Gdx.app.error("CutsceneScreen", "Cutscene file not found: " + pendingCutscenePath, e);
            endCutscene();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle skip confirmation or natural completion
        if (skipConfirmed || completed) {
            endCutscene();
            return;
        }

        // Update skip prompt timer
        if (skipPromptVisible) {
            skipPromptTimer += delta;
            if (skipPromptTimer >= SKIP_PROMPT_DURATION) {
                skipPromptVisible = false;
                skipPromptTimer = 0f;
            }
        }

        if (videoPlayer != null && playing) {
            videoPlayer.update();
            Texture frame = videoPlayer.getTexture();

            if (frame != null) {
                float screenW = Gdx.graphics.getWidth();
                float screenH = Gdx.graphics.getHeight();

                // Calculate aspect-fit dimensions
                float videoW = frame.getWidth();
                float videoH = frame.getHeight();
                float scale = Math.min(screenW / videoW, screenH / videoH);
                float drawW = videoW * scale;
                float drawH = videoH * scale;
                float drawX = (screenW - drawW) / 2f;
                float drawY = (screenH - drawH) / 2f;

                batch.getProjectionMatrix().setToOrtho2D(0, 0, screenW, screenH);
                batch.begin();
                batch.draw(frame, drawX, drawY, drawW, drawH);

                // Draw skip prompt
                if (skipPromptVisible) {
                    // Fade out during the last second
                    float alpha = 1f;
                    float fadeStart = SKIP_PROMPT_DURATION - 1f;
                    if (skipPromptTimer > fadeStart) {
                        alpha = 1f - (skipPromptTimer - fadeStart);
                    }

                    String skipText = "Press ENTER to skip";
                    font.setColor(1f, 1f, 1f, alpha);
                    glyphLayout.setText(font, skipText);
                    float textX = screenW - glyphLayout.width - 20f;
                    float textY = 30f + glyphLayout.height;
                    font.draw(batch, skipText, textX, textY);
                }

                batch.end();
            }
        }
    }

    /**
     * Ends the cutscene and transitions back to the return state.
     */
    private void endCutscene() {
        playing = false;
        if (videoPlayer != null) {
            videoPlayer.pause();
        }
        GameStateManager.changeState(returnState);
    }

    @Override
    public void resize(int width, int height) {
        // Rendering adapts dynamically in render()
    }

    @Override
    public void pause() {
        if (videoPlayer != null && playing) {
            videoPlayer.pause();
        }
    }

    @Override
    public void resume() {
        if (videoPlayer != null && playing) {
            videoPlayer.resume();
        }
    }

    @Override
    public void hide() {
        playing = false;
        if (videoPlayer != null) {
            videoPlayer.stop();
            videoPlayer.dispose();
            videoPlayer = null;
        }
        // Restore the original input processor
        if (previousInputProcessor != null) {
            Gdx.input.setInputProcessor(previousInputProcessor);
            previousInputProcessor = null;
        }
    }

    @Override
    public void dispose() {
        if (videoPlayer != null) {
            videoPlayer.dispose();
            videoPlayer = null;
        }
        if (batch != null) {
            batch.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }

    public static CutsceneScreen getInstance() {
        if (instance == null) {
            instance = new CutsceneScreen();
        }
        return instance;
    }
}
