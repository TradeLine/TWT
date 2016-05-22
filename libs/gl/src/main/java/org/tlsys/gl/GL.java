package org.tlsys.gl;


import org.tlsys.twt.annotations.JSClass;

@JSClass
public interface GL {

    //----------------Clearing buffers----------------//
    /**
     * Passed to clear to clear the current depth buffer.
     */
    public final static int DEPTH_BUFFER_BIT = 0x00000100;

    /**
     * Passed to clear to clear the current stencil buffer.
     */
    public final static int COLOR_BUFFER_BIT = 0x00004000;

    /**
     * Passed to clear to clear the current color buffer.
     */
    public final static int STENCIL_BUFFER_BIT = 0x00004000;


    //----------------Rendering primitives----------------//
    /**
     * Passed to drawElements or drawArrays to draw single points.
     */
    public final static int POINTS = 0x0000;

    /**
     * Passed to drawElements or drawArrays to draw lines. Each vertex connects to the one after it.
     */
    public final static int LINES = 0x0001;

    /**
     * Passed to drawElements or drawArrays to draw lines. Each set of two vertices is treated as a separate line segment.
     */
    public final static int LINE_LOOP = 0x0002;

    /**
     * Passed to drawElements or drawArrays to draw a connected group of line segments from the first vertex to the last.
     */
    public final static int LINE_STRIP = 0x0003;

    /**
     * Passed to drawElements or drawArrays to draw triangles. Each set of three vertices creates a separate triangle.
     */
    public final static int TRIANGLES = 0x0004;

    /**
     * Passed to drawElements or drawArrays to draw a connected group of triangles.
     */
    public final static int TRIANGLE_STRIP = 0x0005;

    /**
     * Passed to drawElements or drawArrays to draw a connected group of triangles. Each vertex connects to the previous and the first vertex in the fan.
     */
    public final static int TRIANGLE_FAN = 0x0006;


    //----------------Blending modes----------------//

    /**
     * Passed to blendFunc or blendFuncSeparate to turn off a component.
     */
    public final static int ZERO = 0;

    /**
     * Passed to blendFunc or blendFuncSeparate to turn on a component.
     */
    public final static int ONE = 1;

    /**
     * Passed to blendFunc or blendFuncSeparate to multiply a component by the source elements color.
     */
    public final static int SRC_COLOR = 0x0300;

    /**
     * Passed to blendFunc or blendFuncSeparate to multiply a component by one minus the source elements color.
     */
    public final static int ONE_MINUS_SRC_COLOR = 0x0301;

    /**
     * Passed to blendFunc or blendFuncSeparate to multiply a component by the source's alpha.
     */
    public final static int SRC_ALPHA = 0x0302;

    /**
     * Passed to blendFunc or blendFuncSeparate to multiply a component by one minus the source's alpha.
     */
    public final static int ONE_MINUS_SRC_ALPHA = 0x0303;

    /**
     * Passed to blendFunc or blendFuncSeparate to multiply a component by the destination's alpha.
     */
    public final static int DST_ALPHA = 0x0304;

    /**
     * Passed to blendFunc or blendFuncSeparate to multiply a component by one minus the destination's alpha.
     */
    public final static int ONE_MINUS_DST_ALPHA = 0x0305;

    /**
     * Passed to blendFunc or blendFuncSeparate to multiply a component by the destination's color.
     */
    public final static int DST_COLOR = 0x0306;

    /**
     * Passed to blendFunc or blendFuncSeparate to multiply a component by one minus the destination's color.
     */
    public final static int ONE_MINUS_DST_COLOR = 0x0307;

    /**
     * Passed to blendFunc or blendFuncSeparate to multiply a component by the minimum of source's alpha or one minus the destination's alpha.
     */
    public final static int SRC_ALPHA_SATURATE = 0x0308;

    /**
     * Passed to blendFunc or blendFuncSeparate to specify a constant color blend function.
     */
    public final static int CONSTANT_COLOR = 0x8001;

    /**
     * Passed to blendFunc or blendFuncSeparate to specify one minus a constant color blend function.
     */
    public final static int ONE_MINUS_CONSTANT_COLOR = 0x8002;

    /**
     * Passed to blendFunc or blendFuncSeparate to specify a constant alpha blend function.
     */
    public final static int CONSTANT_ALPHA = 0x8003;

