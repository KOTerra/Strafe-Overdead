package com.strafergame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.github.tommyettinger.textra.Styles;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;
import com.kotcrab.vis.ui.widget.VisTextField.VisTextFieldStyle;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane.TabbedPaneStyle;

public class SkinMapper {

    public static void map(Skin skin) {
        for (BitmapFont font : skin.getAll(BitmapFont.class).values()) {
            font.getData().setScale(2f);
            font.getData().markupEnabled = true;
        }

        TextButtonStyle tbs = skin.get(TextButtonStyle.class);
        VisTextButtonStyle vtbs = new VisTextButtonStyle();
        vtbs.font = tbs.font;
        vtbs.fontColor = tbs.fontColor;
        vtbs.up = tbs.up;
        vtbs.down = tbs.down;
        vtbs.over = tbs.over;
        vtbs.checked = tbs.checked;
        skin.add("default", vtbs, VisTextButtonStyle.class);

        LabelStyle ls = skin.get(LabelStyle.class);
        skin.add("default", ls, LabelStyle.class);

        Styles.LabelStyle tls = new Styles.LabelStyle(ls.font, ls.fontColor);
        skin.add("default", tls, Styles.LabelStyle.class);

        TextFieldStyle tfs = skin.get(TextFieldStyle.class);
        VisTextFieldStyle vtfs = new VisTextFieldStyle();
        vtfs.font = tfs.font;
        vtfs.fontColor = tfs.fontColor;
        vtfs.background = tfs.background;
        vtfs.cursor = tfs.cursor;
        vtfs.selection = tfs.selection;
        skin.add("default", vtfs, VisTextFieldStyle.class);

        Sizes sizes = new Sizes();
        sizes.scaleFactor = 5f;
        skin.add("default", sizes);

        TabbedPaneStyle tps = new TabbedPaneStyle();
        tps.separatorBar = skin.newDrawable("white", Color.BLACK);
        tps.buttonStyle = vtbs;
        skin.add("default", tps, TabbedPaneStyle.class);

        VisImageButtonStyle closeTabStyle = new VisImageButtonStyle();
        skin.add("close-active-tab", closeTabStyle, VisImageButtonStyle.class);
        skin.add("close", closeTabStyle, VisImageButtonStyle.class);
    }
}
