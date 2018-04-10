#version 400 core

in vec3 position;
in vec2 uv;
in vec3 normal;

out vec2 pass_uv;
out vec3 surfaceNormal;
out vec3 toLightVector[4]; //lights per entity
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];
uniform float useFakeLightning;
uniform float numberOfRows;
uniform vec2 offset;

//animation:

in vec4 bone_index;
in vec4 bone_weight;
uniform float useAnimation;
uniform mat4 bone[250]; // Settings.MAX_BONES
//

const float density = 0.0035; //0.0035
const float gradient = 5.0;

void main(void) {
    vec4 worldPosition;
    vec3 actualNormal;
    if(/*useAnimation < 0.5*/ true ) {
        worldPosition = transformationMatrix * vec4(position,1.0);
        actualNormal = normal;
    } else {
        vec4 animatedPosition = vec4(0);
        vec4 animatedNormal = vec4(0);
        vec4 bi = bone_index;
        vec4 bw = bone_weight;
        for(int j = 0; j < 4; j++) {
            int i = int(bi.x);    //Cast to int
            animatedPosition += bone[i] *  vec4(position, 1.0) * bw.x;
            animatedNormal += bone[i] * vec4(normal, 1.0) * bw.x;
            bi = bi.yzwx;
            bw = bw.yzwx;
        }
        //animatedPosition = vec4(position, 1.0);
        //animatedNormal = vec4(normal, 1.0);
        worldPosition = transformationMatrix * animatedPosition;
        actualNormal = animatedNormal.xyz / animatedNormal.w;
    }
    vec4 positionToCamera = viewMatrix * worldPosition;
    gl_Position = projectionMatrix * positionToCamera;

    pass_uv = (uv / numberOfRows) + offset;

    if(useFakeLightning > 0.5) {
        actualNormal = vec3(0.0,1.0,0.0);
    }

    surfaceNormal = (transformationMatrix * vec4(actualNormal,0.0)).xyz;
    for(int i = 0; i < 4; i++) {
        toLightVector[i] = lightPosition[i] - worldPosition.xyz;
    }

    toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;

    float distance = length(positionToCamera.xyz);
    visibility = exp(-pow((distance*density),gradient));
    visibility = clamp(visibility,0.0,1.0);
}
