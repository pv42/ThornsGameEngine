#version 330
in vec2 position;
in vec2 uv;

out vec2 pass_uv;

uniform mat4 localTransformationMatrix;

void main(void) {
    gl_Position = localTransformationMatrix * vec4(position, 0.0, 1.0);
    pass_uv = uv;
}