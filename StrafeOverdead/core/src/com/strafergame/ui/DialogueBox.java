package com.strafergame.ui;

import com.articy.runtime.model.Branch;
import com.articy.runtime.model.DialogueFragment;
import com.articy.runtime.core.ArticyRuntime;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.strafergame.Strafer;

import java.util.List;

public class DialogueBox extends Table {
    private final TypingLabel label;
    private final Table choicesTable;
    private final VisTextButton nextButton;

    private List<Branch> currentBranches;
    private boolean isEndOfDialogue;

    public DialogueBox() {
        label = new TypingLabel("", VisUI.getSkin());
        label.setWrap(true);
        label.setAlignment(Align.left);
        choicesTable = new Table();

        nextButton = new VisTextButton(">");
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                advanceDialogue();
            }
        });

        this.add(label).expandX().fillX().pad(20).row();
        this.add(choicesTable).expandX().fillX().pad(10).row();
        this.add(nextButton).right().bottom().pad(10);

        this.setBackground(VisUI.getSkin().getDrawable("window"));

        Strafer.uiManager.addFocusableActor(nextButton);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    private void advanceDialogue() {
        if (currentBranches != null && !currentBranches.isEmpty()) {
            if (currentBranches.size() == 1) {
                ArticyRuntime.getFlowPlayer().advance(currentBranches.get(0));
            }
            // If branches > 1, do nothing (force choice button click)
        } else if (isEndOfDialogue) {
            this.setVisible(false);
            choicesTable.clearChildren();
            currentBranches = null;
            Strafer.uiManager.setFocusedActor(null);
            // potentially resume game movement
        }
    }

    public void updateVisibility() {
        if (currentBranches == null || currentBranches.size() <= 1) {
            nextButton.setVisible(true);
            Strafer.uiManager.setFocusedActor(nextButton);
        } else {
            nextButton.setVisible(false);
        }
    }

    public void showText(String text) {
        this.setVisible(true);
        String localizedText = text;
        if (text.startsWith("DFr_")) {
            localizedText = ArticyRuntime.getLocalization().localize(text);
            if (localizedText.equals(text)) {
                System.err.println("DialogueBox: Localization key not found: " + text);
            }
        }
        label.setText(localizedText);
        label.restart();
        choicesTable.clearChildren();
        Strafer.uiManager.setFocusedActor(nextButton);
    }

    public void showChoices(List<Branch> branches) {
        this.setVisible(true);
        choicesTable.clearChildren();

        if (branches.size() > 1) {
            for (Branch branch : branches) {
                String text = "";
                if (branch.getTargetNode() instanceof DialogueFragment df) {
                    text = df.getMenuText() != null && !df.getMenuText().isEmpty() ? df.getMenuText() : df.getText();
                }

                if (text.isEmpty()) {
                    text = "Continue";
                }

                String localizedText = text;
                if (text.startsWith("DFr_")) {
                    localizedText = ArticyRuntime.getLocalization().localize(text);
                }
                VisTextButton button = new VisTextButton(localizedText);
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ArticyRuntime.getFlowPlayer().advance(branch);
                        choicesTable.clearChildren();
                    }
                });
                choicesTable.add(button).fillX().pad(5).row();

                Strafer.uiManager.addFocusableActor(button);
            }
            if (!branches.isEmpty()) {
                Strafer.uiManager.setFocusedActor(choicesTable.getChildren().first());
            }
        }
    }

    public void setCurrentBranches(List<Branch> currentBranches) {
        this.currentBranches = currentBranches;
    }

    public void setEndOfDialogue(boolean isEndOfDialogue) {
        this.isEndOfDialogue = isEndOfDialogue;
    }
}
