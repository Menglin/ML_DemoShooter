package com.menglin.example;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
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
		m_scene = sc;
		m_scene.attachChild(m_blood);
	}
	
	public void fn_setDirection(float x, float y)
	{
		m_dx = x - m_body.getPosition().x;
		m_dy = y - m_body.getPosition().y;
		fn_chasing();
	}
	
	public void fn_chasing()
	{
		final float px = m_speed*(float)Math.cos((float)Math.atan2(m_dy, m_dx));
		final float py = m_speed*(float)Math.sin((float)Math.atan2(m_dy, m_dx));
		m_body.setLinearVelocity(px + (float)(Math.random()*2 - 2), py + (float)(Math.random()*2 - 2));
	}
	
	public void fn_patrolling()
	{
		m_body.setLinearVelocity((float)(Math.random()*10 - 5), (float)(Math.random()*10 - 5));
	}
	
	/*
	@Override
	public void run() {
		// TODO Auto-generated method stub
		m_scene.registerUpdateHandler(new IUpdateHandler() {
			// TODO Game main Loop
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {

				if (m_state.equals("Chasing"))
					fn_chasing();
			}
			// end onupdate
		});
		// end registerUpdateHandler
	}
	*/
	
	private String m_state;
	private float m_dx;
	private float m_dy;
	
	public Rectangle m_blood;
	private Scene m_scene;
}
