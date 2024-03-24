package com.strafergame.graphics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.PositionComponent;

/**
 * an orthographic camera which follows an assigned entity, mainly the player
 *
 * @author mihai_stoica
 */
public class WorldCamera extends OrthographicCamera {

    private enum FocusType {
        /**
         * camera remains in place
         */
        NONE,
        /**
         * focused on an entity
         */
        ENTITY_FOCUS,
        /**
         * focused in a group of entities
         */
        ENTITY_GROUP_FOCUS,
        /**
         * focused on a given vector position
         */
        LOCATION_FOCUS;
    }

    /**
     * the entity which the camera follows
     */
    private Array<Entity> focusEntities;

    /**
     * the location which the camera stays fixed to
     */
    private Vector3 locationFocus;

    /**
     * the current type of focus
     */
    FocusType focusType;

    /**
     * the position to which the camera aims to snap
     */
    private Vector3 cameraSnapPosition = new Vector3();

    /**
     * the alpha used in the interpolation process
     */
    private float alpha = 0.03f;

    /**
     * when groups are weighted
     */
    private boolean weighted;

    /*
     * type of interpolation
     */
    private Interpolation interpolation = Interpolation.linear;

    /**
     * constructor
     *
     * @param width
     * @param height
     */
    public WorldCamera(float width, float height) {
        super(width, height);
        this.focusType = FocusType.NONE;
        this.focusEntities = new Array<>();
        super.zoom = 1.1f;
    }

    /**
     * sets the focus to entity
     *
     * @param entity
     */
    public void setFocusOn(Entity entity) {
        this.focusType = FocusType.ENTITY_FOCUS;
        this.focusEntities.clear();
        this.focusEntities.add(entity);
    }

    /**
     * sets the focus to entity with given transition parameters
     *
     * @param entity
     * @param alpha
     * @param interpolation
     */
    public void setFocusOn(Entity entity, float alpha, Interpolation interpolation) {
        this.focusType = FocusType.ENTITY_FOCUS;
        this.focusEntities.clear();
        this.focusEntities.add(entity);
        this.alpha = alpha;
        this.interpolation = interpolation;
    }

    public void addToFocus(Entity entity) {
        if (!focusEntities.contains(entity, true)) {
            this.focusEntities.add(entity);
        }
        focusType = FocusType.ENTITY_GROUP_FOCUS;
    }

    public void setFocusOnLocation(Vector3 location) {
        this.focusType = FocusType.LOCATION_FOCUS;
        this.locationFocus = location;
    }

    public void setFocusBetween(boolean weighted, Entity... entities) {
        this.focusType = FocusType.ENTITY_GROUP_FOCUS;
        this.weighted = weighted;
        focusEntities.clear();
        focusEntities.addAll(entities);
    }

    public void setFocusBetween(boolean weighted, Array<Entity> entities) {
        this.focusType = FocusType.ENTITY_GROUP_FOCUS;
        this.weighted = weighted;
        focusEntities.clear();
        focusEntities.addAll(entities);
    }

    private void averageCenter(Vector3 center) {
        if (focusEntities.size == 0) {
            return;
        }
        float centerX = 0f;
        float centerY = 0f;
        float totalWeight = 0;
        float weightedSumX =0;
        float weightedSumY = 0;

        for (Entity entity : focusEntities) {
            PositionComponent posCmp = ComponentMappers.position().get(entity);
            Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
            if (posCmp != null && b2dCmp != null) {
                float weight = weighted ? b2dCmp.footprint.getShape().getRadius() : 1;
                weightedSumX += posCmp.renderPos.x * weight;
                weightedSumY += posCmp.renderPos.y * weight;
                totalWeight += weight;
            }
        }
        if (totalWeight != 0) {
            centerX = weightedSumX / totalWeight;
            centerY = weightedSumY / totalWeight;
        }

        center.set(centerX, centerY, 0);
    }

    /**
     * stops focusing on an entity
     */
    public void removeFocus() {
        focusType = FocusType.NONE;
        focusEntities.clear();

    }

    /**
     * follows the focused point if there is one
     */
    @Override
    public void update() {
        super.update();
        if (focusType != null) {
            switch (focusType) {

                case ENTITY_FOCUS: {
                    PositionComponent posCmp = ComponentMappers.position().get(focusEntities.get(0));
                    cameraSnapPosition.set(posCmp.renderPos.x, posCmp.renderPos.y, 0);
                    break;
                }
                case ENTITY_GROUP_FOCUS: {
                    averageCenter(cameraSnapPosition);

                    break;
                }
                case LOCATION_FOCUS: {
                    cameraSnapPosition = locationFocus;
                    break;
                }
                case NONE: {
                    break;
                }
                default:
                    break;
            }

            Strafer.worldCamera.position
                    .set(Math.round(Strafer.worldCamera.position.x / Strafer.SCALE_FACTOR) * Strafer.SCALE_FACTOR,
                            Math.round(Strafer.worldCamera.position.y / Strafer.SCALE_FACTOR) * Strafer.SCALE_FACTOR, 0)
                    .interpolate(cameraSnapPosition, alpha, interpolation);

        }
    }

    public boolean isWeighted() {
        return weighted;
    }

    public void setWeighted(boolean weighted) {
        this.weighted = weighted;
    }

    public Array<Entity> getFocusEntities() {
        return focusEntities;
    }

    public FocusType getFocusType() {
        return focusType;
    }

    public boolean isInFocus() {
        return !focusType.equals(FocusType.NONE);
    }

}