    /**
     * Passed to blendFunc or blendFuncSeparate to specify one minus a constant alpha blend function.
     */
    public final static int ONE_MINUS_CONSTANT_ALPHA = 0x8004;

    //----------------Blending equations----------------//
    /**
     * Passed to blendEquation or blendEquationSeparate to set an addition blend function.
     */
    public final static int FUNC_ADD = 0x8006;

    /**
     * Passed to blendEquation or blendEquationSeparate to specify a subtraction blend function (source - destination).
     */
    public final static int FUNC_SUBSTRACT = 0x800A;

    /**
     * Passed to blendEquation or blendEquationSeparate to specify a reverse subtraction blend function (destination - source).
     */
    public final static int FUNC_REVERSE_SUBTRACT = 0x800B;

    //----------------Getting GL parameter information----------------//

    /**
     * Passed to getParameter to get the current RGB blend function.
     */
    public final static int BLEND_EQUATION = 0x8009;

    /**
     * Passed to getParameter to get the current RGB blend function. Same as BLEND_EQUATION
     */
    public final static int BLEND_EQUATION_RGB = 0x8009;

    /**
     * Passed to getParameter to get the current alpha blend function. Same as BLEND_EQUATION
     */
    public final static int BLEND_EQUATION_ALPHA = 0x883D;

    /**
     * Passed to getParameter to get the current destination RGB blend function.
     */
    public final static int BLEND_DST_RGB = 0x80C8;

    /**
     * Passed to getParameter to get the current destination RGB blend function.
     */
    public final static int BLEND_SRC_RGB = 0x80C9;

    /**
     * Passed to getParameter to get the current destination alpha blend function.
     */
    public final static int BLEND_DST_ALPHA = 0x80CA;

    /**
     * Passed to getParameter to get the current source alpha blend function.
     */
    public final static int BLEND_SRC_ALPHA = 0x80CB;

    /**
     * Passed to getParameter to return a the current blend color.
     */
    public final static int BLEND_COLOR = 0x8005;

    /**
     * Passed to getParameter to get the array buffer binding.
     */
    public final static int ARRAY_BUFFER_BINDING = 0x8894;

    /**
     * Passed to getParameter to get the current element array buffer.
     */
    public final static int ELEMENT_ARRAY_BUFFER_BINDING = 0x8895;

    /**
     * Passed to getParameter to get the current lineWidth (set by the lineWidth method).
     */
    public final static int LINE_WIDTH = 0x0B21;

    /**
     * Passed to getParameter to get the current size of a point drawn with gl.POINTS
     */
    public final static int ALIASED_POINT_SIZE_RANGE = 0x846D;

    /**
     * Passed to getParameter to get the range of available widths for a line. Returns a length-2 array with the lo value at 0, and hight at 1.
     */
    public final static int ALIASED_LINE_WIDTH_RANGE = 0x846E;

    /**
     * Passed to getParameter to get the current value of cullFace. Should return FRONT, BACK, or FRONT_AND_BACK
     */
    public final static int CULL_FACE_MODE = 0x0B45;

    /**
     * Passed to getParameter to determine the current value of frontFace. Should return CW or CCW.
     */
    public final static int FRONT_FACE = 0x0B46;

    /**
     * Passed to getParameter to return a length-2 array of floats giving the current depth range.
     */
    public final static int DEPTH_RANGE = 0x0B70;

    /**
     * Passed to getParameter to determine if the depth write mask is enabled.
     */
    public final static int DEPTH_WRITEMASK = 0x0B72;

    /**
     * Passed to getParameter to determine the current depth clear value.
     */
    public final static int DEPTH_CLEAR_VALUE = 0x0B73;

    /**
     * Passed to getParameter to get the current depth function. Returns NEVER, ALWAYS, LESS, EQUAL, LEQUAL, GREATER, GEQUAL, or NOTEQUAL.
     */
    public final static int DEPTH_FUNC = 0x0B74;

    /**
     * Passed to getParameter to get the value the stencil will be cleared to.
     */
    public final static int STENCIL_CLEAR_VALUE = 0x0B91;

    /**
     * Passed to getParameter to get the current stencil function. Returns NEVER, ALWAYS, LESS, EQUAL, LEQUAL, GREATER, GEQUAL, or NOTEQUAL.
     */
    public final static int STENCIL_FUNC = 0x0B92;

