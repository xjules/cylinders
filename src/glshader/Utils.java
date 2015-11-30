package glshader;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import static com.jogamp.opengl.GL4bc.*;
import com.jogamp.opengl.GL4;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 *
 * @author Adam Jurčík <xjurc@fi.muni.cz>
 */
public class Utils {
    
    public static final int INVALID_LOCATION = -1;
    
    private static final Vector4f TRANSPARENT_YELLOW = new Vector4f(1f, 1f, 0f, 0.3f);
    private static final IntBuffer COUNTER_DATA = Buffers.newDirectIntBuffer(1);
    
    public static int loadShader(GL4 gl, String filename, int shaderType) throws IOException {
        String source = readFile(Utils.class.getResourceAsStream(filename));
        int shader = gl.glCreateShader(shaderType);
        
        // Create and compile GLSL shader
        gl.glShaderSource(shader, 1, new String[] { source }, new int[] { source.length() }, 0);
        gl.glCompileShader(shader);
        
        // Check GLSL shader compile status
        int[] status = new int[1];
        gl.glGetShaderiv(shader, GL_COMPILE_STATUS, status, 0);
        if (status[0] == GL_FALSE) {
            int[] length = new int[1];
            gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, length, 0);
            
            byte[] log = new byte[length[0]];
            gl.glGetShaderInfoLog(shader, length[0], length, 0, log, 0);
            
            String error = new String(log, 0, length[0]);
            System.err.print(filename + ": ");
            System.err.println(error);
        }
        
