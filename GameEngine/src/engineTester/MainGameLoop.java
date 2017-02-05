package engineTester;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Mob;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();

		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer(loader);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		
		//Terrain textures
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		////
		
		
		ModelData treeData = OBJFileLoader.loadOBJ("lowPolyTree");		
		RawModel model = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
		TexturedModel treeModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("lowPolyTree")));
		RawModel model2 = OBJLoader.loadObjModel("grassModel", loader);
		TexturedModel grassModel = new TexturedModel(model2, new ModelTexture(loader.loadTexture("grassTexture")));
		grassModel.getTexture().setHasTransparency(true);
		grassModel.getTexture().setUseFakeLighting(true);
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		TexturedModel fernModel = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernTextureAtlas);
		fernModel.getTexture().setHasTransparency(true);

		ModelTexture texture = treeModel.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(2);
		Light light = new Light(new Vector3f(2000, 10000, -1000), new Vector3f(1.5f, 1.5f, 1.5f));
		ArrayList<Light> lights = new ArrayList<Light>();
		ArrayList<Entity> entities = new ArrayList<>();
		ArrayList<Entity> normalMapEntities = new ArrayList<>();
		ArrayList<Terrain> terrains = new ArrayList<>();
		
		
		lights.add(light);
		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
		terrains.add(terrain);
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		
		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader), new ModelTexture(loader.loadTexture("barrel")));
		barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(0.5f);
		normalMapEntities.add(new Entity(barrelModel,  new Vector3f(420, 0, -400), 0, 180, 0, 0.5f));
		
		
		Random r = new Random();
		for (int i = 0; i < 200; i++){
			float x = r.nextFloat() * 800;
			float z = r.nextFloat() * -800;
			while(terrain.getHeightOfTerrain(x, z) < 0){
				x = r.nextFloat() * 800;
				z = r.nextFloat() * -800;
			}
			float y = terrain.getHeightOfTerrain(x, z);
			entities.add(new Entity(treeModel, new Vector3f(x, y, z), 0, 0, 0f, 0.8f));
		}
		for (int i = 0; i < 750; i++){
			float x = r.nextFloat() * 800;
			float z = r.nextFloat() * -800;
			while(terrain.getHeightOfTerrain(x, z) < 0){
				x = r.nextFloat() * 800;
				z = r.nextFloat() * -800;
			}
			float y = terrain.getHeightOfTerrain(x, z);
			entities.add(new Entity(grassModel, new Vector3f(x, y, z), 0, 0, 0f, 1f));
		}
		for (int i = 0; i < 450; i++){
			float x = r.nextFloat() * 800;
			float z = r.nextFloat() * -800;
			while(terrain.getHeightOfTerrain(x, z) < 0){
				x = r.nextFloat() * 800;
				z = r.nextFloat() * -800;
			}
			float y = terrain.getHeightOfTerrain(x, z);
			entities.add(new Entity(fernModel, r.nextInt(4), new Vector3f(x, y, z), 0, 0, 0f, 0.7f));
		}
		
		RawModel bunnyModel = OBJLoader.loadObjModel("bunny", loader);
		TexturedModel textureBunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("white")));
		Player player = new Player(textureBunny, new Vector3f(400, 0, -400), 0, 180, 0, 0.5f);
		Camera camera = new Camera(player);
		
		Mob mob = new Mob(textureBunny, new Vector3f(450, 0, -400), 0, 180, 0, 2f);
		
		ArrayList<GuiTexture> guis = new ArrayList<>();
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		//Water
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		ArrayList<WaterTile> waters = new ArrayList<>();
		WaterTile water = new WaterTile(0, 0, 0);
		for(int i = 1; i < terrain.SIZE / water.TILE_SIZE; i++){
			for (int j = 1; j < terrain.SIZE / water.TILE_SIZE; j++){
				waters.add(new WaterTile((float) (i * water.TILE_SIZE), (float) (-j * water.TILE_SIZE), -2));
			}
		}		
		//
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4, true);
		
		ParticleSystem pSys = new ParticleSystem(particleTexture, 4f, 10f, 0.3f, 4f, 1f);
		pSys.setSpeedError(5f);
		pSys.setLifeError(3f);
		pSys.setScaleError(3f);
		pSys.setDirection(new Vector3f(0, 1, 0), 5.9f);
		while(!Display.isCloseRequested()){
			//Gamelogic
			player.move(terrain);
			mob.move(terrain);
			camera.move();
			picker.update();
			
			for(int i = 0; i < 50; i++){
				float x = r.nextFloat() * 800;
				float z = r.nextFloat() * -800;
				float y = terrain.getHeightOfTerrain(x, z);
				pSys.generateParticles(new Vector3f(x, y, z));
			}
			
			ParticleMaster.update(camera);
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -waters.get(0).getHeight() + 0.6f));
			renderer.processEntity(player);
			renderer.processEntity(mob);
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			fbos.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, waters.get(0).getHeight()));
			renderer.processEntity(player);
			renderer.processEntity(mob);
			
			fbos.unbindCurrentFrameBuffer();
			renderer.processEntity(player);
			renderer.processEntity(mob);
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 15000));
			waterRenderer.render(waters, camera, light);
			
			ParticleMaster.renderParticles(camera);
			
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}
		ParticleMaster.cleanUp();
		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