    /**
     * Passed to getParameter to get the current stencil fail function. Should return KEEP, REPLACE, INCR, DECR, INVERT, INCR_WRAP, or DECR_WRAP.
     */
    public final static int STENCIL_FAIL = 0x0B94;

    /**
     * Passed to getParameter to get the current stencil fail function should the depth buffer test fail. Should return KEEP, REPLACE, INCR, DECR, INVERT, INCR_WRAP, or DECR_WRAP.
     */
    public final static int STENCIL_PASS_DEPTH_FAIL = 0x0B95;

    /**
     * Passed to getParameter to get the current stencil fail function should the depth buffer test pass. Should return KEEP, REPLACE, INCR, DECR, INVERT, INCR_WRAP, or DECR_WRAP.
     */
    public final static int STENCIL_PASS_DEPTH_PASS = 0x0B96;

    /**
     * Passed to getParameter to get the reference value used for stencil tests.
     */
    public final static int STENCIL_REF = 0x0B97;
    public final static int STENCIL_VALUE_MASK = 0x0B93;
    public final static int STENCIL_WRITEMASK = 0x0B98;
    public final static int STENCIL_BACK_FUNC = 0x8800;
    public final static int STENCIL_BACK_FAIL = 0x8801;
    public final static int STENCIL_BACK_PASS_DEPTH_FAIL = 0x8802;
    public final static int STENCIL_BACK_PASS_DEPTH_PASS = 0x8803;
    public final static int STENCIL_BACK_REF = 0x8CA3;
    public final static int STENCIL_BACK_VALUE_MASK = 0x8CA4;
    public final static int STENCIL_BACK_WRITEMASK = 0x8CA5;

    /**
     * Returns an Int32Array with four elements for the current viewport dimensions.
     */
    public final static int VIEWPORT = 0x0BA2;

    /**
     * Returns an Int32Array with four elements for the current scissor box dimensions.
     */
    public final static int SCISSOR_BOX = 0x0C10;
    public final static int COLOR_CLEAR_VALUE = 0x0C22;
    public final static int COLOR_WRITEMASK = 0x0C23;
    public final static int UNPACK_ALIGNMENT = 0x0CF5;
    public final static int PACK_ALIGNMENT = 0x0D05;
    public final static int MAX_TEXTURE_SIZE = 0x0D33;
    public final static int MAX_VIEWPORT_DIMS = 0x0D3A;
    public final static int SUBPIXEL_BITS = 0x0D50;
    public final static int RED_BITS = 0x0D52;
    public final static int GREEN_BITS = 0x0D53;
    public final static int BLUE_BITS = 0x0D54;
    public final static int ALPHA_BITS = 0x0D55;
    public final static int DEPTH_BITS = 0x0D56;
    public final static int STENCIL_BITS = 0x0D57;
    public final static int POLYGON_OFFSET_UNITS = 0x2A00;
    public final static int POLYGON_OFFSET_FACTOR = 0x8038;
    public final static int TEXTURE_BINDING_2D = 0x8069;
    public final static int SAMPLE_BUFFERS = 0x80A8;
    public final static int SAMPLES = 0x80A9;
    public final static int SAMPLE_COVERAGE_VALUE = 0x80AA;
    public final static int SAMPLE_COVERAGE_INVERT = 0x80AB;
    public final static int COMPRESSED_TEXTURE_FORMATS = 0x86A3;
    public final static int VENDOR = 0x1F00;
    public final static int RENDERER = 0x1F01;
    public final static int VERSION = 0x1F02;
    public final static int IMPLEMENTATION_COLOR_READ_TYPE = 0x8B9A;
    public final static int IMPLEMENTATION_COLOR_READ_FORMAT = 0x8B9B;
    public final static int BROWSER_DEFAULT_WEBGL = 0x9244;

    //----------------Buffers----------------//

    /**
     * Passed to bufferData as a hint about whether the contents of the buffer are likely to be used often and not change often.
     */
    public final static int STATIC_DRAW = 0x88E4;

    /**
     * Passed to bufferData as a hint about whether the contents of the buffer are likely to not be used often.
     */
    public final static int STREAM_DRAW = 0x88E0;

    /**
     * Passed to bufferData as a hint about whether the contents of the buffer are likely to be used often and change often.
     */
    public final static int DYNAMIC_DRAW = 0x88E8;

