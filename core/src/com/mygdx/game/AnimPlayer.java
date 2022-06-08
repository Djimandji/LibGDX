package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimPlayer {

    Texture texture;
    Animation<TextureRegion> animation;
    private float time;
    private boolean loop;

    public AnimPlayer(String name, int width, int height, float fps, boolean loop) {
        this.loop = loop;
        texture = new Texture(name);
        TextureRegion region = new TextureRegion(texture);
        TextureRegion[][] regions = region.split(region.getRegionWidth()/width, region.getRegionHeight()/height);
        TextureRegion[] regions1 = new TextureRegion[width * height];

        int cnt = 0;
        for (int i = 0; i < regions.length; i++) {
            for (int j = 0; j < regions[i].length; j++) {
                regions1[cnt++] = regions[i][j];
            }
        }

        animation = new Animation<TextureRegion>(1.0f/fps, regions1);
        animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void step(float time){
        this.time += time;
    }

    public TextureRegion getTexture() {
        return animation.getKeyFrame(time, loop);
    }

    public void dispose() {
        texture.dispose();
    }
}
