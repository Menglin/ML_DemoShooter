package com.menglin.example;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.math.MathUtils;

public class Weapon_Ranged {
	public Weapon_Ranged()
	{
		m_speed = 50;
		m_fireRate = 80;
		m_spread = 0.3f;
	}
	
	public void fn_fire(float x, float y, float dx, float dy, VertexBufferObjectManager vbom)
	{
		Bullet bullet = new Bullet(x, y, ResourcesManager.m_BulletTextureRegion, vbom);
		bullet.setRotation(MathUtils.radToDeg((float)Math.atan2(dx, -dy)));
		bullet.fn_initPhysicsBody("Bullet");
		
		bullet.fn_setLinearVelocity(m_speed*(float)Math.cos((float)Math.atan2(dy+Math.random()*m_spread-m_spread/2, dx+Math.random()*m_spread-m_spread/2)), m_speed*(float)Math.sin((float)Math.atan2(dy+Math.random()*m_spread-m_spread/2, dx+Math.random()*m_spread-m_spread/2)));
		MainActivity.m_GameScene.attachChild(bullet);
		Thread t = new Thread(bullet);
		t.start();
	}
	
	protected long m_speed;
	protected long m_fireRate;
	protected float m_spread;
}
