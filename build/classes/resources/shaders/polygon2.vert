#version 430 compatibility
layout(std430) buffer ligands { 
    vec4 positions[];
};

// camera
uniform vec3 camIn;
uniform vec3 camUp;
uniform vec3 camRight;
// viewport

out VertexData {
    vec4 objPos;
    float radius;
    vec4 color;
} vertex;

void main() {
    uint index = gl_GlobalInvocationID.x;
    vec4 pos = positions[index].xyzw;
    gl_Position = pos;
}
