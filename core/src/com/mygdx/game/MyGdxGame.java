package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
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
	private PhysX physX;
	private ShapeRenderer renderer;
	private Music music;

	private int[] foreGround, backGround;
	private int score;
	TextureRegion region;
	private int x, y;
	private MyCharacter hero;

	private World world;
	private Box2DDebugRenderer debugRender;
	private Body heroBody;


	@Override
	public void create () {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		physX = new PhysX();

		hero = new MyCharacter();
		fon = new Texture("space fon.png");
		map = new TmxMapLoader().load("maps/map2.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map);
		MapObject moHero = map.getLayers().get("Камера").getObjects().get("hero");
		physX.addObject(moHero, hero.getRect(camera));

		if (map.getLayers().get("land") != null) {
			MapObjects mo = map.getLayers().get("land").getObjects();
			physX.addObjects(mo);
		}
		if (map.getLayers().get("bombs") != null) {
			MapObjects mo = map.getLayers().get("bombs").getObjects();
			physX.addObjects(mo);
		}

		foreGround = new int[1];
		foreGround[0] = map.getLayers().getIndex("Объекты");
		backGround = new int[2];
		backGround[0] = map.getLayers().getIndex("Бэк");
		backGround[1] = map.getLayers().getIndex("Земля");

		batch = new SpriteBatch();
		renderer = new ShapeRenderer();

		label = new Label(50);

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

		camera.zoom = 0.25f;

		music = Gdx.audio.newMusic(Gdx.files.internal("Soundtrack.mp3"));
		music.setLooping(true);
		music.setVolume(0.05f);
		music.play();
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);


		hero.setWalk(false);
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			physX.setHeroForce(new Vector2(-1500, 0));
			camera.position.x--;
			hero.setDir(true);
			hero.setWalk(true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			physX.setHeroForce(new Vector2(1500, 0));
			camera.position.x--;
			hero.setDir(false);
			hero.setWalk(true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP) && physX.cl.isOnGround()){
			physX.setHeroForce(new Vector2(0, 2500));
			camera.position.y++;
			hero.setJump(true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			physX.setHeroForce(new Vector2(0, -2500));
			camera.position.y--;
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {camera.zoom += 0.05f;}
		if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {camera.zoom -= 0.05f;}

		camera.position.x = physX.getHero().getPosition().x;
		camera.position.y = physX.getHero().getPosition().y;
		camera.update();

		batch.begin();
		batch.draw(fon, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();

		mapRenderer.setView(camera);
		mapRenderer.render(backGround);
		mapRenderer.render(foreGround);

		batch.begin();
		batch.draw(hero.getFrame(), hero.getRect(camera).x, hero.getRect(camera).y, hero.getRect(camera).getWidth(), hero.getRect(camera).getHeight());
		label.draw(batch, "МОНЕТОК СОБРАНО: " + String.valueOf(score), 0, 0);
		for (int i = 0; i < coinList.size(); i++) {
			int state;
			state = coinList.get(i).draw(batch, camera);
			if (coinList.get(i).isOverlaps(hero.getRect(camera), camera)) {
				coinList.get(i).setState();
				if (state == 1) {
					coinList.remove(i);
					score++;
				}
			}
		}
		batch.end();

		physX.step();

		physX.debugDraw(camera);

		renderer.begin(ShapeRenderer.ShapeType.Line);
		for (Coin coin: coinList) {
			coin.shapeDraw(renderer, camera);
		}
		hero.shapeDraw(renderer, camera);
		renderer.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		music.stop();
		music.dispose();
		coinList.get(0).dispose();
		physX.dispose();
	}
}
