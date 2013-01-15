package com.menglin.example;

import java.util.Vector;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;


public class Enemy extends Character{
	
	public Enemy(float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
		m_speed = 4;
		m_state = "Chasing";
		
		m_blood = new Rectangle(this.getX(), this.getY(), m_health / 2.5f, 3, pVertexBufferObjectManager);
		m_blood.setColor(new Color(0, 1, 0));
	}
	
	public void fn_getScene(Scene sc)
	{
		MainActivity.m_GameScene = sc;
		MainActivity.m_GameScene.attachChild(m_blood);
	}
	
	public void fn_setDirection()
	{
		m_dx = MainActivity.m_hero.m_body.getPosition().x - m_body.getPosition().x;
		m_dy = MainActivity.m_hero.m_body.getPosition().y - m_body.getPosition().y;

		final float px = m_speed*(float)Math.cos((float)Math.atan2(m_dy, m_dx));
		final float py = m_speed*(float)Math.sin((float)Math.atan2(m_dy, m_dx));
		m_body.setLinearVelocity(px + (float)(Math.random()*2 - 2), py + (float)(Math.random()*2 - 2));
	}
	
	public void fn_patrolling()
	{
		m_body.setLinearVelocity((float)(Math.random()*10 - 5), (float)(Math.random()*10 - 5));
	}
	
	public void fn_getDamage(float dam)
	{
		m_damageReceived = dam;
		m_health -= m_damageReceived;
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
				m_blood.detachSelf();
				m_blood.dispose();
				this.detachSelf();
				this.dispose();
				m_Enemies.remove(this);
			}
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		m_lastUpdateTime = System.currentTimeMillis();
		
		MainActivity.m_GameScene.registerUpdateHandler(new IUpdateHandler() {
			// TODO Game main Loop
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				
				m_blood.setPosition(m_sprite.getX(), m_sprite.getY());
				m_blood.setWidth(m_health / 2.5f);

				if (m_health <= 0)
					fn_destroy();
				else if (m_state.equals("Chasing"))
				{
					long currentTime = System.currentTimeMillis();
					if (currentTime - m_lastUpdateTime >= 100)
					{
						fn_setDirection();
						m_lastUpdateTime = currentTime;
					}
					// end if
				}
				// end if-else
			}
			// end onupdate
		});
		// end registerUpdateHandler
	}
	
	private long m_lastUpdateTime;
	
	private String m_state;
	private float m_dx;
	private float m_dy;
	private float m_damageReceived;
	
	public Rectangle m_blood;
	
	public static Vector<Enemy> m_Enemies = new Vector<Enemy>(); // use iterator to traverse
}
