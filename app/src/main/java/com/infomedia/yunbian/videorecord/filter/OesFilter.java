package com.infomedia.yunbian.videorecord.filter;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.infomedia.yunbian.videorecord.utils.Glutil;
import com.infomedia.yunbian.videorecord.utils.L;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.infomedia.yunbian.videorecord.utils.L.TAG;

/**
 * Created by pc on 2018/1/11.
 */

public class OesFilter extends BaseFilter {


    public SurfaceTexture surfaceTexture;
    private final int floatSize = 4;
    private float[] glcoord = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};//负负正负负，左下开始
    private float[] texCoord = {0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f};//opengl纹理坐标，左下开始
    private FloatBuffer glcoordbuf;
    private FloatBuffer texcoordbuf;
    private int mvpMatrixHandler, glcoordHandler, texCoordHandler, vTextureHandler;
    private int hProgram;
    int maPositionLoc;
    int maTextureCoordLoc;
    int muMVPMatrixLoc;
    int muTexMatrixLoc;



    public float[] IDENTITY_MATRIX = new float[16];
    ;


    private int mvpTexHandler;


    @Override
    public void init() {


    }

    @Override
    public void onSurfaceCreate() {
        super.onSurfaceCreate();
        String frag = "#extension GL_OES_EGL_image_external : require\n" + "precision mediump float;\n" +
                "varying vec2 textureCoordinate;\n" +
//                "uniform sampler2D vTexture;\n" +
                "uniform samplerExternalOES vTexture;\n" +
                "void main() {\n" +
                "    gl_FragColor = texture2D( vTexture, textureCoordinate );\n" +
                "}";
        String vert = "attribute vec4 vPosition;\n" +
                "attribute vec2 vCoord;\n" +
                "uniform mat4 vMatrix;\n" +
                "uniform mat4 uTexMatrix;\n" +
                "\n" +
                "varying vec2 textureCoordinate;\n" +
                "\n" +
                "void main(){\n" +
                "    gl_Position = vMatrix*vPosition;\n" +
                "    textureCoordinate = (uTexMatrix*vCoord).xy;\n" +
                "}";


          program = loadShader(vert,frag);
        program = Glutil.createProgram(vert, frag);
        textureId = Glutil.createOESTextureObject();
        surfaceTexture = new SurfaceTexture(textureId);
        ByteBuffer b = ByteBuffer.allocateDirect(glcoord.length * floatSize);
        b.order(ByteOrder.nativeOrder());
        glcoordbuf = b.asFloatBuffer();
        glcoordbuf.put(glcoord);
        glcoordbuf.position(0);
        ByteBuffer tb = ByteBuffer.allocateDirect(texCoord.length * floatSize);
        tb.order(ByteOrder.nativeOrder());
        texcoordbuf = tb.asFloatBuffer();
        texcoordbuf.put(texCoord);
        texcoordbuf.position(0);


        mvpMatrixHandler = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        glcoordHandler = GLES20.glGetAttribLocation(program, "aPosition");
        texCoordHandler = GLES20.glGetAttribLocation(program, "aTextureCoord");
//        vTextureHandler =GLES20.glGetAttribLocation(program,"vTexture");
        mvpTexHandler = GLES20.glGetUniformLocation(program, "uTexMatrix");
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
        Matrix.setRotateM(IDENTITY_MATRIX,0,270f,0f,0f,1f);
//        GLES20.glUniformMatrix4fv(mvpTexHandler, 1, false, IDENTITY_MATRIX, 0);
//        GLES20.glUniformMatrix4fv(mvpMatrixHandler, 1, false, IDENTITY_MATRIX, 0);

        GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 8, glcoordbuf);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 8, texcoordbuf);
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);


//        GLES20.glVertexAttribPointer(glcoordHandler, 2, GLES20.GL_FLOAT, false, 8, glcoordbuf);
//        GLES20.glEnableVertexAttribArray(glcoordHandler);
//        GLES20.glVertexAttribPointer(texCoordHandler, 2, GLES20.GL_FLOAT, false, 8, texcoordbuf);
//        GLES20.glEnableVertexAttribArray(texCoordHandler);
    }

    @Override
    public void onSurFaceChanged(int width, int height) {
        super.onSurFaceChanged(width, height);


    }


    /**
     * draw specific texture with specific texture matrix
     *
     * @param tex_id     texture ID
     * @param tex_matrix texture matrix、if this is null, the last one use(we don't check size of this array and needs at least 16 of float)
     */
    public void draw(final int tex_id, final float[] tex_matrix) {
        GLES20.glUseProgram(hProgram);
        if (tex_matrix != null)
            GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, tex_matrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, IDENTITY_MATRIX, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex_id);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glUseProgram(0);
    }

//    @Override
//    public void onDraw(int textid,float data[]) {
//        super.onDraw(data);
////        L.e("onDraw","bbbbbbbbbbbbb");
//        GLES20.glUseProgram(program);
//        if (data != null)
//        GLES20.glUniformMatrix4fv(mvpTexHandler, 1, false, data, 0);
//        GLES20.glUniformMatrix4fv(mvpMatrixHandler, 1, false, IDENTITY_MATRIX, 0);
//
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textid);
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
////        GLES20.glUseProgram(0);
//    }

    @Override
    public void ondestory() {
        super.ondestory();
    }


    /**
     * load, compile and link shader
     *
     * @param vss source of vertex shader
     * @param fss source of fragment shader
     * @return
     */
    public static int loadShader(final String vss, final String fss) {
        L.v(TAG, "loadShader:");
        int vs = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vs, vss);
        GLES20.glCompileShader(vs);
        final int[] compiled = new int[1];
        GLES20.glGetShaderiv(vs, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
             L.e(TAG, "Failed to compile vertex shader:"
                    + GLES20.glGetShaderInfoLog(vs));
            GLES20.glDeleteShader(vs);
            vs = 0;
        }

        int fs = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fs, fss);
        GLES20.glCompileShader(fs);
        GLES20.glGetShaderiv(fs, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
           L.e(TAG, "Failed to compile fragment shader:"
                    + GLES20.glGetShaderInfoLog(fs));
            GLES20.glDeleteShader(fs);
            fs = 0;
        }

        final int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vs);
        GLES20.glAttachShader(program, fs);
        GLES20.glLinkProgram(program);

        return program;
    }

}
