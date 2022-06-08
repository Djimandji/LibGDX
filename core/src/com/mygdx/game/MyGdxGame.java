package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	AnimPlayer animPlayer;
	Label label;

	TextureRegion region;
	private int x, y;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		animPlayer = new AnimPlayer("runRight.png", 8, 1, 16f, true);
		label = new Label(50);
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) x--;
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) x++;
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) y++;
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) y--;



		animPlayer.step(Gdx.graphics.getDeltaTime());
		batch.begin();
		batch.draw(animPlayer.getTexture(), x, y);
		label.draw(batch, "ГОТЭМ СИТИ", 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		animPlayer.dispose();
	}
}
