#version 430 core


layout(lines_adjacency) in;
layout(triangle_strip, max_vertices = 32) out;


const int  cubeIndices[24]  = int [24]
    (
      0,1,2,3, //front
      7,6,3,2, //right
      7,5,6,4,  //back or whatever
      4,0,6,2, //btm 
      1,0,5,4, //left
      3,1,7,5
    );

void main() {

}
