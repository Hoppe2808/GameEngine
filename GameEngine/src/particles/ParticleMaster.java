package particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import renderEngine.Loader;

public class ParticleMaster {

	private static HashMap<ParticleTexture, ArrayList<Particle>> particles = new HashMap<>();
	private static ParticleRenderer renderer;
	
	public static void init(Loader loader, Matrix4f projectionMatrix){
		renderer = new ParticleRenderer(loader, projectionMatrix);
	}
	public static void update(Camera camera){
		Iterator<Entry<ParticleTexture, ArrayList<Particle>>> mapIterator = particles.entrySet().iterator();
		while(mapIterator.hasNext()){
			ArrayList<Particle> list = mapIterator.next().getValue();
			Iterator<Particle> iterator = list.iterator();
			while(iterator.hasNext()){
				Particle p = iterator.next();
				boolean stillAlive = p.update(camera);
				if(!stillAlive){
					iterator.remove();
					if(list.isEmpty()){
						mapIterator.remove();
					}
				}
			}
			InsertionSort.sortHighToLow(list);
		}
	}
	public static void renderParticles(Camera camera){
		renderer.render(particles, camera);
	}
	public static void cleanUp(){
		renderer.cleanUp();
	}
	public static void addParticle(Particle particle){
		ArrayList<Particle> list = particles.get(particle.getTexture());
		if(list == null){
			list = new ArrayList<Particle>();
			particles.put(particle.getTexture(), list);
		}
		list.add(particle);
	}
}
