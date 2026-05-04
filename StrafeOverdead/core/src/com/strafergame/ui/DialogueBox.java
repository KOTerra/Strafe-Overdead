package com.strafergame.ui;

import com.articy.runtime.model.Branch;
import com.articy.runtime.model.DialogueFragment;
import com.articy.runtime.core.ArticyRuntime;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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

    public DialogueBox() {
        label = new TypingLabel("", VisUI.getSkin());
        label.setWrap(true);
        label.setAlignment(Align.left);
        choicesTable = new Table();


        this.add(label).expandX().fillX().pad(20).row();
        this.add(choicesTable).expandX().fillX().pad(10);

        this.setBackground(VisUI.getSkin().getDrawable("window")); 
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void showText(String text) {
        this.setVisible(true);
        label.setText(text);
        label.restart();
        choicesTable.clearChildren();
    }

    public void showChoices(List<Branch> branches) {
        this.setVisible(true);
        choicesTable.clearChildren();
        for (Branch branch : branches) {
            String text = "";
            if (branch.getTargetNode() instanceof DialogueFragment df) {
                text = df.getMenuText() != null && !df.getMenuText().isEmpty() ? df.getMenuText() : df.getText();
            }
            
            if (text.isEmpty()) {
                text = "Continue";
            }

            VisTextButton button = new VisTextButton(text);
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
