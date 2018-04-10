package got.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import got.graphics.text.FontBitmap;
import got.graphics.text.Text;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import got.Constants;
import got.utils.Utils;


public class GraphicModule {
	private static GraphicModule _instance = null;

	private int vao;
	private static ICamera camera;
	private static Effect effect;
	private static Matrix4f screenProjection;
	private static Matrix4f projection;
	private static FloatBuffer fbProjection;
	private static int winW, winH;

	private GraphicModule() {
		camera = new Ortho2DCamera();
		camera.lookAt(new Vector3f(600, 600, 0));
		screenProjection = new Matrix4f();
		fbProjection = BufferUtils.createFloatBuffer(16);
		effect = new Effect();
		GraphicModule.resizeCallback(0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
	}

	public void clear(){
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public static GraphicModule instance() {
		if (_instance == null) {
			_instance = new GraphicModule();
		}
		return _instance;
	}
	
	public static void resizeCallback(long window, int w, int h){
		if (window != 0)
			glViewport(0, 0, w, h);
		
		float screenW = Constants.SCREEN_WIDTH;
		float screenH = Constants.SCREEN_HEIGHT;
		
		winW = w;
		winH = h;
		
		//параметры, необходимые для проекции
		float movH = 0, movW = 0;
		float coef;
		
		//Получаем коэффицент для масштабирования.
		coef = winW/screenW;
					
		//Если коэфицент слишком велик, получаем его по другой стороне.
		if (screenH*coef > winH){
			coef = winH/screenH;
			//считаем смещение, для центрирования изображения.
			movW = Math.abs((winW - screenW * coef)) / 2;
		}else{
			movH = Math.abs((winH - screenH * coef)) / 2;
		}
		
		camera.windowResizeCallback(w, h);
		screenProjection.identity().ortho(0, winW, winH, 0, -1, 1).translate(movW, movH, 0).scale(coef, coef, 1);
	}
	
	public void initOpenGl(){
		Texture.init();
		Text.init();
	    vao = glGenVertexArrays();
	    glBindVertexArray(vao);
	    glClearColor(0.0f, 0.0f, 0f, 1.0f);
	}
	
	
	public static int createBuffer(float[] data){
		FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(data.length);
		dataBuffer.put(data);
		dataBuffer.flip();
		
		int resultBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, resultBuffer);
			glBufferData(GL_ARRAY_BUFFER, dataBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		return resultBuffer;
	}
	
	public static int createEmptyBuffer(){
		return glGenBuffers();
	}
	
	public static int setBufferData(int buffer, float[] data){
		FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(data.length);
		dataBuffer.put(data);
		dataBuffer.flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, buffer);
			glBufferData(GL_ARRAY_BUFFER, dataBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		return buffer;
	}
	
	public static void deleteBuffer(int buffer){
		
	}
	
	public static int createShader(int eShaderType, String fnameShaderFile) {
			String strShaderText = null;
			try{
				strShaderText = Utils.readFile(fnameShaderFile);
			}catch (IOException e) {
				e.printStackTrace();
				return 0;
			}
	        int shader = glCreateShader(eShaderType);
	        glShaderSource(shader, strShaderText);

	        glCompileShader(shader);

	        int status = glGetShaderi(shader, GL_COMPILE_STATUS);
	        if (status == 0) {
	          int infoLogLength = glGetShaderi(shader, GL_INFO_LOG_LENGTH);

	          String strInfoLog = glGetShaderInfoLog(shader, infoLogLength);

	          String strShaderType = null;
	         switch (eShaderType) {
	         case GL_VERTEX_SHADER:
	            strShaderType = "vertex";
	            break;
	         case GL_GEOMETRY_SHADER:
	            strShaderType = "geometry";
	            break;
	         case GL_FRAGMENT_SHADER:
	            strShaderType = "fragment";
	            break;
	         }

	          System.err.printf("Compile failure in %s shader:\n%s\n", strShaderType, strInfoLog);
	        }
	        
	      return shader;
	   }
	   	   
	public static int createProgram(ArrayList<Integer> shaderList) {
	      int program = glCreateProgram();

	      for (Integer shader : shaderList) {
	         glAttachShader(program, shader);
	      }

	      glLinkProgram(program);
	      
	      int status = glGetProgrami(program, GL_LINK_STATUS);
	      if (status == 0) {
	         int infoLogLength = glGetProgrami(program, GL_INFO_LOG_LENGTH);

	         String strInfoLog = null;
	         strInfoLog = glGetProgramInfoLog(program, infoLogLength);
	         
	         System.err.printf("Linker failure: %s\n", strInfoLog);
	      }
	      
	      for (Integer shader : shaderList) {
	         glDetachShader(program, shader);
	      }

	      return program;
	   }
	
	public ICamera getCamera(){
		return camera;
	}
	
	public void setEffect(Effect eff){
		effect = eff;
	}
	
	public void resetEffect(){
		effect = new Effect();
	}
	
	public Effect getEffect(){
		return effect;
	}
	
	public Matrix4f getProjection(){
		return projection;
	}
	
	public Matrix4f getScreenProjection() {
		return screenProjection;
	}

	public FloatBuffer getProjectionAsFloatBuffer(){
		return fbProjection;
	}
	
	public void setDrawSpace(DrawSpace space){
		if (space == DrawSpace.WORLD){
			projection = camera.getProjection();
			projection.get(fbProjection);
		}else if(space == DrawSpace.SCREEN){
			projection = screenProjection;
			projection.get(fbProjection);
		}else{
			projection.identity();
			projection.get(fbProjection);
		}
	}
}
