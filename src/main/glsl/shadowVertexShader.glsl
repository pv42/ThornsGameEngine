#version 150

const int MAX_WEIGHTS = 3;
const int MAX_BONES = 250;

in vec3 position;
in vec3 bone_index;
in vec3 bone_weight;

uniform mat4 mvpMatrix;
uniform float useAnimation;
uniform mat4 bone[MAX_BONES];

void main() {
    vec4 realPos;
    if (useAnimation < 0.5) {
        realPos = vec4(position, 1.0);
    } else {
        realPos = vec4(0);
        for (int i = 0; i < MAX_WEIGHTS; i++) {
            realPos += bone[int(bone_index[i])] *  vec4(position, 1.0) * bone_weight[i];
        }
    }
    gl_Position = mvpMatrix * realPos;
}