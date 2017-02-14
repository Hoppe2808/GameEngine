package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity{
	
	private static final float RUN_SPEED = 200, TURN_SPEED = 160, JUMP_POWER = 60;
	private static final float TERRAIN_HEIGHT = 0;
	public static final float GRAVITY = -100;
	private float health = 100;
	private float damage = 10;
	private float currentSpeed = 0, currentTurnSpeed = 0, upwardsSpeed = 0;
	private boolean isInAir = false;
	private Vector3f direction = new Vector3f(1, 0, 1);

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	public void move(Terrain terrain){
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		if(dx != 0 && dz != 0){
			direction.x = dx;
			direction.z = dz;
		}
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(super.getPosition().y < terrainHeight){
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
	}
	private void jump(){
		if(!isInAir){
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}
	private void checkInputs(){
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			this.currentSpeed = RUN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			this.currentSpeed = -RUN_SPEED;
		}else{
			this.currentSpeed = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			this.currentTurnSpeed = -TURN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			this.currentTurnSpeed = TURN_SPEED;
		}else{
			this.currentTurnSpeed = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			jump();
		}
	}
	public Vector3f getDirection() {
		return direction;
	}
	

}
