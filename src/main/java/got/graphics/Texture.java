package got.graphics;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import got.Constants;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import got.InputManager;


public class Texture {
	private static final float[] vertexCoords={
			1.0f, 1.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			1.0f, 1.0f, 0.0f
	};
	
	private static final float[] UVCoords = {
			1.0f, 1.0f,
			1.0f, 0.0f,
			0.0f, 0.0f,
			0.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 1.0f
	};
	
	private static final String vertexShaderFileName = "data/shaders/texture.vertexshader";
	private static final String fragmentShaderFileName = "data/shaders/texture.fragmentshader";
	
	private static int vertexCoordsBuffer;
	private static int vertexUVBuffer;
	private static int drawProgram;
	private static int textureSampler;
	private static int mvLocation;
	private static Matrix4f mv;
	private static int projectionLocation;
	private static int overlayLocation;
	private static int multiplyLocation;
	private String filename;
	private int width;
	private int height;
	private int comp;
	private ByteBuffer data;
	
	private int textureID;
	
	public int getID(){
		return textureID;
	}
	
	protected static void init(){
		//init buffers
		  glEnable(GL_TEXTURE_2D);
		  vertexCoordsBuffer = GraphicModule.createBuffer(vertexCoords);
		  vertexUVBuffer = GraphicModule.createBuffer(UVCoords);
		//init program
		  ArrayList<Integer> shaders = new ArrayList<Integer>();
		  shaders.add(GraphicModule.createShader(GL_VERTEX_SHADER, vertexShaderFileName));
		  shaders.add(GraphicModule.createShader(GL_FRAGMENT_SHADER, fragmentShaderFileName));
		  drawProgram = GraphicModule.createProgram(shaders);
		  textureSampler = glGetUniformLocation(drawProgram, "myTextureSampler");
		  mvLocation = glGetUniformLocation(drawProgram, "MV");
		  projectionLocation = glGetUniformLocation(drawProgram, "Proj");
		  overlayLocation = glGetUniformLocation(drawProgram, "overlay");
		  multiplyLocation = glGetUniformLocation(drawProgram, "multiply");
		  mv = new Matrix4f().identity();
	}
	
	public void delete(){
		stbi_image_free(data);
	}
	
	public Texture(String fileName) throws FileNotFoundException {
		IntBuffer x = BufferUtils.createIntBuffer(1);
		IntBuffer y = BufferUtils.createIntBuffer(1);
		IntBuffer comp = BufferUtils.createIntBuffer(1);

		data = stbi_load(fileName, x, y, comp, STBI_rgb_alpha);
		if (data!=null){
			filename = fileName;
			width = x.get();
			height = y.get();
			this.comp = comp.get(); 
			textureID = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, textureID);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glBindTexture(GL_TEXTURE_2D,0);
		}else{
			throw new FileNotFoundException("Can't load texture "+fileName);
		}
	}

	public void draw(float x, float y, float scale){
		draw(x, y, width*scale, height*scale);
	}

	public void draw(float x, float y, float w, float h){
		draw(x, y, w, h, 0);
	}

	public void draw(float x, float y, float w, float h, float z){
		FloatBuffer mvFB = BufferUtils.createFloatBuffer(16);
		FloatBuffer pm;
		mv.identity().translate(x, y, -z).scale(w,h,1).get(mvFB);
		pm = GraphicModule.instance().getProjectionAsFloatBuffer();
		
		glUseProgram(drawProgram);
		
		//apply effects;
		if (GraphicModule.instance().getEffect()!=null){
			Effect ce = GraphicModule.instance().getEffect();
			if (ce.overlay!=null)
				glUniform4f(overlayLocation, ce.overlay.x, ce.overlay.y,  ce.overlay.z, 0);
			else
				glUniform4f(overlayLocation, 0, 0,  0, 0);
			if (ce.multiply!=null)
				glUniform4f(multiplyLocation, ce.multiply.x, ce.multiply.y, ce.multiply.z, 1);
			else
				glUniform4f(multiplyLocation, 1, 1, 1, 1);
		}else{
			glUniform4f(overlayLocation, 0, 0,  0, 0);
			glUniform4f(multiplyLocation, 1, 1, 1, 1);
		}
		
	    glEnable(GL_BLEND);
	    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureID);
		
		glUniform1i(textureSampler, 0);
		glUniformMatrix4fv(mvLocation, false, mvFB);
		glUniformMatrix4fv(projectionLocation, false, pm);
		
		glEnableVertexAttribArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, vertexCoordsBuffer);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		glEnableVertexAttribArray(1);
		glBindBuffer(GL_ARRAY_BUFFER, vertexUVBuffer); 
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		
		glDrawArrays(GL_TRIANGLES, 0, 6);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		
		glDisable(GL_BLEND);
	}
	public void drawTextureById(float x, float y, float w, float h, int textureID){
		FloatBuffer mvFB = BufferUtils.createFloatBuffer(16);
		FloatBuffer pm;
		mv.identity().translate(x, y, 0).scale(w,h,1).get(mvFB);
		pm = GraphicModule.instance().getProjectionAsFloatBuffer();

		glUseProgram(drawProgram);

		//apply effects;
		if (GraphicModule.instance().getEffect()!=null){
			Effect ce = GraphicModule.instance().getEffect();
			if (ce.overlay!=null)
				glUniform4f(overlayLocation, ce.overlay.x, ce.overlay.y,  ce.overlay.z, 0);
			else
				glUniform4f(overlayLocation, 0, 0,  0, 0);
			if (ce.multiply!=null)
				glUniform4f(multiplyLocation, ce.multiply.x, ce.multiply.y, ce.multiply.z, 1);
			else
				glUniform4f(multiplyLocation, 1, 1, 1, 1);
		}else{
			glUniform4f(overlayLocation, 0, 0,  0, 0);
			glUniform4f(multiplyLocation, 1, 1, 1, 1);
		}

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureID);

		glUniform1i(textureSampler, 0);
		glUniformMatrix4fv(mvLocation, false, mvFB);
		glUniformMatrix4fv(projectionLocation, false, pm);

		glEnableVertexAttribArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, vertexCoordsBuffer);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

		glEnableVertexAttribArray(1);
		glBindBuffer(GL_ARRAY_BUFFER, vertexUVBuffer);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

		glDrawArrays(GL_TRIANGLES, 0, 6);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);

		glDisable(GL_BLEND);
	}

	@Override
	public String toString() {
		return "Texture [filename=" + filename + ", width=" + width + ", height=" + height + ", textureID=" + textureID
				+ "]";
	}

	public Vector3f getPixel(int x, int y){
		int pos = (y*width+x)*comp;
		Vector3f res = new Vector3f((data.get(pos)/255.0f), (data.get(pos+1)/255.0f), (data.get(pos+2)/255.0f));
		return res;
	}
	
	public int getAlfa(float tx, float ty){
		if (comp < 4) return 1;
		int x = (int)(tx*width);
		int y = (int)(ty*height);
		int pos = (y*width+x)*4;
		try{
			byte value = data.get(pos+3);
			return value*-1;
		}catch(Exception e){
			System.out.println("Can't get Alfa for texture");
			System.out.println("Texture filename:"+filename);
			System.out.println("Function pos x:"+x+" y:"+y);
			System.out.println("Mouse World pos:"+InputManager.instance().getMousePosWorld());
			System.out.println("Mouse Win pos"+InputManager.instance().getMousePosWin());
		}
		return 0;
	}
	
	public int getWidth(){
		return width;
	}

	
	public int getHeight() {
		return height;
	}
	
}
