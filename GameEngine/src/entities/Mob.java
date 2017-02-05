package entities;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Mob extends Entity{
	
	private static final float RUN_SPEED = 50, TURN_SPEED = 160, JUMP_POWER = 60;
	private static final float TERRAIN_HEIGHT = 0;
	public static final float GRAVITY = -100;
	private float health = 100;
	private float damage = 10;
	private float currentSpeed = 0, currentTurnSpeed = 0, upwardsSpeed = 0;
	private boolean isInAir = false;
	private int moveInterval;
	Random r = new Random();

	public Mob(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		moveInterval = 100;
	}
	public void move(Terrain terrain){
		if(moveInterval == 0){
			currentSpeed = 0;
			currentTurnSpeed = 0;
			generateMoveSet();
			moveInterval = r.nextInt(151-50) + 50;
		}
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(super.getPosition().y < terrainHeight){
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
		moveInterval--;
	}
	private void jump(){
		if(!isInAir){
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}
	private void generateMoveSet(){
		int move = r.nextInt(10);
		if(move == 1){
			//Move speed
			currentSpeed = RUN_SPEED;
		}else if(move == 2){
			//Turn speed
			currentTurnSpeed = TURN_SPEED;
		}else if(move == 3){
			//Jump
			jump();
		}
	}
}
