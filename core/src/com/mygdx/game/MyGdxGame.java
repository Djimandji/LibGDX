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

		world = new World(new Vector2(0, -9.81f), true);
		debugRender = new Box2DDebugRenderer();

		BodyDef def = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape polygonShape = new PolygonShape();

		def.position.set(new Vector2(88.67f, 150.00f ));
		def.type = BodyDef.BodyType.StaticBody;
		fdef.density = 0.1f;
		fdef.friction = 0.1f;
		fdef.restitution = 0.7f;

		polygonShape.setAsBox(1000, 10);
		fdef.shape = polygonShape;

		world.createBody(def).createFixture(fdef);

		def.type = BodyDef.BodyType.DynamicBody;
		for (int i = 0; i < 10; i++) {
			def.position.set(new Vector2(MathUtils.random(-88f, 88f), 350.00f));

			def.gravityScale = MathUtils.random(0.5f, 5f);
			float size = MathUtils.random(3, 15f);
			polygonShape.setAsBox(size, size);
			fdef.shape = polygonShape;
			world.createBody(def).createFixture(fdef);
		}

		def.position.set(new Vector2(100, 155f));
		def.gravityScale = 1f;
		float size = 5f;
		polygonShape.setAsBox(size, size);
		fdef.shape = polygonShape;
		fdef.density = 0.1f;
		heroBody = world.createBody(def);
		heroBody.createFixture(fdef);


		polygonShape.dispose();

		hero = new MyCharacter();
		fon = new Texture("space fon.png");
		map = new TmxMapLoader().load("maps/map2.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map);
		batch = new SpriteBatch();
		foreGround = new int[1];
		foreGround[0] = map.getLayers().getIndex("Объекты");
		backGround = new int[2];
		backGround[0] = map.getLayers().getIndex("Бэк");
		backGround[1] = map.getLayers().getIndex("Земля");



		label = new Label(50);
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.zoom = 0.25f;
		RectangleMapObject o = (RectangleMapObject) map.getLayers().get("Камера").getObjects().get("камера");
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

		hero.setWalk(false);
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			heroBody.applyForceToCenter(new Vector2(-500.0f, 0.0f), true);
			hero.setDir(true);
			hero.setWalk(true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			heroBody.applyForceToCenter(new Vector2(500.0f, 0.0f), true);
			hero.setDir(false);
			hero.setWalk(true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)){
			heroBody.applyForceToCenter(new Vector2(0.0f, 500.0f), true);
			hero.setJump(true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) heroBody.applyForceToCenter(new Vector2(0.0f, -500.0f), true);;

		camera.position.x = heroBody.getPosition().x;
		camera.position.y = heroBody.getPosition().y;
		camera.update();

		batch.begin();
		batch.draw(fon, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();

		mapRenderer.setView(camera);
		mapRenderer.render(backGround);
		mapRenderer.render(foreGround);

		batch.begin();
		batch.draw(hero.getFrame(), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		label.draw(batch, "МОНЕТОК СОБРАНО: " + String.valueOf(score), 0, 0);
		for (int i = 0; i < coinList.size(); i++) {
			coinList.get(i).draw(batch, camera);
			if (coinList.get(i).isOverlaps(hero.getRect(), camera)) {
				coinList.remove(i);
				score++;
			}
		}
		batch.end();

		world.step(1/60.f, 3, 3);
		debugRender.render(world, camera.combined);

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		coinList.get(0).dispose();
		world.dispose();
	}
}
