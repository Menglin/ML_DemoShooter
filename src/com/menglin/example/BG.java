package com.menglin.example;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class BG extends Sprite{

	public BG(float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
		this.setWidth(2048);
		this.setHeight(2048);
		MainActivity.m_GameScene.attachChild(this);
		
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, MainActivity.m_WorldSize.y-2, MainActivity.m_WorldSize.x, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, MainActivity.m_WorldSize.x, 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, MainActivity.m_WorldSize.y, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(MainActivity.m_WorldSize.x - 2, 0, 2, MainActivity.m_WorldSize.y, vertexBufferObjectManager);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		Body wallbody;
		wallbody = PhysicsFactory.createBoxBody(MainActivity.m_physicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		wallbody.setUserData(new UserData(ground,"Wall"));
		wallbody.setActive(true);
		MainActivity.m_physicsWorld.registerPhysicsConnector(new PhysicsConnector(ground, wallbody, true, true));
		wallbody = PhysicsFactory.createBoxBody(MainActivity.m_physicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		wallbody.setUserData(new UserData(roof,"Wall"));
		wallbody.setActive(true);
		MainActivity.m_physicsWorld.registerPhysicsConnector(new PhysicsConnector(roof, wallbody, true, true));
		wallbody = PhysicsFactory.createBoxBody(MainActivity.m_physicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		wallbody.setUserData(new UserData(left,"Wall"));
		wallbody.setActive(true);
		MainActivity.m_physicsWorld.registerPhysicsConnector(new PhysicsConnector(left, wallbody, true, true));
		wallbody = PhysicsFactory.createBoxBody(MainActivity.m_physicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		wallbody.setUserData(new UserData(right,"Wall"));
		wallbody.setActive(true);
		MainActivity.m_physicsWorld.registerPhysicsConnector(new PhysicsConnector(right, wallbody, true, true));

		MainActivity.m_GameScene.attachChild(ground);
		MainActivity.m_GameScene.attachChild(roof);
		MainActivity.m_GameScene.attachChild(left);
		MainActivity.m_GameScene.attachChild(right);
		
		Rectangle rectObj;
		float tx;
		float ty;
		float tw;
		float th;
		for (int i = 0; i < 10; i++)
		{
			tw = (float)Math.random() * 100 + 50;
			th = (float)Math.random() * 100 + 50;
			
			tx = (float)Math.random() * (MainActivity.m_WorldSize.x - tw);
			ty = (float)Math.random() * (MainActivity.m_WorldSize.y - th);
			
			rectObj = new Rectangle(tx, ty, tw, th, vertexBufferObjectManager);
			wallbody = PhysicsFactory.createBoxBody(MainActivity.m_physicsWorld, rectObj, BodyType.StaticBody, wallFixtureDef);
			wallbody.setUserData(new UserData(rectObj,"Object"));
			wallbody.setActive(true);
			MainActivity.m_physicsWorld.registerPhysicsConnector(new PhysicsConnector(rectObj, wallbody, true, true));
			MainActivity.m_GameScene.attachChild(rectObj);
		}
	}

}
