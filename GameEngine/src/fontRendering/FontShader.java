package fontRendering;

import shaders.ShaderProgram;

public class FontShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/fontRendering/fontVertex.txt";
	private static final String FRAGMENT_FILE = "/fontRendering/fontFragment.txt";
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		
	}

	@Override
	protected void bindAttributes() {

	}


}
