package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
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

	private Label label;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	private OrthographicCamera camera;
	private List<Coin> coinList;
	private Texture fon;
//	private ShapeRenderer renderer;
	private int[] foreGround, backGround;
	private int score;
	TextureRegion region;
	private int x, y;
	private MyCharacter chip;

	@Override
	public void create () {
//		renderer = new ShapeRenderer();
		chip = new MyCharacter();
		fon = new Texture("space fon.png");
		map = new TmxMapLoader().load("maps/map1.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map);
		batch = new SpriteBatch();
		foreGround = new int[1];
		foreGround[0] = map.getLayers().getIndex("Объекты");
		backGround = new int[1];
		backGround[0] = map.getLayers().getIndex("Бэк");


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

		chip.setWalk(false);
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			camera.position.x--;
			chip.setDir(true);
			chip.setWalk(true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			camera.position.x++;
			chip.setDir(false);
			chip.setWalk(true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)){
			camera.position.y++;
			chip.setJump(true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.position.y--;
		camera.update();

		batch.begin();
		batch.draw(fon, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();

		mapRenderer.setView(camera);
		mapRenderer.render(backGround);
		mapRenderer.render(foreGround);

		batch.begin();
		batch.draw(chip.getFrame(), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		label.draw(batch, "МОНЕТОК СОБРАНО: " + String.valueOf(score), 0, 0);
		for (int i = 0; i < coinList.size(); i++) {
			coinList.get(i).draw(batch, camera);
			if (coinList.get(i).isOverlaps(chip.getRect(), camera)) {
				coinList.remove(i);
				score++;
			}
		}
		batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		coinList.get(0).dispose();
	}
}
