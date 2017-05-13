#version 400 core

in vec3 position;
in vec2 uv;
in vec3 normal;

out vec2 pass_uv;
out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];

const float density = 0.0015;
const float gradient = 5.0;

void main(void) {
    vec4 worldPosition = transformationMatrix * vec4(position,1.0);
    vec4 positionToCamera = viewMatrix * worldPosition;
    gl_Position = projectionMatrix * positionToCamera;
    //gl_Position = vec4(position, 1.0);
    pass_uv = uv;
    surfaceNormal = (transformationMatrix * vec4(normal,0.0)).xyz;
    for(int i = 0; i < 4; i++) {
        toLightVector[i] = lightPosition[i] - worldPosition.xyz;
    }
    toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;


    float distance = length(positionToCamera.xyz);
    visibility = exp(-pow((distance*density),gradient));
    visibility = clamp(visibility,0.0,1.0);
}
