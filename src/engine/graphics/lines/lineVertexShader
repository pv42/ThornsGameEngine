#version 400 core

in vec3 position;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 localTransformationMatrix;

void main(void) {
    vec4 worldPosition = localTransformationMatrix * vec4(position,1.0);
    vec4 positionToCamera = viewMatrix * worldPosition;
    gl_Position = projectionMatrix * positionToCamera;
}
