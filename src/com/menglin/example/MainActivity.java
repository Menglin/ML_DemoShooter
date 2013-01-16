package com.menglin.example;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
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
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import android.opengl.GLES20;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends BaseGameActivity implements IOnMenuItemClickListener {
	
	protected static final int OPTION_RESUME = 0;
	protected static final int OPTION_QUIT = OPTION_RESUME + 1;
	protected static final int OPTION_QUIT_OK = OPTION_QUIT + 1;
	protected static final int OPTION_QUIT_CANCLE = OPTION_QUIT + 2;
	
	// Camera Control //
	public static int CAMERA_WIDTH = 800;
	public static int CAMERA_HEIGHT = 480;
	public static Camera m_Camera;

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
	public static SceneType m_CurrentScene = SceneType.SPLASH;
	public static Scene m_SplashScene;
	public static Scene m_StartMenuScene;
	public static Scene m_GameScene;
	public static MenuScene m_OptionMenuScene;
	public static MenuScene m_SubMenuScene;
	
	// Resources
	public static ResourcesManager m_rm = new ResourcesManager();
	
	// splash scene
	public static Sprite m_Splash;
	
	// for Game Scene
	public static Hero m_hero;
	public static BG m_bg;
	
	//private Vector<Enemy> m_Enemies = new Vector<Enemy>();
	
	private long m_currentTime;
	private long m_lastEnemyTime;
	
	// Physics
	public static PhysicsWorld m_physicsWorld;
	
	// World Control
	public static Vector2 m_WorldSize = new Vector2(2048, 2048);
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		CAMERA_WIDTH = metrics.widthPixels;
		CAMERA_HEIGHT = metrics.heightPixels;
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
		
		m_rm.fn_loadResources(this.getTextureManager(), this.getFontManager(), this);
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
		// For splash ////
		initSplashScene();
		pOnCreateSceneCallback.onCreateSceneFinished(m_SplashScene);
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
		m_Splash = new Sprite(0, 0, ResourcesManager.m_SplashTextureRegion, mEngine.getVertexBufferObjectManager())
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
		m_StartMenuScene.setBackground(new Background(0, 0, 0));
		
		/* Calculate the coordinates for the face, so its centered on the camera. */
		final float centerX = (CAMERA_WIDTH - ResourcesManager.m_StartMenuStartTextureRegion.getWidth()) / 2;
		//float centerY = (CAMERA_HEIGHT - this.m_StartMenuStartTextureRegion.getHeight()) / 2;
		final float buttomY = CAMERA_HEIGHT - ResourcesManager.m_StartMenuStartTextureRegion.getHeight();
		
		final Sprite menupic = new Sprite(0, 0, ResourcesManager.m_StartMenuPicTextureRegion, this.getVertexBufferObjectManager());
		menupic.setWidth(CAMERA_WIDTH);
		menupic.setHeight(CAMERA_HEIGHT);
		m_StartMenuScene.attachChild(menupic);
		/* Create the face and add it to the scene. */
		final Sprite btn_start = new ButtonSprite(centerX - 220, buttomY, ResourcesManager.m_StartMenuStartTextureRegion, ResourcesManager.m_StartMenuStartTextureRegion2, ResourcesManager.m_StartMenuStartTextureRegion3, this.getVertexBufferObjectManager(),
				new OnClickListener() {
					@Override
					public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
						//m_GameScene.reset();
						m_StartMenuScene.detachSelf();
						initGameScene();
						initOptionScene();
						mEngine.setScene(m_GameScene);
						m_CurrentScene = SceneType.GAME;
					}
				});
		m_StartMenuScene.registerTouchArea(btn_start);
		m_StartMenuScene.attachChild(btn_start);
		final Sprite btn_quit = new ButtonSprite(centerX + 20, buttomY, ResourcesManager.m_StartMenuQuitTextureRegion, ResourcesManager.m_StartMenuQuitTextureRegion2, ResourcesManager.m_StartMenuQuitTextureRegion3, this.getVertexBufferObjectManager(),
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
		m_GameScene.setBackground(new Background(0.5f, 0.5f, 0.75f));
		
		initPhysics();
		
		m_bg = new BG(0, 0, ResourcesManager.m_BGTextureRegion, this.getVertexBufferObjectManager());
		
		float centerX = (CAMERA_WIDTH - ResourcesManager.m_HeroTiledTextureRegion.getWidth()) / 2;
		float centerY = (CAMERA_HEIGHT - ResourcesManager.m_HeroTiledTextureRegion.getHeight()) / 2;
		
		m_hero = new Hero(centerX, centerY, ResourcesManager.m_HeroTiledTextureRegion, this.getVertexBufferObjectManager());
		m_GameScene.attachChild(m_hero);
		m_hero.fn_initPhysicsBody("Hero");
		m_hero.fn_initControl(this.getVertexBufferObjectManager());
		m_hero.fn_startThread();
		
		for (int i = 0; i < 20; i++)
		{
			Enemy enemy = new Enemy((float)Math.random()*(m_WorldSize.x - 100) + 50, (float)Math.random()*(m_WorldSize.y - 100) + 50, ResourcesManager.m_EnemyTiledTextureRegion, this.getVertexBufferObjectManager());
			m_GameScene.attachChild(enemy);
			enemy.fn_initPhysicsBody("Enemy");
			enemy.fn_getScene(m_GameScene);
			Enemy.m_Enemies.add(enemy);
			enemy.fn_startThread();
		}

		m_lastEnemyTime = 0;
		
		// Game main Loop
		m_GameScene.registerUpdateHandler(new IUpdateHandler() {
			// TODO Game main Loop
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {

				// add enemy ////
				m_currentTime = System.currentTimeMillis();
				
				if (m_currentTime - m_lastEnemyTime >= 5000 && Enemy.m_Enemies.size() < 50)
				{
					for (int i = 0; i < 10; i++)
					{
						Enemy enemy = new Enemy((float)Math.random()*(m_WorldSize.x - 100) + 50, (float)Math.random()*(m_WorldSize.y - 100) + 50, ResourcesManager.m_EnemyTiledTextureRegion, MainActivity.this.getVertexBufferObjectManager());
						m_GameScene.attachChild(enemy);
						enemy.fn_initPhysicsBody("Enemy");
						enemy.fn_getScene(m_GameScene);
						Enemy.m_Enemies.add(enemy);
						enemy.fn_startThread();
					}
					m_lastEnemyTime = m_currentTime;
				}
				// end enemy ////
			}
			// end onupdate
		});
	}
	
	private void initOptionScene()
	{
		m_OptionMenuScene = new MenuScene(m_Camera);
		
		final IMenuItem resumeMenuItem = new ColorMenuItemDecorator(new TextMenuItem(OPTION_RESUME, ResourcesManager.m_Font, "RESUME", this.getVertexBufferObjectManager()), new Color(1,0,0), new Color(0,0,0));
		resumeMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		m_OptionMenuScene.addMenuItem(resumeMenuItem);

		final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(OPTION_QUIT, ResourcesManager.m_Font, "QUIT", this.getVertexBufferObjectManager()), new Color(1,0,0), new Color(0,0,0));
		quitMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		m_OptionMenuScene.addMenuItem(quitMenuItem);

		m_OptionMenuScene.buildAnimations();
		m_OptionMenuScene.setBackgroundEnabled(false);
		m_OptionMenuScene.setOnMenuItemClickListener(this);
		
		m_SubMenuScene = new MenuScene(m_Camera);
		
		final IMenuItem okSubMenuItem = new ColorMenuItemDecorator(new TextMenuItem(OPTION_QUIT_OK, ResourcesManager.m_Font, "Quit", this.getVertexBufferObjectManager()), new Color(1,0,0), new Color(0,0,0));
		resumeMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		m_SubMenuScene.addMenuItem(okSubMenuItem);

		final IMenuItem cancleSubMenuItem = new ColorMenuItemDecorator(new TextMenuItem(OPTION_QUIT_CANCLE, ResourcesManager.m_Font, "Cancle", this.getVertexBufferObjectManager()), new Color(1,0,0), new Color(0,0,0));
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
							if (udata1.m_label.equals("Bullet") && udata2.m_label.equals("Enemy") )
							{
								Bullet tmpBullet = (Bullet)udata1.m_pAreaShape;
								Enemy tmpEnemy = (Enemy)udata2.m_pAreaShape;
								
								tmpBullet.fn_disactive();
								tmpEnemy.fn_getDamage(tmpBullet.m_damage);
							}

							if (udata2.m_label.equals("Bullet") && udata1.m_label.equals("Enemy") )
							{

								Bullet tmpBullet = (Bullet)udata2.m_pAreaShape;
								Enemy tmpEnemy = (Enemy)udata1.m_pAreaShape;

								tmpBullet.fn_disactive();
								tmpEnemy.fn_getDamage(tmpBullet.m_damage);

								// end if
							}
							// end if
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