    /**
     * Passed to bindBuffer or bufferData to specify the type of buffer being used.
     */
    public final static int ARRAY_BUFFER = 0x8892;

    /**
     * Passed to bindBuffer or bufferData to specify the type of buffer being used.
     */
    public final static int ELEMENT_ARRAY_BUFFER = 0x8893;

    /**
     * Passed to getBufferParameter to get a buffer's size.
     */
    public final static int BUFFER_SIZE = 0x8764;

    /**
     * Passed to getBufferParameter to get the hint for the buffer passed in when it was created.
     */
    public final static int BUFFER_USAGE = 0x8765;

    //----------------Vertex attributes----------------//

    /**
     * Passed to getVertexAttrib to read back the current vertex attribute.
     */
    public final static int CURRENT_VERTEX_ATTRIB = 0x8626;
    public final static int VERTEX_ATTRIB_ARRAY_ENABLED = 0x8622;
    public final static int VERTEX_ATTRIB_ARRAY_SIZE = 0x8623;
    public final static int VERTEX_ATTRIB_ARRAY_STRIDE = 0x8624;
    public final static int VERTEX_ATTRIB_ARRAY_TYPE = 0x8625;
    public final static int VERTEX_ATTRIB_ARRAY_NORMALIZED = 0x886A;
    public final static int VERTEX_ATTRIB_ARRAY_POINTER = 0x8645;
    public final static int VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 0x889F;

    //----------------Culling----------------//

    /**
     * Passed to enable/disable to turn on/off culling. Can also be used with getParameter to find the current culling method.
     */
    public final static int CULL_FACE = 0x0B44;

    /**
     * Passed to cullFace to specify that only front faces should be drawn.
     */
    public final static int FRONT = 0x0404;

    /**
     * Passed to cullFace to specify that only back faces should be drawn.
     */
    public final static int BACK = 0x0405;

    /**
     * Passed to cullFace to specify that front and back faces should be drawn.
     */
    public final static int FRONT_AND_BACK = 0x0408;

    //----------------Shaders----------------//
    /**
     * Passed to createShader to define a fragment shader.
     */
    public final static int FRAGMENT_SHADER = 0x8B30;

    /**
     * Passed to createShader to define a vertex shader
     */
    public final static int VERTEX_SHADER = 0x8B31;

    /**
     * Passed to getShaderParamter to get the status of the compilation. Returns false if the shader was not compiled. You can then query getShaderInfoLog to find the exact error
     */
    public final static int COMPILE_STATUS = 0x8B81;

    /**
     * Passed to getShaderParamter to determine if a shader was deleted via deleteShader. Returns true if it was, false otherwise.
     */
    public final static int DELETE_STATUS = 0x8B80;

    /**
     * Passed to getProgramParameter after calling linkProgram to determine if a program was linked correctly. Returns false if there were errors. Use getProgramInfoLog to find the exact error.
     */
    public final static int LINK_STATUS = 0x8B82;

    /**
     * Passed to getProgramParameter after calling validateProgram to determine if it is valid. Returns false if errors were found.
     */
    public final static int VALIDATE_STATUS = 0x8B83;

    /**
     * Passed to getProgramParameter after calling attachShader to determine if the shader was attached correctly. Returns false if errors occurred.
     */
    public final static int ATTACHED_SHADERS = 0x8B85;

    /**
     * Passed to getProgramParameter to get the number of attributes active in a program.
     */
    public final static int ACTIVE_ATTRIBUTES = 0x8B89;

    /**
     * Passed to getProgramParamter to get the number of uniforms active in a program.
     */
    public final static int ACTIVE_UNIFORMS = 0x8B86;
    public final static int MAX_VERTEX_ATTRIBS = 0x8869;
    public final static int MAX_VERTEX_UNIFORM_VECTORS = 0x8DFB;
    public final static int MAX_VARYING_VECTORS = 0x8DFC;
    public final static int MAX_COMBINED_TEXTURE_IMAGE_UNITS = 0x8B4D;
    public final static int MAX_VERTEX_TEXTURE_IMAGE_UNITS = 0x8B4C;

