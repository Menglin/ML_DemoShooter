package com.menglin.example;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
//import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;
import android.opengl.GLES20;

public class Hero extends Character{
	public Hero()
	{
		this.m_health = 100;
		this.m_speed = 10;
		
		m_lastFireTime = System.currentTimeMillis();
		m_curFireTime = 0;
		m_fireRate = 80;
		
		m_bullets = new Bullets();
	}
	
	public void fn_loadRes(TextureManager tm, Context c, String res)
	{
		super.fn_loadRes(tm, c, res);
		
		m_bullets.fn_loadRes(tm, c, "bullet.png");
		
		this.m_OnScreenControlTexture = new BitmapTextureAtlas(tm, 256, 128, TextureOptions.BILINEAR);
		this.m_OnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_OnScreenControlTexture, c, "onscreen_control_base.png", 0, 0);
		this.m_OnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_OnScreenControlTexture, c, "onscreen_control_knob.png", 128, 0);
		this.m_OnScreenControlTexture.load();
	}

	  ///////////////////////
	 // on screen control //
	///////////////////////
	public void fn_initControl(int camW, int camH, Scene sc, VertexBufferObjectManager vbom, Camera cam)
	{
		// get the variables to member variables, in order to use it inside the inner class
		m_scene = sc;
		m_vbom = vbom;
		
		//m_physicsHandler = new PhysicsHandler(m_Sprite);
		//m_Sprite.registerUpdateHandler(m_physicsHandler);
		
		// Velocity control (left). //
		final float x1 = 0;
		final float y1 = camH - m_OnScreenControlBaseTextureRegion.getHeight();
		m_velocityOnScreenControl = new AnalogOnScreenControl(x1, y1, cam, m_OnScreenControlBaseTextureRegion, m_OnScreenControlKnobTextureRegion, 0.1f, vbom, new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				//m_physicsHandler.setVelocity(pValueX * 100, pValueY * 100);
				m_body.setLinearVelocity(pValueX * 10, pValueY * 10);
			}

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
				// Nothing. //
			}
		});
		m_velocityOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		m_velocityOnScreenControl.getControlBase().setAlpha(0.5f);

		sc.setChildScene(m_velocityOnScreenControl);

		// Rotation control (right). //
		final float y2 = (this.mPlaceOnScreenControlsAtDifferentVerticalLocations) ? 0 : y1;
		final float x2 = camW - m_OnScreenControlBaseTextureRegion.getWidth();
		m_rotationOnScreenControl = new AnalogOnScreenControl(x2, y2, cam, m_OnScreenControlBaseTextureRegion, m_OnScreenControlKnobTextureRegion, 0.1f, vbom, new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				if(pValueX == 0 && pValueY == 0) {
				} else {
					m_body.setTransform(m_body.getPosition(), (float)Math.atan2(pValueX, -pValueY));
					m_curFireTime = System.currentTimeMillis();
					if (m_curFireTime-m_lastFireTime >= m_fireRate)
					{
						m_bullets.fn_addBullet(m_Sprite.getX()+m_Sprite.getWidth()/4+m_Sprite.getWidth()*(float)Math.cos((float)Math.atan2(pValueY, pValueX)), 
							m_Sprite.getY()+m_Sprite.getHeight()/4+m_Sprite.getHeight()*(float)Math.sin((float)Math.atan2(pValueY, pValueX)), pValueX, pValueY, m_scene, m_vbom, m_PhysicsWorld);
						m_lastFireTime = m_curFireTime;
					}
				}
			}

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
				// Nothing. //
			}
		});
		m_rotationOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		m_rotationOnScreenControl.getControlBase().setAlpha(0.5f);

		m_velocityOnScreenControl.setChildScene(m_rotationOnScreenControl);
	}

	
	private Bullets m_bullets;
	private Scene m_scene;
	private VertexBufferObjectManager m_vbom;

	// Fire rate control
	private long m_lastFireTime;
	private long m_curFireTime;
	private long m_fireRate;
	
	//private PhysicsHandler m_physicsHandler;
	
	private BitmapTextureAtlas m_OnScreenControlTexture;
	private ITextureRegion m_OnScreenControlBaseTextureRegion;
	private ITextureRegion m_OnScreenControlKnobTextureRegion;
	
	public AnalogOnScreenControl m_velocityOnScreenControl;
	private AnalogOnScreenControl m_rotationOnScreenControl;
	
	public boolean mPlaceOnScreenControlsAtDifferentVerticalLocations = false;
}