        return shader;
    }
    
    public static int loadProgram(GL4 gl, String vertexShaderFN, String fragmentShaderFN) throws IOException {
        // Load frament and vertex shaders (GLSL)
	int vs = loadShader(gl, vertexShaderFN, GL_VERTEX_SHADER);
	int fs = loadShader(gl, fragmentShaderFN, GL_FRAGMENT_SHADER);
        
	// Create GLSL program, attach shaders and compile it
	int program = gl.glCreateProgram();
	gl.glAttachShader(program, vs);
	gl.glAttachShader(program, fs);
	gl.glLinkProgram(program);
        
        int[] linkStatus = new int[1];
        gl.glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] == GL_FALSE) {
            int[] length = new int[1];
            gl.glGetProgramiv(program, GL_INFO_LOG_LENGTH, length, 0);
            
            byte[] log = new byte[length[0]];
            gl.glGetProgramInfoLog(program, length[0], length, 0, log, 0);
            
            String error = new String(log, 0, length[0]);
            System.err.println(error);
        }
        
        return program;
    }
    
    public static int loadProgram(GL4 gl, String vertexShaderFN, String geometryShaderFN, String fragmentShaderFN)
            throws IOException {
        // Load frament and vertex shaders (GLSL)
	int vs = loadShader(gl, vertexShaderFN, GL_VERTEX_SHADER);
        int gs = loadShader(gl, geometryShaderFN, GL_GEOMETRY_SHADER);
	int fs = loadShader(gl, fragmentShaderFN, GL_FRAGMENT_SHADER);
        
	// Create GLSL program, attach shaders and compile it
	int program = gl.glCreateProgram();
	gl.glAttachShader(program, vs);
        gl.glAttachShader(program, gs);
	gl.glAttachShader(program, fs);
	gl.glLinkProgram(program);
        
        int[] linkStatus = new int[1];
        gl.glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] == GL_FALSE) {
            int[] length = new int[1];
            gl.glGetProgramiv(program, GL_INFO_LOG_LENGTH, length, 0);
            
            byte[] log = new byte[length[0]];
            gl.glGetProgramInfoLog(program, length[0], length, 0, log, 0);
            
            String error = new String(log, 0, length[0]);
            System.err.println(error);
        }
        
        return program;
    }
    
    public static int loadComputeProgram(GL4 gl, String computeShaderFN) throws IOException {
        // Load compute shader (GLSL)
	int cs = loadShader(gl, computeShaderFN, GL_COMPUTE_SHADER);
        
	// Create GLSL program, attach shaders and compile it
	int program = gl.glCreateProgram();
	gl.glAttachShader(program, cs);
	gl.glLinkProgram(program);
        
        int[] linkStatus = new int[1];
        gl.glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] == GL_FALSE) {
            int[] length = new int[1];
            gl.glGetProgramiv(program, GL_INFO_LOG_LENGTH, length, 0);
            
            byte[] log = new byte[length[0]];
            gl.glGetProgramInfoLog(program, length[0], length, 0, log, 0);
            
            String error = new String(log, 0, length[0]);
            System.err.print(computeShaderFN + ": ");
            System.err.println(error);
        }
        
        return program;
    }
    
    public static String readFile(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        
        int c;
        while ((c = reader.read()) != -1) {
            sb.append((char) c);
        }
        
        return sb.toString();
    }
    
    public static List<Atom> loadAtoms(String filename) throws IOException {
        // load van der Waals radii
        Map<String, Float> radii = loadVDWRadii("/resources/vdwradii.csv");
        // load volumes
        Map<String, Map<String, Float>> volumes = loadVolumes("/resources/volumes.csv");
        // load atom coordinates
        InputStream is = Utils.class.getResourceAsStream(filename);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is)))
        {
            List<Atom> atoms = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ATOM")) {
                    String name = line.substring(12, 16).trim();
                    String residue = line.substring(17, 20).trim();
                    Atom atom = new Atom();
                    atom.x = Float.parseFloat(line.substring(30, 38));
                    atom.y = Float.parseFloat(line.substring(38, 46));
                    atom.z = Float.parseFloat(line.substring(46, 54));
                    String code = line.substring(76, 78).trim();
                    atom.r = radii.get(code);
                    Float v = volumes.get(residue).get(name);
                    if (v == null) {
                        v = 0f;
                    }
                    atom.v = v;
                    atoms.add(atom);
                }
            }
            return atoms;
        }
    }
    
    public static List<Vector3f> loadLigandTraj(String filename) throws IOException {
        InputStream is = Utils.class.getResourceAsStream(filename);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
//            Map<Integer, Vector3f> pos = new HashMap<>();
            List<Vector3f> pos = new ArrayList<>();
            String line;
            // read out the header
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                try {
                    
                    float x = Float.parseFloat(columns[1]);
                    float y = Float.parseFloat(columns[2]);
                    float z = Float.parseFloat(columns[3]);
                    pos.add(new Vector3f(x,y,z));
                    System.out.printf(new Vector3f(x,y,z).toString());
                } catch (NumberFormatException e) {
                    System.err.printf("no position data", columns);
                }
            }
            return pos;
        }
    }
    
    public static Map<String, Float> loadVDWRadii(String filename) throws IOException {
        InputStream is = Utils.class.getResourceAsStream(filename);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            Map<String, Float> radii = new HashMap<>();
            String line;
            // read out the header
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                // every line has 4 columns
                String[] columns = line.split(";");
                try {
                    float r = Float.parseFloat(columns[3]) / 100f;
                    radii.put(columns[1], r);
                } catch (NumberFormatException e) {
                    // there is no radii data
                }
            }
            return radii;
        }
    }
    
    public static Map<String, Map<String, Float>> loadVolumes(String filename) throws IOException {
        InputStream is = Utils.class.getResourceAsStream(filename);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            Map<String, Map<String, Float>> volumes = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                // every line has 3 columns
                String[] columns = line.split(";");
                try {
                    Map<String, Float> residue = volumes.get(columns[0]);
                    if (residue == null) {
                        residue = new HashMap<>();
                        volumes.put(columns[0], residue);
                    }
                    float v = Float.parseFloat(columns[2].replace(',', '.'));
                    residue.put(columns[1], v);
                } catch (NumberFormatException e) {
                    // should not happen
                }
            }
            return volumes;
        }
    }
    
    public static void bindShaderStorageBlock(GL4 gl, int program, String name, int index) {
        int blockIndex = gl.glGetProgramResourceIndex(program, GL_SHADER_STORAGE_BLOCK, name.getBytes(), 0);
        if (blockIndex != GL_INVALID_INDEX) {
            gl.glShaderStorageBlockBinding(program, blockIndex, index);
        } else {
            System.err.println("Warning: binding " + name + " not found");
        }
    }
    
    public static int getCounter(GL4 gl, int buffer) {
        return getCounter(gl, buffer, 0);
    }
    
    public static int getCounter(GL4 gl, int buffer, int offset) {
        gl.glBindBuffer(GL_SHADER_STORAGE_BUFFER, buffer);
        gl.glGetBufferSubData(GL_SHADER_STORAGE_BUFFER, offset, 4, COUNTER_DATA);
        gl.glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        return COUNTER_DATA.get(0);
    }
    
    public static void clearCounter(GL4 gl, int buffer) {
        setCounter(gl, buffer, 0, 0);
    }
    
    public static void clearCounter(GL4 gl, int buffer, int offset) {
        setCounter(gl, buffer, offset, 0);
    }
    
    public static void setCounter(GL4 gl, int buffer, int offset, int value) {
        COUNTER_DATA.put(value);
        COUNTER_DATA.rewind();
        gl.glBindBuffer(GL_SHADER_STORAGE_BUFFER, buffer);
        gl.glBufferSubData(GL_SHADER_STORAGE_BUFFER, offset, 4, COUNTER_DATA);
        gl.glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }
    
    public static void setSampler(GL4 gl, int program, String name, int unit) {
        int location = gl.glGetUniformLocation(program, name);
        if (location == INVALID_LOCATION) {
            System.err.println("Warning: sampler " + name + " not found");
        } else {
            gl.glUniform1i(location, unit);
        }
    }
    
    public static void setUniform(GL4 gl, int program, String name, int value) {
        int location = gl.glGetUniformLocation(program, name);
        if (location == INVALID_LOCATION) {
            System.err.println("Warning: uniform " + name + " not found");
        } else {
            gl.glUniform1ui(location, value);
        }
    }
    
    public static void setUniform(GL4 gl, int program, String name, int x, int y) {
        int location = gl.glGetUniformLocation(program, name);
        if (location == INVALID_LOCATION) {
            System.err.println("Warning: uniform " + name + " not found");
        } else {
            gl.glUniform2ui(location, x, y);
        }
    }
    
    public static void setUniform(GL4 gl, int program, String name, float value) {
        int location = gl.glGetUniformLocation(program, name);
        if (location == INVALID_LOCATION) {
            System.err.println("Warning: uniform " + name + " not found");
        } else {
            gl.glUniform1f(location, value);
        }
    }
    
    public static void setUniform(GL4 gl, int program, String name, float x, float y, float z) {
        int location = gl.glGetUniformLocation(program, name);
        if (location == INVALID_LOCATION) {
            System.err.println("Warning: uniform " + name + " not found");
        } else {
            gl.glUniform3f(location, x, y, z);
        }
    }
    
    public static void setUniform(GL4 gl, int program, String name, float x, float y, float z, float w) {
        int location = gl.glGetUniformLocation(program, name);
        if (location == INVALID_LOCATION) {
            System.err.println("Warning: uniform " + name + " not found");
        } else {
            gl.glUniform4f(location, x, y, z, w);
        }
    }
    
    public static void setUniform(GL4 gl, int program, String name, boolean value) {
        setUniform(gl, program, name, value ? 1 : 0);
    }
    
    public static void drawAxes(GL2 gl, float size) {
        gl.glBegin(GL2.GL_LINES);
        // X
        gl.glColor3f(1f, 0f, 0f);
        gl.glVertex3f(0f, 0f, 0f);
        gl.glVertex3f(size, 0f, 0f);
        // Y
        gl.glColor3f(0f, 1f, 0f);
        gl.glVertex3f(0f, 0f, 0f);
        gl.glVertex3f(0f, size, 0f);
        // Z
        gl.glColor3f(0f, 0f, 1f);
        gl.glVertex3f(0f, 0f, 0f);
        gl.glVertex3f(0f, 0f, size);
        
        gl.glEnd();
    }
    
    public static void drawPlane(GL2 gl, float size) {
        drawPlane(gl, size, TRANSPARENT_YELLOW);
    }
    
    public static void drawPlane(GL2 gl, float size, Vector4f color) {
        gl.glBegin(GL_QUADS);
        gl.glColor4f(color.x, color.y, color.z, color.w);
        gl.glVertex3f(-size, 0f, -size);
        gl.glVertex3f(size, 0f, -size);
        gl.glVertex3f(size, 0f, size);
        gl.glVertex3f(-size, 0f, size);
        gl.glEnd();
    }
    
    public static void drawPlane(GL2 gl, Vector4f plane, float size) {
        gl.glPushAttrib(GL_ALL_ATTRIB_BITS);
        gl.glLineWidth(2.0f);
        
        Vector3f n = new Vector3f(plane.x, plane.y, plane.z);
        Vector3f z = new Vector3f();
        z.cross(n, new Vector3f(1f, 0f, 0f));
        z.normalize();
        Vector3f x = new Vector3f();
        x.cross(n, z);
        Vector3f t = new Vector3f(plane.x, plane.y, plane.z);
        t.scale(-plane.w);
        
        gl.glPushMatrix();
        
        float[] rotMat = new float[] {
            x.x, x.y, x.z, 0f,
            n.x, n.y, n.z, 0f,
            z.x, z.y, z.z, 0f,
            0f, 0f, 0f, 1f,
        };
        
        gl.glTranslatef(t.x, t.y, t.z);
        gl.glMultMatrixf(rotMat, 0);
        drawAxes(gl, 2);
        drawPlane(gl, size, new Vector4f(1f, 1f, 0f, 0.7f));
        
        gl.glPopMatrix();
        gl.glPopAttrib();
    }
    
    public static void drawPoint(GL2 gl, Vector3f point, float size) {
        gl.glPushMatrix();
        gl.glTranslatef(point.x, point.y, point.z);
        
        drawAxes(gl, size);
        
        gl.glPopMatrix();
    }
    
}
