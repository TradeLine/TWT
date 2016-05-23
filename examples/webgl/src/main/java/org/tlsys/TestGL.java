package org.tlsys;

import org.tlsys.gl.*;
import org.tlsys.gl.webgl.WebGL_ES2;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.dom.DOM;
import org.tlsys.twt.dom.Document;

@JSClass
public class TestGL {
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;

    static WebGL_ES2 gl;
    static float[] vertices = new float[]{
            -1, -1, -1, 1, -1, -1, 1, 1, -1, -1, 1, -1,
            -1, -1, 1, 1, -1, 1, 1, 1, 1, -1, 1, 1,
            -1, -1, -1, -1, 1, -1, -1, 1, 1, -1, -1, 1,
            1, -1, -1, 1, 1, -1, 1, 1, 1, 1, -1, 1,
            -1, -1, -1, -1, -1, 1, 1, -1, 1, 1, -1, -1,
            -1, 1, -1, -1, 1, 1, 1, 1, 1, 1, 1, -1,
    };

    static float[] colors = new float[]{
            5, 3, 7, 5, 3, 7, 5, 3, 7, 5, 3, 7,
            1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3,
            0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,
            1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
            1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0,
            0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0
    };

    static int[] indices = new int[]{
            0, 1, 2, 0, 2, 3, 4, 5, 6, 4, 6, 7,
            8, 9, 10, 8, 10, 11, 12, 13, 14, 12, 14, 15,
            16, 17, 18, 16, 18, 19, 20, 21, 22, 20, 22, 23
    };

    static GLBuffer index_buffer = null;
    static double[] proj_matrix = null;
    static double[] view_matrix = null;
    static GLUniformLocation Pmatrix = null;
    static GLUniformLocation Vmatrix = null;
    static GLUniformLocation Mmatrix = null;
    static double[] mov_matrix = new double[]{1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
    static double time_old = 0;
    static Script.FrameRequest animate = time -> {
        draw(time);
    };

    public static void start() {
        Object body = Script.code(Document.get(), ".body");

        /*============= Creating a canvas =================*/
        gl = new WebGL_ES2();
        DOM.setAttribute(gl, "width", Integer.toString(WIDTH));
        DOM.setAttribute(gl, "height", Integer.toString(HEIGHT));
        DOM.appendChild(body, gl);

        /*============ Defining and storing the geometry =========*/
        // Create and store data into vertex buffer
        GLBuffer vertex_buffer = gl.createBuffer();

        gl.bindBuffer(GL.ARRAY_BUFFER, vertex_buffer);
        gl.bufferData(GL.ARRAY_BUFFER, vertices, GL.STATIC_DRAW);

        // Create and store data into color buffer
        GLBuffer color_buffer = gl.createBuffer();

        gl.bindBuffer(GL.ARRAY_BUFFER, color_buffer);
        gl.bufferData(GL.ARRAY_BUFFER, colors, GL.STATIC_DRAW);

        // Create and store data into index buffer
        index_buffer = gl.createBuffer();
        gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, index_buffer);
        gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, indices, GL.STATIC_DRAW);

        /*=================== Shaders =========================*/
        String vertCode = "attribute vec3 position;" +
                "uniform mat4 Pmatrix;" +
                "uniform mat4 Vmatrix;" +
                "uniform mat4 Mmatrix;" +
                "attribute vec3 color;" +//the color of the point
                "varying vec3 vColor;" +

                "void main(void) { " +//pre-built function
                "gl_Position = Pmatrix*Vmatrix*Mmatrix*vec4(position, 1.);" +
                "vColor = color;" +
                "}";

        String fragCode = "precision mediump float;" +
                "varying vec3 vColor;" +
                "void main(void) {" +
                "gl_FragColor = vec4(vColor, 1.);" +
                "}";

        GLShader vertShader = gl.createShader(GL.VERTEX_SHADER);
        vertShader.setSource(vertCode);
        vertShader.compile();

        GLShader fragShader = gl.createShader(GL.FRAGMENT_SHADER);
        fragShader.setSource(fragCode);
        fragShader.compile();

