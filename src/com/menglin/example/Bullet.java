package com.menglin.example;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Bullet extends Sprite implements Runnable{

	public Bullet(float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
		m_damage = 30;
		m_lifeSpan = 300;
		m_bornTime = System.currentTimeMillis();
		
		m_sprite = this;
		m_isActived = true;
	}
	
	public void fn_initPhysicsBody(String userdata)
	{
		FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		m_body = PhysicsFactory.createBoxBody(MainActivity.m_physicsWorld, this, BodyType.DynamicBody, fixtureDef);
		m_body.setBullet(true);
		m_body.setUserData(new UserData(this, userdata));
		m_body.setActive(true);
		MainActivity.m_physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, m_body, true, true));
	}
	

	public void fn_setLinearVelocity(float f, float g) {
		m_body.setLinearVelocity(f,g);
	}
	
	public Body fn_getBody()
	{
		return m_body;
	}
	
	public void fn_disactive()
	{
		m_isActived = false;
	}
	
	private void fn_destroy()
	{
		if (this != null && m_body != null)
		{
			final PhysicsConnector physicsConnector = 
					MainActivity.m_physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(m_sprite);
			if (physicsConnector != null)
			{
				MainActivity.m_physicsWorld.unregisterPhysicsConnector(physicsConnector);
				m_body.setActive(false);
				MainActivity.m_physicsWorld.destroyBody(m_body);
				MainActivity.m_GameScene.detachChild(m_sprite);
				m_sprite.dispose();
			}
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		MainActivity.m_GameScene.registerUpdateHandler(new IUpdateHandler() {
			// TODO Game main Loop
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				m_currentTime = System.currentTimeMillis();
				if (m_currentTime - m_bornTime > m_lifeSpan)
				{
					if (this != null && m_body != null)
					{
						fn_destroy();
					}
				}
				
				if (!m_isActived)
				{
					fn_destroy();
				}
			}
			// end onupdate
		});
		// end registerUpdateHandler
	}
	
	private Sprite m_sprite;
	private Body m_body;
	
	private boolean m_isActived;
	
	public long m_damage;
	
	public long m_lifeSpan;
	public long m_bornTime;
	public long m_currentTime;
}
