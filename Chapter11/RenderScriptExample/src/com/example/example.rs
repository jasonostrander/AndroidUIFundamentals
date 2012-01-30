#pragma version(1)
#pragma rs java_package_name(com.example);
 
#include "rs_graphics.rsh"
 
 // Background color is a 4 part float
float4 bgColor;

// Triangle mesh
rs_mesh gTriangle;

// Rotation float
float gRotation;

void init() {
    // Initialize background color to black
    bgColor = (float4) { 0.0f, 0.0f, 0.0f, 1.0f };
    gRotation = 0.0f;
}

int root() {
    // Set background color
    rsgClearColor(bgColor.x, bgColor.y, bgColor.z, bgColor.w);
    
    // Load matrix for translate and rotate
    rs_matrix4x4 matrix;
    rsMatrixLoadIdentity(&matrix);
    rsMatrixTranslate(&matrix, 300.0f, 300.0f, 0.0f);
    rsMatrixRotate(&matrix, gRotation, 0.0f, 0.0f, 1.0f);
    rsgProgramVertexLoadModelMatrix(&matrix);
    
    // draw the triangle mesh
    rsgDrawMesh(gTriangle);
    
    // Animate rotation
    gRotation += 1.0f;

    // Run every 20 milliseconds
    return 20;
}