    /**
     * Implementation dependent number of maximum texture units. At least 8.
     */
    public final static int MAX_TEXTURE_IMAGE_UNITS = 0x8872;
    public final static int MAX_FRAGMENT_UNIFORM_VECTORS = 0x8DFD;
    public final static int SHADER_TYPE = 0x8B4F;
    public final static int SHADING_LANGUAGE_VERSION = 0x8B8C;
    public final static int CURRENT_PROGRAM = 0x8B8D;

    //----------------Uniform types----------------//

    public final static int FLOAT_VEC2 = 0x8B50;
    public final static int FLOAT_VEC3 = 0x8B51;
    public final static int FLOAT_VEC4 = 0x8B52;
    public final static int INT_VEC2 = 0x8B53;
    public final static int INT_VEC3 = 0x8B54;
    public final static int INT_VEC4 = 0x8B55;
    public final static int BOOL = 0x8B56;
    public final static int BOOL_VEC2 = 0x8B57;
    public final static int BOOL_VEC3 = 0x8B58;
    public final static int BOOL_VEC4 = 0x8B59;
    public final static int FLOAT_MAT2 = 0x8B5A;
    public final static int FLOAT_MAT3 = 0x8B5B;
    public final static int FLOAT_MAT4 = 0x8B5C;
    public final static int SAMPLER_2D = 0x8B5E;
    public final static int SAMPLER_CUBE = 0x8B60;

    //----------------Data types----------------//

    public final static int BYTE = 0x1400;
    public final static int UNSIGNED_BYTE = 0x1401;
    public final static int SHORT = 0x1402;
    public final static int UNSIGNED_SHORT = 0x1403;
    public final static int INT = 0x1404;
    public final static int UNSIGNED_INT = 0x1405;
    public final static int FLOAT = 0x1406;

    //----------------Enabling and disabling----------------//
    /**
     * Passed to enable/disable to turn on/off blending. Can also be used with getParameter to find the current blending method.
     */
    public final static int BLEND = 0x0BE2;

    /**
     * Passed to enable/disable to turn on/off the depth test. Can also be used with getParameter to query the depth test.
     */
    public final static int DEPTH_TEST = 0x0B71;

    /**
     * Passed to enable/disable to turn on/off dithering. Can also be used with getParameter to find the current dithering method.
     */
    public final static int DITHER = 0x0BD0;

    /**
     * Passed to enable/disable to turn on/off the polygon offset. Useful for rendering hidden-line images, decals, and or solids with highlighted edges. Can also be used with getParameter to query the scissor test.
     */
    public final static int POLYGON_OFFSET_FILL = 0x8037;

    /**
     * Passed to enable/disable to turn on/off the alpha to coverage. Used in multi-sampling alpha channels.
     */
    public final static int SAMPLE_ALPHA_TO_COVERAGE = 0x809E;

    /**
     * Passed to enable/disable to turn on/off the sample coverage. Used in multi-sampling.
     */
    public final static int SAMPLE_COVERAGE = 0x80A0;

    /**
     * Passed to enable/disable to turn on/off the scissor test. Can also be used with getParameter to query the scissor test.
     */
    public final static int SCISSOR_TEST = 0x0C11;

    /**
     * Passed to enable/disable to turn on/off the stencil test. Can also be used with getParameter to query the stencil test.
     */
    public final static int STENCIL_TEST = 0x0B90;

    //----------------Depth or stencil tests----------------//
    /**
     * Passed to depthFunction or stencilFunction to specify depth or stencil tests will never pass. i.e. Nothing will be drawn.
     */
    public final static int NEVER = 0x0200;

    /**
     * Passed to depthFunction or stencilFunction to specify depth or stencil tests will always pass. i.e. Pixels will be drawn in the order they are drawn.
     */
    public final static int ALWAYS = 0x0207;

    /**
     * Passed to depthFunction or stencilFunction to specify depth or stencil tests will pass if the new depth value is less than the stored value.
     */
    public final static int LESS = 0x0201;

    /**
     * Passed to depthFunction or stencilFunction to specify depth or stencil tests will pass if the new depth value is equals to the stored value.
     */
    public final static int EQUAL = 0x0202;

    /**
     * Passed to depthFunction or stencilFunction to specify depth or stencil tests will pass if the new depth value is less than or equal to the stored value.
     */
    public final static int LEQUAL = 0x0203;

    /**
     * Passed to depthFunction or stencilFunction to specify depth or stencil tests will pass if the new depth value is greater than the stored value.
     */
    public final static int GREATER = 0x0204;

