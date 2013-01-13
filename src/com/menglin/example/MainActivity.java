package com.menglin.example;

import java.util.Iterator;
import java.util.Vector;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.animator.SlideMenuAnimator;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Manifold;

import android.opengl.GLES20;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends BaseGameActivity implements IOnMenuItemClickListener {
	
	protected static final int OPTION_RESUME = 0;
	protected static final int OPTION_QUIT = OPTION_RESUME + 1;
	protected static final int OPTION_QUIT_OK = OPTION_QUIT + 1;
	protected static final int OPTION_QUIT_CANCLE = OPTION_QUIT + 2;
	
	// Camera Control //
	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;
	private Camera m_Camera;

	// Game state;
	public enum GameState
	{
		STOPPED,
		RUNNING,
	}
	public GameState m_currentState = GameState.STOPPED;
	
	// Scene Control//
	public enum SceneType
	{
		SPLASH,
		STARTMENU,
		OPTIONMENU,
		GAME,
	}
	public SceneType m_CurrentScene = SceneType.SPLASH;
	private Scene m_SplashScene;
	private Scene m_StartMenuScene;
	private Scene m_GameScene;
	private MenuScene m_OptionMenuScene;
	private MenuScene m_SubMenuScene;
	
	// for splash texture
	private static BitmapTextureAtlas m_SplashTextureAtlas;
	private static ITextureRegion m_SplashTextureRegion;
	private static Sprite m_Splash;
	
	// for startmenu texture
	private static BitmapTextureAtlas m_StartMenuTextureAtlas;
	private static ITextureRegion m_StartMenuStartTextureRegion;
	private static ITextureRegion m_StartMenuStartTextureRegion2;
	private static ITextureRegion m_StartMenuStartTextureRegion3;
	private static ITextureRegion m_StartMenuQuitTextureRegion;
	private static ITextureRegion m_StartMenuQuitTextureRegion2;
	private static ITextureRegion m_StartMenuQuitTextureRegion3;
	private static ITextureRegion m_StartMenuPicTextureRegion;
	
	// for Game Scene
	private Hero m_hero;
	private static BitmapTextureAtlas m_HeroTextureAtlas;
	private static ITextureRegion m_HeroTextureRegion;
	
	private BitmapTextureAtlas m_EnemyTextureAtlas;
	private ITextureRegion m_EnemyTextureRegion;
	
	private Vector<Enemy> m_Enemies = new Vector<Enemy>();
	
	private long m_currentTime;
	private long m_lastUpdateTime;
	private long m_lastEnemyTime;
	
	// for option menu
	private Font m_Font;
	
	// Physics
	private PhysicsWorld m_physicsWorld;

	@Override
	public EngineOptions onCreateEngineOptions() {
		m_Camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), m_Camera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);

		if(MultiTouch.isSupported(this)) {
			if(MultiTouch.isSupportedDistinct(this)) {
				//Toast.makeText(this, "MultiTouch detected --> Both controls will work properly!", Toast.LENGTH_SHORT).show();
			} else {
				m_hero.mPlaceOnScreenControlsAtDifferentVerticalLocations = true;
				Toast.makeText(this, "MultiTouch detected, but your device has problems distinguishing between fingers.\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this, "Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
		}
		
		return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)	throws Exception {
		// set font
		FontFactory.setAssetBasePath("font/");

		final ITexture fontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		m_Font = FontFactory.createFromAsset(this.getFontManager(), fontTexture, this.getAssets(), "Plok.ttf", 48, true, android.graphics.Color.WHITE);
		m_Font.load();
		
		// set pic folder
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		// For splash ////
		m_SplashTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 800, 480, TextureOptions.DEFAULT);
		m_SplashTextureRegion =BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_SplashTextureAtlas,this,"splash.png", 0, 0);
		m_SplashTextureAtlas.load();
		pOnCreateResourcesCallback.onCreateResourcesFinished();
		// end splash ////
		
		// for start menu ////
		m_StartMenuTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 800, 580, TextureOptions.DEFAULT);
		m_StartMenuStartTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_StartMenuTextureAtlas,this,"menu_start.png", 0, 0);
		m_StartMenuStartTextureRegion2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_StartMenuTextureAtlas,this,"menu_start2.png", 170, 0);
		m_StartMenuStartTextureRegion3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_StartMenuTextureAtlas,this,"menu_start3.png", 340, 0);
		m_StartMenuQuitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_StartMenuTextureAtlas,this,"menu_quit.png", 0, 50);
		m_StartMenuQuitTextureRegion2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_StartMenuTextureAtlas,this,"menu_quit2.png", 170, 50);
		m_StartMenuQuitTextureRegion3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_StartMenuTextureAtlas,this,"menu_quit3.png", 340, 50);
		m_StartMenuPicTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_StartMenuTextureAtlas,this,"menu_pic.png", 0, 100);
		m_StartMenuTextureAtlas.load();
		// end start menu ////
		
		// for game ////
		
		m_HeroTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.DEFAULT);
		m_HeroTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_HeroTextureAtlas, this, "hero.png", 0, 0);
		m_HeroTextureAtlas.load();
		
		m_EnemyTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.DEFAULT);
		m_EnemyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_EnemyTextureAtlas, this, "enemy.png", 0, 0);
		m_EnemyTextureAtlas.load();
		// end game ////
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
		// For splash ////
		initSplashScene();
		pOnCreateSceneCallback.onCreateSceneFinished(this.m_SplashScene);
		// end splash ////
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		mEngine.registerUpdateHandler(new TimerHandler(3.0f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				//loadResources();
				initStartMenu();
				m_Splash.detachSelf();
				mEngine.setScene(m_StartMenuScene);
				m_CurrentScene = SceneType.STARTMENU;
			}
		}));
		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN)
		{
			if(m_CurrentScene == SceneType.OPTIONMENU) {
				/* Remove the menu and reset it. */
				m_OptionMenuScene.back();
				m_GameScene.setChildScene(m_hero.m_velocityOnScreenControl);
				m_CurrentScene = SceneType.GAME;
			} else if(m_CurrentScene == SceneType.GAME) {
				/* Attach the menu. */
				m_GameScene.clearChildScene();
				m_GameScene.setChildScene(m_OptionMenuScene, false, true, true);
				m_CurrentScene = SceneType.OPTIONMENU;
			}
			return true;
		}
		else if (pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN)
		{
			if(m_CurrentScene == SceneType.GAME) {
				/* Attach the menu. */
				m_GameScene.clearChildScene();
				m_GameScene.setChildScene(m_SubMenuScene, false, true, true);
				m_CurrentScene = SceneType.OPTIONMENU;
			}
			return true;
		}
		else if (pKeyCode == KeyEvent.KEYCODE_HOME && pEvent.getAction() == KeyEvent.ACTION_DOWN)
		{
			return true;
		}
		else
		{
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		// TODO onMenuItemClicked
		switch(pMenuItem.getID()) {
		case OPTION_QUIT_CANCLE:
			m_SubMenuScene.reset();
		case OPTION_RESUME:
			/* Restart the animation. */
			m_GameScene.reset();
			m_GameScene.setChildScene(m_hero.m_velocityOnScreenControl);
			/* Remove the menu and reset it. */
			m_GameScene.detachChild(m_OptionMenuScene);
			m_OptionMenuScene.reset();
			m_CurrentScene = SceneType.GAME;
			return true;
		case OPTION_QUIT:
			m_GameScene.detachChild(m_OptionMenuScene);
			m_OptionMenuScene.reset();
			m_GameScene.setChildScene(m_SubMenuScene);
			return true;
		case OPTION_QUIT_OK:
			this.finish();
			return true;
		default:
			return false;
		}
	}
	
	private void initSplashScene()
	{
		m_SplashScene = new Scene();
		m_Splash = new Sprite(0, 0, m_SplashTextureRegion, mEngine.getVertexBufferObjectManager())
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		m_Splash.setScale(1.5f);
		m_Splash.setPosition((CAMERA_WIDTH - m_Splash.getWidth()) * 0.5f, (CAMERA_HEIGHT - m_Splash.getHeight()) * 0.5f);
		m_SplashScene.attachChild(m_Splash);
	}
	
	private void initStartMenu()
	{
		// Start Menu Scene ////
		m_StartMenuScene = new Scene();
		m_StartMenuScene.setBackground(new Background(0.05f, 0.5f, 0.75f));
		
		/* Calculate the coordinates for the face, so its centered on the camera. */
		final float centerX = (CAMERA_WIDTH - m_StartMenuStartTextureRegion.getWidth()) / 2;
		//float centerY = (CAMERA_HEIGHT - this.m_StartMenuStartTextureRegion.getHeight()) / 2;
		final float buttomY = CAMERA_HEIGHT - m_StartMenuStartTextureRegion.getHeight();
		
		final Sprite menupic = new Sprite(0, 0, m_StartMenuPicTextureRegion, this.getVertexBufferObjectManager());
		m_StartMenuScene.attachChild(menupic);
		/* Create the face and add it to the scene. */
		final Sprite btn_start = new ButtonSprite(centerX - 220, buttomY, m_StartMenuStartTextureRegion, m_StartMenuStartTextureRegion2, m_StartMenuStartTextureRegion3, this.getVertexBufferObjectManager(),
				new OnClickListener() {
					@Override
					public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
						//m_GameScene.reset();
						m_StartMenuScene.detachSelf();
						MainActivity.this.initGameScene();
						MainActivity.this.initOptionScene();
						MainActivity.this.mEngine.setScene(m_GameScene);
						MainActivity.this.m_CurrentScene = SceneType.GAME;
					}
				});
		m_StartMenuScene.registerTouchArea(btn_start);
		m_StartMenuScene.attachChild(btn_start);
		final Sprite btn_quit = new ButtonSprite(centerX + 20, buttomY, m_StartMenuQuitTextureRegion, m_StartMenuQuitTextureRegion2, m_StartMenuQuitTextureRegion3, this.getVertexBufferObjectManager(),
				new OnClickListener() {
					@Override
					public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
						MainActivity.this.finish();
					}
				});
		m_StartMenuScene.registerTouchArea(btn_quit);
		m_StartMenuScene.attachChild(btn_quit);
		m_StartMenuScene.setTouchAreaBindingOnActionDownEnabled(true);
	}
	
	private void initGameScene()
	{
		// Game Scene ////
		m_GameScene = new Scene();
		m_GameScene.setBackground(new Background(0.05f, 0.5f, 0.75f));
		
		initPhysics();
		
		float centerX = (CAMERA_WIDTH - m_HeroTextureRegion.getWidth()) / 2;
		float centerY = (CAMERA_HEIGHT - m_HeroTextureRegion.getHeight()) / 2;
		
		m_hero = new Hero(centerX, centerY, m_HeroTextureRegion, this.getVertexBufferObjectManager());
		m_GameScene.attachChild(m_hero);
		m_hero.fn_loadRes(this.getTextureManager(), this);
		m_hero.fn_initPhysicsBody(m_physicsWorld, "Hero");
		m_hero.fn_initControl(CAMERA_WIDTH, CAMERA_HEIGHT, m_GameScene, this.getVertexBufferObjectManager(), this.m_Camera);

		for (int i = 0; i < 10; i++)
		{
			Enemy enemy = new Enemy(i*50, i*50, m_EnemyTextureRegion, this.getVertexBufferObjectManager());
			m_GameScene.attachChild(enemy);
			enemy.fn_initPhysicsBody(m_physicsWorld, "Enemy");
			//enemy.fn_initThread();
			m_Enemies.add(enemy);
		}

		m_lastUpdateTime = 0;
		m_lastEnemyTime = 0;
		//m_currentTime = m_lastUpdateTime;
		
		// Game main Loop
		m_GameScene.registerUpdateHandler(new IUpdateHandler() {
			// TODO Game main Loop
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {

				m_currentTime = System.currentTimeMillis();
				if (m_currentTime - m_lastUpdateTime >= 100)
				{
					Iterator<Enemy> it = m_Enemies.iterator();
					
					while (it.hasNext())
					{
						Enemy tmpEnemy = it.next();
						tmpEnemy.fn_chasing(m_hero.m_body.getPosition().x, m_hero.m_body.getPosition().y);
					}
					// end while
					m_lastUpdateTime = m_currentTime;
				}
				
				if (m_currentTime - m_lastEnemyTime >= 2500 && m_Enemies.size() < 20)
				{
					for (int i = 0; i < 5; i++)
					{
						Enemy enemy = new Enemy((float)Math.random()*(CAMERA_WIDTH - 100) + 50, (float)Math.random()*(CAMERA_HEIGHT - 100) + 50, m_EnemyTextureRegion, MainActivity.this.getVertexBufferObjectManager());
						m_GameScene.attachChild(enemy);
						enemy.fn_initPhysicsBody(m_physicsWorld, "Enemy");
						//enemy.fn_initThread();
						m_Enemies.add(enemy);
					}
					m_lastEnemyTime = m_currentTime;
				}
				// end if
			}
			// end onupdate
		});
	}
	
	private void initOptionScene()
	{
		m_OptionMenuScene = new MenuScene(this.m_Camera);
		
		final IMenuItem resumeMenuItem = new ColorMenuItemDecorator(new TextMenuItem(OPTION_RESUME, this.m_Font, "RESUME", this.getVertexBufferObjectManager()), new Color(1,0,0), new Color(0,0,0));
		resumeMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		m_OptionMenuScene.addMenuItem(resumeMenuItem);

		final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(OPTION_QUIT, this.m_Font, "QUIT", this.getVertexBufferObjectManager()), new Color(1,0,0), new Color(0,0,0));
		quitMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		m_OptionMenuScene.addMenuItem(quitMenuItem);

		m_OptionMenuScene.buildAnimations();
		m_OptionMenuScene.setBackgroundEnabled(false);
		m_OptionMenuScene.setOnMenuItemClickListener(this);
		
		m_SubMenuScene = new MenuScene(m_Camera);
		
		final IMenuItem okSubMenuItem = new ColorMenuItemDecorator(new TextMenuItem(OPTION_QUIT_OK, this.m_Font, "Quit", this.getVertexBufferObjectManager()), new Color(1,0,0), new Color(0,0,0));
		resumeMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		m_SubMenuScene.addMenuItem(okSubMenuItem);

		final IMenuItem cancleSubMenuItem = new ColorMenuItemDecorator(new TextMenuItem(OPTION_QUIT_CANCLE, this.m_Font, "Cancle", this.getVertexBufferObjectManager()), new Color(1,0,0), new Color(0,0,0));
		quitMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		m_SubMenuScene.addMenuItem(cancleSubMenuItem);
		
		m_SubMenuScene.setMenuAnimator(new SlideMenuAnimator());
		m_SubMenuScene.buildAnimations();
		m_SubMenuScene.setBackgroundEnabled(false);
		m_SubMenuScene.setOnMenuItemClickListener(this);
	}

	// Physics ////
	// TODO Physics
	private void initPhysics()
	{
		// no gravity
		m_physicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		m_GameScene.registerUpdateHandler(m_physicsWorld);
		
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		Body wallbody; 
		wallbody = PhysicsFactory.createBoxBody(m_physicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		wallbody.setUserData(new UserData(ground,"Wall"));
		wallbody.setActive(true);
		m_physicsWorld.registerPhysicsConnector(new PhysicsConnector(ground, wallbody, true, true));
		wallbody = PhysicsFactory.createBoxBody(m_physicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		wallbody.setUserData(new UserData(roof,"Wall"));
		wallbody.setActive(true);
		m_physicsWorld.registerPhysicsConnector(new PhysicsConnector(roof, wallbody, true, true));
		wallbody = PhysicsFactory.createBoxBody(m_physicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		wallbody.setUserData(new UserData(left,"Wall"));
		wallbody.setActive(true);
		m_physicsWorld.registerPhysicsConnector(new PhysicsConnector(left, wallbody, true, true));
		wallbody = PhysicsFactory.createBoxBody(m_physicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		wallbody.setUserData(new UserData(right,"Wall"));
		wallbody.setActive(true);
		m_physicsWorld.registerPhysicsConnector(new PhysicsConnector(right, wallbody, true, true));

		m_GameScene.attachChild(ground);
		m_GameScene.attachChild(roof);
		m_GameScene.attachChild(left);
		m_GameScene.attachChild(right);
		
		m_physicsWorld.setContactListener(fn_createContactListener());
	}
	
	private ContactListener fn_createContactListener()
	{
		ContactListener contactListener = new ContactListener()
		{

			@Override
			public void beginContact(Contact contact) {
				// TODO Auto-generated method stub
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				final UserData udata1 = (UserData)x1.getBody().getUserData();
				final UserData udata2 = (UserData)x2.getBody().getUserData();
				
				mEngine.runOnUpdateThread(new Runnable()
				{
					@Override
					public void run() {
						// TODO Auto-generated method stub
						final PhysicsConnector physicsConnector = 
								m_physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(udata1.m_pAreaShape);

						final PhysicsConnector physicsConnector2 = 
								m_physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(udata2.m_pAreaShape);
						
						if ( (physicsConnector != null) && (physicsConnector2 != null) )
						{
							if (udata1.m_label.equals("Bullet") && (udata2.m_label.equals("Wall") || udata2.m_label.equals("Enemy")) )
							{
								m_physicsWorld.unregisterPhysicsConnector(physicsConnector);
								x1.getBody().setActive(false);
								m_physicsWorld.destroyBody(x1.getBody());
								m_GameScene.detachChild(udata1.m_pAreaShape);
								udata1.m_pAreaShape.dispose();
								
								if (udata2.m_label.equals("Enemy"))
								{
									m_physicsWorld.unregisterPhysicsConnector(physicsConnector2);
									x2.getBody().setActive(false);
									m_physicsWorld.destroyBody(x2.getBody());
									m_GameScene.detachChild(udata2.m_pAreaShape);
									udata2.m_pAreaShape.dispose();
									m_Enemies.remove(udata2.m_pAreaShape);
								}
							}

							if (udata2.m_label.equals("Bullet") && (udata1.m_label.equals("Wall") || udata1.m_label.equals("Enemy")) )
							{
								m_physicsWorld.unregisterPhysicsConnector(physicsConnector2);
								x2.getBody().setActive(false);
								m_physicsWorld.destroyBody(x2.getBody());
								m_GameScene.detachChild(udata2.m_pAreaShape);
								udata2.m_pAreaShape.dispose();
								
								if (udata1.m_label.equals("Enemy"))
								{
									m_physicsWorld.unregisterPhysicsConnector(physicsConnector);
									x1.getBody().setActive(false);
									m_physicsWorld.destroyBody(x1.getBody());
									m_GameScene.detachChild(udata1.m_pAreaShape);
									udata1.m_pAreaShape.dispose();
									m_Enemies.remove(udata1.m_pAreaShape);
								}
							}
						}
						// end if
					}
					// end run
				});
				// end runOnUpdateThread
			}

			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}
		};
	    return contactListener;
	}
}