        GLProgram shaderProgram = gl.createProgram();
        shaderProgram.attach(vertShader);
        shaderProgram.attach(fragShader);
        shaderProgram.link();

        /* ====== Associating attributes to vertex shader =====*/
        Pmatrix = shaderProgram.getUniformLocation("Pmatrix");
        Vmatrix = shaderProgram.getUniformLocation("Vmatrix");
        Mmatrix = shaderProgram.getUniformLocation("Mmatrix");

        gl.bindBuffer(GL.ARRAY_BUFFER, vertex_buffer);
        long position = shaderProgram.getAttribLocation("position");
        gl.vertexAttribPointer(position, 3, GL.FLOAT, false, 0, 0);

        // Position
        gl.enableVertexAttribArray(position);
        gl.bindBuffer(GL.ARRAY_BUFFER, color_buffer);
        long color = shaderProgram.getAttribLocation("color");
        gl.vertexAttribPointer(color, 3, GL.FLOAT, false, 0, 0);

        // Color
        gl.enableVertexAttribArray(color);
        gl.useProgram(shaderProgram);


        proj_matrix = get_projection(40.0, (double) (WIDTH / HEIGHT), 1.0, 100.0);
        view_matrix = new double[]{1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};

        // translating z
        view_matrix[14] = view_matrix[14] - 6;//zoom

        draw(0.0);
    }

    /*================= Drawing ===========================*/
    public static void draw(double time) {
        double dt = time - time_old;
        rotateZ(mov_matrix, dt * 0.005);//time
        rotateY(mov_matrix, dt * 0.002);
        rotateX(mov_matrix, dt * 0.003);
        time_old = time;

        gl.enable(GL.DEPTH_TEST);
        gl.depthFunc(GL.LEQUAL);
        gl.clearColor(0.5f, 0.5f, 0.5f, 0.9f);
        gl.clearDepth(1.0f);

        gl.viewport(0, 0, WIDTH, HEIGHT);
        gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);
        gl.uniformMatrix4fv(Pmatrix, false, proj_matrix);
        gl.uniformMatrix4fv(Vmatrix, false, view_matrix);
        gl.uniformMatrix4fv(Mmatrix, false, mov_matrix);
        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, index_buffer);
        gl.drawElements(GL.TRIANGLES, indices.length, GL.UNSIGNED_SHORT, 0);

        Script.requestAnimationFrame(animate);
    }








    /*==================== MATRIX =====================*/
    public static double[] get_projection(double angle, double a, double zMin, double zMax) {
        double ang = Math.tan((angle * .5) * Math.PI / 180);//angle*.5
        return new double[]{
                0.5 / ang, 0, 0, 0,
                0, 0.5 * a / ang, 0, 0,
                0, 0, -(zMax + zMin) / (zMax - zMin), -1,
                0, 0, (-2 * zMax * zMin) / (zMax - zMin), 0
        };
    }



         /*==================== Rotation ====================*/

    public static void rotateZ(double[] m, double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double mv0 = m[0], mv4 = m[4], mv8 = m[8];

        m[0] = c * m[0] - s * m[1];
        m[4] = c * m[4] - s * m[5];
        m[8] = c * m[8] - s * m[9];

        m[1] = c * m[1] + s * mv0;
        m[5] = c * m[5] + s * mv4;
        m[9] = c * m[9] + s * mv8;
    }

    public static void rotateX(double[] m, double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double mv1 = m[1], mv5 = m[5], mv9 = m[9];

        m[1] = m[1] * c - m[2] * s;
        m[5] = m[5] * c - m[6] * s;
        m[9] = m[9] * c - m[10] * s;

        m[2] = m[2] * c + mv1 * s;
        m[6] = m[6] * c + mv5 * s;
        m[10] = m[10] * c + mv9 * s;
    }

    public static void rotateY(double[] m, double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double mv0 = m[0], mv4 = m[4], mv8 = m[8];

        m[0] = c * m[0] + s * m[2];
        m[4] = c * m[4] + s * m[6];
        m[8] = c * m[8] + s * m[10];

        m[2] = c * m[2] - s * mv0;
        m[6] = c * m[6] - s * mv4;
        m[10] = c * m[10] - s * mv8;
    }
}