    /**
     * Passed to depthFunction or stencilFunction to specify depth or stencil tests will pass if the new depth value is greater than or equal to the stored value.
     */
    public final static int GEQUAL = 0x0206;

    /**
     * Passed to depthFunction or stencilFunction to specify depth or stencil tests will pass if the new depth value is not equal to the stored value.
     */
    public final static int NOTEQUAL = 0x0205;

    //----------------Textures----------------//
    public final static int NEAREST = 0x2600;
    public final static int LINEAR = 0x2601;
    public final static int NEAREST_MIPMAP_NEAREST = 0x2700;
    public final static int LINEAR_MIPMAP_NEAREST = 0x2701;
    public final static int NEAREST_MIPMAP_LINEAR = 0x2702;
    public final static int LINEAR_MIPMAP_LINEAR = 0x2703;
    public final static int TEXTURE_MAG_FILTER = 0x2800;
    public final static int TEXTURE_MIN_FILTER = 0x2801;
    public final static int TEXTURE_WRAP_S = 0x2802;
    public final static int TEXTURE_WRAP_T = 0x2803;
    public final static int TEXTURE_2D = 0x0DE1;
    public final static int TEXTURE = 0x1702;
    public final static int TEXTURE_CUBE_MAP = 0x8513;
    public final static int TEXTURE_BINDING_CUBE_MAP = 0x8514;
    public final static int TEXTURE_CUBE_MAP_POSITIVE_X = 0x8515;
    public final static int TEXTURE_CUBE_MAP_NEGATIVE_X = 0x8516;
    public final static int TEXTURE_CUBE_MAP_POSITIVE_Y = 0x8517;
    public final static int TEXTURE_CUBE_MAP_NEGATIVE_Y = 0x8518;
    public final static int TEXTURE_CUBE_MAP_POSITIVE_Z = 0x8519;
    public final static int TEXTURE_CUBE_MAP_NEGATIVE_Z = 0x851A;
    public final static int MAX_CUBE_MAP_TEXTURE_SIZE = 0x851C;

    /**
     * A texture unit.
     */
    //public final static int TEXTURE0 - 31=0x84C0 - 0x84DF;

    /**
     * The current active texture unit.
     */
    public final static int ACTIVE_TEXTURE = 0x84E0;
    public final static int REPEAT = 0x2901;
    public final static int CLAMP_TO_EDGE = 0x812F;
    public final static int MIRRORED_REPEAT = 0x8370;

    //----------------Pixel formats----------------//
    public final static int DEPTH_COMPONENT = 0x1902;
    public final static int ALPHA = 0x1906;
    public final static int RGB = 0x1907;
    public final static int RGBA = 0x1908;
    public final static int LUMINANCE = 0x1909;
    public final static int LUMINANCE_ALPHA = 0x190A;


    GLBuffer createBuffer();

    public void bindBuffer(int target, GLBuffer buffer);

    public void bufferData(int target, float[] data, int usage);

    public void bufferData(int target, int[] data, int usage);

    GLShader createShader(int type);

    public default void shaderSource(GLShader shader, String source) {
        shader.setSource(source);
    }

    public default void compileShader(GLShader shader) {
        shader.compile();
    }

    public default void linkProgram(GLProgram program) {
        program.link();
    }

    public GLProgram createProgram();

    public default void attachShader(GLProgram program, GLShader shader) {
        program.attach(shader);
    }

    public default GLUniformLocation getUniformLocation(GLProgram program, String name) {
        return program.getUniformLocation(name);
    }

    public default long getAttribLocation(GLProgram program, String name) {
        return program.getAttribLocation(name);
    }

    public void vertexAttribPointer(long index, long size, long type, boolean normalized, long stride, long offset);

    public void enableVertexAttribArray(long index);

    public void useProgram(GLProgram program);

    public void enable(long cap);

    public void depthFunc(long func);

    public void clearColor(float red, float green, float blue, float alpha);

    public void clearDepth(float depth);

    public void uniformMatrix4fv(GLUniformLocation location, boolean transpose, double[] value);

    public void viewport(long x, long y, long width, long height);

    public void clear(long mask);

    public void drawElements(long mode, long count, long type, long offset);

    public GLTexture createTexture();

    public default void deleteTexture(GLTexture texture) {
        texture.delete();
    }

}
