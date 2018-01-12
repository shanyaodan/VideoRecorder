package com.dyc.testbmp;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL;

import static android.opengl.GLES20.glClearColor;

public class Square {

    private final int aTextureCoordinates;
    private  int uTextureUnitLocation;
    private Context context;

    //float类型的字节数
    private static final int BYTES_PER_FLOAT = 4;
    // 数组中每个顶点的坐标数
    static final int COORDS_PER_VERTEX = 2;

    /*------------------第一步: 修改顶点数据-------------------------*/
    //矩形顶点坐标
    static float squareCoords[] = { //以三角形扇的形式绘制
            -0.5f, 0.5f,   // top left
            0.5f, 0.5f, // top right
            0.5f, -0.5f, // bottom right
            -0.5f, -0.5f};  // bottom left

    static float textture[] = {
            0, 0,
            1, 0,
            1, 1,
            0, 1
    };

    static float colorc[] = {
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
    };
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    //------------第一个是顶点着色器的变量名，第二个是片段着色器的变量名
    private static final String A_POSITION = "a_Position";
    private static final String U_COLOR = "u_Color";
    private static final String U_MATRIX = "u_Matrix";
    //------------获得program的ID的含义类似的
//    private int uColorLocation;
    private int aPositionLocation;
    private int uMatrixLocation;
    private int program;//保存program的id

    /*------------------第二步: 修改顶点个数-------------------------*/
    private static final int POSITION_COMPONENT_COUNT = 4;

    float[] projectionMatrix = new float[16];//变换矩阵

    public Square(Context context) {
        this.context = context;
        vertexBuffer = ByteBuffer
                .allocateDirect(squareCoords.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        // 把坐标们加入FloatBuffer中
        vertexBuffer.put(squareCoords);
        // 设置buffer，从第一个坐标开始读
        vertexBuffer.position(0);
        colorBuffer = ByteBuffer.allocateDirect(textture.length*BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer.put(textture);
        colorBuffer.position(0);
        getProgram();
//      uColorLocation =  GLES20.glGetUniformLocation(program,U_COLOR);
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aTextureCoordinates = GLES20.glGetAttribLocation(program, "a_TextureCoordinates");
//        uTextureUnitLocation = GLES20.glGetAttribLocation(program, "u_TextureUnit");
//        uColorLocation = GLES20.glGetAttribLocation(program, U_COLOR);
//        GLES20.glVertexAttribPointer(uColorLocation,4, GLES20.GL_FLOAT,false,4*4,colorBuffer);
//        GLES20.glEnableVertexAttribArray(uColorLocation);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureHelper.loadTexture(context, R.mipmap.ic_launcher));
//        GLES20.glUniform1i(uTextureUnitLocation, 0);

        GLES20.glVertexAttribPointer(aPositionLocation, 2,
                GLES20.GL_FLOAT, false, 4*2, vertexBuffer);
        GLES20.glEnableVertexAttribArray(aPositionLocation);



        GLES20.glVertexAttribPointer(aTextureCoordinates, 2,
                GLES20.GL_FLOAT, false, 4*2, colorBuffer);

        GLES20.glEnableVertexAttribArray(aTextureCoordinates);
    }


    //通过路径加载Assets中的文本内容
    public static String uRes(Resources mRes, String path) {
        StringBuilder result = new StringBuilder();
        try {
            InputStream is = mRes.getAssets().open(path);
            int ch;
            byte[] buffer = new byte[1024];
            while (-1 != (ch = is.read(buffer))) {
                result.append(new String(buffer, 0, ch));
            }
        } catch (Exception e) {
            return null;
        }
        return result.toString().replaceAll("\\r\\n", "\n");
    }


    //获取program
    private void getProgram() {
        //获取顶点着色器文本
        String vertexShaderSource = uRes(context.getResources(), "shader/simple_vertex.sh");
        //获取片段着色器文本
        String fragmentShaderSource = uRes(context.getResources(), "shader/simple_fragment.sh");
        //获取program的id
        program = uCreateGlProgram(vertexShaderSource, fragmentShaderSource);
        GLES20.glUseProgram(program);
    }

    //设置正交投影矩阵
    public void projectionMatrix(int width, int height) {
        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    //以GL_LINE_LOOP方式绘制
    public void draw() {
        GLES20.glEnable( GLES20.GL_BLEND);

        GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA,  GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
//        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        /*------------------第三步: 修改绘制方式-------------------------*/
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, POSITION_COMPONENT_COUNT);
    }


    //创建GL程序
    public static int uCreateGlProgram(String vertexSource, String fragmentSource) {
        int vertex = uLoadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertex == 0) return 0;
        int fragment = uLoadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragment == 0) return 0;
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertex);
            GLES20.glAttachShader(program, fragment);
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
//                glError(1, "Could not link program:" + GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     * 加载shader
     */
    public static int uLoadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (0 != shader) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
//                glError(1, "Could not compile shader:" + shaderType);
//                glError(1, "GLES20 Error:" + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }
}