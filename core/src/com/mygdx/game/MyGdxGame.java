package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class MyGdxGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private AnimPlayer animPlayer;
	private Label label;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	private OrthographicCamera camera;
	private List<Coin> coinList;


	TextureRegion region;
	private int x, y;
	
	@Override
	public void create () {
		map = new TmxMapLoader().load("maps/map1.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map);

		batch = new SpriteBatch();
		animPlayer = new AnimPlayer("runRight.png", 8, 1, 6f, Animation.PlayMode.LOOP);
		label = new Label(50);

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.zoom = 0.25f;
		RectangleMapObject o = (RectangleMapObject) map.getLayers().get("Камера").getObjects().get("camera");
		camera.position.x = o.getRectangle().x;
		camera.position.y = o.getRectangle().y;
		camera.update();

		coinList = new ArrayList<>();
		MapLayer ml = map.getLayers().get("Монетки");
		if (ml != null) {
			MapObjects mo = ml.getObjects();
			if (mo.getCount() > 0) {
				for (int i = 0; i < mo.getCount(); i++) {
					RectangleMapObject tmpMo = (RectangleMapObject) ml.getObjects().get(i);
					Rectangle rect = tmpMo.getRectangle();
					coinList.add(new Coin(new Vector2(rect.x, rect.y)));
				}
			}
		}


	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) camera.position.x--;
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) camera.position.x++;
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) camera.position.y++;
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.position.y--;
		camera.update();

		mapRenderer.setView(camera);
		mapRenderer.render();

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) x--;
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) x++;
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) y++;
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) y--;



		animPlayer.step(Gdx.graphics.getDeltaTime());
		batch.begin();
		batch.draw(animPlayer.getFrame(), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		label.draw(batch, "ГОТЭМ СИТИ", 0, 0);

		for (int i = 0; i < coinList.size(); i++) {
			coinList.get(i).draw(batch, camera);
		}
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		animPlayer.dispose();
	}
}
