package got.graphics.text;

import got.graphics.Colors;
import got.utils.UI;
import org.joml.Vector3f;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static got.utils.IOUtil.ioResourceToByteBuffer;
import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;

/**
 * Created by Souverain73 on 14.04.2017.
 */
public class FontTrueType implements Font{
    private static final String fontsBase = "data/fonts/";
    private float scale;
    private STBTTPackedchar.Buffer chardata;
    private int font_tex;
    private int BITMAP_W;
    private int BITMAP_H;
    private Vector3f color;
    private static final int oversamples = 2;

    private final STBTTAlignedQuad q  = STBTTAlignedQuad.malloc();
    private final FloatBuffer xb = memAllocFloat(1);
    private final FloatBuffer yb = memAllocFloat(1);

    private LocaleEncoder encoder;

    public FontTrueType(String fontFile){
        this(fontFile, 20);
    }

    public FontTrueType(String fontFile, float scale){
        this(fontFile, scale, Colors.WHITE.asVector3());
    }

    public FontTrueType(String fontFile, float scale, Vector3f color){
        this.scale = scale;
        this.BITMAP_W = (int)(scale * 16 * oversamples);
        this.BITMAP_H = (int)(scale * 16 * oversamples);
        this.encoder = new RussianEncoder();
        this.xb.rewind();
        this.yb.rewind();
        this.color = color;
        loadFont(fontsBase+fontFile+".ttf");
    }

    private void loadFont(String resource) {
        font_tex = glGenTextures();
        chardata = STBTTPackedchar.malloc(256);

        try (STBTTPackContext pc = STBTTPackContext.malloc()) {
            ByteBuffer ttf = ioResourceToByteBuffer(resource, 160 * 1024);
            ByteBuffer bitmap = createByteBuffer(BITMAP_W * BITMAP_H);

            stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1);
            chardata.limit(95);
            chardata.position(0);

            stbtt_PackSetOversampling(pc, oversamples, oversamples);
            stbtt_PackFontRange(pc, ttf, 0, scale, 32, chardata);

            chardata.limit(96+64);
            chardata.position(96);
            stbtt_PackFontRange(pc, ttf, 0, scale, 1040, chardata);

            chardata.clear();
            stbtt_PackEnd(pc);

            glBindTexture(GL_TEXTURE_2D, font_tex);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        } catch (IOException e) {
            UI.systemMessage("Can't read font data for " + resource);
        }
    }

    @Override
    public Text newText(String text){
        xb.put(0, 0).rewind();
        yb.put(0, 0).rewind();
        int length = text.length();

        float [] UV = new float[length*12];
        float [] pos = new float[length*18];

        chardata.position(0);

        for ( int i = 0; i < text.length(); i++ ) {
            stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, encoder.encode(text.charAt(i)), xb, yb, q, false);
            addGlyphUV(q, i, UV);
            addGlyphPos(q, i, pos);
        }

        return new Text(pos, UV, length, this, color);
    }

    private void addGlyphUV(STBTTAlignedQuad q, int index, float[] UV){
        float top = q.t0();
        float left = q.s0();
        float bottom = q.t1();
        float right = q.s1();


        int i = index * 12;  //One glyph needs two triangles. One triangle needs 3 points. One point need 2 floats. 2*3*2 = 12

        UV[i++] = right;	UV[i++] = bottom;
        UV[i++] = right; 	UV[i++] = top;
        UV[i++] = left; 	UV[i++] = top;
        UV[i++] = left; 	UV[i++] = top;
        UV[i++] = left; 	UV[i++] = bottom;
        UV[i++] = right;	UV[i++] = bottom;
    }

    private void addGlyphPos(STBTTAlignedQuad q, int index, float[] pos){
        float top = q.y0() + scale;
        float left = q.x0();
        float bottom = q.y1() + scale;
        float right = q.x1();

        int i = index * 18;	//One glyph needs two triangles. One triangle needs 3 points. One point need 3 floats. 2*3*3 = 18


        pos[i++] = right;	pos[i++] = bottom; 	pos[i++] = 0;
        pos[i++] = right; 	pos[i++] = top;		pos[i++] = 0;
        pos[i++] = left;	pos[i++] = top;		pos[i++] = 0;
        pos[i++] = left; 	pos[i++] = top;		pos[i++] = 0;
        pos[i++] = left; 	pos[i++] = bottom;	pos[i++] = 0;
        pos[i++] = right;	pos[i++] = bottom;	pos[i++] = 0;
    }


    public int getStringWidth(String text){
        xb.put(0, 0).rewind();
        yb.put(0, 0).rewind();
        for (int i = 0; i< text.length(); i++){
            stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, encoder.encode(text.charAt(i)), xb, yb, q, false);
        }
        return (int)xb.get(0);
    }


    public List<String> splitForWidth(String str, int width){
        List<String> result = new ArrayList<>();

        while(true){
            int pos = splitPositionByWords(str, width);
            if (pos == -1) {
                result.add(str);
                break;
            }
            else{
                result.add(str.substring(0, pos++));
                if (pos == str.length()) break;
                str = str.substring(pos);
            }
        }
        return result;
    }

    /**
     * @param str - string to split
     * @param width - needed string width
     * @return -1 if given string width < given width or no spaces found
     */
    private int splitPositionByWords(String str, int width){
        xb.put(0, 0).rewind();
        yb.put(0, 0).rewind();
        int lastSpace = 0;
        int pos = -1;
        int spaceCode = 32;

        while(pos < str.length()-1) {
            pos++;
            if (str.charAt(pos) == spaceCode) {
                lastSpace = pos;
            }
            if (str.charAt(pos) == '\n'){
                lastSpace = pos;
                break;
            }
            stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, encoder.encode(str.charAt(pos)), xb, yb, q, false);
            if (xb.get(0) > width) break;
        }

        return (pos == str.length()-1 || lastSpace == 0) ? -1 : lastSpace;
    }


    @Override
    public int getSize() {
        return (int)scale;
    }

    @Override
    public void changeText(Text text, String newText) {
        xb.put(0, 0).rewind();
        yb.put(0, 0).rewind();

        int length = newText.length();

        float [] UV = new float[length*12];
        float [] pos = new float[length*18];

        chardata.position(0);

        for ( int i = 0; i < length; i++ ) {
            stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, encoder.encode(newText.charAt(i)), xb, yb, q, false);
            addGlyphUV(q, i, UV);
            addGlyphPos(q, i, pos);
        }

        text.setGlyphs(length);
        text.setUVCoords(UV);
        text.setVertexCoords(pos);
    }

    @Override
    public int getTextureID() {
        return font_tex;
    }

    private interface LocaleEncoder{
        char encode(char charCode);
    }

    public static class RussianEncoder implements LocaleEncoder {
        @Override
        public char encode(char charCode) {
            if (charCode < 1040) return  (char) (charCode - 32);
            if (charCode > 1039 && charCode < 1104){
                return (char) (charCode - 944);
            }
            return charCode;
        }
    }
}
