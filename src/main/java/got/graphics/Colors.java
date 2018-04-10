package got.graphics;

import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Created by Souverain73 on 17.04.2017.
 */
public enum Colors {
    WHITE(1.0f, 1.0f, 1.0f),
    BLACK(0.0f, 0.0f, 0.0f),
    RED(1.0f, 0.0f, 0.0f),
    GREEN(0.0f, 1.0f, 0.0f),
    BLUE(0.0f, 0.0f, 1.0f);



    private final float r;
    private final float g;
    private final float b;
    Colors(float r, float g, float b){

        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Vector3f asVector3(){
        return new Vector3f(r, g, b);
    }

    public Vector4f asVector4(){
        return new Vector4f(r, g, b, 0.0f);
    }
}
