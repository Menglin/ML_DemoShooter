package com.menglin.example;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
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
	}
	
	public void fn_initPhysicsBody(PhysicsWorld pw, Scene sc, String userdata)
	{
		m_physicsWorld = pw;
		m_scene = sc;
		FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		m_body = PhysicsFactory.createBoxBody(pw, this, BodyType.DynamicBody, fixtureDef);
		m_body.setBullet(true);
		m_body.setUserData(new UserData(this, userdata));
		m_body.setActive(true);
		pw.registerPhysicsConnector(new PhysicsConnector(this, m_body, true, true));
	}
	

	public void fn_setLinearVelocity(float f, float g) {
		m_body.setLinearVelocity(f,g);
	}
	
	public Body fn_getBody()
	{
		return m_body;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		m_scene.registerUpdateHandler(new IUpdateHandler() {
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
						final PhysicsConnector physicsConnector = 
								m_physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(m_sprite);
						if (physicsConnector != null)
						{
							m_physicsWorld.unregisterPhysicsConnector(physicsConnector);
							m_body.setActive(false);
							m_physicsWorld.destroyBody(m_body);
							m_scene.detachChild(m_sprite);
							m_sprite.dispose();
						}
					}
				}
			}
			// end onupdate
		});
		// end registerUpdateHandler
	}
	
	PhysicsWorld m_physicsWorld;
	Scene m_scene;
	
	private Sprite m_sprite;
	private Body m_body;
	
	public long m_damage;
	
	public long m_lifeSpan;
	public long m_bornTime;
	public long m_currentTime;
}
